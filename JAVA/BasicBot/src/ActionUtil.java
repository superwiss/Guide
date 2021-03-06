import java.util.HashSet;
import java.util.Set;

import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.UnitType;

// 유닛의 Command(Action)에 대한 유틸리티
public class ActionUtil {

    private static Set<Integer> forceAttackUnitIdSet = new HashSet<>();
    private static Game game;

    public static void setGame(Game game) {
	ActionUtil.game = game;
    }

    public static boolean patrolToEnemyUnit(UnitInfo allianceUnitInfo, Unit2 allianceUnit, Unit2 enemyUnit) {
	boolean result = false;

	ActionDetail currnetCommand = getActionDetail("PATROL_TO_UNIT", allianceUnit, enemyUnit);
	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitInfo)) {
	    allianceUnit.patrol(enemyUnit.getPosition());
	    result = true;
	}

	return result;
    }

    public static boolean repair(UnitInfo allianceUnitInfo, Unit2 allianceUnit, Unit2 target) {
	boolean result = false;

	ActionDetail currnetCommand = getActionDetail("REPAIR", allianceUnit, target);
	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitInfo)) {
	    allianceUnit.repair(target);
	    result = true;
	}

	return result;
    }

    public static boolean moveToPosition(UnitInfo allianceUnitInfo, Unit2 allianceUnit, Position position) {
	return moveToPosition(allianceUnitInfo, allianceUnit, position, 0);
    }

    public static boolean moveToPosition(UnitInfo unitInfo, Unit2 unit, TilePosition tilePosition) {
	return moveToPosition(unitInfo, unit, tilePosition.toPosition(), 0);
    }

    public static boolean moveToPosition(UnitInfo allianceUnitInfo, Unit2 allianceUnit, Position position, int margin) {
	boolean result = false;

	ActionDetail currnetCommand = getActionDetail("MOVE_TO_POSITION", allianceUnit, position, margin);
	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitInfo)) {
	    // 이동할 position이 이전에 명령받은 position과 너무 가까우면 무시한다.
	    allianceUnit.move(position);
	    result = true;
	}

	return result;
    }

    // TODO 상대가 움직이면 position이 바뀌면서 매 프레임마다 move 명령이 내려지는 상황이 발생하는데, 큰 이슈 없는지 확인하기
    public static boolean moveToUnit(UnitInfo allianceUnitInfo, Unit2 allianceUnit, Unit2 enemyUnit) {
	boolean result = false;

	ActionDetail currnetCommand = getActionDetail("MOVE_TO_UNIT", allianceUnit, enemyUnit.getPoint());
	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitInfo)) {
	    allianceUnit.move(enemyUnit.getPoint());
	    result = true;
	}

	return result;
    }

    public static boolean attackEnemyUnit(UnitInfo allianceUnitInfo, Unit2 allianceUnit, Unit2 enemyUnit) {
	boolean result = false;

	ActionDetail currnetCommand = getActionDetail("ATTACK_TO_UNIT", allianceUnit, enemyUnit);
	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitInfo)) {
	    allianceUnit.attack(enemyUnit);
	    result = true;
	}
	return result;
    }

    public static boolean attackPosition(UnitInfo allianceUnitInfo, Unit2 allianceUnit, Position position) {
	boolean result = false;

	ActionDetail currnetCommand = getActionDetail("ATTACK_TO_POSITION", allianceUnit, position);
	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitInfo)) {
	    allianceUnit.attack(position);
	    result = true;
	}

	return result;
    }

    public static void attackPosition(UnitInfo unitInfo, Unit2 unit, TilePosition tilePosition) {
	if (null != tilePosition) {
	    attackPosition(unitInfo, unit, tilePosition.toPosition());
	} else {
	    Log.warn("ActionUtil.attackPosition: tilePostion is null");
	}
    }

    public static boolean attackEnemyUnitForcibly(UnitInfo allianceUnitInfo, Unit2 allianceUnit, Unit2 enemyUnit) {
	boolean result = false;

	ActionDetail currnetCommand = getActionDetail("ATTACK_TO_UNIT_FORCIBLY", allianceUnit, enemyUnit);
	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitInfo)) {
	    allianceUnit.attack(enemyUnit);
	    forceAttackUnitIdSet.add(allianceUnit.getID());
	    result = true;
	}

	return result;
    }

    public static boolean stop(UnitInfo allianceUnitInfo, Unit2 allianceUnit) {
	boolean result = false;

	ActionDetail currnetCommand = getActionDetail("STOP", allianceUnit);
	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitInfo)) {
	    allianceUnit.stop();
	    result = true;
	}

	return result;
    }

    public static boolean isAttackingForcibly(Unit2 allianceUnit) {
	boolean result = false;

	if (forceAttackUnitIdSet.contains(Integer.valueOf(allianceUnit.getID()))) {
	    result = true;
	}

	return result;
    }

    public static void attackFinished(Unit2 allianceUnit) {
	forceAttackUnitIdSet.remove(Integer.valueOf(allianceUnit.getID()));
    }

    // 상대방 유닛을 향해 회전한다.
    public static boolean turn(UnitInfo allianceUnitInfo, Unit2 allianceUnit, Unit2 enemyUnit) {
	boolean result = false;

	ActionDetail currnetCommand = getActionDetail("TURN", allianceUnit, enemyUnit);
	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitInfo)) {
	    int deltaScale = 1;
	    int deltaX = 0;
	    int deltaY = 0;
	    // 내 유닛과 적 유닛의 각도를 구한다.
	    double radian = UnitUtil.getAnagleFromBaseUnitToAnotherUnit(allianceUnit, enemyUnit);
	    double factor = Math.PI / 8;
	    if (radian >= 15 * factor || radian < 1 * factor) {
		deltaX = deltaScale;
		deltaY = 0;
	    } else if (radian < 3 * factor) {
		deltaX = deltaScale;
		deltaY = deltaScale;
	    } else if (radian < 5 * factor) {
		deltaX = 0;
		deltaY = deltaScale;
	    } else if (radian < 7 * factor) {
		deltaX = -deltaScale;
		deltaY = deltaScale;
	    } else if (radian < 9 * factor) {
		deltaX = -deltaScale;
		deltaY = 0;
	    } else if (radian < 11 * factor) {
		deltaX = -deltaScale;
		deltaY = -deltaScale;
	    } else if (radian < 13 * factor) {
		deltaX = 0;
		deltaY = -deltaScale;
	    } else if (radian < 15 * factor) {
		deltaX = deltaScale;
		deltaY = -deltaScale;
	    }
	    allianceUnit.move(new Position(allianceUnit.getPosition().getX() + deltaX, allianceUnit.getPosition().getY() + deltaY));
	    result = true;
	}

	return result;
    }

    public static boolean build(UnitInfo unitInfo, Unit2 unit, UnitType unitType, TilePosition tilePosition) {
	boolean result = false;

	ActionDetail currnetCommand = getActionDetail("BUILD", unit, unitType, tilePosition.toPosition());
	if (isAcceptedAction(currnetCommand, unit, unitInfo)) {
	    unit.build(unitType, tilePosition);
	    result = true;
	}

	return result;
    }

    public static void updateStatus(UnitInfo allianceUnitInfo, Unit2 allianceUnit, UnitStatus unitStatus) {
	UnitStatus lastUnitStatus = allianceUnitInfo.getLastStatus(allianceUnit);
	if (!unitStatus.equals(lastUnitStatus)) {
	    allianceUnitInfo.updateLastStatus(allianceUnit, unitStatus);
	}
    }

    // 유닛의 명령을 String으로 표현한다. (Position이 없는 타입의 명령어)
    private static ActionDetail getActionDetail(String command, Unit2 allianceUnit, Unit2 enemyUnit) {
	return new ActionDetail(command, allianceUnit, enemyUnit, null, null, game.getFrameCount(), 0);
    }

    // 유닛의 명령을 String으로 표현한다. (Destination Unit이 없는 타입의 명령어)
    private static ActionDetail getActionDetail(String command, Unit2 unit, Position position) {
	return new ActionDetail(command, unit, null, null, position, game.getFrameCount(), 0);
    }

    // 유닛의 명령을 String으로 표현한다. (Destination Unit, Position이 없는 타입의 명령어)
    private static ActionDetail getActionDetail(String command, Unit2 unit) {
	return new ActionDetail(command, unit, null, null, null, game.getFrameCount(), 0);
    }

    private static ActionDetail getActionDetail(String command, Unit2 unit, Position position, int margin) {
	return new ActionDetail(command, unit, null, null, position, game.getFrameCount(), 0);
    }

    private static ActionDetail getActionDetail(String command, Unit2 unit, UnitType unitType, Position position) {
	return new ActionDetail(command, unit, null, unitType, position, game.getFrameCount(), 0);
    }

    // 유닛의 명령을 ActionDetail 개체로 변환한다.
    private static ActionDetail getActionDetail(String command, Unit2 srcUnit, Unit2 destUnit, UnitType unitType, Position position, int margin) {
	return new ActionDetail(command, srcUnit, destUnit, unitType, position, game.getFrameCount(), margin);
    }

    // 유닛의 액션을 수행할지 말지 결정한다. 이전과 동일한 액션이 들어오면 skip할 수 있도록 false를 리턴한다.
    private static boolean isAcceptedAction(ActionDetail currentActionDetail, Unit2 allianceUnit, UnitInfo allianceUnitInfo) {
	boolean result = false;

	ActionDetail lastActionDetail = allianceUnitInfo.getLastAction(allianceUnit);

	if (false == isAttackingForcibly(allianceUnit) && currentActionDetail.getCommand().equals("ATTACK_TO_UNIT_FORCIBLY")) {
	    // 10프레임동안 10회 강제 공격 명령을 내렸을 경우, 5프레임에서 공격이 성공하더라도 5프레임에서 다시 내린 강제 공격 명령은 무시된다.
	    // 이 현상을 막기 위해서 강제 공격 상태가 아닌 상태에서 강제 공격 명령이 들어오면 이전과 동일한 명령이더라도 Accept한다.
	    allianceUnitInfo.updateLastAction(allianceUnit, currentActionDetail);
	    result = true;
	    Log.trace("Action Accepted: " + currentActionDetail);
	} else if (null != lastActionDetail && lastActionDetail.getActionFrame() + 10 <= currentActionDetail.getActionFrame()) {
	    // 10프레임이 지났으면 동일한 명령이 들어오더라도 다시 수행한다.
	    allianceUnitInfo.updateLastAction(allianceUnit, currentActionDetail);
	    result = true;
	    Log.trace("Action Accepted (Last action frame: %d): %s", lastActionDetail.getActionFrame(), currentActionDetail);
	} else if (forceAttackUnitIdSet.contains(Integer.valueOf(allianceUnit.getID()))) {
	    // 강제 공격 상태라면, 계속 강제 공격을 할 수 있도록 어떠한 액션도 무시한다.
	    Log.trace("Action Rejected: " + currentActionDetail);
	} else if (currentActionDetail.equals(lastActionDetail)) {
	    // 이전과 동일한 액션이라면 무시한다.
	    Log.trace("Action Rejected: " + currentActionDetail);
	} else {
	    // 이전과 다른 액션이다. Accept 할 준비를 한다.
	    result = true;

	    // 이전과 다른 명령이라고 하더라도, 거의 비슷한 위치로의 액션이라면 무시한다. 
	    if (null != currentActionDetail.getCommand() && null != lastActionDetail && currentActionDetail.getCommand().equals(lastActionDetail.getCommand())
		    && null != currentActionDetail.getPosition() && null != lastActionDetail.getPosition() && 0 != currentActionDetail.getMargin()) {

		int diff = UnitUtil.getDistance(currentActionDetail.getPosition(), lastActionDetail.getPosition());

		// 동일한 명령어에 대해서 Position이 10Pixel 이하로 차이가 나면 무시한다.
		if (diff <= currentActionDetail.getMargin()) {
		    result = false;
		    Log.trace("Action Rejected: " + currentActionDetail + "; Too close to last position: " + lastActionDetail.getPosition());
		}
	    }
	    if (false != result) {
		allianceUnitInfo.updateLastAction(allianceUnit, currentActionDetail);
		result = true;
		Log.trace("Action Accepted: " + currentActionDetail);
	    }
	}

	return result;
    }
}
