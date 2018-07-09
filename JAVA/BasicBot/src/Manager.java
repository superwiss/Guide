import bwapi.Unit;

public abstract class Manager {
    protected GameStatus gameStatus = null;
    protected UnitManager allianceUnitManager = null;
    protected UnitManager enemyUnitManager = null;

    protected void onStart(GameStatus gameStatus) {
	this.gameStatus = gameStatus;
	this.allianceUnitManager = gameStatus.getAllianceUnitManager();
	this.enemyUnitManager = gameStatus.getEnemyUnitManager();
    }

    protected void onFrame() {

    }

    protected void onUnitCreate(Unit unit) {

    }

    protected void onUnitDestroy(Unit unit) {

    }

    protected void onUnitComplete(Unit unit) {

    }

    protected void onUnitDiscover(Unit unit) {

    }

    protected void onUnitEvade(Unit unit) {

    }

}
