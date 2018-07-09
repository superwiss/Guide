import bwapi.Unit;

public abstract class Manager {
    protected GameStatus gameStatus = null;

    protected void onStart(GameStatus gameStatus) {
	this.gameStatus = gameStatus;
    }

    protected void onFrame() {

    }

    protected void onUnitCreate(Unit unit) {

    }

    protected void onUnitDestroy(Unit unit) {

    }

    protected void onUnitComplete(Unit unit) {

    }

    protected void onUnitDiscover(Unit unit) {

    }

    protected void onUnitEvade(Unit unit) {

    }

}
