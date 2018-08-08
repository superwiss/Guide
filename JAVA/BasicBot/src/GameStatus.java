import java.util.List;

import bwapi.Color;
import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.Race;
import bwapi.TilePosition;
import bwapi.Unit;

public class GameStatus {

    // bwapi가 제공하는 게임 정보
    private Game game;

    private MagiConfig config = new MagiConfig();

    // 아군의 유닛 정보
    private UnitInfo allianceUnitInfo = new UnitInfo(this);

    // 적군의 유닛 정보
    private UnitInfo enemyUnitInfo = new UnitInfo(this);

    // Manager
    private LocationManager locationManager = null;
    private WorkerManager workerManager = null;
    private BuildManager buildManager = null;
    private ScoutManager scoutManager = null;
    private StrategyManager strategyManager = null;
    private MicroControlManager microControlManager = null;
    private EliminateManager eliminateManager = null;
    private TrainingManager trainingManager = null;
    private MagiUXManager uxManager = null;

    // /////////////////////////
    // Getter & Setter
    // /////////////////////////

    public Game getGame() {
	return game;
    }

    public void setGame(Game game) {
	this.game = game;
    }

    public UnitInfo getAllianceUnitInfo() {
	return allianceUnitInfo;
    }

    public UnitInfo getEnemyUnitInfo() {
	return enemyUnitInfo;
    }

    public MagiConfig getConfig() {
	return config;
    }

    public void setConfig(MagiConfig config) {
	this.config = config;
    }

    // 현재 프레임이 sec의 배수에 해당하는지 여부를 리턴한다.
    // sec초에 한 번씩 주기적으로 수행하는 기능을 구현할 때, 본 메서드를 이용한다.
    public boolean isMatchedInterval(int sec) {
	boolean result = false;

	if (0 != getFrameCount() && 0 == getFrameCount() % (42 * sec)) {
	    result = true;
	}

	return result;
    }

    // ////////////////////////////////////////////////////////////////////////
    // Getter & Setter for Manager
    // ////////////////////////////////////////////////////////////////////////

    public LocationManager getLocationManager() {
	return locationManager;
    }

    public void setLocationManager(LocationManager locationManager) {
	this.locationManager = locationManager;
    }

    public WorkerManager getWorkerManager() {
	return workerManager;
    }

    public void setWorkerManager(WorkerManager workerManager) {
	this.workerManager = workerManager;
    }

    public BuildManager getBuildManager() {
	return buildManager;
    }

    public void setBuildManager(BuildManager buildManager) {
	this.buildManager = buildManager;
    }

    public ScoutManager getScoutManager() {
	return scoutManager;
    }

    public void setScoutManager(ScoutManager scoutManager) {
	this.scoutManager = scoutManager;
    }

    public StrategyManager getStrategyManager() {
	return strategyManager;
    }

    public void setStrategyManager(StrategyManager strategyManager) {
	this.strategyManager = strategyManager;
    }

    public MicroControlManager getMicroControlManager() {
	return microControlManager;
    }

    public void setMicroControlManager(MicroControlManager microControlManager) {
	this.microControlManager = microControlManager;
    }

    public EliminateManager getEliminateManager() {
	return eliminateManager;
    }

    public void setEliminateManager(EliminateManager eliminateManager) {
	this.eliminateManager = eliminateManager;
    }

    public TrainingManager getTrainingManager() {
	return trainingManager;
    }

    public void setTrainingManager(TrainingManager trainingManager) {
	this.trainingManager = trainingManager;
    }

    public MagiUXManager getUxManager() {
	return uxManager;
    }

    public void setUxManager(MagiUXManager uxManager) {
	this.uxManager = uxManager;
    }

    // ////////////////////////////////////////////////////////////////////////
    // Game wrapper
    // ////////////////////////////////////////////////////////////////////////
    public int getFrameCount() {
	return game.getFrameCount();
    }

    public void leaveGame() {
	game.leaveGame();
    }

    public int getMineral() {
	return game.self().minerals();
    }

    public int getGas() {
	return game.self().gas();
    }

    public int getGatheredMinerals() {
	return game.self().gatheredMinerals();
    }

    public int getGatheredGas() {
	return game.self().gatheredGas();
    }

    // 보유 중인 서플라이 용량. (게임 상에는 8이라고 표시되지만, BWAPI에서는 2를 곱한 16으로 계산된다.)
    public int getSupplyTotal() {
	return game.self().supplyTotal();
    }

