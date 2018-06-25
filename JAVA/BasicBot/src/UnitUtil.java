import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import bwapi.Color;
import bwapi.Game;
import bwapi.Order;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;

public class UnitUtil {

    private static Game game = MyBotModule.Broodwar;

    public static enum DistanceType {
	CLOSE, NEAR_IN, NEAR_OUT, FAR
    }

    // Unit의 정보를 출력한다.
    public static String toString(Unit unit) {
	if (null != unit) {
	    return String.format("Unit[id=%d, type=%s, hp=%d, angle=%f, position=%s]", unit.getID(), unit.getType().toString(), unit.getHitPoints(), unit.getAngle(),
		    unit.getPosition().toString());
	}

	return "unit is null";
    }

    // Unit이 alliance인지 판단한다. alliance면 true를 리턴한다.
    public static boolean isAllianceUnit(Unit unit) {
	return unit.getPlayer().isAlly(game.self());
    }

    // Unit이 enemy 인지 판단한다. enemy면 true를 리턴한다.
    public static boolean isEnemyUnit(Unit unit) {
	return unit.getPlayer().isEnemy(game.self());
    }

    // Filter를 위해서 유닛의 종류를 리턴한다.
    public static Set<UnitKind> getUnitKinds(Unit unit) {
	Set<UnitKind> result = new HashSet<>();

	UnitType unitType = unit.getType();
	String strUnitType = unitType.toString();

	// UnitType.Protoss_Zealot;

	switch (strUnitType) {
	case "Terran_Marine":
	    result.add(UnitKind.ATTACKABLE_NORMAL);
	    break;
	case "Zerg_Zergling":
	    result.add(UnitKind.ATTACKABLE_NORMAL);
	case "Protoss_Zealot":
	    result.add(UnitKind.ATTACKABLE_NORMAL);
	default:
	    break;
	}

	// 빌딩 여부를 확인
	if (unitType.isBuilding()) {
	    result.add(UnitKind.ALL_BUILDING);
	}

	return result;
    }

    // 유닛의 타입을 판별해서 스펙을 리턴한다.
    public static UnitSpec getUnitSpec(Unit unit) {
	UnitType unitType = unit.getType();

	if (UnitType.Terran_Marine == unitType) {
	    return new UnitSpecMarine();
	}

	Log.warn("Can not found CombatData because of undefined unit type: {}", unit.getType());

	return null;
    }

