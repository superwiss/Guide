import java.util.ArrayList;
import java.util.List;

import bwapi.TilePosition;
import bwapi.Unit;

public class CircuitBreakerLocationManager extends Manager implements ILocation {

    private static CircuitBreakerLocationManager instance = new CircuitBreakerLocationManager();

    public static CircuitBreakerLocationManager Instance() {
	return instance;
    }

    public enum ClockLocation {
	ONE, FIVE, SEVEN, ELEVEN
    }

    private static final TilePosition ONE_TILE_POSITION = new TilePosition(117, 9);
    private static final TilePosition FIVE_TILE_POSITION = new TilePosition(117, 118);
    private static final TilePosition SEVEN_TILE_POSITION = new TilePosition(7, 118);
    private static final TilePosition ELEVEN_TILE_POSITION = new TilePosition(7, 9);
    private ClockLocation allianceStartLocation;
    private TilePosition enemyStartTilePosition = null;
    private TilePosition allianceStartTilePosition = null;

    @Override
    protected void onFrame() {
	super.onFrame();

	if (1 == gameStatus.getFrameCount()) {
	    init(gameStatus.getAllianceUnitManager().getFirstCommandCenter());
	}
    }

    // 최초 커맨드센터의 위치를 기반으로 건물 심시티를 결정한다. 
    @Override
    public void init(Unit commandCenter) {
	if (commandCenter.getTilePosition().equals(ONE_TILE_POSITION)) {
	    allianceStartLocation = ClockLocation.ONE;
	    allianceStartTilePosition = ONE_TILE_POSITION;
	} else if (commandCenter.getTilePosition().equals(FIVE_TILE_POSITION)) {
	    allianceStartLocation = ClockLocation.FIVE;
	    allianceStartTilePosition = FIVE_TILE_POSITION;
	} else if (commandCenter.getTilePosition().equals(SEVEN_TILE_POSITION)) {
	    allianceStartLocation = ClockLocation.SEVEN;
	    allianceStartTilePosition = SEVEN_TILE_POSITION;
	} else if (commandCenter.getTilePosition().equals(ELEVEN_TILE_POSITION)) {
	    allianceStartLocation = ClockLocation.ELEVEN;
	    allianceStartTilePosition = ELEVEN_TILE_POSITION;
	}
    }

    @Override
    public List<TilePosition> getBarracks() {
	// 가로 4, 세로 3
	List<TilePosition> result = new ArrayList<>();

	switch (allianceStartLocation) {
	case ONE:
	    result.add(new TilePosition(118, 23));
	    result.add(new TilePosition(118, 19));
	    result.add(new TilePosition(118, 15));
	    result.add(new TilePosition(111, 19));
	    result.add(new TilePosition(111, 15));
	    result.add(new TilePosition(111, 11));
	    result.add(new TilePosition(111, 7));
	    result.add(new TilePosition(105, 7));
	    result.add(new TilePosition(105, 11));
	    result.add(new TilePosition(105, 15));
	    break;
	case FIVE:
	    result.add(new TilePosition(118, 102));
	    result.add(new TilePosition(118, 106));
	    result.add(new TilePosition(118, 110));
	    result.add(new TilePosition(111, 106));
	    result.add(new TilePosition(111, 110));
	    result.add(new TilePosition(111, 114));
	    result.add(new TilePosition(105, 110));
	    result.add(new TilePosition(105, 114));
	    result.add(new TilePosition(105, 118));
	    result.add(new TilePosition(111, 118));
	    break;
	case SEVEN:
	    result.add(new TilePosition(0, 101));
	    result.add(new TilePosition(0, 105));
	    result.add(new TilePosition(4, 106));
	    result.add(new TilePosition(7, 105));
	    result.add(new TilePosition(7, 109));
	    result.add(new TilePosition(13, 105));
	    result.add(new TilePosition(13, 109));
	    result.add(new TilePosition(13, 113));
	    result.add(new TilePosition(12, 117));
	    result.add(new TilePosition(12, 121));
	    break;
	case ELEVEN:
	    result.add(new TilePosition(0, 24));
	    result.add(new TilePosition(0, 20));
	    result.add(new TilePosition(0, 16));
	    result.add(new TilePosition(7, 16));
	    result.add(new TilePosition(7, 20));
	    result.add(new TilePosition(13, 20));
	    result.add(new TilePosition(14, 16));
	    result.add(new TilePosition(8, 12));
	    result.add(new TilePosition(14, 12));
	    result.add(new TilePosition(14, 8));
	    break;
	default:
	    break;
	}

	return result;
    }

    @Override
    public List<TilePosition> getBunker() {
	// 가로 4, 세로 3
	List<TilePosition> result = new ArrayList<>();

	switch (allianceStartLocation) {
	case ONE:
	    result.add(new TilePosition(122, 25));
	    break;
	case FIVE:
	    result.add(new TilePosition(122, 102));
	    break;
	case SEVEN:
	    result.add(new TilePosition(4, 102));
	    break;
	case ELEVEN:
	    result.add(new TilePosition(4, 24));
	    break;
	default:
	    break;
	}

	return result;
    }

