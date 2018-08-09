import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bwapi.Order;
import bwapi.TilePosition;
import bwapi.UnitType;

/// 일꾼 유닛들의 상태를 관리하고 컨트롤하는 class
public class WorkerManager extends Manager {
    private StrategyManager strategyManager;
    private BuildManager buildManager;
    private LocationManager locationManager;
    private Deque<Integer> mineralQueue = new LinkedList<>();
    private Deque<Integer> gasQueue = new LinkedList<>();
    private int mineralIncome = 0; // 초당 얼마만큼의 미네랄을 모으는지 확인 
    private int gasIncome = 0; // 초당 얼마만큼의 가스를 모으는지 확인 

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);

	this.strategyManager = gameStatus.getStrategyManager();
	this.buildManager = gameStatus.getBuildManager();
	this.locationManager = gameStatus.getLocationManager();
    }

    @Override
    public void onFrame() {
	super.onFrame();

	// 최초 일꾼을 미네랄에 골고루 분배시킨다.
	if (1 < gameStatus.getFrameCount()) {
	    idleWorkerCheck();
	}

	if (gameStatus.isMatchedInterval(1)) {
	    updateResourceGatheringRate();
	}

	if (gameStatus.isMatchedInterval(3)) {
	    checkRebuild();
	}

	autoRebalanceWorker();
	autoTrainWorker();
	doRefineryJob();

	//loggingDetailSCVInfo();
    }

    @Override
    protected void onUnitComplete(Unit2 unit) {
	super.onUnitComplete(unit);

	// idle 상태의 일꾼을 체크해서, 놀고 있는 일꾼을 일하게 만든다.
	// 0 프레임은 onStart 단계이므로 무시한다
	if (0 != gameStatus.getFrameCount()) {
	    if (UnitUtil.isAllianceUnit(unit)) {
		if (unit.getType().isWorker()) {
		    idleWorkerCheck();
		}
	    }
	}
    }

    // tilePosition에서 가장 가까운 투입 가능한 아군 일꾼을 리턴한다. 주로 건물을 수리하거나, 건물을 짓거나, 정찰을 할 SCV를 선택할 때 사용한다.
    // 투입 가능한 일꾼: 미네랄을 캐거나 놀고 있는 일꾼. 건설 중이거나 가스 캐는 일꾼은 대상이 아니다.
    public Unit2 getInterruptableWorker(TilePosition tilePosition) {
	Unit2 result = null;

	if (null != tilePosition) {
	    Set<Unit2> candidate = new HashSet<>();
	    for (Unit2 worker : allianceUnitInfo.getUnitSet(UnitKind.Worker)) {
		if (isinterruptableWorker(worker)) {
		    candidate.add(worker);
		}
	    }

	    // unitIdSet 중에서 tilePosition과 가장 가까운 유닛을 리턴한다.
	    result = allianceUnitInfo.getClosestUnit(candidate, tilePosition.toPosition());
	}

	return result;
    }

    // 프레임 당 예상되는 미네랄 수입
    public int getMineralIncome() {
	return mineralIncome;
    }

    // 프레임 당 예상되는 가스 수입
    public int getGasIncome() {
	return mineralIncome;
    }

    // 일꾼이 새로운 임무(정찰, 건설, 수리 등)를 수행할 수 있는지 여부를 리턴한다.
    private boolean isinterruptableWorker(Unit2 worker) {
	boolean result = false;

	if (null != worker) {
	    if (true == allianceUnitInfo.isKindOf(worker, UnitKind.Worker_Gather_Gas)) {
		// 가스 캐는 일꾼은 건드리지 말자.
		result = false;
	    } else {
		// 커맨드센터에서 훈련중인 SCV가 아니고, 건설중이 아니고, idle 상태의 일꾼이거나 미네랄을 캐는 일꾼이면 OK
		result = worker.isCompleted() && !worker.isConstructing() && !worker.isRepairing()
			&& (worker.isIdle() || worker.isGatheringMinerals() || !worker.isCarryingMinerals() || worker.isMoving() || worker.getOrder().equals(Order.MoveToMinerals));
	    }
	}

	return result;
    }

    // idle 상태의 일꾼을 찾아서 미네랄을 캐도록 일을 시킨다.
    private void idleWorkerCheck() {
	Set<Unit2> workerSet = allianceUnitInfo.getUnitSet(UnitType.Terran_SCV); // 전체 일꾼 목록
	Set<Unit2> commandCenterSet = allianceUnitInfo.getUnitSet(UnitType.Terran_Command_Center); // 전체 SCV 목록

	// Key: Command Center ID, Value: Command Center로 자원을 캐러 갈 일꾼 ID 목록
	Map<Unit2, List<Unit2>> commandCenterWorksListMap = new HashMap<>();

	for (Unit2 worker : workerSet) {
	    // 놀고 있는 일꾼을 대상으로 미네랄을 캔다.
	    if (null != worker && worker.isCompleted() && worker.isIdle()) {
		Log.info("Found idle worker: %d", worker.getID());
		// 일꾼에서 가장 가까운 커맨드 센터를 가져온다.
		Unit2 commandCenter = allianceUnitInfo.getClosestUnit(commandCenterSet, worker.getPosition());
		if (null != commandCenter) {
		    if (!commandCenterWorksListMap.containsKey(commandCenter)) {
			commandCenterWorksListMap.put(commandCenter, new LinkedList<>());
		    }
		    List<Unit2> workerList = commandCenterWorksListMap.get(commandCenter);
		    workerList.add(worker);
		    commandCenterWorksListMap.put(commandCenter, workerList);
		} else {
		    Log.warn("커맨드 센터가 없습니다. 일꾼 ID: %s", worker);
		}
	    }
	}
	for (Unit2 commandCenter : commandCenterWorksListMap.keySet()) {
	    mining(commandCenter, commandCenterWorksListMap.get(commandCenter));
	}
    }

    // 가장 양이 많은 미네랄, 혹은 가장 가까운 미네랄을 캔다. 
    private void mining(Unit2 commandCenter, List<Unit2> workerList) {
	Set<Unit2> mineralSet = new HashSet<>(allianceUnitInfo.getUnitSet(UnitKind.Resource_Mineral_Field));
	for (Unit2 worker : workerList) {
	    Unit2 largestAmountMineral = getLargestAmountMineral(mineralSet, commandCenter, 300); // 커맨드 센터와 100 거리 이내의 미네랄 중, 가장 양이 많은 미네랄
	    if (null != largestAmountMineral) {
		worker.gather(largestAmountMineral);
		mineralSet.remove(largestAmountMineral);
	    }
	}
    }

    // 가장 양이 많은 미네랄을 선택한다. 단, unit으로부터 maxDistance 이내에 있는 미네랄을 대상으로 선택한다.
    private Unit2 getLargestAmountMineral(Set<Unit2> mineralIdSet, Unit2 unit, int maxDistance) {
	Unit2 result = null;

	int largest = Integer.MIN_VALUE;
	for (Unit2 mineral : mineralIdSet) {
	    if (maxDistance > unit.getDistance(mineral) && mineral.isVisible()) {
		if (largest < mineral.getResources()) {
		    largest = mineral.getResources();
		    result = mineral;
		}
	    }
	}

	return result;
    }

    // 건설이 중단된 건물이 있으면 건설을 재개한다.
    private void checkRebuild() {
	// 모든 건물 목록을 가져온다.
	Set<Unit2> buildingSet = allianceUnitInfo.getUnitSet(UnitKind.Building);
	for (Unit2 building : buildingSet) {
	    if (!building.isCompleted() && null == building.getBuildUnit()) {
		// 건물이 건설 중인데, 건설하고 있는 유닛(SCV)가 없으면 다시 rebuild한다.
		Unit2 worker = getInterruptableWorker(building.getTilePosition());
		if (worker.canRightClick(building)) {
		    Log.debug("Rebuild Building=%s, Worker=%s", building, worker);
		    worker.rightClick(building);
		}
	    }
	}
    }

    // 초당 어느 정도의 비율로 자원을 캐는지 모니터링 한다.
    private void updateResourceGatheringRate() {
	if (mineralQueue.size() > 10) {
	    mineralQueue.pollFirst();
	}
	mineralQueue.offer(gameStatus.getGatheredMinerals());
	mineralIncome = (mineralQueue.peekLast() - mineralQueue.peekFirst()) / mineralQueue.size();

	if (gasQueue.size() > 10) {
	    gasQueue.pollFirst();
	}
	gasQueue.offer(gameStatus.getGatheredGas());
	gasIncome = (gasQueue.peekLast() - gasQueue.peekFirst()) / gasQueue.size();
	Log.info("미네랄 채취량=%d, 가스 채취량=%d", mineralIncome, gasIncome);
    }

    private void autoRebalanceWorker() {
	if (!gameStatus.isMatchedInterval(5)) {
	    // 5초에 한 번만 수행한다.
	    return;
	}

	if (!strategyManager.hasStrategyItem(StrategyItem.AUTO_REBALANCE_WORKER)) {
	    return;
	}

	Set<Unit2> rebalanceWorkerSet = new HashSet<>();

	Set<Unit2> commandCenterSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center);
	int lackWorkerCount = 0;

	// 남는 일꾼을 뺀다.
	for (Unit2 commandCenter : commandCenterSet) {
	    if (!commandCenter.exists() || !commandCenter.isCompleted()) {
		continue;
	    }

	    // 커맨드 센터 주변의 미네랄 개수를 구한다.
	    Set<Unit2> mineralSet = allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.Resource_Mineral_Field, 200);
	    // 커맨드 센터 주변의 일꾼 개수를 구한다.
	    Set<Unit2> workerSet = allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.Worker, 200);

	    // 미네랄 개수 * 2 보다 일꾼이 많으면, 초과 일꾼을 옮길 준비한다.
	    int rebalanceWorkerSize = workerSet.size() - mineralSet.size() * 1;
	    Log.info("autoRebalanceWorker (빼기): CommandCenter(%s)의 미네랄 수: %d, 일꾼 수: %d, rebalanceWorkerSize: %d", commandCenter, mineralSet.size(), workerSet.size(),
		    rebalanceWorkerSize);
	    for (Unit2 worker : workerSet) {
		if (rebalanceWorkerSize <= 0) {
		    break;
		}
		rebalanceWorkerSet.add(worker);
		rebalanceWorkerSize -= 1;
	    }
	}

	// 부족한 일꾼을 붙인다.
	for (Unit2 commandCenter : commandCenterSet) {
	    if (!commandCenter.exists() || !commandCenter.isCompleted()) {
		continue;
	    }

	    //이미 적정 숫자의 일꾼이 있는 커맨드 센터는 제외한다.
	    if (findMineralWorkerSetNear(commandCenter, UnitKind.Terran_SCV, 320).size() > 10) {
		continue;
	    }

	    // 커맨드 센터 주변의 미네랄 개수를 구한다.
	    Set<Unit2> mineralSet = allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.Resource_Mineral_Field, 200);
	    // 커맨드 센터 주변의 일꾼 개수를 구한다.
	    Set<Unit2> workerSet = allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.Worker, 200);

	    // 미네랄 개수 * 2 보다 일꾼이 적으면, 초과 일꾼을 붙인다.
	    int rebalanceWorkerSize = workerSet.size() - mineralSet.size() * 1;
	    Log.info("autoRebalanceWorker (더하기): CommandCenter(%s)의 미네랄 수: %d, 일꾼 수: %d, rebalanceWorkerSize: %d", commandCenter, mineralSet.size(), workerSet.size(),
		    rebalanceWorkerSize);
	    for (Unit2 worker : rebalanceWorkerSet) {
		if (rebalanceWorkerSize >= 0) {
		    break;
		}

		if (allianceUnitInfo.isKindOf(worker, UnitKind.Worker_Gather_Gas)) {
		    continue;
		}

		Log.info("autoRebalanceWorker: 일꾼을 데려온다: %s -> %s", worker, commandCenter);
		ActionUtil.moveToPosition(allianceUnitInfo, worker, commandCenter.getPosition());
		rebalanceWorkerSize -= 1;
	    }

	    lackWorkerCount += rebalanceWorkerSize;
	}
	lackWorkerCount *= -1;
	int totalWorkerSize = allianceUnitInfo.getUnitSet(UnitKind.Worker).size();
	Log.info("최종 부족한 일꾼 개수: %d, 생산되어 있는 일꾼 수: %d", lackWorkerCount, totalWorkerSize);

	// 부족한 일꾼 개수만큼 유닛을 훈련한다. (일꾼 최대 개수는 60)
	//	if (totalWorkerSize < 60) {
	//	    Set<Unit2> CommandCenterSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center);
	//	    for (Unit2 commandCenter : CommandCenterSet) {
	//		if (!commandCenter.exists() || !commandCenter.isCompleted()) {
	//		    continue;
	//		}
	//		if (lackWorkerCount <= 0) {
	//		    break;
	//		}
	//		if (0 == commandCenter.getTrainingQueue().size()) {
	//		    int trainingRemainSize = buildManager.getBuildOrderQueueItemCount(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV);
	//		    if (1 > trainingRemainSize) {
	//			Log.info("일꾼 생산. 남은 훈련시간: %d, 커맨드 센터: %s", commandCenter.getRemainingTrainTime(), commandCenter);
	//			buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	//			lackWorkerCount -= 1;
	//		    }
	//		}
	//	    }
	//	}

	/*
	Log.info("autoRebalanceWorker: 부족한 일꾼 수=%d", lackWorkerCount);
	if (lackWorkerCount > 5) {
	    // 부족한 일꾼 수가 5개 이상이라면, 확장을 하지 않고 일꾼을 뽑는데 힘쓴다.
	    Log.info("autoRebalanceWorker: 확장을 하지 않고 일꾼을 뽑는다.");
	    strategyManager.removeStrategyItem(StrategyItem.AUTO_EXTENSION);
	} else {
	    // 부족한 일꾼이 2개 이하라면 다음 확장을 시도한다.
	    if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center).size() < 4) {
		Log.info("autoRebalanceWorker: 확장을 시도한다.");
		strategyManager.addStrategyItem(StrategyItem.AUTO_EXTENSION);
	    }
	}
	*/
    }

    // 자동으로 SCV를 훈련하는 작업을 수행한다
    private void autoTrainWorker() {

	// 1초에 한 번만 수행된다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	if (buildManager.isInitialBuildFinished()) {

	    if (gameStatus.getSupplyTotal() - gameStatus.getSupplyUsed() < 2) {
		return;
	    }

	    //커맨드 센터 주변의 총 미네랄 갯수를 가져온다.
	    int mineral_count = 0;
	    Set<Unit2> commandCenters = allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center);
	    for (Unit2 commandCenter : commandCenters) {
		int minerals = allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.Resource_Mineral_Field, 320).size();
		if (minerals > 0) {
		    mineral_count += minerals;
		}
	    }

	    if (0 == buildManager.getQueueSize()) {

		if (gameStatus.getMineral() >= 50) {

		    //최대 일꾼 갯수는 미네랄 덩이 *2 + 커맨드 센터 갯수 *4(여유분) 이다. 
		    int maxworkerCount = mineral_count * 2 + 4 * commandCenters.size();

		    Set<Unit2> scvSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_SCV);
		    int scvCount = scvSet.size() + allianceUnitInfo.getTrainingQueueUnitCount(UnitType.Terran_Command_Center, UnitType.Terran_SCV);
		    int maxscv = 60;

		    if (scvCount < maxscv && scvCount < maxworkerCount) {
			Unit2 commandCenter = allianceUnitInfo.getTrainableBuilding(UnitType.Terran_Command_Center, UnitType.Terran_SCV);
			if (null != commandCenter) {
			    if (allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.Resource_Mineral_Field, 320).size() > 4
				    && allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.Resource_Vespene_Geyser, 300).size() > 0) {
				Log.info("SCV 생산. SCV 수: %d,", scvCount);
				commandCenter.train(UnitType.Terran_SCV);
			    }
			}
		    }
		}
	    }
	}
    }

    //가스 일꾼이 3기 미만인 경우, 자동으로 일꾼 3마리를 할당시켜준다. 확장 주변에 베스핀이 있을 경우 리파이너리를 지어준다.
    private void doRefineryJob() {

	if (strategyManager.hasStrategyItem(StrategyItem.AUTO_REFINERY_JOB)) {

	    //5초에 한번만 시행한다. 
	    if (!gameStatus.isMatchedInterval(3)) {
		return;
	    }

	    //아군의 모든 완성된 커맨드 센터를 대상으로 한다.
	    for (Unit2 commandCenter : allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Command_Center)) {

		//대상 커맨드 센터에 할당된 가스 일꾼이 3기 미만일 경우,
		if (allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.Worker_Gather_Gas, 320).size() < 3) {

		    System.out.println(allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.Worker_Gather_Gas, 320).size());

		    //대상 커맨드 센터에 할당된 리파이너리를 가져온다.
		    Unit2 refinery = allianceUnitInfo.getAnyUnitInRange(commandCenter.getPosition(), UnitKind.Terran_Refinery, 320);

		    //리파이너를 찾았을 경우
		    if (refinery != null) {
			//대상 커맨드 센터 주변의 미네랄 일꾼을 찾는다.
			//미네랄 일꾼이 3기 이상이고, 리파이너리가 지어져 있으면 미네랄 일꾼을 가스에 할당한다.
			if (findMineralWorkerSetNear(commandCenter, UnitKind.Terran_SCV, 320).size() >= 3 && refinery.isCompleted()) {
			    buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.GATHER_GAS, refinery));
			} else {
			    //미네랄 일꾼이 부족하거나 리파이너리가 아직 건설 중이다.
			}

		    } else {
			//리파이너리가 없어서 건설이 필요하다.
			//큐에 아무것도 없고, 대상 커맨드 센터가 정상적인 상태일 경우 리파이너리 건설
			if (0 == buildManager.getQueueSize() && commandCenter.isCompleted() && !commandCenter.isLifted()) {
			    //대상 커맨드 센터 주변의 베스핀 가스를 가져온다.
			    //가져온 베스핀 가스 위치에 리파이너리를 건설한다.
			    Unit2 vespene = allianceUnitInfo.getAnyUnitInRange(commandCenter.getPosition(), UnitKind.Resource_Vespene_Geyser, 320);
			    if (vespene != null) {
				buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Refinery, vespene.getTilePosition()));
			    } else {
				//베스핀이 없는 미네랄 멀티이다.
				continue;
			    }
			} else {
			    //큐가 찼거나 커맨드 센터가 건설중이다.
			}
			return;
		    }

		}

		//대상 커맨드 센터에 할당된 가스 일꾼이 3기 이상일 경우 해제하여 미네랄 일꾼으로 돌린다.
		Set<Unit2> gasScv = allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.Worker_Gather_Gas, 320);
		int gasScvCount = gasScv.size();
		if (gasScvCount > 3) {
		    int seq = 0;
		    int releaseNum = gasScvCount - 3;
		    for (Unit2 scv : gasScv) {
			allianceUnitInfo.releaseGasUnit(scv);
			scv.stop();
			seq++;
			if (seq == releaseNum) {
			    return;
			}
		    }

		}
	    }
	}
    }

    private void loggingDetailSCVInfo() {
	Set<Unit2> scvSet = allianceUnitInfo.getUnitSet(UnitType.Terran_SCV);
	Log.trace("SCV size: %d", scvSet.size());
	for (Unit2 scv : scvSet) {
	    UnitUtil.loggingDetailUnitInfo(scv);
	}
    }

    //대상 유닛 근처에 있는 미네랄 일꾼을 리턴한다.
    public Set<Unit2> findMineralWorkerSetNear(Unit2 baseUnit, UnitKind wantFind, int findRange) {

	Set<Unit2> scvUnitSet = allianceUnitInfo.getUnitsInRange(baseUnit.getPosition(), wantFind, findRange);
	Set<Unit2> findUnitSet = new HashSet<>();
	WorkerManager workerManager = gameStatus.getWorkerManager();

	for (Unit2 scv : scvUnitSet) {
	    if (workerManager.isinterruptableWorker(scv)) {
		findUnitSet.add(scv);
	    }
	}

	return findUnitSet;
    }
}