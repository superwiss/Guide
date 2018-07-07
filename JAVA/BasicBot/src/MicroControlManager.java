import java.util.HashSet;
import java.util.Set;

import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

// MagiBot을 빠르게 연습시키기 위해서, 유즈맵으로 미션을 만들어 MagiBot이 미션을 해결하는 방식으로 훈련한다.
public class MicroControlManager {
    private static MicroControlManager instance = new MicroControlManager();

    public static MicroControlManager Instance() {
	return instance;
    }

    private MicroControlManager() {
	medicUnitTypeSet.add(UnitType.Terran_Medic);
    }

    private GameData gameData;
    private TilePosition attackTilePosition = null;
    private final Set<UnitType> medicUnitTypeSet = new HashSet<>();

    public void onFrame(GameData gameData) {
	this.gameData = gameData;
	UnitManager allianceUnitManager = gameData.getAllianceUnitManager();
	UnitManager enemyUnitManager = gameData.getEnemyUnitManager();

	medicControl(allianceUnitManager);

	Log.trace("Enemy Count: %d", enemyUnitManager.getUnitsIdByUnitKind(UnitKind.Combat_Unit).size());
	if (true) {
	    return;
	}
	// 적을 공격할 수 있는 아군 유닛을 대상으로 컨트롤을 한다.
	for (Integer allianceUnitId : allianceUnitManager.getUnitsIdByUnitKind(UnitKind.Combat_Unit)) {

	    Unit allianceUnit = allianceUnitManager.getUnit(allianceUnitId);

	    // 속도 테스트
	    /*
	    if (false == speedTest(gameData, allianceUnit)) {
	    return;
	    }
	    */

	    // 공격할 적 유닛을 선택한다.
	    Unit enemyUnit = UnitUtil.selectEnemyTargetUnit(allianceUnit, enemyUnitManager);
	    if (null == enemyUnit) {
		Log.trace("Alliance Unit(%d) -> Selected Enemy is null.", allianceUnitId);
		continue;
	    }

	    UnitUtil.loggingDetailUnitInfo(allianceUnit);
	    UnitUtil.loggingDetailUnitInfo(enemyUnit);
	    //UnitUtil.drawTargetPosition(enemyUnit);

	    Log.debug("Distance between alliance unit and enemy unit: %d", allianceUnit.getDistance(enemyUnit));

	    // 도망칠 때 집결 장소. 적군 유닛과 반대 방향으로 움직인다.
	    Position backPosition = UnitUtil.getBackPosition(allianceUnit.getPosition(), enemyUnit.getPosition());

	    // 게임 속도 제어
	    /*
	    if (false == speedControl(gameData, allianceUnit, enemyUnit)) {
	    return;
	    }
	    */

	    // 강제 공격을 처리한다.
	    if (ActionUtil.isAttackingForcibly(allianceUnit)) {
		if (UnitUtil.isAttackFinished(allianceUnit)) {
		    Log.debug(":::::::: 공격 완료");
		    ActionUtil.attackFinished(allianceUnit);
		} else if (allianceUnit.getDistance(enemyUnit) < 70) {
		    Log.debug(":::::::: 너무 가까워서 강제 공격 해제.");
		    ActionUtil.attackFinished(allianceUnit);
		} else {
		    Log.debug(":::::::: 강제 공격 중이다.");
		    ActionUtil.attackEnemyUnitForcibly(allianceUnitManager, allianceUnit, enemyUnit);
		    continue;
		}
	    }

	    // 적 유닛의 상태를 기반으로 아군 유닛에게 다음 행동에 대한 명령을 내린다.
	    EnemyUnitStatus unitCombatStatus = UnitUtil.getUnitCombatStatus(allianceUnit, enemyUnit);
	    switch (unitCombatStatus) {
	    case NEAR_MOVE:
		Log.debug(":::::::: 적이 내 근처로 Attack Move 중이다. 이전 동작을 계속하자.");
		break;
	    case SAME_DIR_CLOSE:
		Log.debug(":::::::: 적이 나와 같은 방향으로 이동 중이다. 거리가 가깝다. 도망가자.");
		ActionUtil.moveToPosition(allianceUnitManager, allianceUnit, backPosition, 100);
		break;
	    case SAME_DIR_MIDDLE:
		Log.debug(":::::::: 적이 나와 같은 방향으로 이동 중이다. 거리가 애매하다. Stop 하자");
		ActionUtil.stop(allianceUnitManager, allianceUnit);
		break;
	    case SAME_DIR_FAR:
		Log.debug(":::::::: 적이 나와 같은 방향으로 이동 중이다. 거리가 멀다. 공격하자.");
		ActionUtil.attackEnemyUnit(allianceUnitManager, allianceUnit, enemyUnit);
		break;
	    case DIFFERENCE_DIR_CLOSE:
		Log.debug(":::::::: 적이 나와 반대 방향으로 이동 중이다. 거리가 가깝다. 이전 동작을 계속하자.");
		break;
	    case DIFFERENCE_DIR_MIDDLE:
		Log.debug(":::::::: 적이 나와 반대 방향으로 이동 중이다. 거리가 애매하다. Stop 하자.");
		ActionUtil.stop(allianceUnitManager, allianceUnit);
		break;
	    case DIFFERENCE_DIR_FAR:
		Log.debug(":::::::: 적이 나와 반대 방향으로 이동 중이다. 거리가 멀다. 강제 공격하자.");
		ActionUtil.attackEnemyUnitForcibly(allianceUnitManager, allianceUnit, enemyUnit);
		break;
	    default:
		Log.error(":::::::: 추가 예외 처리가 필요한 상황.");
		break;
	    }
	}
    }

