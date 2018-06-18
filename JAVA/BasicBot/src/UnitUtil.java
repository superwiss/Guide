import java.util.LinkedList;
import java.util.List;

import bwapi.Unit;
import bwapi.UnitType;

public class UnitUtil {

    private static UnitSpec marineData = new MarineSpec();

    // Unit의 정보를 출력한다.
    public static String getUnitAsString(Unit unit) {
	if (null != unit) {
	    return String.format("Unit[id=%d, type=%s, hp=%d, angle=%f, position=%s]", unit.getID(), unit.getType().toString(), unit.getHitPoints(), unit.getAngle(),
		    unit.getPosition().toString());
	}

	return "unit is null";
    }

    // 유닛의 타입을 판별해서 스펙을 리턴한다.
    public static UnitSpec getUnitSpec(Unit unit) {
	UnitType unitType = unit.getType();

	if (UnitType.Terran_Marine == unitType) {
	    return marineData;
	}

	Log.warn("Can not found CombatData because of undefined unit type: {}", unit.getType());

	return null;
    }

    // 파라메터로 전달 받은 내 유닛이 공격해야 할 가장 적당한 적 유닛을 선택한다.
    // 적당한 유닛이 없으면 null을 리턴한다.
    public static Unit selectEnemyTargetUnit(Unit myUnit, UnitManager enemyUnitManager) {
	List<Unit> combatDistanceList = new LinkedList<>();
	List<Unit> attackDistanceList = new LinkedList<>();
	UnitSpec unitSpec = UnitUtil.getUnitSpec(myUnit);

	// 전투 반경 내의 유닛이 대상이다.
	for (Unit enemyUnit : enemyUnitManager.getUnitList()) {
	    int distance = myUnit.getDistance(enemyUnit);
	    if (distance <= unitSpec.getCombatDistance()) {
		combatDistanceList.add(enemyUnit);
	    }
	    if (distance <= unitSpec.getWeaponMaxRange()) {
		attackDistanceList.add(enemyUnit);
	    }
	}

	// TODO 예를 들어 내가 벌쳐라면 드라군보다 질럿을 먼저 때리도록 로직을 상세화 한다.

	if (0 < attackDistanceList.size()) {
	    return attackDistanceList.get(0);
	}

	if (0 < combatDistanceList.size()) {
	    return combatDistanceList.get(0);
	}

	return null;
    }

    // 내 유닛과 적 유닛의 각도를 구한다.
    public static double getAnagleFromMyUnitToEnemyUnit(Unit myUnit, Unit enemyUnit) {
	double ret = -1.0;

	if (null != myUnit && null != enemyUnit) {

	    int x1 = myUnit.getPosition().getX();
	    int y1 = myUnit.getPosition().getY();

	    int x2 = enemyUnit.getPosition().getX();
	    int y2 = enemyUnit.getPosition().getY();

	    int dx = x2 - x1;
	    int dy = y2 - y1;

	    ret = -Math.atan2(dy, dx);

	    if (ret < 0) {
		ret = Math.PI * 2 + ret;
	    }
	}

	return ret;
    }

    // 적 유닛이 나를 바라보고 있는지 구한다.
    public static boolean isEnemyUnitLookingMyUnit(Unit myUnit, Unit enemyUnit, double rad) {
	// 나와 적의 방향
	double angleFromMe = getAnagleFromMyUnitToEnemyUnit(myUnit, enemyUnit);
	if (0 > angleFromMe) {
	    return false;
	}
	double angleFromEnemy = enemyUnit.getAngle();
	double diff = Math.abs(angleFromMe - angleFromEnemy);
	if (diff < rad) {
	    return true;
	}
	return false;
    }

    // 한방에 적을 죽일 수 있는지 판단한다.
    public static boolean canKillSingleShoot(Unit myUnit, Unit enemyUnit) {
	// 무기를 사용할 수 없으면 false
	if (0 != myUnit.getGroundWeaponCooldown()) {
	    return false;
	}
	// 사거리 밖이면 false
	if (!myUnit.isInWeaponRange(enemyUnit)) {
	    return false;
	}
	if (enemyUnit.getHitPoints() <= myUnit.getType().groundWeapon().damageAmount()) {
	    return true;
	}
	return false;
    }
}
