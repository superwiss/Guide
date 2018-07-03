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
	    Log.debug("BuildOrder Start: %s", buildItem.toString());
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

    private void process(MagiBuildOrderItem buildItem, GameData gameData) {
	UnitManager allianceUnitManager = gameData.getAllianceUnitManager();
	MagiBuildOrderItem.Order type = buildItem.getOrder();
	switch (type) {
	case INITIAL_BUILDORDER_FINISH:
	    initialBuildFinished = true;
	    Log.info("Initial build order has finished.");
	    queue.poll();
	    break;
	case SCOUT:
	    Unit workerForScout = allianceUnitManager.getBuildableWorker();
	    allianceUnitManager.setScoutUnit(workerForScout);
	    Log.info("Scout start: ");
	    queue.poll();
	    break;
	case TRAINING_WORKER:
	    trainingWorker(gameData, buildItem);
	    break;
	case TRAINING_MARINE:
	    trainingMarine(gameData, buildItem);
	    break;
	case BUILD:
	    if (false == buildItem.isInProgress()) {
		Unit worker = allianceUnitManager.getBuildableWorker();
		if (null != worker) {
		    Log.info("Build worker id: %d", worker.getID());
		    UnitType targetType = buildItem.getTargetUnitType();
		    List<TilePosition> tilePositionList = null;
		    if (UnitType.Terran_Barracks.equals(targetType)) {
			tilePositionList = LocationManager.Instance().getBarracks();
		    } else if (UnitType.Terran_Refinery.equals(targetType)) {
			tilePositionList = LocationManager.Instance().getRefinery();
		    } else if (UnitType.Terran_Supply_Depot.equals(targetType)) {
			tilePositionList = LocationManager.Instance().getSupplyDepot();
		    } else if (UnitType.Terran_Academy.equals(targetType)) {
			tilePositionList = LocationManager.Instance().getSupplyDepot();
		    } else if (UnitType.Terran_Bunker.equals(targetType)) {
			tilePositionList = LocationManager.Instance().getBunker();
		    }
		    if (null != tilePositionList) {
			for (TilePosition tilePosition : tilePositionList) {
			    boolean canBuild = worker.canBuild(targetType, tilePosition);
			    if (true == canBuild) {
				worker.build(targetType, tilePosition);
				buildItem.setInProgress(true);
				MagiWorkerManager.Instance().assignBuildWorker(worker, buildItem);
				break;
			    }
			}
		    }
		} else {
		    buildItem.setInProgress(false);
		}
	    } else {
		Log.warn("Valid worker does not exist");
	    }
	    break;
	default:
	    break;
	}
    }

    // 일꾼을 가장 먼저 만들어진 커맨드 센터에서 훈련한다.
    private void trainingWorker(GameData gameData, MagiBuildOrderItem buildItem) {
	UnitManager allianceUnitManager = gameData.getAllianceUnitManager();
	Set<Integer> commandCenters = allianceUnitManager.getUnitsByUnitKind(UnitKind.COMMAND_CENTER);
	if (commandCenters.size() == 0) {
	    Log.warn("trainingWorker() failed. Command Center does not exist.");
	} else {
	    int firstCommandCenterId = commandCenters.iterator().next();
	    Unit firstCommandCenter = allianceUnitManager.getUnit(firstCommandCenterId);
	    int oldQueueSize = firstCommandCenter.getTrainingQueue().size();
	    firstCommandCenter.build(UnitType.Terran_SCV);
	    int newQueueSie = firstCommandCenter.getTrainingQueue().size();
	    if (newQueueSie > oldQueueSize) {
		queue.poll();
		Log.debug("BuildOrder Finish: %s", buildItem.toString());
	    }
	}
    }

    public Unit getTrainableBarracks(UnitManager allianceUnitManager) {
	Unit targetBarracks = null;

	int minQueueSize = Integer.MAX_VALUE;
	Set<Integer> barracksSet = allianceUnitManager.getUnitsByUnitKind(UnitKind.BARRACKS);
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