import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bwapi.Order;
import bwapi.TilePosition;
import bwapi.UnitType;

/// 빌드(건물 건설 / 유닛 훈련 / 테크 리서치 / 업그레이드) 명령을 순차적으로 실행하기 위해 빌드 큐를 관리하고, 빌드 큐에 있는 명령을 하나씩 실행하는 class<br>
/// 빌드 명령 중 건물 건설 명령은 ConstructionManager로 전달합니다
/// @see ConstructionManager
public class BuildManager extends Manager {
    private Deque<BuildOrderItem> queue = new LinkedList<>(); // 현재 빌드 오더 정보가 들어 있는 큐.
    private boolean initialBuildFinished = false; // 초기 빌드 오더가 완료되었는지 여부를 리턴.
    private Map<Integer, Integer> buildingWorkerMap = new HashMap<>(); // 건설 중인 건물과, 이 건물을 짓고 있는 일꾼을 매핑하고 있는 맵
    private boolean isMoving = false;

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);
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
	    if (buildItem.getOrder().equals(BuildOrderItem.Order.BUILD) && unit.getType().equals(buildItem.getTargetUnitType())) {
		buildingWorkerMap.put(unit.getID(), buildItem.getWorker().getID());
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

	Integer unitId = unit.getID();
	if (buildingWorkerMap.containsKey(unitId)) {
	    Integer workerId = buildingWorkerMap.get(unitId);
	    buildingWorkerMap.remove(unitId);
	    allianceUnitInfo.addUnitKind(UnitKind.Worker, allianceUnitInfo.getUnit(workerId));

	}
    }

    public void add(BuildOrderItem buildItem) {
	queue.offer(buildItem);
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
	case TRAINING:
	    UnitType targetUnitType = buildOrderItem.getTargetUnitType();

	    if (true == allianceUnitInfo.trainingUnit(targetUnitType)) {
		queue.poll();
		Log.debug("BuildOrder Finish: %s", buildOrderItem.toString());
	    }
	    break;
	case ADD_ON:
	    allianceUnitInfo.buildAddon(UnitType.Terran_Comsat_Station);
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
		} else if (UnitType.Terran_Supply_Depot.equals(buildingType)) {
		    tilePositionList = locationManager.get3by2SizeBuildings();
		} else if (UnitType.Terran_Academy.equals(buildingType)) {
		    tilePositionList = locationManager.get3by2SizeBuildings();
		} else if (UnitType.Terran_Bunker.equals(buildingType)) {
		    tilePositionList = locationManager.getBaseEntranceBunker();
		}

		if (null != tilePositionList) {
		    for (TilePosition tilePosition : tilePositionList) {
			/*
			if (!gameStatus.isExplored(tilePosition)) {
			    if (false == isMoving) {
				Log.debug("current mineral: %d", gameStatus.getMineral());
				BuildOrderItem moveOrder = new BuildOrderItem(BuildOrderItem.Order.MOVE_SCV, tilePosition);
				queue.addFirst(moveOrder);
			    }
			    break;
			}
			*/
			// 건설 가능한 일꾼을 가져온다.
			Unit2 worker = workerManager.getInterruptableWorker(tilePosition);
			if (null != worker) {
			    // 일꾼이 건물을 지을 수 있으면
			    boolean canBuild = worker.canBuild(buildingType, tilePosition);
			    if (true == canBuild) {
				worker.build(buildingType, tilePosition);
				buildOrderItem.setInProgress(true);
				buildOrderItem.setWorker(worker);
				allianceUnitInfo.removeUnitKind(UnitKind.Worker, worker);
				Log.info("빌드 오더를 실행합니다: %s", buildOrderItem);
				isMoving = false;
				break;
			    }
			    if (!gameStatus.isExplored(tilePosition)) {
				if (false == isMoving) {
				    Log.debug("current mineral: %d", gameStatus.getMineral());
				    BuildOrderItem moveOrder = new BuildOrderItem(BuildOrderItem.Order.MOVE_SCV, tilePosition);
				    queue.addFirst(moveOrder);
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

    // 초기 빌드 오더가 완료되었는지 여부를 리턴한다.
    public boolean isInitialBuildFinished() {
	return initialBuildFinished;
    }
};