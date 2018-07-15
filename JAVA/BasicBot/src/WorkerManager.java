import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bwapi.Order;
import bwapi.TilePosition;
import bwapi.Unit;
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

	//loggingDetailSCVInfo();
    }

    @Override
    protected void onUnitComplete(Unit unit) {
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
    public Unit getInterruptableWorker(TilePosition tilePosition) {
	Unit result = null;

	if (null != tilePosition) {
	    Set<Integer> candidate = new HashSet<>();
	    for (Integer workerId : allianceUnitManager.getUnitIdSetByUnitKind(UnitKind.Worker)) {
		if (isinterruptableWorker(workerId)) {
		    candidate.add(workerId);
		}
	    }

	    // unitIdSet 중에서 tilePosition과 가장 가까운 유닛을 리턴한다.
	    result = allianceUnitManager.getClosestUnit(candidate, tilePosition.toPosition());
	}

	return result;
    }

    // 일꾼이 새로운 임무(정찰, 건설, 수리 등)를 수행할 수 있는지 여부를 리턴한다.
    private boolean isinterruptableWorker(Integer workerId) {
	boolean result = false;

	if (null != workerId) {
	    Unit worker = allianceUnitManager.getUnit(workerId);
	    if (null != worker) {
		if (true == allianceUnitManager.getUnitIdSetByUnitKind(UnitKind.Worker_Gather_Gas).contains(Integer.valueOf(workerId))) {
		    // 가스 캐는 일꾼은 건드리지 말자.
		    result = false;
		} else {
		    // 커맨드센터에서 훈련중인 SCV가 아니고, 건설중이 아니고, idle 상태의 일꾼이거나 미네랄을 캐는 일꾼이면 OK
		    result = worker.isCompleted() && !worker.isConstructing()
			    && (worker.isIdle() || worker.isGatheringMinerals() || worker.isMoving() || worker.getOrder().equals(Order.MoveToMinerals));
		}
	    }
	}

	return result;
    }

    // idle 상태의 일꾼을 찾아서 미네랄을 캐도록 일을 시킨다.
    private void idleWorkerCheck() {
	Set<Integer> workerIdSet = allianceUnitManager.getUnitIdSetByUnitKind(UnitType.Terran_SCV); // 전체 일꾼 목록
	Set<Integer> commandCenterIdSet = allianceUnitManager.getUnitIdSetByUnitKind(UnitType.Terran_Command_Center); // 전체 SCV 목록

	// Key: Command Center ID, Value: Command Center로 자원을 캐러 갈 일꾼 ID 목록
	Map<Integer, List<Integer>> commandCenterWorksListMap = new HashMap<>();

	for (Integer workerId : workerIdSet) {
	    Unit worker = allianceUnitManager.getUnit(workerId);
	    // 놀고 있는 일꾼을 대상으로 미네랄을 캔다.
	    if (null != worker && worker.isCompleted() && worker.isIdle()) {
		Log.info("Found idle worker: %d", worker.getID());
		// 일꾼에서 가장 가까운 커맨드 센터를 가져온다.
		Unit commandCenter = allianceUnitManager.getClosestUnit(commandCenterIdSet, worker.getPosition());
		if (null != commandCenter) {
		    if (!commandCenterWorksListMap.containsKey(commandCenter.getID())) {
			commandCenterWorksListMap.put(commandCenter.getID(), new LinkedList<>());
		    }
		    List<Integer> workerIdList = commandCenterWorksListMap.get(commandCenter.getID());
		    workerIdList.add(workerId);
		    commandCenterWorksListMap.put(commandCenter.getID(), workerIdList);
		} else {
		    Log.warn("커맨드 센터가 없습니다. 일꾼 ID: %d", workerId);
		}
	    }
	}
	for (Integer commandCenterId : commandCenterWorksListMap.keySet()) {
	    Unit commandCenter = allianceUnitManager.getUnit(commandCenterId);
	    mining(commandCenter, commandCenterWorksListMap.get(commandCenterId));
	}
    }

    // 가장 양이 많은 미네랄, 혹은 가장 가까운 미네랄을 캔다. 
    private void mining(Unit commandCenter, List<Integer> workerIdList) {
	//Set<Integer> mineralIdSet = allianceUnitManager.getUnitIdSetByUnitKind(UnitKind.Resource_Mineral_Field);
	Set<Integer> mineralIdSet = new HashSet<>(allianceUnitManager.getUnitIdSetByUnitKind(UnitKind.Resource_Mineral_Field));
	for (Integer workerId : workerIdList) {
	    Unit worker = allianceUnitManager.getUnit(workerId);
	    Unit largestAmountMineral = getLargestAmountMineral(mineralIdSet, commandCenter, 300); // 커맨드 센터와 100 거리 이내의 미네랄 중, 가장 양이 많은 미네랄
	    //Unit closestMineral = allianceUnitManager.getClosestUnit(mineralIdSet, worker.getPosition());
	    if (null != largestAmountMineral) {
		worker.gather(largestAmountMineral);
		mineralIdSet.remove(Integer.valueOf(largestAmountMineral.getID()));
	    }
	}
    }

    // 가장 양이 많은 미네랄을 선택한다. 단, unit으로부터 maxDistance 이내에 있는 미네랄을 대상으로 선택한다.
    private Unit getLargestAmountMineral(Set<Integer> mineralIdSet, Unit unit, int maxDistance) {
	Unit result = null;

	int largest = Integer.MIN_VALUE;
	for (Integer mineralId : mineralIdSet) {
	    Unit mineral = allianceUnitManager.getUnit(mineralId);
	    if (maxDistance > unit.getDistance(mineral) && mineral.isVisible()) {
		if (largest < mineral.getResources()) {
		    largest = mineral.getResources();
		    result = mineral;
		}
	    }
	}

	return result;
    }

    private void loggingDetailSCVInfo() {
	Set<Integer> scvIdSet = allianceUnitManager.getUnitIdSetByUnitKind(UnitType.Terran_SCV);
	Log.trace("SCV size: %d", scvIdSet.size());
	for (Integer scvId : scvIdSet) {
	    Unit scv = allianceUnitManager.getUnit(scvId);
	    UnitUtil.loggingDetailUnitInfo(scv);
	}
    }
}