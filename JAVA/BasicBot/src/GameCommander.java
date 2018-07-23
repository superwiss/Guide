import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;

import bwapi.Player;
import bwapi.Position;
import bwapi.UnitType;

/// 실제 봇프로그램의 본체가 되는 class<br>
/// 스타크래프트 경기 도중 발생하는 이벤트들이 적절하게 처리되도록 해당 Manager 객체에게 이벤트를 전달하는 관리자 Controller 역할을 합니다
public class GameCommander implements EventDispatcher {
    private GameStatus gameStatus;
    private Deque<EventHandler> eventHandlers = new LinkedList<>();

    public GameCommander() {
	gameStatus = new GameStatus();

	MagiConfig config = new MagiConfig();

	if (true == config.isReleaseMode()) {
	    // 로그 레벨 설정. 로그는 stdout으로 출력되는데, 로그 양이 많으면 속도가 느려져서 Timeout 발생할 수 있으니 주의
	    Log.setLogLevel(Log.Level.WARN);
	} else {
	    Log.setLogLevel(Log.Level.TRACE);
	}

	// GameStatus에 각종 Manager 등록
	gameStatus.setWorkerManager(new WorkerManager());
	gameStatus.setBuildManager(new BuildManager());
	gameStatus.setScoutManager(new ScoutManager());
	gameStatus.setStrategyManager(new StrategyManager());
	gameStatus.setMicroControlManager(new MicroControlManager());
	gameStatus.setEliminateManager(new EliminateManager());
	if (true != config.isReleaseMode()) {
	    gameStatus.setTrainingManager(new TrainingManager());
	    gameStatus.setUxManager(new MagiUXManager());
	}

	// Event Handler 등록
	eventHandlers.add(gameStatus.getWorkerManager());
	eventHandlers.add(gameStatus.getBuildManager());
	eventHandlers.add(gameStatus.getScoutManager());
	eventHandlers.add(gameStatus.getStrategyManager());
	eventHandlers.add(gameStatus.getMicroControlManager());
	eventHandlers.add(gameStatus.getEliminateManager());
	if (true != config.isReleaseMode()) {
	    eventHandlers.add(gameStatus.getTrainingManager());
	    eventHandlers.add(gameStatus.getUxManager());
	}
    }

    /// 경기가 시작될 때 일회적으로 발생하는 이벤트를 처리합니다
    public void onStart() {
	Log.info("Game has started");

	gameStatus.setGame(MyBotModule.Broodwar);

	initLocationManager();

	ActionUtil.setGame(gameStatus.getGame());

	try {
	    EventData eventData = new EventData(EventData.ON_START, gameStatus);
	    executeEventHandler(eventData);
	} catch (Exception e) {
	    e.printStackTrace();
	    Log.error("onStart() Exception: %s", e.toString());
	    throw e;
	}
    }

    // 맵 이름으로 맵 종류를 판단해서 locationManager 구현체를 선택한다.
    private void initLocationManager() {
	LocationManager locationManager = null;
	String mapFileName = gameStatus.getGame().mapFileName();
	if (mapFileName.contains("ircuit")) {
	    // 서킷 브레이커
//	    locationManager = new LocationManagerCircuitBreaker();
	    locationManager = new LocationManagerCircuitBreaker_Defense_Terran();
	    locationManager.setMapName("CircuitBreaker");
	} else if (mapFileName.contains("atch")) {
	    // 오버워치
	    locationManager = new LocationManagerOverWatch();
	    locationManager.setMapName("Overwatch");
	} else if (mapFileName.contains("pirit")) {
	    // 투혼
	    locationManager = new LocationManagerSprit();
	    locationManager.setMapName("Sprit");
	}
	gameStatus.setLocationManager(locationManager);
	eventHandlers.addFirst(locationManager);
    }

    /// 경기가 종료될 때 일회적으로 발생하는 이벤트를 처리합니다
    public void onEnd(boolean isWinner) {
	Log.info("Game has finished");
    }

    /// 경기 진행 중 매 프레임마다 발생하는 이벤트를 처리합니다
    public void onFrame() {
	Log.info("\nonFrame() started");

	if (MyBotModule.Broodwar.isPaused() || MyBotModule.Broodwar.self() == null || MyBotModule.Broodwar.self().isDefeated() || MyBotModule.Broodwar.self().leftGame()
		|| MyBotModule.Broodwar.enemy() == null || MyBotModule.Broodwar.enemy().isDefeated() || MyBotModule.Broodwar.enemy().leftGame()) {
	    return;
	}

	try {
	    EventData eventData = new EventData(EventData.ON_FRAME);
	    executeEventHandler(eventData);
	} catch (Exception e) {
	    Log.error("onFrame() Exception: %s", e.toString());
	    e.printStackTrace();
	    throw e;
	}
    }

    /// 유닛(건물/지상유닛/공중유닛)이 Create 될 때 발생하는 이벤트를 처리합니다
    public void onUnitCreate(bwapi.Unit rawUnit) {
    }

