public class EventData {
    public static final int ON_START = 0;
    public static final int ON_FRAME = 1;
    public static final int ON_UNIT_COMPLETE = 2;
    public static final int ON_UNIT_DESTROY = 3;
    public static final int ON_UNIT_EVADE = 4;
    public static final int ON_UNIT_DISCOVER = 5;

    private int eventType;
    private GameStatus gameStatus;
    private Unit2 unit;

    public EventData(int eventType) {
	this.eventType = eventType;
    }

    public EventData(int eventType, GameStatus gameStatus) {
	this.eventType = eventType;
	this.gameStatus = gameStatus;
    }

    public EventData(int eventType, Unit2 unit) {
	this.eventType = eventType;
	this.unit = unit;
    }

    public int getEventType() {
	return eventType;
    }

    public GameStatus getGameStatus() {
	return gameStatus;
    }

    public Unit2 getUnit() {
	return unit;
    }
}