    // 메딕을 컨트롤 한다.
    private void medicControl(UnitManager allianceUnitManager) {
	// 메딕은 42프레임에 1번만 컨트롤 한다.
	if (gameData.getFrameCount() % 42 != 0) {
	    return;
	}
	Position newPosition = null;
	Set<Integer> bionicSet = null;
	if (true == hasAttackTilePosition()) {
	    Position attackPosition = attackTilePosition.toPosition();
	    bionicSet = allianceUnitManager.getUnitsIdByUnitKind(UnitKind.Bionic_Unit);
	    // 공격 목표 지점에서 가장 가까운 선두 바이오닉 유닛을 구한다.
	    Unit headBionicUnit = allianceUnitManager.getClosestUnit(bionicSet, attackPosition, medicUnitTypeSet);
	    if (null != headBionicUnit) {
		// 선두 바이오닉 유닛으로부터 공격 목표 지점 방향으로 +100 position 거리의 위치를 구한다.
		newPosition = UnitUtil.getPositionAsDistance(headBionicUnit.getPosition(), attackPosition, 100);
	    }

	    if (null == newPosition) {
		Log.warn("medicControl(): newPosition is null. Bionic Unit size = %d, headBionicUnitPosition=%s, attackTilePosition: %s", bionicSet.size(),
			null != headBionicUnit ? headBionicUnit.getPosition() : "null", attackTilePosition);
	    } else {
		Set<Integer> medicSet = allianceUnitManager.getUnitsIdByUnitKind(UnitKind.Terran_Medic);
		for (Integer medicId : medicSet) {
		    Unit medic = allianceUnitManager.getUnit(medicId);
		    boolean updated = ActionUtil.attackPosition(allianceUnitManager, medic, newPosition);
		    if (updated) {
			Log.debug("메딕(%d)을 %s 위치로 이동한다. headBionicUnitId=%d, headBionicUnit=%s", medicId, newPosition, headBionicUnit.getID(), headBionicUnit.getPosition());
		    }
		}
	    }
	}

    }

    // 필요한 프레임으로 빨리 이동하기 위해서 게임 속도를 제어한다. false를 리턴하면 frmae을 종료한다.
    private boolean speedControl(GameData gameData, Unit allianceUnit, Unit enemyUnit) {
	boolean result = true;

	Game game = gameData.getGame();
	UnitManager allianceUnitManager = gameData.getAllianceUnitManager();

	// 24프레임에 한 번씩 아군 유닛을 중심으로 화면을 이동한다.
	if (0 == gameData.getFrameCount() % 24) {
	    gameData.setScreen(allianceUnit.getPosition());
	}

	switch (game.getFrameCount()) {
	case 17:
	    game.setLocalSpeed(42);
	    ActionUtil.attackEnemyUnit(allianceUnitManager, allianceUnit, enemyUnit);
	    break;
	case 50:
	    //game.setLocalSpeed(20);
	    break;
	case 130:
	    //game.setLocalSpeed(2000);
	    break;
	default:
	    break;
	}

	return result;
    }

    public boolean hasAttackTilePosition() {
	boolean result = false;

	if (null != attackTilePosition) {
	    result = true;
	}

	return result;
    }

    public TilePosition getAttackTilePosition() {
	return attackTilePosition;
    }

    public void setAttackTilePosition(TilePosition attackTilePosition) {
	this.attackTilePosition = attackTilePosition;
    }

    // 유닛의 이동 거리를 측정하기 위한 테스트용 메서드
    /*
    private boolean speedTest(GameData gameData, Unit allianceUnit) {
    boolean result = false;
    
    Game game = gameData.getGame();
    UnitManager allianceUnitManager = gameData.getAllianceUnitManager();
    
    switch (game.getFrameCount()) {
    case 1:
        game.setLocalSpeed(42);
    case 17:
        ActionUtil.moveToPosition(allianceUnitManager, allianceUnit, new Position(1500, 2000));
        break;
    case 200:
        Log.error("============================================================= Start (%s)", allianceUnit.getPosition());
        ActionUtil.moveToPosition(allianceUnitManager, allianceUnit, new Position(2500, 2000));
        break;
    case 140:
        //game.setLocalSpeed(1000);
        break;
    default:
        break;
    }
    
    if (allianceUnit.getPosition().getX() == 2500 && allianceUnit.getPosition().getY() == 2000) {
        Log.error("============================================================= Finish (%s)", allianceUnit.getPosition());
        System.exit(0);
    }
    Log.error("Position: %s, speed: %f", allianceUnit.getPosition(), allianceUnit.getType().topSpeed());
    
    return result;
    
    }
    */
}