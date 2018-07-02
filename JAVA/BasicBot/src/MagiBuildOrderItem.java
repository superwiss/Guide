import bwapi.UnitType;

public class MagiBuildOrderItem {
    public enum Order {
	TRAINING_WORKER, BUILD, TRAINING_MARINE
    }

    private Order order;
    private UnitType targetUnitType;
    private boolean inProgress = false;

    public MagiBuildOrderItem(Order order) {
	this.order = order;
    }

    public MagiBuildOrderItem(Order order, UnitType targetUnitType) {
	this.order = order;
	this.targetUnitType = targetUnitType;

    }

    public Order getOrder() {
	return order;
    }

    public UnitType getTargetUnitType() {
	return targetUnitType;
    }

    public boolean isInProgress() {
	return inProgress;
    }

    public void setInProgress(boolean inProgress) {
	this.inProgress = inProgress;
    }

    @Override
    public String toString() {
	return "MagiBuildItem[Order=" + order.toString() + ",targetUnitType=" + targetUnitType + "]";
    }

}
