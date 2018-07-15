import bwapi.Unit;

public abstract class Manager implements EventHandler {
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

    protected void onUnitComplete(Unit unit) {

    }

    protected void onUnitDestroy(Unit unit) {

    }

    protected void onUnitDiscover(Unit unit) {

    }

    protected void onUnitEvade(Unit unit) {

    }

    @Override
    public void onEvent(EventData event) {
	switch (event.getEventType()) {
	case EventData.ON_START:
	    onStart(event.getGameStatus());
	    break;
	case EventData.ON_FRAME:
	    onFrame();
	    break;
	case EventData.ON_UNIT_COMPLETE:
	    onUnitComplete(event.getUnit());
	    break;
	case EventData.ON_UNIT_DESTROY:
	    onUnitDestroy(event.getUnit());
	    break;
	case EventData.ON_UNIT_DISCOVER:
	    onUnitDiscover(event.getUnit());
	    break;
	case EventData.ON_UNIT_EVADE:
	    onUnitEvade(event.getUnit());
	    break;
	default:
	    break;
	}
    }
}
