public abstract class Manager implements EventHandler {
    protected GameStatus gameStatus = null;
    protected UnitInfo allianceUnitInfo = null;
    protected UnitInfo enemyUnitInfo = null;

    protected void onStart(GameStatus gameStatus) {
	this.gameStatus = gameStatus;
	this.allianceUnitInfo = gameStatus.getAllianceUnitInfo();
	this.enemyUnitInfo = gameStatus.getEnemyUnitInfo();
    }

    protected void onFrame() {

    }

    protected void onUnitComplete(Unit2 unit) {

    }

    protected void onUnitDestroy(Unit2 unit) {

    }

    protected void onUnitDiscover(Unit2 unit) {

    }

    protected void onUnitEvade(Unit2 unit) {

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
