import java.util.Set;

public abstract class StrategyBase {
    protected GameStatus gameStatus = null;
    protected UnitInfo allianceUnitInfo = null;
    protected UnitInfo enemyUnitInfo = null;
    protected String strategyName = "";

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

    public boolean hasStrategyItem(StrategyItem strategyItem) {
	return gameStatus.getStrategyManager().getStrategyItems().contains(strategyItem);
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

    public String getStrategyName() {
	return strategyName;
    }
}