    @Override
    public List<TilePosition> getSupplyDepot() {
	// 가로 3, 세로 2
	List<TilePosition> result = new ArrayList<>();

	switch (allianceStartLocation) {
	case ONE:
	    result.add(new TilePosition(125, 25));
	    result.add(new TilePosition(125, 23));
	    result.add(new TilePosition(125, 21));
	    result.add(new TilePosition(125, 19));
	    result.add(new TilePosition(125, 17));
	    result.add(new TilePosition(125, 0));
	    result.add(new TilePosition(125, 2));
	    result.add(new TilePosition(125, 4));
	    result.add(new TilePosition(122, 0));
	    result.add(new TilePosition(122, 2));
	    result.add(new TilePosition(122, 4));
	    result.add(new TilePosition(119, 0));
	    result.add(new TilePosition(119, 2));
	    result.add(new TilePosition(116, 0));
	    result.add(new TilePosition(116, 2));
	    result.add(new TilePosition(113, 0));
	    result.add(new TilePosition(113, 2));
	    result.add(new TilePosition(110, 0));
	    result.add(new TilePosition(110, 2));
	    result.add(new TilePosition(107, 0));
	    result.add(new TilePosition(107, 2));
	    result.add(new TilePosition(107, 4));
	    result.add(new TilePosition(104, 0));
	    result.add(new TilePosition(104, 2));
	    result.add(new TilePosition(104, 4));
	    result.add(new TilePosition(101, 0));
	    result.add(new TilePosition(101, 2));
	    break;
	case FIVE:
	    result.add(new TilePosition(125, 102));
	    result.add(new TilePosition(125, 104));
	    result.add(new TilePosition(125, 106));
	    result.add(new TilePosition(125, 108));
	    result.add(new TilePosition(125, 110));
	    result.add(new TilePosition(125, 112));
	    result.add(new TilePosition(96, 125));
	    result.add(new TilePosition(99, 125));
	    result.add(new TilePosition(99, 123));
	    result.add(new TilePosition(102, 125));
	    result.add(new TilePosition(102, 123));
	    result.add(new TilePosition(105, 125));
	    result.add(new TilePosition(105, 123));
	    result.add(new TilePosition(108, 125));
	    result.add(new TilePosition(108, 123));
	    result.add(new TilePosition(111, 125));
	    result.add(new TilePosition(111, 123));
	    result.add(new TilePosition(114, 125));
	    result.add(new TilePosition(114, 123));
	    result.add(new TilePosition(117, 125));
	    result.add(new TilePosition(117, 123));
	    result.add(new TilePosition(120, 125));
	    result.add(new TilePosition(120, 123));
	    result.add(new TilePosition(117, 121));
	    result.add(new TilePosition(114, 125));
	    result.add(new TilePosition(111, 17));
	    result.add(new TilePosition(114, 17));
	    break;
	case SEVEN:
	    result.add(new TilePosition(7, 102));
	    result.add(new TilePosition(8, 125));
	    result.add(new TilePosition(11, 125));
	    result.add(new TilePosition(14, 125));
	    result.add(new TilePosition(17, 125));
	    result.add(new TilePosition(20, 125));
	    result.add(new TilePosition(23, 125));
	    result.add(new TilePosition(26, 125));
	    result.add(new TilePosition(29, 125));
	    result.add(new TilePosition(29, 123));
	    result.add(new TilePosition(26, 123));
	    result.add(new TilePosition(23, 123));
	    result.add(new TilePosition(20, 123));
	    result.add(new TilePosition(24, 121));
	    result.add(new TilePosition(21, 121));
	    result.add(new TilePosition(21, 119));
	    result.add(new TilePosition(1, 115));
	    result.add(new TilePosition(4, 115));
	    result.add(new TilePosition(4, 113));
	    result.add(new TilePosition(1, 113));
	    result.add(new TilePosition(20, 117));
	    result.add(new TilePosition(20, 115));
	    result.add(new TilePosition(20, 113));
	    result.add(new TilePosition(20, 111));
	    result.add(new TilePosition(13, 103));
	    result.add(new TilePosition(8, 121));
	    result.add(new TilePosition(8, 123));
	    break;
	case ELEVEN:
	    result.add(new TilePosition(7, 24));
	    result.add(new TilePosition(0, 0));
	    result.add(new TilePosition(0, 2));
	    result.add(new TilePosition(0, 4));
	    result.add(new TilePosition(3, 0));
	    result.add(new TilePosition(3, 2));
	    result.add(new TilePosition(3, 4));
	    result.add(new TilePosition(6, 0));
	    result.add(new TilePosition(6, 2));
	    result.add(new TilePosition(9, 0));
	    result.add(new TilePosition(9, 2));
	    result.add(new TilePosition(12, 0));
	    result.add(new TilePosition(12, 2));
	    result.add(new TilePosition(11, 4));
	    result.add(new TilePosition(15, 0));
	    result.add(new TilePosition(15, 2));
	    result.add(new TilePosition(14, 4));
	    result.add(new TilePosition(18, 0));
	    result.add(new TilePosition(18, 2));
	    result.add(new TilePosition(17, 4));
	    result.add(new TilePosition(21, 0));
	    result.add(new TilePosition(21, 2));
	    result.add(new TilePosition(20, 4));
	    result.add(new TilePosition(24, 0));
	    result.add(new TilePosition(24, 2));
	    result.add(new TilePosition(23, 4));
	    result.add(new TilePosition(20, 6));
	    break;
	default:
	    break;
	}

	return result;
    }

