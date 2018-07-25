import bwapi.TilePosition;
import bwapi.UnitType;

public class BuildOrderItem {
    public enum Order {
	INITIAL_BUILDORDER_FINISH, TRAINING, BUILD, SCOUTING, GATHER_GAS, MOVE_SCV, ADD_ON
    }

    private Order order;
    private UnitType targetUnitType;
    private Unit2 worker;
    private Unit2 targetUnit;
    private TilePosition tilePosition;
    private boolean inProgress = false;

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

    @Override
    public String toString() {
	return "MagiBuildItem[Order=" + order.toString() + ",targetUnitType=" + targetUnitType + ",worker=" + (null != worker ? worker.getID() : "null") + ",inProgress="
		+ String.valueOf(inProgress) + ",tilePosition=" + (null != tilePosition ? tilePosition.toString() : "null") + "]";
    }
}
