import java.util.LinkedList;
import java.util.List;

import bwapi.Game;
import bwapi.Position;

// MagiBot을 빠르게 연습시키기 위해서, 유즈맵으로 미션을 만들어 MagiBot이 미션을 해결하는 방식으로 훈련한다.
public class MicroControlManager extends Manager implements EventDispatcher {
    private List<EventHandler> eventHandlers = new LinkedList<>();

    public MicroControlManager() {
	eventHandlers.add(new MicroControlWorker());
	eventHandlers.add(new MicroControlMarine());
	eventHandlers.add(new MicroControlMadic());
    }

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);

	EventData eventData = new EventData(EventData.ON_START, gameStatus);
	executeEventHandler(eventData);
    }

    @Override
    public void onFrame() {
	super.onFrame();

	EventData eventData = new EventData(EventData.ON_FRAME);
	executeEventHandler(eventData);

	if (true) {
	    return;
	}
	Log.trace("Enemy Count: %d", enemyUnitManager.getUnitSet(UnitKind.Combat_Unit).size());
	UnitManager allianceUnitManager = gameStatus.getAllianceUnitManager();
	UnitManager enemyUnitManager = gameStatus.getEnemyUnitManager();

	// 적을 공격할 수 있는 아군 유닛을 대상으로 컨트롤을 한다.
	for (Unit2 allianceUnit : allianceUnitManager.getUnitSet(UnitKind.Combat_Unit)) {

	    // 속도 테스트
	    /*
	    if (false == speedTest(gameStatus, allianceUnit)) {
	    return;
	    }
	    */

	    // 공격할 적 유닛을 선택한다.
	    Unit2 enemyUnit = UnitUtil.selectEnemyTargetUnit(allianceUnit, enemyUnitManager);
	    if (null == enemyUnit) {
		Log.trace("Alliance Unit(%s) -> Selected Enemy is null.", allianceUnit);
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
	    if (false == speedControl(gameStatus, allianceUnit, enemyUnit)) {
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

    @Override
    protected void onUnitComplete(Unit2 unit) {
	super.onUnitComplete(unit);

	EventData eventData = new EventData(EventData.ON_UNIT_COMPLETE, unit);
	executeEventHandler(eventData);
    }

    @Override
    protected void onUnitDestroy(Unit2 unit) {
	super.onUnitDestroy(unit);

	EventData eventData = new EventData(EventData.ON_UNIT_DESTROY, unit);
	executeEventHandler(eventData);
    }

    @Override
    protected void onUnitDiscover(Unit2 unit) {
	super.onUnitDiscover(unit);

	EventData eventData = new EventData(EventData.ON_UNIT_DISCOVER, unit);
	executeEventHandler(eventData);
    }

    @Override
    protected void onUnitEvade(Unit2 unit) {
	super.onUnitEvade(unit);

	EventData eventData = new EventData(EventData.ON_UNIT_EVADE, unit);
	executeEventHandler(eventData);
    }

    // 필요한 프레임으로 빨리 이동하기 위해서 게임 속도를 제어한다. false를 리턴하면 frame을 종료한다.
    private boolean speedControl(GameStatus gameStatus, Unit2 allianceUnit, Unit2 enemyUnit) {
	boolean result = true;

	Game game = gameStatus.getGame();
	UnitManager allianceUnitManager = gameStatus.getAllianceUnitManager();

	// 24프레임에 한 번씩 아군 유닛을 중심으로 화면을 이동한다.
	if (0 == gameStatus.getFrameCount() % 24) {
	    gameStatus.setScreen(allianceUnit.getPosition());
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

    @Override
    public void addEventHandler(EventHandler eventHandler) {
	eventHandlers.add(eventHandler);
    }

    @Override
    public void removeEventHandler(EventHandler eventHandler) {
	eventHandlers.remove(eventHandler);
    }

    @Override
    public void executeEventHandler(EventData event) {
	for (EventHandler eventHandler : eventHandlers) {
	    eventHandler.onEvent(event);
	}
    }

    // 유닛의 이동 거리를 측정하기 위한 테스트용 메서드
    /*
    private boolean speedTest(gameStatus gameStatus, Unit allianceUnit) {
    boolean result = false;
    
    Game game = gameStatus.getGame();
    UnitManager allianceUnitManager = gameStatus.getAllianceUnitManager();
    
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