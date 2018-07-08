
import java.util.Set;

import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;

/// 실제 봇프로그램의 본체가 되는 class<br>
/// 스타크래프트 경기 도중 발생하는 이벤트들이 적절하게 처리되도록 해당 Manager 객체에게 이벤트를 전달하는 관리자 Controller 역할을 합니다
public class GameCommander {
    private Game broodwar;
    private TrainingManager trainingManager = TrainingManager.Instance();
    private MagiStrategyManager strategymanager = MagiStrategyManager.Instance();
    private MicroControlManager microControlManager = MicroControlManager.Instance();
    private MagiWorkerManager workerManager = MagiWorkerManager.Instance();
    private MagiBuildManager buildManager = MagiBuildManager.Instance();
    private MagiScoutManager scoutManager = MagiScoutManager.Instance();
    private MagiEliminateManager eliminateManager = MagiEliminateManager.Instance();
    private GameData gameData;

    public GameCommander() {
	this.broodwar = MyBotModule.Broodwar;
    }

    /// 경기가 시작될 때 일회적으로 발생하는 이벤트를 처리합니다
    public void onStart() {
	gameData = new GameData(broodwar);
	Log.setLogLevel(Log.Level.WARN);
	ActionUtil.setGame(broodwar);
	Log.info("Game has started");
	trainingManager.onStart();
	strategymanager.onStart();
    }

    /// 경기가 종료될 때 일회적으로 발생하는 이벤트를 처리합니다
    public void onEnd(boolean isWinner) {
	Log.info("Game has finished");
    }

    /// 경기 진행 중 매 프레임마다 발생하는 이벤트를 처리합니다
    public void onFrame() {
	if (1 == gameData.getFrameCount()) {
	    LocationManager.Instance().init(gameData.getAllianceUnitManager().getFirstCommandCenter());
	}
	Log.info("\nonFrame() started");
	if (trainingManager.isTrainingMode()) {
	    if (MyBotModule.Broodwar.isPaused() || MyBotModule.Broodwar.self() == null || MyBotModule.Broodwar.self().isDefeated() || MyBotModule.Broodwar.self().leftGame()
		    || MyBotModule.Broodwar.enemy() == null || MyBotModule.Broodwar.enemy().isDefeated() || MyBotModule.Broodwar.enemy().leftGame()) {
		return;
	    }

	    microControlManager.onFrame(gameData);

	    if (true == trainingManager.isFinished()) {
		if (-1 == trainingManager.getExitFrame()) {
		    trainingManager.setExitFrame(gameData.getFrameCount() + 24);
		    trainingManager.printResult();
		    Log.setLogLevel(Log.Level.NONE);
		}
		if (gameData.getFrameCount() >= trainingManager.getExitFrame()) {
		    gameData.leaveGame();
		    System.exit(0);
		}
	    }
	} else {
	    workerManager.onFrame(gameData);
	    buildManager.onFrame(gameData);
	    scoutManager.onFrame(gameData);
	    strategymanager.onFrame(gameData);
	    microControlManager.onFrame(gameData);
	    eliminateManager.onFrame(gameData);
	}
    }

    /// 유닛(건물/지상유닛/공중유닛)이 Create 될 때 발생하는 이벤트를 처리합니다
    public void onUnitCreate(Unit unit) {
    }

    ///  유닛(건물/지상유닛/공중유닛)이 Destroy 될 때 발생하는 이벤트를 처리합니다
    public void onUnitDestroy(Unit unit) {
	try {
	    if (true == UnitUtil.isAllianceUnit(unit)) {
		scoutManager.onUnitDestroy(unit, gameData);
		gameData.getAllianceUnitManager().remove(unit);
	    } else if (true == UnitUtil.isEnemyUnit(unit)) {
		gameData.getEnemyUnitManager().remove(unit);
	    } else {
		// else 상황 = 즉 중립 건물, 중립 동물에 대해서는 아무런 처리도 하지 않는다.
	    }
	} catch (Exception e) {
	    Log.error("Exception: %s", e.toString());
	    e.printStackTrace();
	}

	Log.info("onUnitDestroy: %s", UnitUtil.toString(unit));
    }

    /// 유닛(건물/지상유닛/공중유닛)이 Morph 될 때 발생하는 이벤트를 처리합니다<br>
    /// Zerg 종족의 유닛은 건물 건설이나 지상유닛/공중유닛 생산에서 거의 대부분 Morph 형태로 진행됩니다
    public void onUnitMorph(Unit unit) {
    }

    /// 유닛(건물/지상유닛/공중유닛)의 소속 플레이어가 바뀔 때 발생하는 이벤트를 처리합니다<br>
    /// Gas Geyser에 어떤 플레이어가 Refinery 건물을 건설했을 때, Refinery 건물이 파괴되었을 때, Protoss 종족 Dark Archon 의 Mind Control 에 의해 소속 플레이어가 바뀔 때 발생합니다
    public void onUnitRenegade(Unit unit) {
	try {
	    if (true == UnitUtil.isAllianceUnit(unit)) {
		gameData.getAllianceUnitManager().add(unit);
	    } else if (true == UnitUtil.isEnemyUnit(unit)) {
		gameData.getEnemyUnitManager().add(unit);
	    } else {
		// else 상황 = 즉 중립 건물, 중립 동물에 대해서는 아무런 처리도 하지 않는다.
	    }
	    if (unit.getType().isMineralField()) {
		gameData.getAllianceUnitManager().add(unit);
	    }
	    MagiBuildManager.Instance().onUnitDiscover(unit, gameData);
	} catch (Exception e) {
	    Log.error("Exception: %s", e.toString());
	    e.printStackTrace();
	}

	Log.info("onUnitRenegade: %s", UnitUtil.toString(unit));
    }

