import bwapi.Position;
import bwapi.Unit;

// 유닛의 Command(Action)에 대한 유틸리티
public class ActionUtil {

    public static void patrolToEnemyUnit(UnitManager allianceUnitManager, Unit allianceUnit, Unit enemyUnit) {
	String currnetCommand = getCommand("PATROL_TO_UNIT", allianceUnit, enemyUnit);

	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitManager)) {
	    allianceUnit.patrol(enemyUnit.getPosition());
	}
    }

    public static void moveToPosition(UnitManager allianceUnitManager, Unit allianceUnit, Position position) {
	String currnetCommand = getCommand("MOVE_TO_POSITION", allianceUnit, position);

	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitManager)) {
	    allianceUnit.move(position);
	}
    }

    // TODO 상대가 움직이면 position이 바뀌면서 매 프레임마다 move 명령이 내려지는 상황이 발생하는데, 큰 이슈 없는지 확인하기 
    public static void moveToUnit(UnitManager allianceUnitManager, Unit allianceUnit, Unit enemyUnit) {
	String currnetCommand = getCommand("MOVE_TO_UNIT", allianceUnit, enemyUnit.getPoint());

	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitManager)) {
	    allianceUnit.move(enemyUnit.getPoint());
	}
    }

    public static void attackEnemyUnit(UnitManager allianceUnitManager, Unit allianceUnit, Unit enemyUnit) {
	String currnetCommand = getCommand("ATTACK_TO_UNIT", allianceUnit, enemyUnit);

	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitManager)) {
	    allianceUnit.attack(enemyUnit);
	}
    }

    public static void stop(UnitManager allianceUnitManager, Unit allianceUnit) {
	String currnetCommand = getCommand("STOP", allianceUnit);

	if (isAcceptedAction(currnetCommand, allianceUnit, allianceUnitManager)) {
	    allianceUnit.stop();
	}
    }

    // 상대방 유닛을 향해 회전한다.
    public static void turn(UnitManager allianceUnitManager, Unit allianceUnit, Unit enemyUnit) {
	String currnetCommand = getCommand("TURN", allianceUnit, enemyUnit);

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
    private static String getCommand(String command, Unit allianceUnit, Unit enemyUnit) {
	return getCommand(command, allianceUnit, enemyUnit, null);
    }

    // 유닛의 명령을 String으로 표현한다. (Destination Unit이 없는 타입의 명령어)
    private static String getCommand(String command, Unit allianceUnit, Position position) {
	return getCommand(command, allianceUnit, null, position);
    }

    // 유닛의 명령을 String으로 표현한다. (Destination Unit, Position이 없는 타입의 명령어)
    private static String getCommand(String command, Unit allianceUnit) {
	return getCommand(command, allianceUnit, null, null);
    }

    // 유닛의 명령을 String으로 표현한다.
    private static String getCommand(String command, Unit srcUnit, Unit destUnit, Position position) {
	String result = "";

	String strDestUnit = (null == destUnit ? "null" : String.valueOf(destUnit.getID()));
	String strPosition = (null == position ? "null" : position.toString());

	result += "[" + command;
	result += "] (" + srcUnit.getID();
	result += "->" + strDestUnit;
	result += "," + strPosition;
	result += ")";

	return result;
    }

    // 유닛의 액션을 수행할지 말지 결정한다. 이전과 동일한 액션이 들어오면 skip할 수 있도록 false를 리턴한다.
    private static boolean isAcceptedAction(String currnetCommand, Unit unit, UnitManager allianceUnitManager) {
	boolean result = false;

	String lastCommand = allianceUnitManager.getLastAction(unit);

	if (currnetCommand.equals(lastCommand)) {
	    Log.trace("Action Rejected: " + currnetCommand);
	} else {
	    allianceUnitManager.updateLastAction(unit, currnetCommand);
	    result = true;
	    Log.trace("Action Accepted: " + currnetCommand);
	}

	return result;
    }
}
