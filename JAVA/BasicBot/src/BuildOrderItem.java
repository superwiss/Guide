import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class BuildOrderItem {
    public enum Order {
	INITIAL_BUILDORDER_FINISH, TRAINING, BUILD, SCOUTING, GATHER_GAS, MOVE_SCV, ADD_ON, UPGRADE, SET_STRATEGY_ITEM, CLEAR_STRATEGY_ITEM
    }

    private Order order;
    private UnitType targetUnitType;
    private Unit2 worker;
    private TilePosition tilePosition;
    private UpgradeType upgradeType;
    private TechType techType;
    private boolean inProgress = false;
    private StrategyItem strategyItem;

    public BuildOrderItem(Order order) {
	this.order = order;
    }

    public BuildOrderItem(Order order, UnitType targetUnitType) {
	this.order = order;
	this.targetUnitType = targetUnitType;

    }

    public BuildOrderItem(Order order, TilePosition tilePosition) {
	this.order = order;
	this.tilePosition = tilePosition;
    }

    public BuildOrderItem(Order order, UpgradeType upgradeType) {
	this.order = order;
	this.upgradeType = upgradeType;
    }

    public BuildOrderItem(Order order, TechType techType) {
	this.order = order;
	this.techType = techType;
    }

    public BuildOrderItem(Order order, StrategyItem strategyItem) {
	this.order = order;
	this.strategyItem = strategyItem;
    }

    public Order getOrder() {
	return order;
    }

    public UnitType getTargetUnitType() {
	return targetUnitType;
    }

    public Unit2 getWorker() {
	return worker;
    }

    public void setWorker(Unit2 worker) {
	this.worker = worker;
    }

    public TilePosition getTilePosition() {
	return tilePosition;
    }

    public void setTilePosition(TilePosition tilePosition) {
	this.tilePosition = tilePosition;
    }

    public boolean isInProgress() {
	return inProgress;
    }

    public void setInProgress(boolean inProgress) {
	this.inProgress = inProgress;
    }

    public TechType getTechType() {
	return techType;
    }

    public UpgradeType getUpgradeType() {
	return upgradeType;
    }

    public StrategyItem getStrategyItem() {
	return strategyItem;
    }

    @Override
    public String toString() {
	return "MagiBuildItem[Order=" + order.toString() + ", targetUnitType=" + targetUnitType + ", worker=" + worker + ", inProgress=" + inProgress + ", tilePosition=" + tilePosition
		+ ", upgradeType=" + upgradeType + ", techType=" + techType + ", strategyItem=" + strategyItem + "]";
    }
}
