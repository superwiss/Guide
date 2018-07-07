import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

/// 빌드(건물 건설 / 유닛 훈련 / 테크 리서치 / 업그레이드) 명령을 순차적으로 실행하기 위해 빌드 큐를 관리하고, 빌드 큐에 있는 명령을 하나씩 실행하는 class<br>
/// 빌드 명령 중 건물 건설 명령은 ConstructionManager로 전달합니다
/// @see ConstructionManager
public class MagiBuildManager {

    private static MagiBuildManager instance = new MagiBuildManager();

    private Queue<MagiBuildOrderItem> queue = new LinkedList<>();

    /// static singleton 객체를 리턴합니다
    public static MagiBuildManager Instance() {
	return instance;
    }

    private boolean initialBuildFinished = false;
    private int supplyBuildingCount = 0;

    public void onFrame(GameData gameData) {
	if (!queue.isEmpty()) {
	    MagiBuildOrderItem buildItem = queue.peek();
	    if (false == buildItem.isInProgress()) {
		Log.info("BuildOrder Start: %s", buildItem.toString());
	    }
	    process(buildItem, gameData);
	}
    }

    public void onUnitComplete(Unit unit, GameData gameData) {
	if (unit.getType().equals(UnitType.Terran_Supply_Depot)) {
	    supplyBuildingCount--;
	}

    }

    public void onUnitDiscover(Unit unit, GameData gameData) {
	if (!queue.isEmpty() && 0 != gameData.getFrameCount()) {
	    MagiBuildOrderItem buildItem = queue.peek();
	    if (buildItem.getOrder().equals(MagiBuildOrderItem.Order.BUILD) && unit.getType().toString().equals(buildItem.getTargetUnitType().toString())) {
		queue.poll();
		Log.debug("BuildOrder Finish: %s", buildItem.toString());

		if (unit.getType().equals(UnitType.Terran_Supply_Depot)) {
		    supplyBuildingCount++;
		}
	    }
	}
    }

    public void add(MagiBuildOrderItem buildItem) {
	queue.offer(buildItem);
    }

    public int getQueueSize() {
	return queue.size();
    }