    ///  유닛(건물/지상유닛/공중유닛)이 Destroy 될 때 발생하는 이벤트를 처리합니다
    public void onUnitDestroy(bwapi.Unit rawUnit) {
	Unit2 unit = Unit2.get(rawUnit);
	Log.info("onUnitDestroy(%s)", UnitUtil.toString(unit));

	if (true == UnitUtil.isAllianceUnit(unit)) {
	    gameStatus.getAllianceUnitInfo().remove(unit);
	} else if (true == UnitUtil.isEnemyUnit(unit)) {
	    gameStatus.getEnemyUnitInfo().remove(unit);
	} else {
	    // else 상황 = 즉 중립 건물, 중립 동물에 대해서는 아무런 처리도 하지 않는다.
	}

	try {
	    EventData eventData = new EventData(EventData.ON_UNIT_DESTROY, unit);
	    executeEventHandler(eventData);
	} catch (Exception e) {
	    Log.error("onUnitDestroy() Exception: %s", e.toString());
	    e.printStackTrace();
	    throw e;
	}
    }

    /// 유닛(건물/지상유닛/공중유닛)이 Morph 될 때 발생하는 이벤트를 처리합니다<br>
    /// Zerg 종족의 유닛은 건물 건설이나 지상유닛/공중유닛 생산에서 거의 대부분 Morph 형태로 진행됩니다
    public void onUnitMorph(bwapi.Unit rawUnit) {
	Unit2 unit = Unit2.get(rawUnit);
	Log.info("onUnitMorph: %s", UnitUtil.toString(unit));
    }

    /// 유닛(건물/지상유닛/공중유닛)의 소속 플레이어가 바뀔 때 발생하는 이벤트를 처리합니다<br>
    /// Gas Geyser에 어떤 플레이어가 Refinery 건물을 건설했을 때, Refinery 건물이 파괴되었을 때, Protoss 종족 Dark Archon 의 Mind Control 에 의해 소속 플레이어가 바뀔 때 발생합니다
    public void onUnitRenegade(bwapi.Unit rawUnit) {
	Unit2 unit = Unit2.get(rawUnit);
	Log.info("onUnitRenegade(%s)", UnitUtil.toString(unit));

	try {
	    // 귀찮게도 가스 건물을 지을 때와 같은 상황에서는 onUnitDiscover가 호출되지 않고 onUnitRenegade가 호출된다.
	    // 각 메니져는 onUnitDiscover와 onUnitRenegade를 중복해서 구현하지 않고 onUnitDiscover만 구현한다.
	    if (unit.getType().equals(UnitType.Terran_Refinery) || unit.getType().equals(UnitType.Zerg_Extractor) || unit.getType().equals(UnitType.Protoss_Assimilator)) {
		if (true == UnitUtil.isAllianceUnit(unit)) {
		    gameStatus.getAllianceUnitInfo().add(unit);
		} else if (true == UnitUtil.isEnemyUnit(unit)) {
		    gameStatus.getEnemyUnitInfo().add(unit);
		}
		EventData eventData = new EventData(EventData.ON_UNIT_DISCOVER, unit);
		executeEventHandler(eventData);
	    }
	} catch (Exception e) {
	    Log.error("onUnitRenegade() Exception: %s", e.toString());
	    e.printStackTrace();
	    throw e;
	}
    }

    /// 유닛(건물/지상유닛/공중유닛)의 하던 일 (건물 건설, 업그레이드, 지상유닛 훈련 등)이 끝났을 때 발생하는 이벤트를 처리합니다
    public void onUnitComplete(bwapi.Unit rawUnit) {
	Unit2 unit = Unit2.get(rawUnit);
	Log.info("onUnitComplete(%s)", UnitUtil.toString(unit));

	try {
	    EventData eventData = new EventData(EventData.ON_UNIT_COMPLETE, unit);
	    executeEventHandler(eventData);
	} catch (Exception e) {
	    Log.error("onUnitComplete() Exception: %s", e.toString());
	    e.printStackTrace();
	    throw e;
	}
    }

    /// 유닛(건물/지상유닛/공중유닛)이 Discover 될 때 발생하는 이벤트를 처리합니다<br>
    /// 아군 유닛이 Create 되었을 때 라든가, 적군 유닛이 Discover 되었을 때 발생합니다
    public void onUnitDiscover(bwapi.Unit rawUnit) {
	Unit2 unit = Unit2.get(rawUnit);
	Log.info("onUnitDiscover(%s)", UnitUtil.toString(unit));

	if (true == UnitUtil.isAllianceUnit(unit)) {
	    gameStatus.getAllianceUnitInfo().add(unit);
	} else if (true == UnitUtil.isEnemyUnit(unit)) {
	    gameStatus.getEnemyUnitInfo().add(unit);
	} else {
	    if (unit.getType().isMineralField()) {
		gameStatus.getAllianceUnitInfo().add(unit);
	    }
	}

	try {
	    EventData eventData = new EventData(EventData.ON_UNIT_DISCOVER, unit);
	    executeEventHandler(eventData);
	} catch (Exception e) {
	    Log.error("onUnitDiscover() Exception: %s", e.toString());
	    e.printStackTrace();
	    throw e;
	}
    }

