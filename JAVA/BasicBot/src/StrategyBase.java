import bwapi.Unit;

public abstract class StrategyBase {
    protected GameStatus gameStatus = null;
    protected UnitManager allianceUnitManager = null;
    protected UnitManager enemyUnitManager = null;

    public abstract void initialBuildOrder();

    public void onStart(GameStatus gameStatus) {
	this.gameStatus = gameStatus;
	this.allianceUnitManager = gameStatus.getAllianceUnitManager();
	this.enemyUnitManager = gameStatus.getEnemyUnitManager();

	initialBuildOrder();
    }

    public void onFrame() {

    }

    public void onUnitComplete(Unit unit) {

    }

    public void onUnitDestroy(Unit unit) {

    }

    public void onUnitDiscover(Unit unit) {

    }

    public void onUnitEvade(Unit unit) {

    }
}