    // 사용 중인 서플라이 용량. (게임 상에는 8이라고 표시되지만, BWAPI에서는 2를 곱한 16으로 계산된다.)
    public int getSupplyUsed() {
	return game.self().supplyUsed();
    }

    // 여유가 있는 서플라이 용량. (게임 상에는 8이라고 표시되지만, BWAPI에서는 2를 곱한 16으로 계산된다.)
    public int getSupplyRemain() {
	return game.self().supplyTotal() - game.self().supplyUsed();
    }

    public boolean isExplored(TilePosition position) {
	boolean result = false;

	if (null != position) {
	    result = game.isExplored(position);
	}

	return result;
    }

    public boolean isVisible(TilePosition tilePosition) {
	return game.isVisible(tilePosition);
    }

    public String mapFileName() {
	return game.mapFileName();
    }

    public boolean isComputer() {
	boolean result = false;

	for (Player player : game.getPlayers()) {
	    String playerName = player.getName();
	    Log.debug("PlayerName: %s", playerName);
	    switch (playerName) {
	    case "Barelrog Brood":
	    case "Fenris Brood":
	    case "Garm Brood":
	    case "Grendal Brood":
	    case "Jormungand Brood":
	    case "Leviathan Brood":
	    case "Sutur Brood":
	    case "Tiamat Brood":
	    case "Antiga":
	    case "Atlas Wing":
	    case "Cronus Wing":
	    case "Delta Squadron":
	    case "Elite Guard":
	    case "Epsilon Squadron":
	    case "Kel-Morian Combine":
	    case "Mar Sara":
	    case "Akilae Tribe":
	    case "Ara Tribe":
	    case "Auriga Tribe":
	    case "Furinax Tribe":
	    case "Sargas Tribe":
	    case "Shelak Tribe":
	    case "Velari Tribe":
	    case "Venatir Tribe":
		result = true;
		break;
	    default:
		break;
	    }
	}

	return result;
    }

    public boolean isMatchPlayerByName(String name) {
	boolean result = false;

	for (Player player : game.getPlayers()) {
	    String playerName = player.getName();
	    Log.debug("PlayerName: %s", playerName);
	    if (true == player.isEnemy(game.self()) && true == playerName.toLowerCase().contains(name.toLowerCase())) {
		result = true;
		break;
	    }
	}

	return result;
    }

    public Race getEnemyRace() {
	Race result = null;

	for (Player player : game.getPlayers()) {
	    Race playerRace = player.getRace();
	    if (true == player.isEnemy(game.self())) {
		result = playerRace;
	    }
	}

	return result;
    }

    public List<Unit> getAllUnits() {
	return game.getAllUnits();
    }

    public void setScreen(Position position) {
	// 화면의 위치를 이동한다.
	// 화면 하단의 게임 인터페이스 공간을 고려해서, y 좌표는 64 pixel만큼 더해서 계산한다.
	game.setScreenPosition(position.getX() - 320, position.getY() - 240 + 64);

    }

    public void drawTextMap(Position position, String string) {
	game.drawTextMap(position, string);
    }

    public void drawCircleMap(int x, int y, int radius, Color color) {
	game.drawCircleMap(x, y, radius, color);
    }

    public void drawCircleMap(Unit2 unit, int radius, Color color) {
	drawCircleMap(unit, radius, color, false);
    }

    public void drawCircleMap(Unit2 unit, int radius, Color color, boolean isSolid) {
	game.drawCircleMap(unit.getPosition().getX(), unit.getPosition().getY(), radius, color, isSolid);
    }

    public Position getMousePosition() {
	return game.getMousePosition();
    }

    public Position getScreenPosition() {
	return game.getScreenPosition();
    }

    public void drawTextMap(int x, int y, String message) {
	game.drawTextMap(x, y, message);
    }

    public void drawLineMap(int x1, int y1, int x2, int y2, Color color) {
	game.drawLineMap(x1, y1, x2, y2, color);
    }

    public void drawLineMap(Unit2 unit1, TilePosition tilePosition, Color color) {
	if (null == unit1 || null == tilePosition) {
	    return;
	}
	Position p1 = unit1.getPosition();
	Position p2 = tilePosition.toPosition();
	drawLineMap(p1.getX(), p1.getY(), p2.getX(), p2.getY(), color);
    }

    public void sendText(String string) {
	game.sendText(string);
    }

    @Override
    public String toString() {
	String result = "";

	result = String.format("MyUnit: %s, EnemyUnit: %s", getAllianceUnitInfo().toString(), getEnemyUnitInfo().toString());

	return result;
    }

}