    /// 유닛(건물/지상유닛/공중유닛)이 Evade 될 때 발생하는 이벤트를 처리합니다<br>
    /// 유닛이 Destroy 될 때 발생합니다
    public void onUnitEvade(bwapi.Unit rawUnit) {
	Unit2 unit = Unit2.get(rawUnit);
	Log.info("onUnitEvade(%s)", UnitUtil.toString(unit));

	try {
	    EventData eventData = new EventData(EventData.ON_UNIT_EVADE, unit);
	    executeEventHandler(eventData);
	} catch (Exception e) {
	    Log.error("onUnitEvade() Exception: %s", e.toString());
	    e.printStackTrace();
	    throw e;
	}
    }

    /// 유닛(건물/지상유닛/공중유닛)이 Show 될 때 발생하는 이벤트를 처리합니다<br>
    /// 아군 유닛이 Create 되었을 때 라든가, 적군 유닛이 Discover 되었을 때 발생합니다
    public void onUnitShow(bwapi.Unit rawUnit) {
	Unit2 unit = Unit2.get(rawUnit);
	Log.info("onUnitShow(%s)", UnitUtil.toString(unit));
    }

    /// 유닛(건물/지상유닛/공중유닛)이 Hide 될 때 발생하는 이벤트를 처리합니다<br>
    /// 보이던 유닛이 Hide 될 때 발생합니다
    public void onUnitHide(bwapi.Unit rawUnit) {
	Unit2 unit = Unit2.get(rawUnit);
	Log.info("onUnitHide(%s)", UnitUtil.toString(unit));
    }

    /// 핵미사일 발사가 감지되었을 때 발생하는 이벤트를 처리합니다
    public void onNukeDetect(Position target) {
	Log.info("onNukeDetect(%s)", target);
    }

    /// 다른 플레이어가 대결을 나갔을 때 발생하는 이벤트를 처리합니다
    public void onPlayerLeft(Player player) {
	Log.info("onPlayerLeft(%s)", player.getName());
    }

    /// 게임을 저장할 때 발생하는 이벤트를 처리합니다
    public void onSaveGame(String gameName) {
	Log.info("onSaveGame(%s)", gameName);
    }

    /// 텍스트를 입력 후 엔터를 하여 다른 플레이어들에게 텍스트를 전달하려 할 때 발생하는 이벤트를 처리합니다
    public void onSendText(String text) {
	Log.input("Input text: %s", text);
	boolean statusMode = false;
	if (text.startsWith("status")) {
	    text = text.substring(7);
	    statusMode = true;
	}
	try {
	    int number = Integer.parseInt(text);
	    if (false == statusMode) {
		Log.input("Set game speed to %d", number);
		gameStatus.getGame().setLocalSpeed(number);
	    } else {
		Unit2 unit = gameStatus.getAllianceUnitInfo().getUnit(number);
		if (null == unit) {
		    unit = gameStatus.getEnemyUnitInfo().getUnit(number);
		}
		Log.input("status %d", number);
		UnitUtil.loggingDetailUnitInfo(unit);
	    }
	} catch (NumberFormatException e) {
	    switch (text) {
	    case "p":
	    case "pp":
	    case "ppp":
		// 일시 정지를 위해서 3초만 대기한다.
		Log.input("Set game speed to 3000");
		gameStatus.getGame().setLocalSpeed(3000);
		break;
	    case "enemy":
		Log.input("[EnemyUnits] %s", gameStatus.getEnemyUnitInfo().toString());
		break;
	    case "enemyBuilding":
		// 적군 빌딩 정보를 로그에 출력한다.
		String msg = "";
		UnitInfo enemyUnitInfo = gameStatus.getEnemyUnitInfo();
		Set<Unit2> enemyBuildingIds = enemyUnitInfo.getUnitSet(UnitKind.Building);
		msg += String.format("enemy building size: %d\n", enemyBuildingIds.size());
		Set<Unit2> mainBuildingSet = enemyUnitInfo.getUnitSet(UnitKind.MAIN_BUILDING);
		for (Unit2 enemyBuilding : enemyBuildingIds) {
		    msg += String.format("Building=%s, TilePosition=%s, isVisible=%b, UnitType=%s, isMainBuilding=%b\n", enemyBuilding,
			    enemyUnitInfo.getLastTilePosition(enemyBuilding), enemyBuilding.isVisible(), enemyBuilding.getType(), mainBuildingSet.contains(enemyBuilding));
		}
		Log.input(msg);
		break;
	    case "alliance":
		Log.input("[AllianceUnits] :%s", gameStatus.getAllianceUnitInfo().toString());
		break;
	    case "getAttackPosition":
		Log.info("Attack Position: %s", gameStatus.getStrategyManager().getAttackTilePositon());
		break;
	    default:
		Log.input("Input text has ignored.");
		break;
	    }
	}
    }

    /// 다른 플레이어로부터 텍스트를 전달받았을 때 발생하는 이벤트를 처리합니다
    public void onReceiveText(Player player, String text) {
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
}