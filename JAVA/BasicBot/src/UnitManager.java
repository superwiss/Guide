
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class UnitManager {

    public static enum Assignment {
	SCOUT, GATHER_GAS
    }

    private Game game = MyBotModule.Broodwar;

    // 모든 유닛 목록
    private List<Unit> unitList = new ArrayList<>();

    // Unit ID - Unit 매핑
    private Map<Integer, Unit> idUnitMap = new HashMap<>();

    // 유닛의 종류
    private Map<UnitKind, Set<Integer>> unitKindMap = new HashMap<>();

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
	for (UnitKind unitKind : UnitKind.values()) {
	    Set<Integer> set = new HashSet<>();
	    unitKindMap.put(unitKind, set);
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

    // UnitKind에 unit을 추가한다.
    public void addUnitKind(UnitKind unitKind, Unit unit) {
	if (null != unitKind && null != unit) {
	    unitKindMap.get(unitKind).add(unit.getID());
	} else {
	    Log.warn("putUnitKind(): invalid paramter. unitKind: %s, unit: %s", unitKind, unit);
	}
    }

    // UnitKind에 unit을 추가한다.
    public void removeUnitKind(UnitKind unitKind, Unit unit) {
	if (null != unitKind && null != unit) {
	    unitKindMap.get(unitKind).remove(Integer.valueOf(unit.getID()));
	} else {
	    Log.warn("removeUnitKind(): invalid paramter. unitKind: %s, unit: %s", unitKind, unit);
	}
    }

    // Unit ID에 해당하는 유닛 객체를 리턴한다.
    public Unit getUnit(int id) {
	return idUnitMap.get(id);
    }

    public Set<Integer> getUnitsIdByUnitKind(UnitKind unitKind) {
	return unitKindMap.get(unitKind);
    }

    // unitKind 유닛 중에서 아무거나 하나 가져온 뒤, 그 유닛의 ID를 리턴한다.
    public Integer getFirstUnitIdByUnitKind(UnitKind unitKind) {
	Integer result = null;

	if (0 != unitKindMap.get(unitKind).size()) {
	    result = unitKindMap.get(unitKind).iterator().next();
	}

	return result;
    }

    // unitKind 유닛 중에서 아무거나 하나 가져온 뒤, 그 유닛을 리턴한다.
    public Unit getFirstUnitByUnitKind(UnitKind unitKind) {
	Unit result = null;

	Integer unitId = getFirstUnitIdByUnitKind(unitKind);
	if (null != unitId) {
	    result = getUnit(unitId);
	}

	return result;
    }

    // unitKind 유닛 중에서 아무거나 하나 가져온 뒤, 그 유닛의 tilePosition을 리턴한다.
    public TilePosition getFirstUnitTilePositionByUnitKind(UnitKind unitKind) {
	TilePosition result = null;

	Unit unit = getFirstUnitByUnitKind(unitKind);
	if (null != unit) {
	    result = unit.getTilePosition();
	}

	return result;
    }

    // 현재 존재하는 커맨드센터 중 하나를 리턴한다.
    public Unit getFirstCommandCenter() {
	Unit result = null;

	Set<Integer> commandCenters = unitKindMap.get(UnitKind.Terran_Command_Center);
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
		unitKindMap.get(unitKind).add(id);
	    }

	    // 유닛의 초기 상태는 Idle이다.
	    lastStatusMap.put(id, UnitStatus.IDLE);

	    unitList.add(unit);
	} else {
	    idUnitMap.remove(id);

	    for (UnitKind unitKind : unitKinds) {
		unitKindMap.get(unitKind).remove(id);
	    }

	    lastActionMap.remove(id);
	    lastStatusMap.remove(id);

	    unitList.remove(unit);
	}
    }

    // command center 주변의 미네랄 정보를 업데이트 한다.
    public void initMimeralInfo() {
	for (Integer commandCneter : unitKindMap.get(UnitKind.Terran_Command_Center)) {
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
	for (Integer commandCenter : unitKindMap.get(UnitKind.Terran_Command_Center)) {
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

    // tilePosition에서 가장 가까운 건설 가능한 일꾼을 리턴한다.
    public Unit getBuildableWorker(TilePosition tilePosition) {
	Unit result = null;

	if (null != tilePosition) {
	    Set<Integer> candidate = new HashSet<>();
	    for (Integer workerId : unitKindMap.get(UnitKind.Terran_SCV)) {
		if (isinterruptableWorker(workerId)) {
		    candidate.add(workerId);
		}
	    }

	    result = getClosestUnit(candidate, tilePosition.toPosition());
	}

	return result;
    }

    public boolean isinterruptableWorker(Integer workerId) {
	boolean result = false;

	if (null != workerId) {
	    Unit worker = getUnit(workerId);
	    if (null != worker) {
		if (true == unitKindMap.get(UnitKind.Worker_Gather_Gas).contains(Integer.valueOf(workerId))) {
		    // 가스 캐는 일꾼은 건드리지 말자.
		    result = false;
		} else {
		    result = worker.isCompleted() && !worker.isConstructing() && (worker.isIdle() || worker.isGatheringMinerals());
		}
	    }
	}

	return result;
    }

    public void setScoutUnit(Unit unit) {
	// 유닛을 관리 대상에서 삭제하고 SCOUT 타입으로 변경한다.
	Set<UnitKind> unitKinds = UnitUtil.getUnitKinds(unit);
	Integer id = unit.getID();
	for (UnitKind unitKind : unitKinds) {
	    unitKindMap.get(unitKind).remove(id);
	}
	unitKindMap.get(UnitKind.Scouting_Unit).add(id);
    }

    public void releaseScoutUnit(Unit unit) {
	if (null != unit) {
	    // 유닛을 SCOUT 타입에서 원래 타입으로 원복한다.
	    Set<UnitKind> unitKinds = UnitUtil.getUnitKinds(unit);
	    Integer id = unit.getID();
	    for (UnitKind unitKind : unitKinds) {
		unitKindMap.get(unitKind).add(id);
	    }
	    unitKindMap.get(UnitKind.Scouting_Unit).remove(id);
	} else {
	    Log.trace("정찰 유닛이 죽어버렸음..");
	}
    }

    // unitSet 중에서 position에 제일 가까운 unit을 리턴한다.
    public Unit getClosestUnit(Set<Integer> unitSet, Position position) {
	return getClosestUnit(unitSet, position, null);
    }

    // unitSet 중에서 position에 제일 가까운 unit을 리턴한다. excludeUnitType은 계산에서 제외한다.
    public Unit getClosestUnit(Set<Integer> unitSet, Position position, Set<UnitType> excludeUnitType) {
	Unit result = null;

	if (null != unitSet && null != position) {
	    int minDistance = Integer.MAX_VALUE;
	    for (Integer unitId : unitSet) {
		Unit unit = getUnit(unitId);
		if (null != unit) {
		    if (null != excludeUnitType && excludeUnitType.contains(unit.getType())) {
			continue;
		    }
		    int distance = unit.getDistance(position);
		    if (distance < minDistance) {
			minDistance = distance;
			result = unit;
		    }
		} else {
		    Log.warn("getClosestUnit(): Failed to getting unit by unitId(%d)", unitId);
		}
	    }
	} else {
	    Log.warn("Invalid Parameter: unitset: %s, position: %s", unitSet, position);
	}

	return result;
    }
}