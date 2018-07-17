import java.util.Set;

public abstract class StrategyBase {
    protected GameStatus gameStatus = null;
    protected UnitInfo allianceUnitInfo = null;
    protected UnitInfo enemyUnitInfo = null;

    public abstract void initialBuildOrder();

    public abstract void initialStrategyItem(Set<StrategyItem> strategyItems);

    public void onStart(GameStatus gameStatus) {
	this.gameStatus = gameStatus;
	this.allianceUnitInfo = gameStatus.getAllianceUnitInfo();
	this.enemyUnitInfo = gameStatus.getEnemyUnitInfo();

	Set<StrategyItem> strategyItems = gameStatus.getStrategyManager().getStrategyItems();

	initialBuildOrder();
	initialStrategyItem(strategyItems);
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
