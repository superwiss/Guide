import bwapi.Game;

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

    public Game getGame() {
	return broodwar;
    }

    @Override
    public String toString() {
	String result = "";

	result = String.format("MyUnit: %s, EnemyUnit: %s", getAllianceUnitManager().toString(), getEnemyUnitManager().toString());

	return result;
    }
}