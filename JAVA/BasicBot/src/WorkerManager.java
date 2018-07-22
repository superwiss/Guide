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
    @Override
    public void onFrame() {
	super.onFrame();

	// 최초 일꾼을 미네랄에 골고루 분배시킨다.
	if (1 < gameStatus.getFrameCount()) {
	    idleWorkerCheck();
	}

	if (gameStatus.isMatchedInterval(3)) {
	    checkRebuild();
	}

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

    // 일꾼이 새로운 임무(정찰, 건설, 수리 등)를 수행할 수 있는지 여부를 리턴한다.
    private boolean isinterruptableWorker(Unit2 worker) {
	boolean result = false;

	if (null != worker) {
	    if (true == allianceUnitInfo.isKindOf(worker, UnitKind.Worker_Gather_Gas)) {
		// 가스 캐는 일꾼은 건드리지 말자.
		result = false;
	    } else {
		// 커맨드센터에서 훈련중인 SCV가 아니고, 건설중이 아니고, idle 상태의 일꾼이거나 미네랄을 캐는 일꾼이면 OK
		result = worker.isCompleted() && !worker.isConstructing()
			&& (worker.isIdle() || worker.isGatheringMinerals() || worker.isMoving() || worker.getOrder().equals(Order.MoveToMinerals));
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
		mineralSet.remove(Integer.valueOf(largestAmountMineral.getID()));
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

    private void loggingDetailSCVInfo() {
	Set<Unit2> scvSet = allianceUnitInfo.getUnitSet(UnitType.Terran_SCV);
	Log.trace("SCV size: %d", scvSet.size());
	for (Unit2 scv : scvSet) {
	    UnitUtil.loggingDetailUnitInfo(scv);
	}
    }
}