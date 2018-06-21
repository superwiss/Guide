import java.util.HashMap;
import java.util.Map;

import bwapi.Game;
import bwapi.Position;
import bwapi.Unit;

// MagiBot을 빠르게 연습시키기 위해서, 유즈맵으로 미션을 만들어 MagiBot이 미션을 해결하는 방식으로 훈련한다.
public class MicroControlManager {
    private static MicroControlManager instance = new MicroControlManager();

    public static MicroControlManager Instance() {
	return instance;
    }

    private Map<StateTableKey, UnitStatus> stateTable = new HashMap<>();

    private MicroControlManager() {

	stateTable.put(new StateTableKey(UnitUtil.DistanceType.CLOSE, true, true), UnitStatus.RUNAWAY_FROM_ENEMY);
	stateTable.put(new StateTableKey(UnitUtil.DistanceType.NEAR_IN, true, true), UnitStatus.ATTACK_ENEMY);
	stateTable.put(new StateTableKey(UnitUtil.DistanceType.NEAR_OUT, true, true), UnitStatus.WAIT_ENEMY);
	stateTable.put(new StateTableKey(UnitUtil.DistanceType.FAR, true, true), UnitStatus.MOVE_TO_ENEMY);

	stateTable.put(new StateTableKey(UnitUtil.DistanceType.CLOSE, true, false), UnitStatus.WAIT_ENEMY);
	stateTable.put(new StateTableKey(UnitUtil.DistanceType.NEAR_IN, true, false), UnitStatus.ATTACK_ENEMY);
	stateTable.put(new StateTableKey(UnitUtil.DistanceType.NEAR_OUT, true, false), UnitStatus.MOVE_TO_ENEMY);
	stateTable.put(new StateTableKey(UnitUtil.DistanceType.FAR, true, false), UnitStatus.MOVE_TO_ENEMY);

	stateTable.put(new StateTableKey(UnitUtil.DistanceType.CLOSE, false, true), UnitStatus.RUNAWAY_FROM_ENEMY);
	stateTable.put(new StateTableKey(UnitUtil.DistanceType.NEAR_IN, false, true), UnitStatus.RUNAWAY_FROM_ENEMY);
	stateTable.put(new StateTableKey(UnitUtil.DistanceType.NEAR_OUT, false, true), UnitStatus.TURN_TO_ENEMY);
	stateTable.put(new StateTableKey(UnitUtil.DistanceType.FAR, false, true), UnitStatus.MOVE_TO_ENEMY);

	stateTable.put(new StateTableKey(UnitUtil.DistanceType.CLOSE, false, false), UnitStatus.RUNAWAY_FROM_ENEMY);
	stateTable.put(new StateTableKey(UnitUtil.DistanceType.NEAR_IN, false, false), UnitStatus.ATTACK_ENEMY);
	stateTable.put(new StateTableKey(UnitUtil.DistanceType.NEAR_OUT, false, false), UnitStatus.TURN_TO_ENEMY);
	stateTable.put(new StateTableKey(UnitUtil.DistanceType.FAR, false, false), UnitStatus.TURN_TO_ENEMY);
    }

    public void onFrame(GameData gameData) {
	UnitManager allianceUnitManager = gameData.getAllianceUnitManager();
	UnitManager enemyUnitManager = gameData.getEnemyUnitManager();

	// 적을 공격할 수 있는 아군 유닛을 대상으로 컨트롤을 한다.
	for (Integer allianceUnitId : allianceUnitManager.getAttackableUnitList()) {

	    Unit allianceUnit = allianceUnitManager.getUnit(allianceUnitId);

	    // 공격할 적 유닛을 선택한다.
	    Unit enemyUnit = UnitUtil.selectEnemyTargetUnit(allianceUnit, enemyUnitManager);
	    if (null == enemyUnit) {
		return;
	    }

	    UnitUtil.loggingDetailUnitInfo(allianceUnit);
	    UnitUtil.loggingDetailUnitInfo(enemyUnit);
	    UnitUtil.drawTargetPosition(enemyUnit);

	    // 도망칠 때 집결 장소. ex) first chockpoint
	    Position backPosition = new Position(0, 0);

	    // 아군과 적의 거리 종류
	    UnitUtil.DistanceType distanceType = UnitUtil.getDistanceType(allianceUnit, enemyUnit);
	    // 아군 유닛이 적 유닛을 바라보고 있는가?
	    boolean allianceDirection = UnitUtil.isBaseUnitLookingAnotherUnit(allianceUnit, enemyUnit);
	    // 적 유닛이 아군 유닛을 바라보고 있는가?
	    boolean enemyDirection = UnitUtil.isBaseUnitLookingAnotherUnit(enemyUnit, allianceUnit);

	    // 게임 속도 제어
	    if (false == speedControl(gameData, allianceUnit, enemyUnit)) {
		return;
	    }

	    int weaponCooldown = allianceUnit.getGroundWeaponCooldown();
	    StateTableKey stateTableKey = new StateTableKey(distanceType, allianceDirection, enemyDirection);
	    UnitStatus action = stateTable.get(stateTableKey);
	    Log.debug("stateTableKey: %s, weaponCooldown: %d, action: %s", stateTableKey, weaponCooldown, action);
	    switch (action) {
	    case MOVE_TO_ENEMY:
		Log.debug(":::::::: 적에게 이동");
		ActionUtil.moveToUnit(allianceUnitManager, allianceUnit, enemyUnit);
		break;
	    case WAIT_ENEMY:
		Log.debug(":::::::: 대기한다.");
		ActionUtil.stop(allianceUnitManager, allianceUnit);
		break;
	    case RUNAWAY_FROM_ENEMY:
		Log.debug(":::::::: 도망");
		ActionUtil.moveToPosition(allianceUnitManager, allianceUnit, backPosition);
		break;
	    case TURN_TO_ENEMY:
		Log.debug(":::::::: 적을 바라본다");
		ActionUtil.turn(allianceUnitManager, allianceUnit, enemyUnit);
		break;
	    case ATTACK_ENEMY:
		Log.debug(":::::::: 적을 공격한다.");
		ActionUtil.attackEnemyUnit(allianceUnitManager, allianceUnit, enemyUnit);
		break;
	    default:
		break;
	    }
	    break;
	}
    }

    // 필요한 프레임으로 빨리 이동하기 위해서 게임 속도를 제어한다. false를 리턴하면 frmae을 종료한다.
    private boolean speedControl(GameData gameData, Unit allianceUnit, Unit enemyUnit) {
	boolean result = true;

	Game game = gameData.getGame();

	switch (game.getFrameCount()) {
	case 17:
	    game.setLocalSpeed(42);
	    break;
	case 50:
	    //game.setLocalSpeed(20);
	    break;
	case 140:
	    //game.setLocalSpeed(1000);
	    break;
	default:
	    break;
	}

	return result;
    }
}