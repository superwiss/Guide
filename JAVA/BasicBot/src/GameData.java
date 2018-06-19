
import bwapi.Game;

public class GameData {
    private static Game game = MyBotModule.Broodwar;
    private UnitManager myUnitManager = new UnitManager();
    private UnitManager enemyUnitManager = new UnitManager();

    // /////////////////////////
    // Getter & Setter
    // /////////////////////////

    public UnitManager getAllianceUnitManager() {
	return myUnitManager;
    }

    public UnitManager getEnemyUnitManager() {
	return enemyUnitManager;
    }

    @Override
    public String toString() {
	String result = "";

	result = String.format("MyUnit: %s, EnemyUnit: %s", getAllianceUnitManager().toString(), getEnemyUnitManager().toString());

	return result;
    }
}