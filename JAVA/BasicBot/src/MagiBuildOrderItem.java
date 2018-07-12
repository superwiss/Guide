import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class MagiBuildOrderItem {
    public enum Order {
	INITIAL_BUILDORDER_FINISH, TRAINING, BUILD, SCOUTING, GATHER_GAS, MOVE_SCV, ADD_ON
    }

    private Order order;
    private UnitType targetUnitType;
    private Unit worker;
    private TilePosition tilePosition;
    private boolean inProgress = false;

    public MagiBuildOrderItem(Order order) {
	this.order = order;
    }

    public MagiBuildOrderItem(Order order, UnitType targetUnitType) {
	this.order = order;
	this.targetUnitType = targetUnitType;

    }

    public MagiBuildOrderItem(Order order, TilePosition tilePosition) {
	this.order = order;
	this.tilePosition = tilePosition;
    }

    public Order getOrder() {
	return order;
    }

    public UnitType getTargetUnitType() {
	return targetUnitType;
    }

    public Unit getWorker() {
	return worker;
    }

    public void setWorker(Unit worker) {
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
