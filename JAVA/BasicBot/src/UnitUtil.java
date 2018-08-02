import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import bwapi.Color;
import bwapi.Game;
import bwapi.Order;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.UnitType;

public class UnitUtil {

    private static Game game = MyBotModule.Broodwar;

    public static enum DistanceType {
	CLOSE, NEAR_IN, NEAR_OUT, FAR
    }

    private static enum DamageType {
	NORMAL, CONCUSSIVE, EXPLOSIVE
    };

    private static enum UnitSize {
	SMALL, MEDIUM, LARGE
    };

    private static Map<Integer, Integer> lastAttackFinishedFrame = new HashMap<>();

    private UnitUtil() {
    }

    // Unit의 정보를 출력한다.
    public static String toString(Unit2 unit) {
	if (null != unit) {
	    return String.format("Unit[id=%d, type=%s, hp=%d, angle=%f, position=%s, tilePosition=%s]", unit.getID(), unit.getType().toString(), unit.getHitPoints(),
		    unit.getAngle(), unit.getPosition().toString(), unit.getTilePosition().toString());
	}

	return "unit is null";
    }

    // Unit이 alliance인지 판단한다. alliance면 true를 리턴한다.
    public static boolean isAllianceUnit(Unit2 unit) {
	return unit.getPlayer().isAlly(game.self());
    }

    // Unit이 enemy 인지 판단한다. enemy면 true를 리턴한다.
    public static boolean isEnemyUnit(Unit2 unit) {
	return unit.getPlayer().isEnemy(game.self());
    }

    // Unit이 중립인지 판단한다. 중립면 true를 리턴한다.
    public static boolean isNutral(Unit2 unit) {
	return unit.getPlayer().isNeutral();
    }

    // BWMirror에는 BWAPI에는 존재하는 UnitSet, Filter가 없다. 이를 위해서 UnitSet과 비슷한 목적의 Set<UnitKind>를 사용한다.
    public static Set<UnitKind> getUnitKinds(Unit2 unit) {
	Set<UnitKind> result = new HashSet<>();

	if (null != unit) {
	    UnitType unitType = unit.getType();
	    String strUnitType = unitType.toString();

	    // Terran, Protoss, Zerg에 대한 UnitType을 처리한다.
	    if (strUnitType.startsWith("Terran_") || strUnitType.startsWith("Protoss_") || strUnitType.startsWith("Zerg_")) {
		result.add(UnitKind.valueOf(unit.getType().toString()));
		checkUnitType(result, unitType);
	    } else if (strUnitType.startsWith("Resource_Mineral_Field")) {
		// 미네랄 필드는 Resource_Mineral_Field, Resource_Mineral_Field_Type_2, Resource_Mineral_Field_Type_3 이렇게 세 종류가 존재한다.
		// Type2, 3일 경우 Resource_Mineral_Field로 처리한다.
		result.add(UnitKind.Resource_Mineral_Field);
	    } else if (strUnitType.equals("Resource_Vespene_Geyser")) {
		result.add(UnitKind.Resource_Vespene_Geyser);
	    }
	}

	return result;

    }

    // 유닛의 타입을 판별해서 스펙을 리턴한다.
    // 마이크로 컨트롤을 할 때 사용한다.
    public static UnitSpec getUnitSpec(Unit2 unit) {
	UnitType unitType = unit.getType();

	if (UnitType.Terran_Marine == unitType) {
	    return UnitSpecMarine.Instance();
	}

	Log.warn("Can not found CombatData because of undefined unit type: {}", unit.getType());

	return null;
    }

