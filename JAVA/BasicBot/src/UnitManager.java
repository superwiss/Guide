
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bwapi.Unit;

public class UnitManager {

    // 모든 유닛 목록
    private List<Unit> unitList = new ArrayList<>();

    // Unit ID - Unit 매핑
    private Map<Integer, Unit> idUnitMap = new HashMap<>();

    // Unit ID - Unit 현재 Status 매핑
    private Map<Integer, UnitStatus> idCurrentStatusMap = new HashMap<>();

    // Unit ID - Unit 과거 Status 매핑
    private Map<Integer, UnitStatus> idBeforeStatusMap = new HashMap<>();

    // Unit Kind : Attackable - Unit ID 매핑
    private List<Integer> attackableUnitList = new ArrayList<>();

    // Unit Kind : Building - Unit ID 매핑
    private List<Integer> buildingUnitList = new ArrayList<>();

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

    // 유닛의 현재 상태를 리턴한다.
    public UnitStatus getUnitCurrentStatus(Unit unit) {
	return idCurrentStatusMap.get(unit.getID());
    }

    // 유닛의 이전 상태를 리턴한다.
    public UnitStatus getUnitBeforeStatus(Unit unit) {
	return idBeforeStatusMap.get(unit.getID());
    }

    // 유닛의 현재 상태를 업데이트 한다.
    public void setUnitStatus(Unit unit, UnitStatus unitStatus) {
	int id = unit.getID();
	idBeforeStatusMap.put(id, getUnitCurrentStatus(unit));
	idCurrentStatusMap.put(id, unitStatus);
    }

    // 공격 가능한 타입의 유닛을 리턴한다.
    public List<Integer> getAttackableUnitList() {
	return attackableUnitList;
    }

    private void addOrRemove(Unit unit, boolean isAddMode) {
	Integer id = unit.getID();
	boolean isAttackableTypeUnit = UnitUtil.isAttackableTypeUnit(unit);
	boolean isBuildingTypeUnit = UnitUtil.isBuildingTypeUnit(unit);

	if (true == isAddMode) {
	    idUnitMap.put(id, unit);
	    idCurrentStatusMap.put(id, UnitStatus.IDLE);
	    idBeforeStatusMap.put(id, UnitStatus.IDLE);

	    if (isAttackableTypeUnit) {
		attackableUnitList.add(id);
	    }
	    if (isBuildingTypeUnit) {
		buildingUnitList.add(id);
	    }

	    unitList.add(unit);
	} else {
	    idUnitMap.remove(id);
	    idCurrentStatusMap.remove(id);
	    idBeforeStatusMap.remove(id);

	    if (isAttackableTypeUnit) {
		attackableUnitList.remove(id);
	    }
	    if (isBuildingTypeUnit) {
		buildingUnitList.remove(id);
	    }

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