    // 파라메터로 전달 받은 내 유닛이 공격해야 할 가장 적당한 적 유닛을 선택한다.
    // 적당한 유닛이 없으면 null을 리턴한다.
    public static Unit selectEnemyTargetUnit(Unit allianceUnit, UnitManager enemyUnitManager) {
	List<Unit> combatDistanceList = new LinkedList<>();
	List<Unit> attackDistanceList = new LinkedList<>();
	UnitSpec unitSpec = UnitUtil.getUnitSpec(allianceUnit);

	// 전투 반경 내의 유닛이 대상이다.
	// TODO: Unit.getUnitsInRadius(arg0)을 활용해 보자. 
	for (Integer enemyUnitId : enemyUnitManager.getAttackableUnitList()) {
	    Unit enemyUnit = enemyUnitManager.getUnit(enemyUnitId);
	    int distance = allianceUnit.getDistance(enemyUnit);
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
    public static double getAnagleFromBaseUnitToAnotherUnit(Unit baseUnit, Unit targetUnit) {
	double ret = -1.0;

	if (null != baseUnit && null != targetUnit) {
	    ret = getAngleFromPositions(baseUnit.getPosition(), targetUnit.getPosition());
	}

	return ret;
    }

    // 내 유닛과 적 유닛의 각도를 구한다.
    public static double getAngleFromPositions(Position base, Position target) {
	double ret = -1.0;

	int x1 = base.getX();
	int y1 = base.getY();

	int x2 = target.getX();
	int y2 = target.getY();

	int dx = x2 - x1;
	int dy = y2 - y1;

	ret = Math.atan2(dy, dx);

	if (ret < 0) {
	    ret = Math.PI * 2 + ret;
	}

	return ret;
    }

    // rad1 - (diff / 2) ~ rad1 + (diff / 2) 범위에 rad2가 위치하면 true를 리턴.
    public static boolean inRangeRadius(double rad1, double rad2, double diff) {
	boolean result = false;

	double from = rad1 - (diff / 2);
	double to = rad1 + (diff / 2);

	if (rad2 >= from && rad2 <= to) {
	    result = true;
	} else {
	    rad2 += Math.PI * 2;

	    if (rad2 >= from && rad2 <= to) {
		result = true;
	    }
	}

	return result;
    }

    // baseUnit이 anotherUnit을 바라보고 있는지 여부를 리턴
    public static boolean isBaseUnitLookingAnotherUnit(Unit baseUnit, Unit anotherUnit) {
	boolean result = false;

	double baseUnitAngle = baseUnit.getAngle();
	double angleBetweenBaseAndOnother = UnitUtil.getAnagleFromBaseUnitToAnotherUnit(baseUnit, anotherUnit);
	result = UnitUtil.inRangeRadius(baseUnitAngle, angleBetweenBaseAndOnother, Math.PI * 2 / 3);

	return result;
    }

    // 적 유닛이 나를 바라보고 있는지 구한다. (target position 기반)
    public static boolean isEnemyUnitLookingMyUnit2(Unit allianceUnit, Unit enemyUnit) {
	boolean result = false;

	Position enemyTargetPosition = enemyUnit.getTargetPosition();
	if (allianceUnit.getDistance(enemyTargetPosition) < 100) {
	    result = true;
	}

	return result;
    }

    // 적과 적의 이동 목적지 각도와 적과 아군의 각도가 일치하면 true
    public static boolean isSameAngleBetweenEnemyMoveAndAllianceUnit(Unit allianceUnit, Unit enemyUnit) {
	boolean result = false;

	Position alliancePosition = allianceUnit.getPosition();
	Position enemyPosition = enemyUnit.getPosition();
	Position enemyTargetPosition = enemyUnit.getOrderTargetPosition();
	if (null == enemyTargetPosition) {
	    if (null != enemyUnit.getOrderTarget()) {
		enemyTargetPosition = enemyUnit.getOrderTarget().getPosition();
	    }
	}

	if (null != alliancePosition && null != enemyPosition && null != enemyTargetPosition) {
	    double angleToEnemyPosition = getAngleFromPositions(enemyPosition, enemyTargetPosition);
	    double angleToAlliance = getAngleFromPositions(enemyPosition, alliancePosition);
	    if (inRangeRadius(angleToEnemyPosition, angleToAlliance, Math.PI * 2 / 3)) {
		result = true;
	    }
	}

	return result;
    }

    // 한방에 적을 죽일 수 있는지 판단한다.
    public static boolean canKillSingleShoot(Unit allianceUnit, Unit enemyUnit) {
	// 무기를 사용할 수 없으면 false
	if (0 != allianceUnit.getGroundWeaponCooldown()) {
	    return false;
	}
	// 사거리 밖이면 false
	if (!allianceUnit.isInWeaponRange(enemyUnit)) {
	    return false;
	}
	if (enemyUnit.getHitPoints() <= allianceUnit.getType().groundWeapon().damageAmount()) {
	    return true;
	}
	return false;
    }

    // 적이 바라보고 있는 target position을 화면에 표시한다.
    public static void drawTargetPosition(Unit unit) {
	Position targetPosition = unit.getTargetPosition();
	if (null != targetPosition) {
	    game.drawCircleMap(targetPosition, 2, Color.Purple, true);
	    game.drawLineMap(unit.getPosition(), targetPosition, Color.Purple);
	}
    }

    // 유닛의 정보를 엄청 자세히 로그로 남긴다.
    public static void loggingDetailUnitInfo(Unit unit) {
	if (null != unit) {
	    String unitId = "[" + unit.getID() + "] ";

	    // 현재 수행 가능한 액션을 로깅
	    String posibility = unitId + "Possible action: ";
	    if (unit.canStop()) {
		posibility += "[Stop] ";
	    }
	    if (unit.canMove()) {
		posibility += "[Move] ";
	    }
	    if (unit.canAttackMove()) {
		posibility += "[Attack Move] ";
	    }
	    if (unit.canHoldPosition()) {
		posibility += "[Hold] ";
	    }
	    if (unit.canPatrol()) {
		posibility += "[Patrol] ";
	    }
	    // 큰 의미가 없어서 Trace Level
	    Log.trace(posibility);

	    // 현재 동작 중인 액션을 로깅
	    String currentAction = unitId + "Current action: ";
	    if (unit.isIdle()) {
		currentAction += "[Idle] ";
	    }
	    if (unit.isAccelerating()) {
		currentAction += "[Accelerating] ";
	    }
	    if (unit.isMoving()) {
		currentAction += "[Moving] ";
	    }
	    if (unit.isBraking()) {
		currentAction += "[Braking] ";
	    }
	    if (unit.isAttacking()) {
		currentAction += "[Attacking] ";
	    }
	    if (unit.isAttackFrame()) {
		currentAction += "[Attack Frame] ";
	    }
	    if (unit.isHoldingPosition()) {
		currentAction += "[Holding] ";
	    }
	    if (unit.isFollowing()) {
		currentAction += "[Following] ";
	    }
	    if (unit.isSelected()) {
		currentAction += "[Selected] ";
	    }
	    if (unit.isStuck()) {
		currentAction += "[Stuck] ";
	    }
	    /*
	    if (unit.isInterruptible()) {
	    currentAction += "[Interruptible] ";
	    }
	    if (unit.isCompleted()) {
	    currentAction += "[Completed] ";
	    }
	    */
	    Log.trace(currentAction);

	    // 기타 정보: Target과 Order 정보를 로깅
	    String etcInfo = unitId + "Etc Info: ";
	    etcInfo += "[HP:" + unit.getHitPoints() + "] ";
	    etcInfo += "[Current Pos:" + unit.getPosition() + "] ";
	    if (null != unit.getTarget()) {
		etcInfo += "[Target:" + unit.getTarget().getID() + "] ";
	    }
	    if (null != unit.getTargetPosition()) {
		etcInfo += "[TargetPosition:" + unit.getTargetPosition() + "] ";
	    }
	    if (null != unit.getOrder()) {
		etcInfo += "[Order:" + unit.getOrder() + "] ";
	    }
	    if (null != unit.getOrderTarget()) {
		etcInfo += "[OrderTarget:" + unit.getOrderTarget().getID() + "] ";
	    }
	    if (null != unit.getOrderTargetPosition()) {
		etcInfo += "[OrderTargetPosition:" + unit.getOrderTargetPosition() + "] ";
	    }
	    etcInfo += "[OrderTimer:" + unit.getOrderTimer() + "] ";
	    if (null != unit.getSecondaryOrder()) {
		etcInfo += "[SecondaryOrder:" + unit.getSecondaryOrder() + "] ";
	    }

	    Log.trace(etcInfo);
	}
    }

    // 적과 아군의 위치를 계산해서 최적의 후퇴 지점을 리턴한다.
    public static Position getBackPosition(Position allianceUnitPosition, Position enemyUnitPosition) {
	Position result;

	int allianceX = allianceUnitPosition.getX();
	int allianceY = allianceUnitPosition.getY();

	int enemyX = enemyUnitPosition.getX();
	int enemyY = enemyUnitPosition.getY();

	result = new Position(allianceX + (allianceX - enemyX) * 4, allianceY + (allianceY - enemyY) * 4);

	return result;
    }

    public static int getDistance(Position p1, Position p2) {
	int diffX = p1.getX() - p2.getX();
	int diffY = p1.getY() - p2.getY();
	diffX *= diffX;
	diffY *= diffY;

	return diffX + diffY;
    }

    // 적군 유닛의 현재 상태(아군을 향하고 있고 가깝다, 아군을 등지고 있고 멀리 있다, 아군 근처로 MoveAttack명을 내렸다 등)를 리턴한다.
    public static EnemyUnitStatus getUnitCombatStatus(Unit allianceUnit, Unit enemyUnit) {
	EnemyUnitStatus result = EnemyUnitStatus.UNKNOWN;

	UnitSpec unitSpec = getUnitSpec(allianceUnit);
	int distanceFromEnemyUnitToAllianceUnit = enemyUnit.getDistance(allianceUnit);
	boolean isSameAngleBetweenEnemyMoveAndAllianceUnit = UnitUtil.isSameAngleBetweenEnemyMoveAndAllianceUnit(allianceUnit, enemyUnit);

	if (enemyUnit.getOrder().equals(Order.ComputerReturn) || enemyUnit.getOrder().equals(Order.Move) || enemyUnit.getOrder().equals(Order.AttackMove)
		|| enemyUnit.getOrder().equals(Order.AttackUnit)) {
	    if (enemyUnit.getOrder().equals(Order.AttackMove) && unitSpec.getNearMoveDistance() > allianceUnit.getDistance(enemyUnit.getOrderTargetPosition())) {
		// 적이 내 유닛 근처로 이동했다.
		// TODO AttackMove 뿐만 아니라 Move도 처리해야 하지 않을까?
		result = EnemyUnitStatus.NEAR_MOVE;
	    } else if (false == isSameAngleBetweenEnemyMoveAndAllianceUnit) {
		// 나와 같은 다른 방향으로 이동한다.
		if (distanceFromEnemyUnitToAllianceUnit < unitSpec.getDifferenceDirectionCloseDistance()) {
		    result = EnemyUnitStatus.DIFFERENCE_DIR_CLOSE;
		} else if (distanceFromEnemyUnitToAllianceUnit < unitSpec.getDifferenceDirectionFarDistance()) {
		    result = EnemyUnitStatus.DIFFERENCE_DIR_MIDDLE;
		} else {
		    result = EnemyUnitStatus.DIFFERENCE_DIR_FAR;
		}
	    } else {
		// 나와 같은 방향으로 이동한다.
		if (distanceFromEnemyUnitToAllianceUnit <= unitSpec.getSameDirectionCloseDistance()) {
		    result = EnemyUnitStatus.SAME_DIR_CLOSE;
		} else if (distanceFromEnemyUnitToAllianceUnit < unitSpec.getSameDirectionFarDistance()) {
		    result = EnemyUnitStatus.SAME_DIR_MIDDLE;
		} else {
		    result = EnemyUnitStatus.SAME_DIR_FAR;
		}
	    }
	}

	return result;
    }
}
