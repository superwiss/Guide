
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bwapi.Unit;

public class UnitManager {
    private List<Unit> unitList = new LinkedList<>();
    private Map<Integer, Unit> unitIdMap = new HashMap<>();
    private Map<Integer, UnitStatus> unitCurrentStatusMap = new HashMap<>();
    private Map<Integer, UnitStatus> unitBeforeStatusMap = new HashMap<>();
    private Map<Unit, Unit> enemyUnitTargetmap = new HashMap<>();

    public boolean add(Unit unit) {
	unitIdMap.put(unit.getID(), unit);
	unitCurrentStatusMap.put(unit.getID(), UnitStatus.IDLE);
	unitBeforeStatusMap.put(unit.getID(), UnitStatus.IDLE);
	return getUnitList().add(unit);
    }

    public boolean remove(Unit unit) {
	unitIdMap.remove(unit.getID());
	unitCurrentStatusMap.remove(unit.getID());
	unitBeforeStatusMap.remove(unit.getID());
	return getUnitList().remove(unit);
    }

    public Unit getUnitById(int id) {
	return unitIdMap.get(id);
    }

    public UnitStatus getUnitCurrentStatus(Unit unit) {
	return unitCurrentStatusMap.get(unit.getID());
    }

    public UnitStatus getUnitBeforeStatus(Unit unit) {
	return unitBeforeStatusMap.get(unit.getID());
    }

    public void setUnitStatus(Unit unit, UnitStatus unitStatus) {
	unitBeforeStatusMap.put(unit.getID(), getUnitCurrentStatus(unit));
	unitCurrentStatusMap.put(unit.getID(), unitStatus);
    }

    public Unit getEnemy(Unit myUnit) {
	return enemyUnitTargetmap.get(myUnit);
    }

    public Unit setEnemy(Unit myUnit, Unit enemyUnit) {
	return enemyUnitTargetmap.put(myUnit, enemyUnit);
    }

    // /////////////////////////
    // Getter & Setter
    // /////////////////////////

    public List<Unit> getUnitList() {
	return unitList;
    }

    public void setUnitList(List<Unit> unitList) {
	this.unitList = unitList;
    }

    @Override
    public String toString() {
	String result = "";

	for (Unit unit : getUnitList()) {
	    result += "(" + UnitUtil.getUnitAsString(unit) + ") ";
	}

	return result;
    }
}