    // 파라메터로 전달 받은 내 유닛이 공격해야 할 가장 적당한 적 유닛을 선택한다.
    // 적당한 유닛이 없으면 null을 리턴한다.
    // 마이크로 컨트롤을 할 때 사용한다.
    public static Unit2 selectEnemyTargetUnit(Unit2 allianceUnit, UnitInfo enemyUnitInfo) {
	UnitSpec unitSpec = UnitUtil.getUnitSpec(allianceUnit);

	// 전투 반경 내의 유닛이 대상이다.
	// TODO: Unit.getUnitsInRadius(arg0)을 활용해 보자.
	Unit2 targetUnit = null;
	int targetDistance = Integer.MAX_VALUE;
	for (Unit2 enemyUnit : enemyUnitInfo.getUnitSet(UnitKind.Combat_Unit)) {
	    int distance = allianceUnit.getDistance(enemyUnit);
	    if (distance > unitSpec.getCombatDistance()) {
		continue;
	    }
	    if (distance < targetDistance) {
		targetUnit = enemyUnit;
		targetDistance = distance;
	    }
	    loggingDetailUnitInfo(enemyUnit);
	}

	// TODO 예를 들어 내가 벌쳐라면 드라군보다 질럿을 먼저 때리도록 로직을 상세화 한다.
	if (null != targetUnit) {
	    Log.debug("Alliance Unit [%d] -> Enemy Unit [%d]", allianceUnit.getID(), targetUnit.getID());
	}
	return targetUnit;
    }

