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
    private Unit2 targetUnit;
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

    public BuildOrderItem(Order order, UnitType targetUnitType, TilePosition tilePosition) {
	this.order = order;
	this.targetUnitType = targetUnitType;
	this.tilePosition = tilePosition;

    }

    public BuildOrderItem(Order order, TilePosition tilePosition) {
	this.order = order;
	this.tilePosition = tilePosition;
    }

    public BuildOrderItem(Order order, Unit2 targetUnit) {
	this.order = order;
	this.targetUnit = targetUnit;
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

    public Unit2 getTargetUnit() {
	return targetUnit;
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
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (inProgress ? 1231 : 1237);
	result = prime * result + ((order == null) ? 0 : order.hashCode());
	result = prime * result + ((strategyItem == null) ? 0 : strategyItem.hashCode());
	result = prime * result + ((targetUnitType == null) ? 0 : targetUnitType.hashCode());
	result = prime * result + ((techType == null) ? 0 : techType.hashCode());
	result = prime * result + ((tilePosition == null) ? 0 : tilePosition.hashCode());
	result = prime * result + ((upgradeType == null) ? 0 : upgradeType.hashCode());
	result = prime * result + ((worker == null) ? 0 : worker.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	BuildOrderItem other = (BuildOrderItem) obj;
	if (inProgress != other.inProgress)
	    return false;
	if (order != other.order)
	    return false;
	if (strategyItem != other.strategyItem)
	    return false;
	if (targetUnitType == null) {
	    if (other.targetUnitType != null)
		return false;
	} else if (!targetUnitType.equals(other.targetUnitType))
	    return false;
	if (techType == null) {
	    if (other.techType != null)
		return false;
	} else if (!techType.equals(other.techType))
	    return false;
	if (tilePosition == null) {
	    if (other.tilePosition != null)
		return false;
	} else if (!tilePosition.equals(other.tilePosition))
	    return false;
	if (upgradeType == null) {
	    if (other.upgradeType != null)
		return false;
	} else if (!upgradeType.equals(other.upgradeType))
	    return false;
	if (worker == null) {
	    if (other.worker != null)
		return false;
	} else if (!worker.equals(other.worker))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "MagiBuildItem[Order=" + order.toString() + ", targetUnitType=" + targetUnitType + ", worker=" + worker + ", inProgress=" + inProgress + ", tilePosition="
		+ tilePosition + ", upgradeType=" + upgradeType + ", techType=" + techType + ", strategyItem=" + strategyItem + "]";
    }
}
