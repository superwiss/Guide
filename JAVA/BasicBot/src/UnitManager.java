
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bwapi.Game;
import bwapi.Unit;

public class UnitManager {

    private Game game = MyBotModule.Broodwar;

    // 모든 유닛 목록
    private List<Unit> unitList = new ArrayList<>();

    // Unit ID - Unit 매핑
    private Map<Integer, Unit> idUnitMap = new HashMap<>();

    // 유닛의 종류
    private Map<UnitKind, Set<Integer>> unitFilterMap = new HashMap<>();

    // 유닛의 마지막 명령
    private Map<Integer, ActionDetail> lastActionMap = new HashMap<>();

    // 유닛의 마지막 상태
    private Map<Integer, UnitStatus> lastStatusMap = new HashMap<>();

    // Key: Command Center의 ID, Value: Key 주변의 미네랄 목록
    private Map<Integer, Set<Integer>> mineralMap = new HashMap<>();

    // Key: 미네랄, Value: 미네랄에 일꾼이 할당되었는지 여부
    private Map<Integer, Boolean> assignedMineralMap = new HashMap<>();

    // 생성자
    public UnitManager() {
	// unitFilter를 초기화 한다.
	for (UnitKind unitFilter : UnitKind.values()) {
	    Set<Integer> set = new HashSet<>();
	    unitFilterMap.put(unitFilter, set);
	}
    }

    // 유닛을 추가한다.
    public void add(Unit unit) {
	addOrRemove(unit, true);
    }

    // 유닛을 삭제한다.
    public void remove(Unit unit) {
	addOrRemove(unit, false);
    }

    // Unit ID에 해당하는 유닛 객체를 리턴한다.
    public Unit getUnit(int id) {
	return idUnitMap.get(id);
    }

    public Set<Integer> getUnitsByUnitKind(UnitKind unitKind) {
	return unitFilterMap.get(unitKind);
    }

    public Integer getFirstUnitByUnitKind(UnitKind unitKind) {
	Integer result = null;

	if (0 != unitFilterMap.get(unitKind).size()) {
	    result = unitFilterMap.get(unitKind).iterator().next();
	}

	return result;
    }

    // 현재 존재하는 커맨드센터 중 하나를 리턴한다.
    public Unit getFirstCommandCenter() {
	Unit result = null;

	Set<Integer> commandCenters = unitFilterMap.get(UnitKind.COMMAND_CENTER);
	if (commandCenters.size() > 0) {
	    result = getUnit(commandCenters.iterator().next());
	}

	return result;
    }

    // 유닛의 마지막 Action 정보를 리틴한다.
    public ActionDetail getLastAction(Unit unit) {
	return lastActionMap.get(unit.getID());
    }

    // 유닛의 Action 정보를 Update 한다.
    public void updateLastAction(Unit unit, ActionDetail lastAction) {
	lastActionMap.put(unit.getID(), lastAction);
    }

    // 유닛의 마지막 Status 정보를 리틴한다.
    public UnitStatus getLastStatus(Unit unit) {
	return lastStatusMap.get(unit.getID());
    }

    // 유닛의 Status 정보를 Update 한다.
    public void updateLastStatus(Unit unit, UnitStatus status) {
	lastStatusMap.put(unit.getID(), status);
    }

    private void addOrRemove(Unit unit, boolean isAddMode) {
	Integer id = unit.getID();
	Set<UnitKind> unitKinds = UnitUtil.getUnitKinds(unit);

	if (true == isAddMode) {
	    idUnitMap.put(id, unit);

	    for (UnitKind unitKind : unitKinds) {
		unitFilterMap.get(unitKind).add(id);
	    }

	    // 유닛의 초기 상태는 Idle이다.
	    lastStatusMap.put(id, UnitStatus.IDLE);

	    unitList.add(unit);
	} else {
	    idUnitMap.remove(id);

	    for (UnitKind unitKind : unitKinds) {
		unitFilterMap.get(unitKind).remove(id);
	    }

	    lastActionMap.remove(id);
	    lastStatusMap.remove(id);

	    unitList.remove(unit);
	}
    }