    // 내 유닛과 적 유닛의 각도를 구한다.
    public static double getAnagleFromBaseUnitToAnotherUnit(Unit2 baseUnit, Unit2 targetUnit) {
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
    public static boolean isBaseUnitLookingAnotherUnit(Unit2 baseUnit, Unit2 anotherUnit) {
	boolean result = false;

	double baseUnitAngle = baseUnit.getAngle();
	double angleBetweenBaseAndOnother = UnitUtil.getAnagleFromBaseUnitToAnotherUnit(baseUnit, anotherUnit);
	result = UnitUtil.inRangeRadius(baseUnitAngle, angleBetweenBaseAndOnother, Math.PI * 2 / 3);

	return result;
    }

    // 상대방 유닛을 향해 회전한다.
    public static Position getBackCounterClockWisePosition(Unit2 allianceUnit, Unit2 enemyUnit) {
	// 직선 이동 거리
	int deltaScale = 200;
	// 대각선 이동 거리
	int diagonal = (int) (deltaScale / 1.414);

	int deltaX = 0;
	int deltaY = 0;
	// 내 유닛과 적 유닛의 각도를 구한다.
	double radian = UnitUtil.getAnagleFromBaseUnitToAnotherUnit(allianceUnit, enemyUnit);
	double factor = Math.PI / 8;
	if (radian >= 15 * factor || radian < 1 * factor) {
	    deltaX = -diagonal;
	    deltaY = diagonal;
	} else if (radian < 3 * factor) {
	    deltaX = -deltaScale;
	    deltaY = 0;
	} else if (radian < 5 * factor) {
	    deltaX = -diagonal;
	    deltaY = -diagonal;
	} else if (radian < 7 * factor) {
	    deltaX = 0;
	    deltaY = -deltaScale;
	} else if (radian < 9 * factor) {
	    deltaX = diagonal;
	    deltaY = -diagonal;
	} else if (radian < 11 * factor) {
	    deltaX = 100;
	    deltaY = 0;
	} else if (radian < 13 * factor) {
	    deltaX = diagonal;
	    deltaY = diagonal;
	} else if (radian < 15 * factor) {
	    deltaX = 0;
	    deltaY = deltaScale;
	}
	return new Position(allianceUnit.getPosition().getX() + deltaX, allianceUnit.getPosition().getY() + deltaY);
    }

    // 적과 적의 이동 목적지 각도와 적과 아군의 각도가 일치하면 true
    public static boolean isSameAngleBetweenEnemyMoveAndAllianceUnit(Unit2 allianceUnit, Unit2 enemyUnit) {
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

    // 적이 바라보고 있는 target position을 화면에 표시한다.
    public static void drawTargetPosition(Unit2 unit) {
	Position targetPosition = unit.getTargetPosition();
	if (null != targetPosition) {
	    game.drawCircleMap(targetPosition, 2, Color.Purple, true);
	    game.drawLineMap(unit.getPosition(), targetPosition, Color.Purple);
	}
    }

    // 유닛의 정보를 엄청 자세히 로그로 남긴다.
    // 주의: 속도가 느려지므로, 디버깅할 때만 사용할 것.
    public static void loggingDetailUnitInfo(Unit2 unit) {
	if (null != unit) {
	    String unitId = "UnitDetailInfo[" + unit.getID() + "] [" + unit.getType() + "] ";

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

	    // force 정보를 출력
	    if (isNutral(unit)) {
		etcInfo += "\nForce: Nutral";
	    } else if (isAllianceUnit(unit)) {
		etcInfo += "\nForce: Alliance";
	    } else if (isEnemyUnit(unit)) {
		etcInfo += "\nForce: Enemy";
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

    // 두 Position 간의 거리를 리턴한다.
    public static int getDistance(Position p1, Position p2) {
	int result = 0;

	if (!p1.equals(p2)) {
	    result = p1.getApproxDistance(p2);
	}

	return result;
    }

    // 두 유닛간의 거리를 구한다.
    public static int getDistance(Unit2 unit1, Unit2 unit2) {
	return getDistance(unit1.getPosition(), unit2.getPosition());
    }

    // 두 Position 간의 거리를 리턴한다.
    public static int getDistance(TilePosition p1, Position p2) {
	return getDistance(p1.toPosition(), p2);
    }

    // 두 Position 간의 거리를 리턴한다.
    public static int getDistance(TilePosition p1, Unit2 p2) {
	return getDistance(p1.toPosition(), p2.getPosition());
    }

    // 두 Position 간의 거리를 리턴한다.
    public static int getDistance(Unit2 unit, TilePosition tilePosition) {
	return getDistance(unit.getPosition(), tilePosition.toPosition());
    }

    // 두 Position 간의 거리를 리턴한다.
    public static int getDistance(TilePosition tilePosition1, TilePosition tilePosition2) {
	return getDistance(tilePosition1.toPosition(), tilePosition2.toPosition());
    }

    // 공격 모션이 완료되었는지 리턴한다.
    // 예를 들어 마리의 경우, 공격이 끝나기도 전에 이동하면 총을 꺼내고 쏘기도 전에 총을 집어 넣고 이동한다.
    public static boolean isAttackFinished(Unit2 allianceUnit) {
	boolean result = false;

	if (allianceUnit.isAttackFrame() && 0 != allianceUnit.getGroundWeaponCooldown()) {
	    int currentFrame = game.getFrameCount();
	    Integer lastFrame = lastAttackFinishedFrame.get(allianceUnit.getID());
	    if (null == lastFrame || currentFrame - lastFrame > 10) {
		lastAttackFinishedFrame.put(allianceUnit.getID(), currentFrame);
		result = true;
	    }
	}

	return result;
    }

    // 적군 유닛의 현재 상태(아군을 향하고 있고 가깝다, 아군을 등지고 있고 멀리 있다, 아군 근처로 MoveAttack명을 내렸다 등)를 리턴한다.
    public static EnemyUnitStatus getUnitCombatStatus(Unit2 allianceUnit, Unit2 enemyUnit) {
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

    // 마이크로 컨트롤이 구현된 적군 유닛인지 여부를 리턴한다.
    public static boolean isMicroControlableEnemyType(UnitType unitType) {
	boolean result = false;

	switch (unitType.toString()) {
	case "Terran_Firebat":
	case "Zerg_Zergling":
	case "Protoss_Zealot":
	    result = true;
	    break;
	default:
	    break;
	}

	return result;
    }

    private static void checkUnitType(final Set<UnitKind> unitKindSet, final UnitType unitType) {
	String strUnitType = unitType.toString();

	checkIfWorker(unitKindSet, strUnitType);
	checkIfMainBuilding(unitKindSet, unitType);
	checkIfCombatUnit(unitKindSet, strUnitType);
	checkIfBionicUnit(unitKindSet, unitType);
	checkIfBuilding(unitKindSet, unitType);
	checkIfClockingUnit(unitKindSet, strUnitType);

	unitKindSet.add(UnitKind.ALL);
    }

    // 일꾼 여부를 판단해서, 일꾼일 경우, UnitKind set에 추가한다.
    private static void checkIfWorker(final Set<UnitKind> unitKindSet, final String strUnitType) {
	switch (strUnitType) {
	case "Terran_SCV":
	case "Protoss_Probe":
	case "Zerg_Drone":
	    unitKindSet.add(UnitKind.Worker);
	    break;
	default:
	    break;
	}
    }

    // Command Center, Nexus, Hatchery 류의 메인 빌딩 여부 반환.
    // UnitKind.MAIN_BUILDING : 건물 여부 판단.
    // UnitKind.Addon : 애드온 건물 여부 판단.
    // UnitKind.Building_Trainable : 유닛을 훈련 가능한 건물 여부 판단.
    private static void checkIfMainBuilding(final Set<UnitKind> unitKindSet, final UnitType unitType) {
	switch (unitType.toString()) {
	case "Terran_Command_Center":
	case "Protoss_Nexus":
	case "Zerg_Hatchery":
	case "Zerg_Lair":
	case "Zerg_Hive":
	    unitKindSet.add(UnitKind.MAIN_BUILDING);
	    unitKindSet.add(UnitKind.Building_Trainable);
	    break;
	case "Terran_Barracks":
	case "Terran_Factory":
	case "Terran_Starport":
	    unitKindSet.add(UnitKind.Building_Trainable);
	    break;
	default:
	    break;
	}

	// 애드온 건물 여부를 확인
	if (unitType.isAddon()) {
	    unitKindSet.add(UnitKind.Addon);
	}
    }

    // 공격 유닛 타입 여부를 리턴.
    private static void checkIfCombatUnit(final Set<UnitKind> unitKindSet, final String strUnitType) {
	switch (strUnitType) {
	case "Terran_Firebat":
	case "Terran_Ghost":
	case "Terran_Goliath":
	case "Terran_Marine":
	case "Terran_Medic":
	case "Terran_Vulture":
	case "Terran_Siege_Tank_Siege_Mode":
	case "Terran_Siege_Tank_Tank_Mode":
	case "Terran_Battlecruiser":
	case "Terran_Science_Vessel":
	case "Terran_Valkyrie":
	case "Terran_Wraith":
	case "Protoss_Archon":
	case "Protoss_Dark_Archon":
	case "Protoss_Dark_Templar":
	case "Protoss_Dragoon":
	case "Protoss_High_Templar":
	case "Protoss_Reaver":
	case "Protoss_Zealot":
	case "Protoss_Arbiter":
	case "Protoss_Carrier":
	case "Protoss_Corsair":
	case "Protoss_Scout":
	case "Zerg_Defiler":
	case "Zerg_Hydralisk":
	case "Zerg_Infested_Terran":
	case "Zerg_Lurker":
	case "Zerg_Ultralisk":
	case "Zerg_Zergling":
	case "Zerg_Devourer":
	case "Zerg_Guardian":
	case "Zerg_Mutalisk":
	case "Zerg_Queen":
	case "Zerg_Scourge":
	    unitKindSet.add(UnitKind.Combat_Unit);
	    break;
	default:
	    break;
	}
    }

    // 바이오닉 유닛 타입 여부를 리턴.
    private static void checkIfBionicUnit(final Set<UnitKind> unitKindSet, final UnitType unitType) {
	if (unitType.isOrganic()) {
	    unitKindSet.add(UnitKind.Bionic_Unit);
	    if (!UnitType.Terran_Medic.equals(unitType) && !UnitType.Terran_SCV.equals(unitType)) {
		unitKindSet.add(UnitKind.Bionic_Attackable);
	    }
	}
    }

    // 건물 여부를 리턴
    private static void checkIfBuilding(final Set<UnitKind> unitKindSet, final UnitType unitType) {
	// BWAPI에서 드론을 건물로 체크하는 경우가 있는데, 이 경우에 대한 예외처리를 해준다. 드론은 건물이 아니다.
	if (unitType.isBuilding() && !unitType.equals(UnitType.Zerg_Drone)) {
	    unitKindSet.add(UnitKind.Building);
	}
    }

    private static void checkIfClockingUnit(final Set<UnitKind> unitKindSet, final String strUnitType) {
	switch (strUnitType) {
	case "Protoss_Dark_Templar":
	case "Zerg_Lurker":
	case "Terran_Wraith":
	case "Protoss_Observer":
	    unitKindSet.add(UnitKind.Clocked);
	    break;
	default:
	    break;
	}
    }

    // from 에서 to 방향으로 distance 거리만큼 떨어진 위치를 반환한다.
    public static Position getPositionAsDistance(Position from, Position to, int distance) {
	Position result = null;

	if (null == from || null == to || distance < 1) {
	    Log.warn("getPositionAsDistance(): Invalid parameters. from=%s,to=%s,distance=%d", from, to, distance);
	} else {
	    int fullDistance = UnitUtil.getDistance(from, to);
	    if (0 != fullDistance) {
		double percentage = distance / fullDistance;

		double deltaX = (from.getX() - to.getX()) * percentage;
		double deltaY = (from.getY() - to.getY()) * percentage;

		result = new Position(from.getX() - (int) deltaX, from.getY() - (int) deltaY);
	    }
	}

	return result;
    }

    // unitType에 해당하는 unitKind를 리턴한다.
    public static UnitKind getUnitKindByUnitType(UnitType unitType) {
	return UnitKind.valueOf(unitType.toString());
    }

    // unit이 unitKind 종류인지 여부를 리턴한다.
    public static boolean compareUnitKind(Unit2 unit, UnitKind unitKind) {
	return getUnitKinds(unit).contains(unitKind);
    }

    // u1이 u2에 입히는 예상 공격력을 계산한다.
    public static int getDamage(Unit2 u1, Unit2 u2) {
	int result = 0;

	int defaultDamage = u1.getType().groundWeapon().damageAmount();
	UnitSize unitSize = getUnitSize(u2);

	result = defaultDamage;

	DamageType damageType = gameDamageType(u1, u2);

	if (damageType.equals(DamageType.CONCUSSIVE)) {
	    // 진동형: 소형 100%, 중형 50%, 대형 25% 들어간다.
	    if (unitSize.equals(UnitSize.SMALL)) {
		result = defaultDamage;
	    } else if (unitSize.equals(UnitSize.MEDIUM)) {
		result = (int) (defaultDamage * 0.5);
	    } else {
		result = (int) (defaultDamage * 0.25);
	    }
	} else if (damageType.equals(DamageType.EXPLOSIVE)) {
	    // 폭발형: 대형 100%, 중형 75%, 소형 50% 들어간다.
	    if (unitSize.equals(UnitSize.SMALL)) {
		result = (int) (defaultDamage * 0.5);
	    } else if (unitSize.equals(UnitSize.MEDIUM)) {
		result = (int) (defaultDamage * 0.75);
	    } else {
		result = defaultDamage;
	    }
	} else {
	    // 일반형: 유닛 크기와 상관 없이 100% 들어간다.
	}

	return result;
    }

    private static DamageType gameDamageType(Unit2 u1, Unit2 u2) {
	DamageType result = DamageType.NORMAL;

	String strUnitType1 = u1.getType().toString();
	switch (strUnitType1) {
	case "Terran_Firebat":
	case "Terran_Ghost":
	case "Terran_Vulture":
	    result = DamageType.CONCUSSIVE;
	    break;
	case "Terran_Siege_Tank_Siege_Mode":
	case "Terran_Siege_Tank_Tank_Mode":
	    result = DamageType.EXPLOSIVE;
	    break;
	default:
	    break;
	}

	if ((strUnitType1.equals("Terran_Goliath") || strUnitType1.equals("Terran_Wraith")) && u2.getType().isFlyer()) {
	    result = DamageType.EXPLOSIVE;
	}
	return result;
    }

    // http://classic.battle.net/scc/GS/damage.shtml
    private static UnitSize getUnitSize(Unit2 unit) {
	UnitSize result = UnitSize.SMALL;

	String strUnitType = unit.getType().toString();

	switch (strUnitType) {
	case "Terran_Vulture":
	case "Zerg_Hydralisk":
	case "Zerg_Defiler":
	case "Zerg_Queen":
	case "Zerg_Lurker":
	case "Protoss_Corsair":
	    result = UnitSize.MEDIUM;
	    break;
	case "Terran_Siege_Tank_Siege_Mode":
	case "Terran_Siege_Tank_Tank_Mode":
	case "Terran_Goliath":
	case "Terran_Wraith":
	case "Terran_Dropship":
	case "Terran_Science_Vessel":
	case "Terran_Battlecruiser":
	case "Terran_Valkyrie":
	case "Zerg_Ultralisk":
	case "Zerg_Overlord":
	case "Zerg_Guardian":
	case "Zerg_Devourer":
	case "Protoss_Dragoon":
	case "Protoss_Archon":
	case "Protoss_Reaver":
	case "Protoss_Shuttle":
	case "Protoss_Scout":
	case "Protoss_Carrier":
	case "Protoss_Arbiter":
	case "Protoss_Dark_Archon":
	    result = UnitSize.LARGE;
	    break;
	default:
	    break;
	}

	return result;
    }

    // 둘 중 tilePosition에 더 가까운 유닛을 리턴한다.
    public static Unit2 getCloseUnit(Unit2 unit1, Unit2 unit2, TilePosition tilePosition) {
	return getCloseUnit(unit1, unit2, tilePosition, 0, Integer.MAX_VALUE);
    }

    // 둘 중 tilePosition에 더 가까운 유닛을 리턴한다. tilePosition과 각 유닛간의 거리 차이가 minDiff 이하라면, unit2가 더 가깝더라도 unit1을 리턴한다.
    // 예를 들어, tilePosition과 unit1의 거리는 100, unit2의 거리는 90이고, minDiff가 16이라면, 비록 unit2가 tilePosition과 더 가깝지만 거리 차이가 10이라서 16이하이므로 unit1을 리턴한다.
    public static Unit2 getCloseUnit(Unit2 unit1, Unit2 unit2, TilePosition tilePosition, int minDiff) {
	return getCloseUnit(unit1, unit2, tilePosition, minDiff, Integer.MAX_VALUE);
    }

    // 둘 중 tilePosition에 더 가까운 유닛을 리턴한다. tilePosition과 각 유닛간의 거리 차이가 minDiff 이하라면, unit2가 더 가깝더라도 unit1을 리턴한다. 타일과의 거리가 maxDistance 이상이라면, 무조건 unit1을 리턴한다.
    // ex 1) tilePosition과 unit1의 거리는 100, unit2의 거리는 90이고, minDiff가 16이라면, 비록 unit2가 tilePosition과 더 가깝지만 거리 차이가 10이라서 16이하이므로 unit1을 리턴한다.
    // ex 2) tilePosition과 unit1의 거리는 1000, unit2의 거리는 1090이고, maxDistance가 1000이라면, 비록 unit2가 tilePosition과 더 가깝지만 거리가 1000 이상이라서 unit1을 리턴한다.
    public static Unit2 getCloseUnit(Unit2 unit1, Unit2 unit2, TilePosition tilePosition, int minDiff, int maxDistance) {
	Unit2 result = null;

	if (null != unit1 && null == unit2) {
	    result = unit1;
	} else if (null == unit1 && null != unit2) {
	    result = unit2;
	} else {
	    int distanceUnit1 = getDistance(unit1, tilePosition);
	    int distanceUnit2 = getDistance(unit2, tilePosition);
	    if (distanceUnit1 <= distanceUnit2) {
		result = unit1;
	    } else {
		result = unit2;
	    }
	    if (result.equals(unit2) && (distanceUnit1 - distanceUnit2) <= minDiff) {
		result = unit1;
	    }
	    if (distanceUnit1 >= maxDistance || distanceUnit1 >= maxDistance) {
		result = unit1;
	    }
	}

	return result;
    }
}