    /// 유닛(건물/지상유닛/공중유닛)의 하던 일 (건물 건설, 업그레이드, 지상유닛 훈련 등)이 끝났을 때 발생하는 이벤트를 처리합니다
    public void onUnitComplete(Unit unit) {
	Log.info("onUnitComplete: %s", UnitUtil.toString(unit));
	if (unit.getPlayer() == MyBotModule.Broodwar.self()) {
	    if (unit.getType().isWorker()) {
		MagiWorkerManager.Instance().onUnitComplete(unit, gameData);
	    }
	    buildManager.onUnitComplete(unit, gameData);
	    strategymanager.onUnitComplete(unit, gameData);
	}
    }

    /// 유닛(건물/지상유닛/공중유닛)이 Discover 될 때 발생하는 이벤트를 처리합니다<br>
    /// 아군 유닛이 Create 되었을 때 라든가, 적군 유닛이 Discover 되었을 때 발생합니다
    public void onUnitDiscover(Unit unit) {
	try {
	    if (true == UnitUtil.isAllianceUnit(unit)) {
		gameData.getAllianceUnitManager().add(unit);
	    } else if (true == UnitUtil.isEnemyUnit(unit)) {
		gameData.getEnemyUnitManager().add(unit);
	    } else {
		// else 상황 = 즉 중립 건물, 중립 동물에 대해서는 아무런 처리도 하지 않는다.
	    }
	    if (unit.getType().isMineralField()) {
		gameData.getAllianceUnitManager().add(unit);
	    }
	    MagiBuildManager.Instance().onUnitDiscover(unit, gameData);
	} catch (Exception e) {
	    Log.error("Exception: %s", e.toString());
	    e.printStackTrace();
	}

	Log.info("onUnitDiscover: %s", UnitUtil.toString(unit));
    }

    /// 유닛(건물/지상유닛/공중유닛)이 Evade 될 때 발생하는 이벤트를 처리합니다<br>
    /// 유닛이 Destroy 될 때 발생합니다
    public void onUnitEvade(Unit unit) {
	Log.info("onUnitEvade: %s", UnitUtil.toString(unit));
	if (trainingManager.isTrainingMode()) {
	    trainingManager.onUnitEvade(unit);
	}
    }

    /// 유닛(건물/지상유닛/공중유닛)이 Show 될 때 발생하는 이벤트를 처리합니다<br>
    /// 아군 유닛이 Create 되었을 때 라든가, 적군 유닛이 Discover 되었을 때 발생합니다
    public void onUnitShow(Unit unit) {
	Log.info("onUnitShow(%s)", UnitUtil.toString(unit));
    }

    /// 유닛(건물/지상유닛/공중유닛)이 Hide 될 때 발생하는 이벤트를 처리합니다<br>
    /// 보이던 유닛이 Hide 될 때 발생합니다
    public void onUnitHide(Unit unit) {
	Log.info("onUnitHide(%s)", UnitUtil.toString(unit));
    }

    /// 핵미사일 발사가 감지되었을 때 발생하는 이벤트를 처리합니다
    public void onNukeDetect(Position target) {
    }

    /// 다른 플레이어가 대결을 나갔을 때 발생하는 이벤트를 처리합니다
    public void onPlayerLeft(Player player) {
    }

    /// 게임을 저장할 때 발생하는 이벤트를 처리합니다
    public void onSaveGame(String gameName) {
    }

    /// 텍스트를 입력 후 엔터를 하여 다른 플레이어들에게 텍스트를 전달하려 할 때 발생하는 이벤트를 처리합니다
    public void onSendText(String text) {
	try {
	    int number = Integer.parseInt(text);
	    Log.info("Set game speed to %d", number);
	    broodwar.setLocalSpeed(number);
	} catch (NumberFormatException e) {
	    switch (text) {
	    case "p":
	    case "pp":
	    case "ppp":
		// 일시 정지를 위해서 3초만 대기한다.
		Log.info("Set game speed to 3000");
		broodwar.setLocalSpeed(3000);
		break;
	    case "enemy":
		Log.info("[EnemyUnits] %s", gameData.getEnemyUnitManager().toString());
		break;
	    case "enemyBuilding":
		String msg = "";
		UnitManager enemyUnitManager = gameData.getEnemyUnitManager();
		Set<Integer> enemyBuildingIds = enemyUnitManager.getUnitsIdByUnitKind(UnitKind.Building);
		msg += String.format("enemy building size: %d\n", enemyBuildingIds.size());
		Set<Integer> mainBuildingIds = enemyUnitManager.getUnitsIdByUnitKind(UnitKind.MAIN_BUILDING);
		for (Integer enemyBuildingId : enemyBuildingIds) {
		    Unit enemyBuilding = enemyUnitManager.getUnit(enemyBuildingId);
		    msg += String.format("Building id=%d, TilePosition: %s, isVisible: %b, UnitType: %s, isMainBuilding: %b\n", enemyBuildingId,
			    enemyUnitManager.getLastTilePosition(enemyBuildingId), enemyBuilding.isVisible(), enemyBuilding.getType(), mainBuildingIds.contains(enemyBuildingId));
		}
		Log.warn(msg);
		break;
	    case "alliance":
		Log.info("[AllianceUnits] :%s", gameData.getAllianceUnitManager().toString());
		break;
	    default:
		// nothing
		break;
	    }
	}
    }

    /// 다른 플레이어로부터 텍스트를 전달받았을 때 발생하는 이벤트를 처리합니다
    public void onReceiveText(Player player, String text) {
    }
}