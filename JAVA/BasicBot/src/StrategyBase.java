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

    public void onUnitComplete(Unit2 unit) {

    }

    public void onUnitDestroy(Unit2 unit) {

    }

    public void onUnitDiscover(Unit2 unit) {

    }

    public void onUnitEvade(Unit2 unit) {

    }
}
