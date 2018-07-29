import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bwapi.Order;
import bwapi.TilePosition;
import bwapi.UnitType;

/// 빌드(건물 건설 / 유닛 훈련 / 테크 리서치 / 업그레이드) 명령을 순차적으로 실행하기 위해 빌드 큐를 관리하고, 빌드 큐에 있는 명령을 하나씩 실행하는 class<br>
/// 빌드 명령 중 건물 건설 명령은 ConstructionManager로 전달합니다
/// @see ConstructionManager
public class BuildManager extends Manager {
    private StrategyManager strategyManager;
    private Deque<BuildOrderItem> queue = new LinkedList<>(); // 현재 빌드 오더 정보가 들어 있는 큐.
    private boolean initialBuildFinished = false; // 초기 빌드 오더가 완료되었는지 여부를 리턴.
    private Map<Unit2, Unit2> buildingWorkerMap = new HashMap<>(); // 건설 중인 건물과, 이 건물을 짓고 있는 일꾼을 매핑하고 있는 맵
    private boolean isMoving = false;

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);
	strategyManager = gameStatus.getStrategyManager();
    }

    @Override
    public void onFrame() {
	super.onFrame();

	if (!queue.isEmpty()) {
	    BuildOrderItem buildItem = queue.peek();
	    if (false == buildItem.isInProgress()) {
		Log.info("BuildOrder Start: %s", buildItem.toString());
	    } else {
		// 건설 시작했는데, (돈이 없는 등의 이유로 취소되어서) 할당된 일꾼이 없으면, 건설을 다시 시작한다.
		if (buildItem.getOrder().equals(BuildOrderItem.Order.BUILD)) {
		    Unit2 worker = buildItem.getWorker();
		    switch (worker.getOrder().toString()) {
		    case "ConstructingBuilding":
		    case "PlaceBuilding":
		    case "ResetCollision":
		    case "Harvest3":
			// 건물을 짓기 위해서 뭔가 하는 중이다.
			break;
		    default:
			// 건물을 짓지 못하는 상황이다.
			buildItem.setInProgress(false);
			allianceUnitInfo.addUnitKind(UnitKind.Worker, worker);
			UnitUtil.loggingDetailUnitInfo(worker);
			Log.info("일꾼이 건설을 하지 않아서, 다시 건설을 시작합니다. BuildOrderItem: %s", buildItem);
			break;
		    }
		    if (!worker.getOrder().equals(Order.ConstructingBuilding) && !worker.getOrder().equals(Order.PlaceBuilding)
			    && !worker.getOrder().equals(Order.ResetCollision)) {
		    }

		}
	    }

	    process(buildItem);
	}

	if (gameStatus.isMatchedInterval(1)) {
	    rebalanceFactoryTrainingQueue();
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

    @Override
    public void onUnitDiscover(Unit2 unit) {
	super.onUnitDiscover(unit);

	// 빌드 오더 메니져는 아군 유닛에 대해서만 신경쓴다.
	if (!UnitUtil.isAllianceUnit(unit)) {
	    return;
	}

	if (!queue.isEmpty() && 0 != gameStatus.getFrameCount()) {
	    BuildOrderItem buildItem = queue.peek();
	    if ((buildItem.getOrder().equals(BuildOrderItem.Order.BUILD) || buildItem.getOrder().equals(BuildOrderItem.Order.ADD_ON))
		    && unit.getType().equals(buildItem.getTargetUnitType())) {
		// 건설일 경우만 일꾼을 관리해준다. (애드온은 일꾼을 사용하지 않았으므로 관리하지 않는다.)
		if (buildItem.getOrder().equals(BuildOrderItem.Order.BUILD)) {
		    buildingWorkerMap.put(unit, buildItem.getWorker());
		}
		Log.debug("BuildOrder Finish: %s", buildItem.toString());
		if (buildItem.getTargetUnitType().equals(UnitType.Terran_Refinery)) {
		    allianceUnitInfo.removeUnitKind(UnitKind.Worker, buildItem.getWorker());
		    allianceUnitInfo.addUnitKind(UnitKind.Worker_Gather_Gas, buildItem.getWorker());
		}
		queue.poll();
	    }
	}
    }

    @Override
    protected void onUnitComplete(Unit2 unit) {
	super.onUnitComplete(unit);

	if (buildingWorkerMap.containsKey(unit)) {
	    Unit2 worker = buildingWorkerMap.get(unit);
	    buildingWorkerMap.remove(worker);
	    allianceUnitInfo.addUnitKind(UnitKind.Worker, worker);
	}
    }

    // 높은 우선 순위로 빌드 오더 큐에 넣는다.
    public void addFirst(BuildOrderItem buildItem) {
	queue.addFirst(buildItem);
    }

    // 낮은 우선 순위로 빌드 오더 큐에 넣는다.
    public void addLast(BuildOrderItem buildItem) {
	queue.addLast(buildItem);
    }

    public int getQueueSize() {
	return queue.size();
    }

    private void process(BuildOrderItem buildOrderItem) {
	UnitInfo allianceUnitInfo = gameStatus.getAllianceUnitInfo();
	LocationManager locationManager = gameStatus.getLocationManager();
	ScoutManager scoutManager = gameStatus.getScoutManager();
	WorkerManager workerManager = gameStatus.getWorkerManager();

	BuildOrderItem.Order type = buildOrderItem.getOrder();
	switch (type) {
	case INITIAL_BUILDORDER_FINISH:
	    initialBuildFinished = true;
	    Log.info("Initial build order has finished.");
	    queue.poll();
	    break;
	case SCOUTING:
	    boolean didScout = scoutManager.doFirstSearch();
	    if (true == didScout) {
		queue.poll();
	    }
	    break;
	case SET_STRATEGY_ITEM:
	    strategyManager.addStrategyItem(buildOrderItem.getStrategyItem());
	    queue.poll();
	    break;
	case CLEAR_STRATEGY_ITEM:
	    strategyManager.removeStrategyItem(buildOrderItem.getStrategyItem());
	    queue.poll();
	    break;
	case TRAINING:
	    UnitType targetUnitTypeForTraining = buildOrderItem.getTargetUnitType();

	    if (true == allianceUnitInfo.trainingUnit(targetUnitTypeForTraining)) {
		queue.poll();
		Log.debug("BuildOrder Finish: %s", buildOrderItem.toString());
	    }
	    break;
	case ADD_ON:
	    allianceUnitInfo.buildAddon(buildOrderItem.getTargetUnitType());
	    break;
	case UPGRADE:
	    if (true == allianceUnitInfo.upgrade(buildOrderItem.getUpgradeType())) {
		queue.poll();
		Log.debug("BuildOrder Finish: %s", buildOrderItem.toString());
	    } else if (true == allianceUnitInfo.upgrade(buildOrderItem.getTechType())) {
		queue.poll();
		Log.debug("BuildOrder Finish: %s", buildOrderItem.toString());
	    }
	    break;
	case GATHER_GAS:
	    Unit2 refinery = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Refinery);
	    if (null != refinery && refinery.isCompleted()) {
		Unit2 workerForGatherGas = workerManager.getInterruptableWorker(refinery.getTilePosition());
		if (null != workerForGatherGas) {
		    if (workerForGatherGas.canGather(refinery)) {
			workerForGatherGas.gather(refinery);
			Log.info("일꾼 가스 투입: %d -> %d", workerForGatherGas.getID(), refinery.getID());
			allianceUnitInfo.removeUnitKind(UnitKind.Worker, workerForGatherGas);
			allianceUnitInfo.addUnitKind(UnitKind.Worker_Gather_Gas, workerForGatherGas);
			queue.poll();
		    } else {
			Log.warn("일꾼 가스 투입 실패: %d -> %d", workerForGatherGas.getID(), refinery.getID());
		    }
		} else {
		    Log.warn("가스를 캘 일꾼이 없음.");
		}
	    }
	    break;
	case MOVE_SCV:
	    Unit2 moveWorker = workerManager.getInterruptableWorker(buildOrderItem.getTilePosition());
	    ActionUtil.moveToPosition(allianceUnitInfo, moveWorker, buildOrderItem.getTilePosition().toPosition());
	    if (gameStatus.isExplored(buildOrderItem.getTilePosition())) {
		queue.poll();
		isMoving = true;
	    }
	    break;
	case BUILD:
	    if (false == buildOrderItem.isInProgress()) {
		List<TilePosition> tilePositionList = null; // 건물을 지을 위치
		UnitType buildingType = buildOrderItem.getTargetUnitType(); // 건설할 건물의 종류.
		if (UnitType.Terran_Barracks.equals(buildingType)) {
		    tilePositionList = locationManager.getTrainingBuildings();
		} else if (UnitType.Terran_Refinery.equals(buildingType)) {
		    tilePositionList = locationManager.getBaseRefinery();
		} else if (UnitType.Terran_Factory.equals(buildingType)) {
		    tilePositionList = locationManager.getTrainingBuildings();
		} else if (UnitType.Terran_Supply_Depot.equals(buildingType)) {
		    tilePositionList = locationManager.get3by2SizeBuildings();
		} else if (UnitType.Terran_Academy.equals(buildingType)) {
		    tilePositionList = locationManager.get3by2SizeBuildings();
		} else if (UnitType.Terran_Bunker.equals(buildingType)) {
		    tilePositionList = locationManager.getBaseEntranceBunker();
		}

		if (null != tilePositionList) {
		    // 
		    for (TilePosition tilePosition : tilePositionList) {

			// 건설할 위치에 다른 건물이 이미 건설되어 있거나, 다른 유닛이 위치하고 있으면 skip 한다.
			boolean alreadyBuilt = false;
			Set<Unit2> unitSet = allianceUnitInfo.getUnitSet(UnitKind.ALL);
			for (Unit2 unit : unitSet) {
			    if (unit.getTilePosition().equals(tilePosition)) {
				alreadyBuilt = true;
				break;
			    }
			}
			if (true == alreadyBuilt) {
			    continue;
			}

			// 건설 가능한 일꾼을 가져온다.
			Unit2 worker = workerManager.getInterruptableWorker(tilePosition);
			if (null != worker) {
			    boolean canBuild = worker.canBuild(buildingType, tilePosition);
			    if (true == canBuild) {
				// 건물을 건설할 수 있는 상태가 되면, 건설을 즉시 시작한다.
				worker.build(buildingType, tilePosition);
				buildOrderItem.setInProgress(true);
				buildOrderItem.setWorker(worker);
				allianceUnitInfo.removeUnitKind(UnitKind.Worker, worker);
				Log.info("빌드 오더를 실행합니다: %s", buildOrderItem);
				isMoving = false;
				break;
			    } else {
				// 건설할 수 없는 상태라면, 어느 정도 타이밍에 일꾼이 건설할 위치로 미리 이동해야 할지 컨트롤 한다. 

				// 이미 건물을 짓기 위해서 이동 중이라면 아무것도 하지 않고 skip 한다.
				if (true == isMoving) {
				    break;
				}

				// 건설을 위해서 이동하는 동안 얼마나 미네랄을 모을 수 있을지 계산한다.
				int marginMineral = getMarginMinerals(workerManager, worker, tilePosition);

				if (gameStatus.getMineral() + marginMineral > buildOrderItem.getTargetUnitType().mineralPrice()) {
				    if (false == isMoving) {
					Log.debug("미리 이동: current mineral: %d, margin mineral: %d, building price: %d", gameStatus.getMineral(), marginMineral,
						buildOrderItem.getTargetUnitType().mineralPrice());
					BuildOrderItem moveOrder = new BuildOrderItem(BuildOrderItem.Order.MOVE_SCV, tilePosition);
					queue.addFirst(moveOrder);
				    }
				}
				break;
			    }
			} else {
			    Log.error("건물을 건설할 일꾼이 없습니다. buildOrderItem: %s", buildOrderItem);
			}

		    }
		} else {
		    Log.error("더 이상 건물을 지을 공간이 없습니다. BuildOrderItem: ", buildOrderItem);
		}
	    }
	    break;
	default:
	    break;
	}
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

	Deque<BuildOrderItem> newQueue = new LinkedList<>(); // 새로 재배열 될 큐.

	// 큐를 순회하면서, 서플라이가 부족한 시점에 서플라이를 생성한다.
	Log.debug("빌드 오더 재배열 시작");
	for (BuildOrderItem buildOrderItem : queue) {
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
		newQueue.add(buildOrderBuildSupplyDepot);
	    }
	    Log.debug("기존 빌드 오더 add: %s", buildOrderItem);
	    newQueue.add(buildOrderItem);
	}
	while (supplyRemain < 16) {
	    BuildOrderItem buildOrderBuildSupplyDepot = new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot);
	    Log.debug("신규 빌드 오더 add: %s", buildOrderBuildSupplyDepot);
	    supplyRemain += supplyProvided;
	    Log.debug("빌드 오더 큐에 서플라이 신규 추가해서 여유량 증가함=%d", supplyRemain);
	    newQueue.add(buildOrderBuildSupplyDepot);
	}
	queue = newQueue;
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

	for (BuildOrderItem buildOrderItem : queue) {
	    if (buildOrderItem.getOrder().equals(order) && buildOrderItem.getTargetUnitType().equals(unitType)) {
		result += 1;
	    }

	}

	return result;
    }
};