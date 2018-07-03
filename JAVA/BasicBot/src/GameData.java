import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;

public class GameData {

    private Game broodwar;
    private UnitManager myUnitManager = new UnitManager();
    private UnitManager enemyUnitManager = new UnitManager();

    // /////////////////////////
    // Getter & Setter
    // /////////////////////////

    public GameData(Game broodwar) {
	this.broodwar = broodwar;
    }

    public UnitManager getAllianceUnitManager() {
	return myUnitManager;
    }

    public UnitManager getEnemyUnitManager() {
	return enemyUnitManager;
    }

    public int getFrameCount() {
	return broodwar.getFrameCount();
    }

    public Game getGame() {
	return broodwar;
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

    public void setScreen(Position position) {
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