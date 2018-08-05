import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bwapi.Color;
import bwapi.Order;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.UnitType;

/// 빌드(건물 건설 / 유닛 훈련 / 테크 리서치 / 업그레이드) 명령을 순차적으로 실행하기 위해 빌드 큐를 관리하고, 빌드 큐에 있는 명령을 하나씩 실행하는 class<br>
/// 빌드 명령 중 건물 건설 명령은 ConstructionManager로 전달합니다
/// @see ConstructionManager
public class BuildManager extends Manager {
    private static final String TAG = "[BuildManager]";

    private StrategyManager strategyManager;
    private LocationManager locationManager;
    private WorkerManager workerManager;
    private ScoutManager scoutManager;

    private int maxConstructWorker = 5;

    private List<BuildOrderItem> buildOrderItemList = new LinkedList<>();
    private boolean initialBuildFinished = false; // 초기 빌드 오더가 완료되었는지 여부를 리턴.
    private Map<Unit2, Unit2> buildingWorkerMap = new HashMap<>(); // 건설 중인 건물과, 이 건물을 짓고 있는 일꾼을 매핑하고 있는 맵

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);
	strategyManager = gameStatus.getStrategyManager();
	locationManager = gameStatus.getLocationManager();
	workerManager = gameStatus.getWorkerManager();
	scoutManager = gameStatus.getScoutManager();
    }

    @Override
    public void onFrame() {
	super.onFrame();

	if (0 == gameStatus.getFrameCount()) {
	    return;
	}

	Iterator<BuildOrderItem> iterator = buildOrderItemList.iterator();
	//	Log.info("%s BuildOrder Execute start. buildOrderSize=%d, Mieral=%d, Gas=%d", TAG, buildOrderItemList.size(), gameStatus.getMineral(), gameStatus.getGas());
	while (iterator.hasNext()) {

	    BuildOrderItem buildOrderItem = iterator.next();
	    Log.info("%s Current Order=%s", TAG, buildOrderItem);

	    BuildOrderItem.Order order = buildOrderItem.getOrder();

	    if (!buildOrderItemList.get(0).equals(buildOrderItem)) {
		Log.trace("%s 빌드 오더 Skip (sequence): %s", TAG, buildOrderItem);
		break;
	    }

	    boolean jobFinish = false;

	    switch (order) {
	    case ADD_ON:
		jobFinish = tryAddOn(buildOrderItem);
		break;
	    case BUILD:
		jobFinish = tryBuild(buildOrderItem);
		break;
	    case CLEAR_STRATEGY_ITEM:
		jobFinish = tryClearStrategyItem(buildOrderItem);
		break;
	    case GATHER_GAS:
		jobFinish = tryGatherGas(buildOrderItem);
		break;
	    case INITIAL_BUILDORDER_FINISH:
		jobFinish = tryInitialBuildOrderFinish(buildOrderItem);
		break;
	    case SCOUTING:
		jobFinish = tryScouting(buildOrderItem);
		break;
	    case SET_STRATEGY_ITEM:
		jobFinish = trySetStrategyItem(buildOrderItem);
		break;
	    case TRAINING:
		jobFinish = tryTraining(buildOrderItem);
		break;
	    case UPGRADE:
		jobFinish = tryUpgrade(buildOrderItem);
		break;
	    default:
		break;
	    }

	    if (true == jobFinish) {
		//		Log.trace("%s 빌드 오더 실행 완료: %s", TAG, buildOrderItem);
		iterator.remove();
	    } else if (true == buildOrderItem.isInProgress()) {
		//		Log.trace("%s 빌드 오더 실행 중: %s", TAG, buildOrderItem);
	    } else {
		//		Log.trace("%s 빌드 오더 Skip: %s", TAG, buildOrderItem);
	    }

	}
	//	Log.info("%s BuildOrder Execute finish", TAG);

	if (gameStatus.isMatchedInterval(1)) {
	    rebalanceFactoryTrainingQueue();
	}
    }

    @Override
    public void onUnitDiscover(Unit2 unit) {
	super.onUnitDiscover(unit);

	if (0 == gameStatus.getFrameCount()) {
	    return;
	}

	// 빌드 오더 메니져는 아군 유닛에 대해서만 신경쓴다.
	if (!UnitUtil.isAllianceUnit(unit)) {
	    return;
	}

	Iterator<BuildOrderItem> iterator = buildOrderItemList.iterator();
	while (iterator.hasNext()) {
	    BuildOrderItem buildOrderItem = iterator.next();

	    if ((buildOrderItem.getOrder().equals(BuildOrderItem.Order.BUILD) || buildOrderItem.getOrder().equals(BuildOrderItem.Order.ADD_ON))
		    && unit.getType().equals(buildOrderItem.getTargetUnitType())) {
		// 건설일 경우만 일꾼을 관리해준다. (애드온은 일꾼을 사용하지 않았으므로 관리하지 않는다.)
		if (buildOrderItem.getOrder().equals(BuildOrderItem.Order.BUILD)) {
		    Log.debug("wiss 건물=%s,일꾼=%s 건설 시작", unit, buildOrderItem.getWorker());
		    buildingWorkerMap.put(unit, buildOrderItem.getWorker());
		}
		Log.trace("%s 빌드 오더 실행 완료: %s", TAG, buildOrderItem);
		if (buildOrderItem.getTargetUnitType().equals(UnitType.Terran_Refinery)) {
		    allianceUnitInfo.removeUnitKind(UnitKind.Worker, buildOrderItem.getWorker());
		    allianceUnitInfo.addUnitKind(UnitKind.Worker_Gather_Gas, buildOrderItem.getWorker());
		}

		// 빌드 오더 리스트에서 제외해 준다.
		iterator.remove();
		break;
	    }
	}
    }

    @Override
    protected void onUnitComplete(Unit2 unit) {
	super.onUnitComplete(unit);

	if (unit.getType().isBuilding()) {
	    Log.debug("wiss 건물=%s,일꾼=%s 건설 종료", unit, buildingWorkerMap.get(unit));
	    if (buildingWorkerMap.containsKey(unit)) {
		Unit2 worker = buildingWorkerMap.get(unit);
		buildingWorkerMap.remove(unit);
		allianceUnitInfo.removeUnitKind(UnitKind.Worker_Construct, worker);
		allianceUnitInfo.addUnitKind(UnitKind.Worker, worker);
		Log.debug("wiss 일꾼 kind 해제");
	    }
	}
    }

    private void rebalanceFactoryTrainingQueue() {
	Unit2 emptyAddon = null;
	Unit2 waitingAddon = null;
	Unit2 emptyNotAddon = null;
	Unit2 waitingNotAddon = null;

	Set<Unit2> factorySet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Factory);
	for (Unit2 factory : factorySet) {
	    Unit2 addon = factory.getAddon();
	    if (null == addon) {
		if (factory.getTrainingQueue().size() >= 2) {
		    waitingNotAddon = factory;
		} else if (factory.getTrainingQueue().size() == 0) {
		    emptyNotAddon = factory;
		}
	    } else if (addon.isCompleted()) {
		if (factory.getTrainingQueue().size() >= 2) {
		    waitingAddon = factory;
		} else if (factory.getTrainingQueue().size() == 0) {
		    emptyAddon = factory;
		}
	    }
	}
	if (null != emptyAddon && null != waitingAddon) {
	    UnitType unit = waitingAddon.getTrainingQueue().get(1);
	    waitingAddon.cancelTrain(1);
	    emptyAddon.train(unit);
	}

	if (null != emptyNotAddon && null != waitingNotAddon) {
	    UnitType unit = waitingNotAddon.getTrainingQueue().get(1);
	    waitingNotAddon.cancelTrain(1);
	    emptyNotAddon.train(unit);
	}
    }

    // 높은 우선 순위로 빌드 오더 큐에 넣는다.
    public void addFirst(BuildOrderItem buildItem) {
	Log.info("%s addFirst(%s)", TAG, buildItem);
	buildOrderItemList.add(0, buildItem);
	//queue.addFirst(buildItem);
    }

    // 낮은 우선 순위로 빌드 오더 큐에 넣는다.
    public void addLast(BuildOrderItem buildItem) {
	Log.info("%s addLast(%s)", TAG, buildItem);
	buildOrderItemList.add(buildItem);
	//queue.addLast(buildItem);
    }

    public int getQueueSize() {
	return buildOrderItemList.size();
	//return queue.size();
    }

    private int getMarginMinerals(WorkerManager workerManager, Unit2 worker, TilePosition tilePosition) {
	int distance = UnitUtil.getDistance(worker, tilePosition);
	double moveTime = distance / worker.getType().topSpeed() / 42;
	int mineralIncome = workerManager.getMineralIncome();

	return (int) (moveTime * mineralIncome);
    }

    // 초기 빌드 오더가 완료되었는지 여부를 리턴한다.
    public boolean isInitialBuildFinished() {
	return initialBuildFinished;
    }

    public void rearrangeForSupply() {
	final int supplyProvided = UnitType.Terran_Supply_Depot.supplyProvided();

	// 서플라이가 꽉 찼으면 더 이상 짓지 않는다.
	if (gameStatus.getSupplyTotal() >= 400) {
	    return;
	}

	// 현재 남아있는 서플라이 여유량을 가져온다.
	int supplyRemain = gameStatus.getSupplyRemain();

	// 빌드 오더 큐에는 없지만, 예약되어서 생산을 대기 중인 유닛의 서플라이 사용량을 계산한다.
	int supplyReserved = allianceUnitInfo.getReservedSupply();

	// 여유 서플라이 양에 훈련 대기 중인 유닛과 건설중인 서플라이 디팟을 반영한다.
	supplyRemain = supplyRemain - supplyReserved + getUnderConstructingSupplyDepotSize() * supplyProvided;
	Log.info("서플라이 여유량=%d", supplyRemain);

	List<BuildOrderItem> newBuildOrderItemList = new LinkedList<>(); // 새로 재배열 될 큐.

	// 큐를 순회하면서, 서플라이가 부족한 시점에 서플라이를 생성한다.
	Log.debug("빌드 오더 재배열 시작");
	for (BuildOrderItem buildOrderItem : buildOrderItemList) {
	    if (buildOrderItem.getOrder().equals(BuildOrderItem.Order.TRAINING)) {
		supplyRemain -= buildOrderItem.getTargetUnitType().supplyRequired();
		Log.debug("빌드 오더 큐에 유닛 훈련해서 여유량 감소함=%d", supplyRemain);
	    } else if (buildOrderItem.getOrder().equals(BuildOrderItem.Order.BUILD) && buildOrderItem.getTargetUnitType().equals(UnitType.Terran_Supply_Depot)) {
		supplyRemain += supplyProvided;
		Log.debug("빌드 오더 큐에 서플라이 건설해서 여유량 증가함=%d", supplyRemain);
	    }
	    Log.debug("빌드 오더 큐의 서플라이 여유량=%d", supplyRemain);
	    // 서플라이가 부족하다면, 서플라이 짓는 빌드 오더를 추가한다.
	    while (supplyRemain < 16) {
		BuildOrderItem buildOrderBuildSupplyDepot = new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot);
		Log.debug("신규 빌드 오더 add: %s", buildOrderBuildSupplyDepot);
		supplyRemain += supplyProvided;
		Log.debug("빌드 오더 큐에 서플라이 신규 추가해서 여유량 증가함=%d", supplyRemain);
		newBuildOrderItemList.add(buildOrderBuildSupplyDepot);
	    }
	    Log.debug("기존 빌드 오더 add: %s", buildOrderItem);
	    newBuildOrderItemList.add(buildOrderItem);
	}
	while (supplyRemain < 16) {
	    BuildOrderItem buildOrderBuildSupplyDepot = new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot);
	    Log.debug("신규 빌드 오더 add: %s", buildOrderBuildSupplyDepot);
	    supplyRemain += supplyProvided;
	    Log.debug("빌드 오더 큐에 서플라이 신규 추가해서 여유량 증가함=%d", supplyRemain);
	    newBuildOrderItemList.add(buildOrderBuildSupplyDepot);
	}
	buildOrderItemList = newBuildOrderItemList;
	Log.debug("빌드 오더 재배열 완료");
    }

    // 현재 건설중인(완성되지 않은) 서플라이 디팟의 개수
    private int getUnderConstructingSupplyDepotSize() {
	int result = 0;

	for (Unit2 building : allianceUnitInfo.getUnitSet(UnitKind.Building)) {
	    if (building.getType().equals(UnitType.Terran_Supply_Depot) && !building.isCompleted()) {
		result += 1;
	    }
	}

	return result;
    }

    // 현재 빌드 오더 큐에서 order를 대기 중인 unitType의 개수
    public int getBuildOrderQueueItemCount(BuildOrderItem.Order order, UnitType unitType) {
	int result = 0;

	for (BuildOrderItem buildOrderItem : buildOrderItemList) {
	    if (buildOrderItem.getOrder().equals(order) && buildOrderItem.getTargetUnitType().equals(unitType)) {
		result += 1;
	    }

	}

	return result;
    }

    private boolean tryUpgrade(BuildOrderItem buildOrderItem) {
	boolean result = false;

	if (true == allianceUnitInfo.upgrade(buildOrderItem.getUpgradeType())) {
	    result = true;
	} else if (true == allianceUnitInfo.upgrade(buildOrderItem.getTechType())) {
	    result = true;
	}

	if (true == result) {
	    buildOrderItem.setInProgress(true);
	}

	return result;
    }

    private boolean tryTraining(BuildOrderItem buildOrderItem) {
	boolean result = false;

	UnitType targetUnitTypeForTraining = buildOrderItem.getTargetUnitType();

	result = allianceUnitInfo.trainingUnit(targetUnitTypeForTraining);

	if (true == result) {
	    buildOrderItem.setInProgress(true);
	}

	return result;
    }

    private boolean tryScouting(BuildOrderItem buildOrderItem) {
	boolean result = scoutManager.doFirstSearch();

	if (true == result) {
	    buildOrderItem.setInProgress(true);
	}

	return result;
    }

    private boolean tryInitialBuildOrderFinish(BuildOrderItem buildOrderItem) {
	initialBuildFinished = true;
	buildOrderItem.setInProgress(true);
	return true;
    }

    private boolean tryGatherGas(BuildOrderItem buildOrderItem) {
	boolean result = false;

	Unit2 refinery = null;
	if (buildOrderItem.getTargetUnit() != null) {
	    refinery = buildOrderItem.getTargetUnit();
	} else {
	    refinery = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Refinery);
	}

	if (null != refinery && refinery.isCompleted()) {
	    Unit2 workerForGatherGas = workerManager.getInterruptableWorker(refinery.getTilePosition());
	    if (null != workerForGatherGas) {
		if (workerForGatherGas.canGather(refinery)) {
		    workerForGatherGas.gather(refinery);
		    Log.info("%s 일꾼 가스 투입: %s -> %s", TAG, workerForGatherGas, refinery);
		    allianceUnitInfo.removeUnitKind(UnitKind.Worker, workerForGatherGas);
		    allianceUnitInfo.addUnitKind(UnitKind.Worker_Gather_Gas, workerForGatherGas);
		    buildOrderItem.setInProgress(true);
		    result = true;
		} else {
		    Log.warn("%s 일꾼 가스 투입 실패: %s -> %s", TAG, workerForGatherGas, refinery);
		}
	    } else {
		Log.warn("%s 가스를 캘 일꾼이 없음.", TAG);
	    }
	} else {
	    Log.warn("%s 가스를 캘 수 있는 건물이 없습니다.", TAG);
	}

	return result;
    }

    private boolean tryClearStrategyItem(BuildOrderItem buildOrderItem) {
	strategyManager.removeStrategyItem(buildOrderItem.getStrategyItem());
	buildOrderItem.setInProgress(true);

	return true;
    }

    private boolean trySetStrategyItem(BuildOrderItem buildOrderItem) {
	strategyManager.addStrategyItem(buildOrderItem.getStrategyItem());
	buildOrderItem.setInProgress(true);

	return true;
    }

    private boolean tryBuild(BuildOrderItem buildOrderItem) {
	Set<Unit2> constructWorker = allianceUnitInfo.getUnitSet(UnitKind.Worker_Construct);

	// 어떤 일꾼이 어디로 건설을 할 예정인지 라인을 그려서 화면에 표시한다.
	if (gameStatus.getConfig().isDrawbuildLine()) {
	    gameStatus.drawLineMap(buildOrderItem.getWorker(), buildOrderItem.getTilePosition(), Color.Grey);
	}

	Log.debug("%s 건설 중인 일꾼 수: %d", TAG, constructWorker.size());
	// 동시에 지을 수 있는 건물 수 제한을 체크한다.
	if (constructWorker.size() <= maxConstructWorker) {

	    List<TilePosition> tilePositionList = null; // 건물을 지을 위치
	    UnitType buildingType = buildOrderItem.getTargetUnitType(); // 건설할 건물의 종류.

	    // 건물을 지을 수 있는 타일 리스트를 가져온다.
	    tilePositionList = getTileListForBuild(buildingType, buildOrderItem);

	    for (TilePosition tilePosition : tilePositionList) {

		// 건설할 위치에 다른 건물이 이미 건설되어 있거나, 다른 유닛이 위치하고 있으면 skip하고 다음 타일에 건물을 짓는다.
		// TODO: 건물을 지을 때 기준 타일 위치는 건물의 top left이다. 2by2 건물을 짓는다고 했을 때 유닛 위치가 top + 1, left + 1일 경우에도 건물을 지을 수 없지만, 현재는 이를 확인하지 못하는 문제가 있다. 개선하자.
		boolean alreadyBuilt = false;

		// 건물을 지을 때 방해 받는지 여부를 아군 건물/유닛에 대해서 검사한다
		Set<Unit2> unitSet = allianceUnitInfo.getUnitSet(UnitKind.ALL);
		alreadyBuilt = isCanNotBuild(tilePosition, unitSet);

		// 건물을 지을 때 방해 받는지 여부를 적군 건물/유닛에 대해서 검사한다.
		if (false == alreadyBuilt) {
		    unitSet = enemyUnitInfo.getUnitSet(UnitKind.ALL);
		    alreadyBuilt = isCanNotBuild(tilePosition, unitSet);
		}

		// 건물이나 유닛에 의해서 건설이 방해 받는 상황이라면, 다음 타일에 건물을 짓는다.
		if (true == alreadyBuilt) {
		    continue;
		}

		// 기존에 할당 받았던 일꾼을 가져온다.
		Unit2 oldWorker = buildOrderItem.getWorker();

		// 현 시점에 건물 짓기에 최적화된 일꾼을 다시 가져온다.
		Unit2 newWorker = workerManager.getInterruptableWorker(tilePosition);

		// 최종적으로 건물을 지을 일꾼.
		Unit2 worker = null;

		// 건물을 지을 일꾼이 할당되지 않았던 상태에서, 최초로 건물을 지을 일꾼이 지정되었다.
		if (null == oldWorker && null != newWorker) {
		    worker = newWorker;
		}

		// 둘 중 더 가까운 일꾼을 다시 선택한다. 거리 차이가 16 이하라면, 조금 멀더라도 기존 일꾼이 계속 건설하는 것이 효율적이다.
		worker = UnitUtil.getCloseUnit(oldWorker, newWorker, tilePosition, 16, 500);

		// 건설용 일꾼이 교체되었다면, 이에 대한 처리를 한다.
		if (null != worker && !worker.equals(oldWorker)) {
		    if (null != oldWorker) {
			Log.debug("wiss oldWorker=%s(%s)와 newWorker=%s를 변경한다. old: %d, new: %d; tile: %s", oldWorker, oldWorker.getOrder(), newWorker,
				UnitUtil.getDistance(oldWorker, tilePosition), UnitUtil.getDistance(newWorker, tilePosition), tilePosition);
			allianceUnitInfo.removeUnitKind(UnitKind.Worker_Construct, oldWorker);
			allianceUnitInfo.addUnitKind(UnitKind.Worker, oldWorker);
			ActionUtil.stop(allianceUnitInfo, oldWorker);
		    }
		    buildOrderItem.setWorker(newWorker);
		}
		if (null != worker) {
		    buildOrderItem.setTilePosition(tilePosition);

		    Log.trace("일꾼 상태: %s", worker.getOrder());

		    // 건물을 건설하기 진전 상태라면, 추가 명령을 주지 않는다.
		    if (worker.getOrder().equals(Order.PlaceBuilding)) {
			break;
		    }

		    boolean canBuild = worker.canBuild(buildingType, tilePosition);
		    if (true == canBuild) {
			// 건물을 건설할 수 있는 상태가 되면, 건설을 즉시 시작한다.
			ActionUtil.build(allianceUnitInfo, worker, buildingType, tilePosition);
			//worker.build(buildingType, tilePosition);
			allianceUnitInfo.removeUnitKind(UnitKind.Worker, worker);
			allianceUnitInfo.addUnitKind(UnitKind.Worker_Construct, worker);
			buildOrderItem.setWorker(worker);
			Log.info("%s 건물을 짓는다. 건물=%s, 위치=%s, 일꾼=%s", TAG, buildingType, tilePosition, worker);
			break;
		    } else {

			//데드락 확인용 원 그리기
			TilePosition checkTile = new TilePosition(tilePosition.getX() + 2, tilePosition.getY() + 1);
			MyBotModule.Broodwar.drawCircleMap(checkTile.getX() * 32, checkTile.getY() * 32, 50, Color.Green);
			for (Unit2 unit2 : allianceUnitInfo.findUnitSetNearTile(checkTile, UnitKind.ALL, 50)) {
			    if (unit2.getID() != worker.getID()) {
				Position randomPosition = randomPosition(unit2.getPosition(), 1000);
				ActionUtil.moveToPosition(allianceUnitInfo, unit2, randomPosition, 1000);
			    }

			}
			// 건설할 수 없는 상태라면, 어느 정도 타이밍에 일꾼이 건설할 위치로 미리 이동해야 할지 컨트롤 한다.
			// 이미 건물을 짓기 위해서 이동 중이라면 아무것도 하지 않고 skip 한다.

			// 건설을 위해서 이동하는 동안 얼마나 미네랄을 모을 수 있을지 계산한다.
			// TODO 가스도 포함해서 계산하자.
			int expectGatherMineral = getMarginMinerals(workerManager, worker, tilePosition);

			if (gameStatus.getMineral() + expectGatherMineral > buildOrderItem.getTargetUnitType().mineralPrice()) {
			    Log.debug("%s 건설 일꾼을 미리 이동: current mineral: %d, margin mineral: %d, building price: %d", TAG, gameStatus.getMineral(), expectGatherMineral,
				    buildOrderItem.getTargetUnitType().mineralPrice());
			    ActionUtil.moveToPosition(allianceUnitInfo, worker, tilePosition);
			    buildOrderItem.setWorker(worker);
			}
			break;
		    }
		} else {
		    Log.warn("%s 건물을 건설할 일꾼이 없습니다. buildOrderItem: %s", TAG, buildOrderItem);
		}
	    }
	}

	return false;
    }

    private boolean isCanNotBuild(TilePosition tilePosition, Set<Unit2> unitSet) {
	boolean result = false;

	for (Unit2 unit : unitSet) {
	    if (unit.exists() && unit.isVisible()) {
		// 방해하는 유닛이 아군 유닛이고, 건설 중이라면 조금 뒤에 건설할 수 있는 상태이므로 건물을 지을 수 있다고 판단한다.
		if (UnitUtil.isAllianceUnit(unit) && null != unit.getOrder()
			&& ((unit.getOrder().equals(Order.PlaceBuilding) || unit.getOrder().equals(Order.ConstructingBuilding)))) {
		    continue;
		}
		// 건물이나 유닛에 막혀 있다면, 포기하고 다음 위치에 건물을 짓는다.
		if (unit.getTilePosition().equals(tilePosition)) {
		    result = true;
		    break;
		}
	    }
	}
	return result;

    }

    private boolean tryAddOn(BuildOrderItem buildOrderItem) {
	boolean orderExecuted = allianceUnitInfo.buildAddon(buildOrderItem.getTargetUnitType());

	if (true == orderExecuted) {
	    buildOrderItem.setInProgress(true);
	}

	return false;
    }

    private List<TilePosition> getTileListForBuild(UnitType buildingType, BuildOrderItem buildOrderItem) {
	List<TilePosition> result = new ArrayList<>();

	if (UnitType.Terran_Barracks.equals(buildingType)) {
	    result = locationManager.getEntranceBuilding();
	} else if (UnitType.Terran_Refinery.equals(buildingType)) {
	    if (buildOrderItem.getTilePosition() != null) {
		result.add(buildOrderItem.getTilePosition());
	    } else {
		result = locationManager.getBaseRefinery();
	    }
	} else if (UnitType.Terran_Factory.equals(buildingType) || UnitType.Terran_Starport.equals(buildingType) || UnitType.Terran_Science_Facility.equals(buildingType)) {
	    result = locationManager.getTrainingBuildings();
	} else if (UnitType.Terran_Supply_Depot.equals(buildingType)) {
	    result = locationManager.get3by2SizeBuildings();
	} else if (UnitType.Terran_Academy.equals(buildingType) || UnitType.Terran_Armory.equals(buildingType)) {
	    result = locationManager.get3by2SizeBuildings();
	} else if (UnitType.Terran_Bunker.equals(buildingType)) {
	    if (buildOrderItem.getTilePosition() != null) {
		result.add(buildOrderItem.getTilePosition());
	    } else {
		result = locationManager.getBaseEntranceBunker();
	    }
	} else if (UnitType.Terran_Command_Center.equals(buildingType)) {
	    if (buildOrderItem.getTilePosition() != null) {
		result.add(buildOrderItem.getTilePosition());
	    } else {
		result = locationManager.getTrainingBuildings();
	    }
	} else {
	    Log.error("%s 정의되지 않는 건물 타입입니다: %s", TAG, buildingType);
	    result = new LinkedList<>();
	}

	return result;
    }

    public static Position randomPosition(Position sourcePosition, int dist) {
	int x = sourcePosition.getX() + (int) (Math.random() * dist) - dist / 2;
	int y = sourcePosition.getY() + (int) (Math.random() * dist) - dist / 2;
	Position destPosition = new Position(x, y);
	return destPosition;
    }

}