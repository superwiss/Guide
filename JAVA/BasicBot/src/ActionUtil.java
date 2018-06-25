import java.util.HashSet;
import java.util.Set;

import bwapi.Position;
import bwapi.Unit;

// 유닛의 Command(Action)에 대한 유틸리티
public class ActionUtil {

    private static Set<Integer> forceAttackUnitIdSet = new HashSet<>();

    public static void patrolToEnemyUnit(UnitManager allianceUnitManager, Unit allianceUnit, Unit enemyUnit) {
	ActionDetail currnetCommand = getActionDetail("PATROL_TO_UNIT", allianceUnit, enemyUnit);

	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitManager)) {
	    allianceUnit.patrol(enemyUnit.getPosition());
	}
    }

    public static void moveToPosition(UnitManager allianceUnitManager, Unit allianceUnit, Position position, int margin) {
	ActionDetail currnetCommand = getActionDetail("MOVE_TO_POSITION", allianceUnit, position, margin);

	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitManager)) {
	    // 이동할 position이 이전에 명령받은 position과 너무 가까우면 무시한다.
	    allianceUnit.move(position);
	}
    }

    // TODO 상대가 움직이면 position이 바뀌면서 매 프레임마다 move 명령이 내려지는 상황이 발생하는데, 큰 이슈 없는지 확인하기 
    public static void moveToUnit(UnitManager allianceUnitManager, Unit allianceUnit, Unit enemyUnit) {
	ActionDetail currnetCommand = getActionDetail("MOVE_TO_UNIT", allianceUnit, enemyUnit.getPoint());

	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitManager)) {
	    allianceUnit.move(enemyUnit.getPoint());
	}
    }

    public static void attackEnemyUnit(UnitManager allianceUnitManager, Unit allianceUnit, Unit enemyUnit) {
	ActionDetail currnetCommand = getActionDetail("ATTACK_TO_UNIT", allianceUnit, enemyUnit);

	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitManager)) {
	    allianceUnit.attack(enemyUnit);
	}
    }

    public static void attackEnemyUnitForcibly(UnitManager allianceUnitManager, Unit allianceUnit, Unit enemyUnit) {
	ActionDetail currnetCommand = getActionDetail("ATTACK_TO_UNIT_Forcibly", allianceUnit, enemyUnit);

	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitManager)) {
	    allianceUnit.attack(enemyUnit);
	    forceAttackUnitIdSet.add(allianceUnit.getID());
	}
    }

    public static void attackFinished(Unit allianceUnit) {
	forceAttackUnitIdSet.remove(Integer.valueOf(allianceUnit.getID()));
    }

    public static boolean isAttackingForcibly(Unit allianceUnit) {
	boolean result = false;

	if (forceAttackUnitIdSet.contains(Integer.valueOf(allianceUnit.getID()))) {
	    result = true;
	}

	return result;
    }

    public static void stop(UnitManager allianceUnitManager, Unit allianceUnit) {
	ActionDetail currnetCommand = getActionDetail("STOP", allianceUnit);

	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitManager)) {
	    allianceUnit.stop();
	}
    }

    // 상대방 유닛을 향해 회전한다.
    public static void turn(UnitManager allianceUnitManager, Unit allianceUnit, Unit enemyUnit) {
	ActionDetail currnetCommand = getActionDetail("TURN", allianceUnit, enemyUnit);

	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitManager)) {
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
	}
    }

    public static void updateStatus(UnitManager allianceUnitManager, Unit allianceUnit, UnitStatus unitStatus) {
	UnitStatus lastUnitStatus = allianceUnitManager.getLastStatus(allianceUnit);
	if (!unitStatus.equals(lastUnitStatus)) {
	    allianceUnitManager.updateLastStatus(allianceUnit, unitStatus);
	}
    }

    // 유닛의 명령을 String으로 표현한다. (Position이 없는 타입의 명령어)
    private static ActionDetail getActionDetail(String command, Unit allianceUnit, Unit enemyUnit) {
	return getActionDetail(command, allianceUnit, enemyUnit, null, 0);
    }

    // 유닛의 명령을 String으로 표현한다. (Destination Unit이 없는 타입의 명령어)
    private static ActionDetail getActionDetail(String command, Unit allianceUnit, Position position) {
	return getActionDetail(command, allianceUnit, null, position, 0);
    }

    // 유닛의 명령을 String으로 표현한다. (Destination Unit, Position이 없는 타입의 명령어)
    private static ActionDetail getActionDetail(String command, Unit allianceUnit) {
	return getActionDetail(command, allianceUnit, null, null, 0);
    }

    private static ActionDetail getActionDetail(String command, Unit allianceUnit, Position position, int margin) {
	return getActionDetail(command, allianceUnit, null, position, margin);
    }

    // 유닛의 명령을 ActionDetail 개체로 변환한다.
    private static ActionDetail getActionDetail(String command, Unit srcUnit, Unit destUnit, Position position, int margin) {
	return new ActionDetail(command, srcUnit, destUnit, position, margin);
    }

    // 유닛의 액션을 수행할지 말지 결정한다. 이전과 동일한 액션이 들어오면 skip할 수 있도록 false를 리턴한다.
    private static boolean isAcceptedAction(ActionDetail currentActionDetail, Unit allianceUnit, UnitManager allianceUnitManager) {
	boolean result = false;

	ActionDetail lastActionDetail = allianceUnitManager.getLastAction(allianceUnit);

	if (forceAttackUnitIdSet.contains(Integer.valueOf(allianceUnit.getID()))) {
	    Log.trace("Action Rejected: " + currentActionDetail);
	} else if (currentActionDetail.equals(lastActionDetail)) {
	    Log.trace("Action Rejected: " + currentActionDetail);
	} else {
	    result = true;
	    if (null != currentActionDetail.getCommand() && null != lastActionDetail && currentActionDetail.getCommand().equals(lastActionDetail.getCommand())
		    && null != currentActionDetail.getPosition() && null != lastActionDetail.getPosition() && 0 != currentActionDetail.getMargin()) {

		int diff = UnitUtil.getDistance(currentActionDetail.getPosition(), lastActionDetail.getPosition());

		// 동일한 명령어에 대해서 Position이 10Pixel 이하로 차이가 나면 무시한다. 
		if (diff <= currentActionDetail.getMargin() * currentActionDetail.getMargin()) {
		    result = false;
		    Log.trace("Action Rejected: " + currentActionDetail + "; Too close to last position: " + lastActionDetail.getPosition());
		}
	    }
	    if (false != result) {
		allianceUnitManager.updateLastAction(allianceUnit, currentActionDetail);
		result = true;
		Log.trace("Action Accepted: " + currentActionDetail);
	    }
	}

	return result;
    }
}