    // command center 주변의 미네랄 정보를 업데이트 한다.
    public void initMimeralInfo() {
	for (Integer commandCneter : unitFilterMap.get(UnitKind.COMMAND_CENTER)) {
	    insertMineralMap(commandCneter);
	}
    }

    // id(command center) 주위의 미네랄 세팅
    public void insertMineralMap(int commandCenterId) {
	Set<Integer> value = new HashSet<>();
	for (Unit mineral : game.getMinerals()) {
	    if (getUnit(commandCenterId).getDistance(mineral) < 500) {
		value.add(mineral.getID());
		assignedMineralMap.put(mineral.getID(), false);
	    }
	}
	mineralMap.put(commandCenterId, value);
    }

    // TODO 커맨드센터 파괴될 때 처리해주기.
    public void removeMineralMap(Integer CommandCenterId) {
	mineralMap.remove(CommandCenterId);
    }

    @Override
    public String toString() {
	String result = "";

	for (Unit unit : unitList) {
	    result += "(" + UnitUtil.toString(unit) + ") ";
	}

	return result;
    }

    public Unit getCloseCommandCenter(Unit worker) {
	Unit result = null;

	int minDistance = Integer.MAX_VALUE;
	for (Integer commandCenter : unitFilterMap.get(UnitKind.COMMAND_CENTER)) {
	    int distance = worker.getDistance(getUnit(commandCenter));
	    if (distance < minDistance) {
		minDistance = distance;
		result = getUnit(commandCenter);
	    }
	}

	return result;
    }

    public void mining(Unit worker, Unit commandCenter) {
	// 커맨드 센터 주변의 미네랄 목록을 가져온다.
	Set<Integer> minerals = mineralMap.get(commandCenter.getID());
	int assignedDistance = Integer.MAX_VALUE;
	int notAssignedMinDistance = Integer.MAX_VALUE;
	Unit notAssignedMineral = null;
	Unit assignedMineral = null;
	for (Integer mineral : minerals) {
	    int distance = getUnit(mineral).getDistance(worker);
	    if (distance < assignedDistance) {
		assignedDistance = distance;
		assignedMineral = getUnit(mineral);
	    }

	    if (true == assignedMineralMap.get(mineral)) {
		continue;
	    }
	    if (distance < notAssignedMinDistance) {
		notAssignedMinDistance = distance;
		notAssignedMineral = getUnit(mineral);
	    }
	}

	if (null == notAssignedMineral) {
	    worker.gather(assignedMineral);
	    Log.debug("worker(%d) mining assigned mineral(%d)", worker.getID(), assignedMineral.getID());
	} else {
	    worker.gather(notAssignedMineral);
	    assignedMineralMap.put(notAssignedMineral.getID(), true);
	    Log.debug("worker(%d) mining new mineral(%d)", worker.getID(), notAssignedMineral.getID());
	}
    }

    public Unit getBuildableWorker() {
	Unit result = null;
	for (Integer workerId : unitFilterMap.get(UnitKind.WORKER)) {
	    Unit worker = getUnit(workerId);
	    if (isinterruptableWorker(worker)) {
		result = worker;
		break;
	    }
	}

	return result;
    }

    public boolean isinterruptableWorker(Unit worker) {
	return worker.isCompleted() && !worker.isConstructing() && !worker.isBeingConstructed() && (worker.isIdle() || worker.isGatheringMinerals());
    }

    public void setScoutUnit(Unit unit) {
	// 유닛을 관리 대상에서 삭제하고 SCOUT 타입으로 변경한다.
	Set<UnitKind> unitKinds = UnitUtil.getUnitKinds(unit);
	Integer id = unit.getID();
	for (UnitKind unitKind : unitKinds) {
	    unitFilterMap.get(unitKind).remove(id);
	}
	unitFilterMap.get(UnitKind.SCOUT).add(id);
    }

    public void releaseScoutUnit(Unit unit) {
	// 유닛을 SCOUT 타입에서 원래 타입으로 원복한다.
	Set<UnitKind> unitKinds = UnitUtil.getUnitKinds(unit);
	Integer id = unit.getID();
	for (UnitKind unitKind : unitKinds) {
	    unitFilterMap.get(unitKind).add(id);
	}
	unitFilterMap.get(UnitKind.SCOUT).remove(id);
    }
}