    @Override
    public List<TilePosition> getRefinery() {
	List<TilePosition> result = new ArrayList<>();

	switch (allianceStartLocation) {
	case ONE:
	    result.add(new TilePosition(116, 4));
	    break;
	case FIVE:
	    result.add(new TilePosition(117, 113));
	    break;
	case SEVEN:
	    result.add(new TilePosition(7, 113));
	    break;
	case ELEVEN:
	    result.add(new TilePosition(7, 4));
	    break;
	default:
	    break;
	}

	return result;
    }

    @Override
    public List<TilePosition> getTurret() {
	List<TilePosition> result = new ArrayList<>();

	switch (allianceStartLocation) {
	case ONE:
	    //result.add(new TilePosition(105, 17));
	    //result.add(new TilePosition(119, 10));
	    break;
	case FIVE:
	    //result.add(new TilePosition(113, 109));
	    //result.add(new TilePosition(119, 120));
	    break;
	case SEVEN:
	    //result.add(new TilePosition(23, 108));
	    //result.add(new TilePosition(7, 120));
	    break;
	case ELEVEN:
	    //result.add(new TilePosition(20, 20));
	    //result.add(new TilePosition(8, 10));
	    break;
	default:
	    break;
	}

	return result;
    }

    @Override
    public TilePosition getChokePoint1() {
	TilePosition result = null;

	switch (allianceStartLocation) {
	case ONE:
	    result = new TilePosition(122, 26);
	    break;
	case FIVE:
	    result = new TilePosition(121, 100);
	    break;
	case SEVEN:
	    result = new TilePosition(5, 102);
	    break;
	case ELEVEN:
	    result = new TilePosition(4, 25);
	    break;
	default:
	    break;
	}

	return result;
    }

    @Override
    public TilePosition getChokePoint2() {
	TilePosition result = null;

	switch (allianceStartLocation) {
	case ONE:
	    result = new TilePosition(109, 34);
	    break;
	case FIVE:
	    result = new TilePosition(109, 93);
	    break;
	case SEVEN:
	    result = new TilePosition(18, 93);
	    break;
	case ELEVEN:
	    result = new TilePosition(18, 34);
	    break;
	default:
	    break;
	}

	return result;
    }

    // 몇시 방향인지 리턴한다.
    @Override
    public TilePosition getAllianceStartTilePosition() {
	return allianceStartTilePosition;
    }

    // 정찰 순서를 리턴한다.
    @Override
    public List<TilePosition> getSearchList() {
	List<TilePosition> result = new ArrayList<>(4);

	switch (allianceStartLocation) {
	case ONE:
	    // 5시, 11시, 7시 순서로 정찰
	    result.add(FIVE_TILE_POSITION);
	    result.add(ELEVEN_TILE_POSITION);
	    result.add(SEVEN_TILE_POSITION);
	    break;
	case FIVE:
	    // 7시, 1시, 11시 순서로 정찰
	    result.add(SEVEN_TILE_POSITION);
	    result.add(ONE_TILE_POSITION);
	    result.add(ELEVEN_TILE_POSITION);
	    break;
	case SEVEN:
	    // 11시, 5시, 1시 순서로 정찰
	    result.add(ELEVEN_TILE_POSITION);
	    result.add(FIVE_TILE_POSITION);
	    result.add(ONE_TILE_POSITION);
	    break;
	case ELEVEN:
	    // 1시, 7시, 5시 순서로 정찰 
	    result.add(ONE_TILE_POSITION);
	    result.add(SEVEN_TILE_POSITION);
	    result.add(FIVE_TILE_POSITION);
	    break;
	default:
	    break;
	}

	return result;
    }

    @Override
    public TilePosition getEnemyStartTilePosition() {
	return enemyStartTilePosition;
    }

    @Override
    public void setEnemyStartLocation(TilePosition enemyStartTilePosition) {
	this.enemyStartTilePosition = enemyStartTilePosition;
    }
}