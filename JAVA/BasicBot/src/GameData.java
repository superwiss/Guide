
import bwapi.Game;
import bwapi.Unit;

public class GameData {
    private static Game game = MyBotModule.Broodwar;
    private UnitManager myUnitManager = new UnitManager();
    private UnitManager enemyUnitManager = new UnitManager();

    public void addUnit(Unit unit) {
	boolean isBuilding = unit.getType().isBuilding();
	if (game.self().isEnemy(unit.getPlayer())) {
	    // 상대방 유닛일 경우
	    if (!isBuilding) {
		getEnemyUnitManager().add(unit);
	    }
	} else {
	    // 내 유닛일 경우
	    if (!isBuilding) {
		getMyUnitManager().add(unit);
	    }
	}
    }

    public void removeUnit(Unit unit) {
	boolean isBuilding = unit.getType().isBuilding();
	if (game.self().isEnemy(unit.getPlayer())) {
	    // 상대방 유닛일 경우
	    if (!isBuilding) {
		Log.info("\tEnemy Unit has terminated", UnitUtil.getUnitAsString(unit));
		getEnemyUnitManager().remove(unit);
	    }
	} else {
	    // 내 유닛일 경우
	    if (!isBuilding) {
		Log.info("\tMy Unit has terminated", UnitUtil.getUnitAsString(unit));
		getMyUnitManager().remove(unit);
	    }
	}
    }

    // /////////////////////////
    // Getter & Setter
    // /////////////////////////

    public UnitManager getMyUnitManager() {
	return myUnitManager;
    }

    public void setMyUnitManager(UnitManager myUnitManager) {
	this.myUnitManager = myUnitManager;
    }

    public UnitManager getEnemyUnitManager() {
	return enemyUnitManager;
    }

    public void setEnemyUnitManager(UnitManager enemyUnitManager) {
	this.enemyUnitManager = enemyUnitManager;
    }

    @Override
    public String toString() {
	String result = "";

	result = String.format("MyUnit: %s, EnemyUnit: %s", getMyUnitManager().toString(), getEnemyUnitManager().toString());

	return result;
    }
}