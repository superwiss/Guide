import java.util.HashSet;
import java.util.Set;

import bwapi.Order;
import bwapi.TilePosition;
import bwapi.Unit;

/// 일꾼 유닛들의 상태를 관리하고 컨트롤하는 class
public class MagiWorkerManager extends Manager {
    private static MagiWorkerManager instance = new MagiWorkerManager();

    /// static singleton 객체를 리턴합니다
    public static MagiWorkerManager Instance() {
	return instance;
    }

    // unitIdSet을 저장할 캐시 자료구조. single thread에서 동작하니까, 이렇게 과감하게 동기화를 무시한 cache를 사용한다.
    private Set<Integer> unitIdSetCache = new HashSet<>();

    @Override
    public void onFrame() {
	super.onFrame();

	// 최초 일꾼을 미네랄에 골고루 분배시킨다.
	if (1 < gameStatus.getFrameCount()) {
	    idleWorkerCheck();
	}
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
	    // 대상 일꾼을 저장할 unitIdSet 초기화
	    unitIdSetCache.clear();
	    for (Integer workerId : allianceUnitManager.getUnitIdSetByUnitKind(UnitKind.Worker)) {
		if (isinterruptableWorker(workerId)) {
		    unitIdSetCache.add(workerId);
		}
	    }

	    // unitIdSet 중에서 tilePosition과 가장 가까운 유닛을 리턴한다.
	    result = allianceUnitManager.getClosestUnit(unitIdSetCache, tilePosition.toPosition());
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
	Set<Integer> workerList = allianceUnitManager.getUnitIdSetByUnitKind(UnitKind.Worker);
	for (Integer unitId : workerList) {
	    // 일꾼 하나를 가져온다.
	    Unit worker = allianceUnitManager.getUnit(unitId);
	    if (null != worker && worker.isCompleted() && worker.isIdle()) {
		Log.info("Found idle worker: %d", worker.getID());
		// 일꾼에서 가장 가까운 커맨드 센터를 가져온다.
		Unit commandCenter = allianceUnitManager.getCloseCommandCenter(worker);
		if (null != commandCenter) {
		    // 적절한 미네랄을 채취한다.
		    mining(worker, commandCenter);
		}
	    }
	}
    }

    // worker에게 commandCeter 주변의 미네랄을 채굴하도록 명령한다.
    public void mining(Unit worker, Unit commandCenter) {
	// 파라메터 체크
	if (null == worker || null == commandCenter) {
	    Log.warn("mining(): Invalid parameters. worker=%s, commandCenter=%s", UnitUtil.toString(worker), UnitUtil.toString(commandCenter));
	    return;
	}

	// 일꾼이 캘 미네랄 후보들을 mineralIdSet에 저장해 놓는다.
	Set<Integer> mineralIdSet = allianceUnitManager.getMineralIdSetAssignedByCommandCenter(commandCenter);

	// 다른 일꾼이 할당되지 않은 미네랄 중에서 일꾼과 가장 가까운 미네랄. (가급적 notAssignedMineral부터 캔다)
	Unit notAssignedMineral = null;
	int notAssignedMinDistance = Integer.MAX_VALUE;

	// 다른 일꾼이 할당된 미네랄 중에서 일꾼과 가장 가까운 미네랄. (notAssignedMineral이 없으면 assignedMineral이라도 캔다)
	Unit assignedMineral = null;
	int assignedDistance = Integer.MAX_VALUE;

	// 일꾼과 미네랄 사이의 거리를 계산해서 가장 가까운 assignedMineral과 가장 가까운 notAssignedMineral을 구한다.
	for (Integer mineralId : mineralIdSet) {
	    Unit mineral = allianceUnitManager.getUnit(mineralId);

	    int distance = allianceUnitManager.getUnit(mineralId).getDistance(worker);
	    if (distance < assignedDistance) {
		assignedDistance = distance;
		assignedMineral = mineral;
	    }

	    if (true == allianceUnitManager.isAssignedWorkerToMiniral(mineralId)) {
		continue;
	    }
	    if (distance < notAssignedMinDistance) {
		notAssignedMinDistance = distance;
		notAssignedMineral = mineral;
	    }
	}

	if (null == notAssignedMineral && null != assignedMineral) {
	    // 모든 미네랄에 일꾼이 최소 1기씩 할당되어 있는 경우... 가장 가까운 미네랄을 캔다.
	    if (true == assignedMineral.isVisible()) {
		Log.debug("일꾼(%d)이 (남들이 캐고 있는) 미네랄(%d)을 채굴한다.", worker.getID(), assignedMineral.getID());
		allianceUnitManager.assignWorkerToMiniral(assignedMineral.getID());
		worker.gather(assignedMineral);
	    } else {
		Log.error("일꾼(%d)이 미네랄(%d)을 채굴할려고 했으나, 미네랄이 보이지 않는 이상한 상황... 확인 필요함.", worker.getID(), assignedMineral.getID());
	    }
	} else if (null != notAssignedMineral) {
	    // 남들이 아직 캐지 않고 있는 새로운 미네랄이 존재할 경우, 거리가 살짝 멀더라도 이것 부터 캔다.
	    if (true == notAssignedMineral.isVisible()) {
		Log.debug("일꾼(%d)이 (남들이 캐지 않은 새로운) 미네랄(%d)을 채굴한다.", worker.getID(), notAssignedMineral.getID());
		allianceUnitManager.assignWorkerToMiniral(notAssignedMineral.getID());
		worker.gather(notAssignedMineral);
	    } else {
		Log.error("일꾼(%d)이 새로운 미네랄(%d)을 채굴할려고 했으나, 미네랄이 보이지 않는 이상한 상황... 확인 필요함.", worker.getID(), assignedMineral.getID());
	    }
	}
    }

}