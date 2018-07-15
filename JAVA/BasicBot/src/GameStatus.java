import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;

public class GameStatus {

    // bwapi가 제공하는 게임 정보
    private Game game;

    // 아군의 유닛 정보
    private UnitManager myUnitManager = new UnitManager();

    // 적군의 유닛 정보
    private UnitManager enemyUnitManager = new UnitManager();

    // 공격 지점. 공격 지점이 null이면 유닛들은 대기한다.
    // 공격 지점이 설정되면, 유닛들은 해당 지점으로 Attack Position을 수행한다.
    private TilePosition attackTilePosition = null;

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

    public UnitManager getAllianceUnitManager() {
	return myUnitManager;
    }

    public UnitManager getEnemyUnitManager() {
	return enemyUnitManager;
    }

    public boolean hasAttackTilePosition() {
	return null != attackTilePosition ? true : false;
    }

    public TilePosition getAttackTilePositon() {
	return attackTilePosition;
    }

    public void setAttackTilePosition(TilePosition attackTilePosition) {
	Log.debug("공격 지점이 %s -> %s로 변경됨.", this.attackTilePosition, attackTilePosition);
	this.attackTilePosition = attackTilePosition;
    }

    public void clearAttackTilePosition() {
	Log.debug("공격 지점이 %s -> null 로 변경됨.", this.attackTilePosition);
	attackTilePosition = null;
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

    public int getSupplyRemain() {
	return game.self().supplyTotal() - game.self().supplyUsed();
    }

    public boolean isExplored(TilePosition position) {
	return game.isExplored(position);
    }

    public boolean isVisible(TilePosition tilePosition) {
	return game.isVisible(tilePosition);
    }

    public void setScreen(Position position) {
	// 화면의 위치를 이동한다.
	// 화면 하단의 게임 인터페이스 공간을 고려해서, y 좌표는 64 pixel만큼 더해서 계산한다.
	game.setScreenPosition(position.getX() - 320, position.getY() - 240 + 64);

    }

    @Override
    public String toString() {
	String result = "";

	result = String.format("MyUnit: %s, EnemyUnit: %s", getAllianceUnitManager().toString(), getEnemyUnitManager().toString());

	return result;
    }
}