
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bwapi.Unit;

public class UnitManager {

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

    // 공격 가능한 타입의 유닛을 리턴한다.
    public Set<Integer> getAttackableUnitList() {
	return unitFilterMap.get(UnitKind.ATTACKABLE_NORMAL);
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

    @Override
    public String toString() {
	String result = "";

	for (Unit unit : unitList) {
	    result += "(" + UnitUtil.toString(unit) + ") ";
	}

	return result;
    }
}