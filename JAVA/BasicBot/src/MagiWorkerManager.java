import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import bwapi.Unit;
import bwapi.UnitType;

/// 일꾼 유닛들의 상태를 관리하고 컨트롤하는 class
public class MagiWorkerManager {
    private static MagiWorkerManager instance = new MagiWorkerManager();

    /// static singleton 객체를 리턴합니다
    public static MagiWorkerManager Instance() {
	return instance;
    }

    private Map<Integer, MagiBuildOrderItem> buildWorker = new HashMap<>();
    private Map<Integer, Integer> buildWorkerFailCount = new HashMap<>();

    public void onFrame(GameData gameData) {
	if (1 == gameData.getFrameCount()) {
	    gameData.getAllianceUnitManager().initMimeralInfo();
	    // 일꾼분배
	    assignWorkersToMineral(gameData.getAllianceUnitManager());
	} else if (1 < gameData.getFrameCount()) {
	    assignWorkersToMineral(gameData.getAllianceUnitManager());
	}

	Log.debug("Build Worker size: %d", buildWorker.keySet().size());
	Set<Unit> releaseWorker = new HashSet<>();
	for (Integer workerId : buildWorker.keySet()) {
	    Unit worker = gameData.getAllianceUnitManager().getUnit(workerId);
	    if (null == worker) {
		// TODO 어떤 경우에 이런 상황이 발생하는지 분석 필요
		Log.warn("분석 필요...");
		continue;
	    }
	    MagiBuildOrderItem buildItem = buildWorker.get(workerId);

	    // 건물을 짓기 시작했으면 건설 일꾼 맵에서 release 한다.
	    if (null != worker.getBuildUnit() && worker.getBuildUnit().getType().equals(buildItem.getTargetUnitType())) {
		Log.debug("Worker(%d) construct started: %s", worker.getID(), buildItem.getTargetUnitType());
		releaseWorker.add(worker);
		continue;
	    }

	    // 건물 짓는 동작이 3Frame 이상 방해 받았으면, 일꾼을 교체하고 다른 일꾼으로 건설을 재시도 한다.
	    if (false == worker.isConstructing()) {
		increaseBuildFailCount(worker);
		if (getBuildFailCount(worker) > 3) {
		    Log.warn("Worker(%d) cancled to construct(%s)", worker.getID(), buildItem.getTargetUnitType());
		    buildItem.setInProgress(false);
		    releaseWorker.add(worker);
		}
	    }
	}
	for (Unit unit : releaseWorker) {
	    releaseBuildWorker(unit);
	}
    }

    public void assignBuildWorker(Unit worker, MagiBuildOrderItem buildItem) {
	buildWorker.put(worker.getID(), buildItem);
	buildWorkerFailCount.put(worker.getID(), 0);
    }

    public void releaseBuildWorker(Unit worker) {
	buildWorker.remove(Integer.valueOf(worker.getID()));
	buildWorkerFailCount.remove(Integer.valueOf(worker.getID()));
    }

    public UnitType getBuildTypeByWorker(Unit worker) {
	return buildWorker.get(Integer.valueOf(worker.getID())).getTargetUnitType();
    }

    public int getBuildFailCount(Unit worker) {
	return buildWorkerFailCount.get(worker.getID());
    }

    public void increaseBuildFailCount(Unit worker) {
	buildWorkerFailCount.put(worker.getID(), buildWorkerFailCount.get(worker.getID()) + 1);
    }

    public Set<Integer> getBuildWorkers() {
	return buildWorker.keySet();
    }

    // idle 상태의 일꾼에게 미네랄을 캐도록 일을 시킨다.
    private void assignWorkersToMineral(UnitManager allianceUnitManager) {
	Set<Integer> workerList = allianceUnitManager.getUnitsIdByUnitKind(UnitKind.Worker);
	for (Integer unitId : workerList) {
	    // 일꾼 하나를 가져온다.
	    Unit worker = allianceUnitManager.getUnit(unitId);
	    if (worker.isCompleted() && worker.isIdle()) {
		Log.info("Found idle worker: %d", worker.getID());
		// 일꾼에서 가장 가까운 커맨드 센터를 가져온다.
		Unit commandCenter = allianceUnitManager.getCloseCommandCenter(worker);
		if (null != commandCenter) {
		    // 적절한 미네랄을 채취한다.
		    allianceUnitManager.mining(worker, commandCenter);
		}
	    }
	}
    }

    /// 일꾼 유닛들의 상태를 저장하는 workerData 객체를 업데이트하고, 일꾼 유닛들이 자원 채취 등 임무 수행을 하도록 합니다
    public void update() {
    }

    public void onUnitComplete(Unit unit, GameData gameData) {
	if (0 != gameData.getFrameCount()) {
	    if (unit.getType().isWorker() && unit.getPlayer() == MyBotModule.Broodwar.self()) {
		assignWorkersToMineral(gameData.getAllianceUnitManager());
	    }
	}
    }
}