    private void process(MagiBuildOrderItem buildOrderItem, GameData gameData) {
	UnitManager allianceUnitManager = gameData.getAllianceUnitManager();
	MagiBuildOrderItem.Order type = buildOrderItem.getOrder();
	switch (type) {
	case INITIAL_BUILDORDER_FINISH:
	    initialBuildFinished = true;
	    Log.info("Initial build order has finished.");
	    queue.poll();
	    break;
	case SCOUTING:
	    // TODO first chock point에서 제일 가까운 유닛으로 변경하기
	    Unit workerForScout = allianceUnitManager.getBuildableWorker(allianceUnitManager.getFirstUnitTilePositionByUnitKind(UnitKind.Terran_Command_Center));
	    if (null != workerForScout) {
		allianceUnitManager.setScoutUnit(workerForScout);
		Log.info("정찰 시작. 정찰 유닛 ID: %d", workerForScout.getID());
		queue.poll();
	    } else {
		Log.warn("정찰할 유닛이 없습니다.");
	    }
	    break;
	case TRAINING_WORKER:
	    trainingWorker(gameData, buildOrderItem);
	    break;
	case TRAINING_MARINE:
	    trainingMarine(gameData, buildOrderItem);
	    break;
	case GATHER_GAS:
	    Unit refinary = allianceUnitManager.getFirstUnitByUnitKind(UnitKind.Terran_Refinery);
	    if (null != refinary) {
		Unit workerForGatherGas = allianceUnitManager.getBuildableWorker(refinary.getTilePosition());
		if (null != workerForGatherGas) {
		    if (workerForGatherGas.canGather(refinary)) {
			workerForGatherGas.gather(refinary);
			Log.info("일꾼 가스 투입: %d -> %d", workerForGatherGas.getID(), refinary.getID());
			allianceUnitManager.addUnitKind(UnitKind.Worker_Gather_Gas, workerForGatherGas);
			queue.poll();
		    } else {
			Log.warn("일꾼 가스 투입 실패: %d -> %d", workerForGatherGas.getID(), refinary.getID());
		    }
		} else {
		    Log.warn("가스를 캘 일꾼이 없음.");
		}
	    }
	    break;
	case BUILD:
	    if (false == buildOrderItem.isInProgress()) {
		List<TilePosition> tilePositionList = null; // 건물을 지을 위치
		UnitType buildingType = buildOrderItem.getTargetUnitType(); // 건설할 건물의 종류.
		if (UnitType.Terran_Barracks.equals(buildingType)) {
		    tilePositionList = LocationManager.Instance().getBarracks();
		} else if (UnitType.Terran_Refinery.equals(buildingType)) {
		    tilePositionList = LocationManager.Instance().getRefinery();
		} else if (UnitType.Terran_Supply_Depot.equals(buildingType)) {
		    tilePositionList = LocationManager.Instance().getSupplyDepot();
		} else if (UnitType.Terran_Academy.equals(buildingType)) {
		    tilePositionList = LocationManager.Instance().getSupplyDepot();
		} else if (UnitType.Terran_Bunker.equals(buildingType)) {
		    tilePositionList = LocationManager.Instance().getBunker();
		}

		if (null != tilePositionList) {
		    for (TilePosition tilePosition : tilePositionList) {
			Unit worker = allianceUnitManager.getBuildableWorker(tilePosition);
			if (null != worker) {
			    boolean canBuild = worker.canBuild(buildingType, tilePosition);
			    if (true == canBuild) {
				worker.build(buildingType, tilePosition);
				buildOrderItem.setInProgress(true);
				MagiWorkerManager.Instance().assignBuildWorker(worker, buildOrderItem);
				break;
			    }
			} else {
			    Log.warn("건물을 건설할 일꾼이 없습니다. BuildOrderItem: %s", buildOrderItem);
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

    // 일꾼을 랜덤한 커맨드 센터에서 훈련한다.
    private void trainingWorker(GameData gameData, MagiBuildOrderItem buildItem) {
	UnitManager allianceUnitManager = gameData.getAllianceUnitManager();
	Unit firstCommandCenter = allianceUnitManager.getFirstUnitByUnitKind(UnitKind.Terran_Command_Center);
	if (null != firstCommandCenter) {
	    int oldQueueSize = firstCommandCenter.getTrainingQueue().size();
	    firstCommandCenter.build(UnitType.Terran_SCV);
	    int newQueueSie = firstCommandCenter.getTrainingQueue().size();
	    if (newQueueSie > oldQueueSize) {
		queue.poll();
		Log.debug("BuildOrder Finish: %s", buildItem.toString());
	    }
	} else {
	    Log.warn("trainingWorker() failed. Command Center does not exist.");
	}
    }

    public Unit getTrainableBarracks(UnitManager allianceUnitManager) {
	Unit targetBarracks = null;

	int minQueueSize = Integer.MAX_VALUE;
	Set<Integer> barracksSet = allianceUnitManager.getUnitsByUnitKind(UnitKind.Terran_Barracks);
	// 마린 훈련이 가능한 배럭 중에서 TrainingQueue가 가장 적은 배럭을 선택
	// TrainingQueue는 최대 2개까지만 허용
	for (Integer barracksId : barracksSet) {
	    Unit barracks = allianceUnitManager.getUnit(barracksId);
	    if (barracks.canTrain(UnitType.Terran_Marine)) {
		if (barracks.getTrainingQueue().size() < 2) {
		    if (minQueueSize > barracks.getTrainingQueue().size()) {
			minQueueSize = barracks.getTrainingQueue().size();
			targetBarracks = barracks;
		    }
		}
	    }
	}

	return targetBarracks;
    }

    public int getTrainingQueueUnitCount(UnitManager allianceUnitManager, UnitType unitType) {
	int result = 0;

	Set<Integer> barracksSet = allianceUnitManager.getUnitsByUnitKind(UnitKind.Terran_Barracks);
	for (Integer barracksId : barracksSet) {
	    Unit barracks = allianceUnitManager.getUnit(barracksId);
	    List<UnitType> trainingQueue = barracks.getTrainingQueue();
	    for (UnitType trainingUnitType : trainingQueue) {
		if (unitType.equals(trainingUnitType)) {
		    result++;
		}
	    }
	}

	return result;

    }

    public void trainingMarine(GameData gameData, MagiBuildOrderItem buildItem) {
	UnitManager allianceUnitManager = gameData.getAllianceUnitManager();

	Unit targetBarracks = getTrainableBarracks(allianceUnitManager);
	// 마린 훈련하기
	if (null != targetBarracks && targetBarracks.canTrain(UnitType.Terran_Marine)) {
	    int beforeQueueSize = targetBarracks.getTrainingQueue().size();
	    targetBarracks.train(UnitType.Terran_Marine);
	    int afterQueueSize = targetBarracks.getTrainingQueue().size();
	    if (afterQueueSize > beforeQueueSize) {
		queue.poll();
		Log.debug("BuildOrder Finish: %s", buildItem.toString());
	    }
	}
    }

    public boolean isInitialBuildFinished() {
	return initialBuildFinished;
    }

    public boolean isBuildingSupply() {
	boolean result = false;

	if (supplyBuildingCount > 0) {
	    result = true;
	}

	return result;
    }

};