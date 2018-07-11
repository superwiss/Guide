import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;

public class GameStatus {

    // bwapi가 제공하는 게임 정보
    private Game broodwar;

    // 아군의 유닛 정보
    private UnitManager myUnitManager = new UnitManager();

    // 적군의 유닛 정보
    private UnitManager enemyUnitManager = new UnitManager();

    // 공격 지점. 공격 지점이 null이면 유닛들은 대기한다.
    // 공격 지점이 설정되면, 유닛들은 해당 지점으로 Attack Position을 수행한다.
    private TilePosition attackTilePosition = null;

    // 맵 정보 구현체
    private LocationManager locationManager = null;

    public GameStatus(Game broodwar) {
	this.broodwar = broodwar;
    }

    // /////////////////////////
    // Getter & Setter
    // /////////////////////////

    public UnitManager getAllianceUnitManager() {
	return myUnitManager;
    }

    public UnitManager getEnemyUnitManager() {
	return enemyUnitManager;
    }

    public void setLocationManager(LocationManager locationManager) {
	this.locationManager = locationManager;
    }

    public LocationManager getLocationManager() {
	return locationManager;
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
    // Game wrapper
    // ////////////////////////////////////////////////////////////////////////
    public Game getGame() {
	return broodwar;
    }

    public int getFrameCount() {
	return broodwar.getFrameCount();
    }

    public void leaveGame() {
	broodwar.leaveGame();
    }

    public int getMineral() {
	return broodwar.self().minerals();
    }

    public int getSupplyRemain() {
	return broodwar.self().supplyTotal() - broodwar.self().supplyUsed();
    }

    public boolean isExplored(TilePosition position) {
	return broodwar.isExplored(position);
    }

    public boolean isVisible(TilePosition tilePosition) {
	return broodwar.isVisible(tilePosition);
    }

    public void setScreen(Position position) {
	// 화면의 위치를 이동한다.
	// 화면 하단의 게임 인터페이스 공간을 고려해서, y 좌표는 64 pixel만큼 더해서 계산한다.
	broodwar.setScreenPosition(position.getX() - 320, position.getY() - 240 + 64);

    }

    @Override
    public String toString() {
	String result = "";

	result = String.format("MyUnit: %s, EnemyUnit: %s", getAllianceUnitManager().toString(), getEnemyUnitManager().toString());

	return result;
    }
}