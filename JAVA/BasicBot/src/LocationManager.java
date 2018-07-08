import java.util.ArrayList;
import java.util.List;

import bwapi.TilePosition;
import bwapi.Unit;

public class LocationManager {

    private static LocationManager instance = new LocationManager();

    public static LocationManager Instance() {
	return instance;
    }

    public enum ClockLocation {
	ONE, FIVE, SEVEN, ELEVEN
    }

    private static final TilePosition ONE_TILE_POSITION = new TilePosition(117, 7);
    private static final TilePosition FIVE_TILE_POSITION = new TilePosition(117, 117);
    private static final TilePosition SEVEN_TILE_POSITION = new TilePosition(7, 117);
    private static final TilePosition ELEVEN_TILE_POSITION = new TilePosition(7, 7);
    private ClockLocation allianceStartLocation;
    private TilePosition enemyStartLocation = null;

    // 최초 커맨드센터의 위치를 기반으로 건물 심시티를 결정한다. 
    public void init(Unit commandCenter) {
	if (commandCenter.getTilePosition().equals(ONE_TILE_POSITION)) {
	    allianceStartLocation = ClockLocation.ONE;
	} else if (commandCenter.getTilePosition().equals(FIVE_TILE_POSITION)) {
	    allianceStartLocation = ClockLocation.FIVE;
	} else if (commandCenter.getTilePosition().equals(SEVEN_TILE_POSITION)) {
	    allianceStartLocation = ClockLocation.SEVEN;
	} else if (commandCenter.getTilePosition().equals(ELEVEN_TILE_POSITION)) {
	    allianceStartLocation = ClockLocation.ELEVEN;
	}
    }

    public List<TilePosition> getBarracks() {
	// 가로 4, 세로 3
	List<TilePosition> result = new ArrayList<>();

	switch (allianceStartLocation) {
	case ONE:
	    result.add(new TilePosition(108, 14));
	    result.add(new TilePosition(108, 18));
	    result.add(new TilePosition(114, 18));
	    result.add(new TilePosition(120, 18));
	    result.add(new TilePosition(120, 14));
	    result.add(new TilePosition(108, 22));
	    result.add(new TilePosition(114, 22));
	    result.add(new TilePosition(120, 22));
	    result.add(new TilePosition(114, 26));
	    result.add(new TilePosition(120, 26));
	    result.add(new TilePosition(120, 30));
	    break;
	case FIVE:
	    result.add(new TilePosition(108, 106));
	    result.add(new TilePosition(102, 106));
	    result.add(new TilePosition(107, 110));
	    result.add(new TilePosition(100, 110));
	    result.add(new TilePosition(107, 114));
	    result.add(new TilePosition(100, 114));
	    result.add(new TilePosition(107, 118));
	    result.add(new TilePosition(100, 118));
	    result.add(new TilePosition(94, 118));
	    result.add(new TilePosition(107, 122));
	    result.add(new TilePosition(100, 122));
	    break;
	case SEVEN:
	    result.add(new TilePosition(19, 108));
	    result.add(new TilePosition(12, 108));
	    result.add(new TilePosition(6, 108));
	    result.add(new TilePosition(18, 104));
	    result.add(new TilePosition(11, 104));
	    result.add(new TilePosition(5, 104));
	    result.add(new TilePosition(14, 100));
	    result.add(new TilePosition(7, 100));
	    result.add(new TilePosition(1, 100));
	    result.add(new TilePosition(14, 96));
	    result.add(new TilePosition(7, 96));
	    break;
	case ELEVEN:
	    result.add(new TilePosition(16, 19));
	    result.add(new TilePosition(16, 15));
	    result.add(new TilePosition(22, 15));
	    result.add(new TilePosition(16, 11));
	    result.add(new TilePosition(22, 11));
	    result.add(new TilePosition(16, 7));
	    result.add(new TilePosition(22, 7));
	    result.add(new TilePosition(28, 7));
	    result.add(new TilePosition(16, 3));
	    result.add(new TilePosition(22, 3));
	    result.add(new TilePosition(28, 3));
	    break;
	default:
	    break;
	}

	return result;
    }

    public List<TilePosition> getBunker() {
	// 가로 4, 세로 3
	List<TilePosition> result = new ArrayList<>();

	switch (allianceStartLocation) {
	case ONE:
	    result.add(new TilePosition(105, 15));
	    break;
	case FIVE:
	    result.add(new TilePosition(112, 107));
	    break;
	case SEVEN:
	    result.add(new TilePosition(18, 111));
	    break;
	case ELEVEN:
	    result.add(new TilePosition(13, 19));
	    break;
	default:
	    break;
	}

	return result;
    }

    public List<TilePosition> getSupplyDepot() {
	// 가로 3, 세로 2
	List<TilePosition> result = new ArrayList<>();

	switch (allianceStartLocation) {
	case ONE:
	    result.add(new TilePosition(110, 12));
	    result.add(new TilePosition(102, 16));
	    result.add(new TilePosition(117, 13));
	    result.add(new TilePosition(117, 15));
	    result.add(new TilePosition(112, 10));
	    result.add(new TilePosition(110, 5));
	    result.add(new TilePosition(113, 5));
	    result.add(new TilePosition(113, 7));
	    result.add(new TilePosition(125, 2));
	    result.add(new TilePosition(125, 0));
	    result.add(new TilePosition(122, 2));
	    result.add(new TilePosition(122, 0));
	    result.add(new TilePosition(119, 0));
	    result.add(new TilePosition(116, 0));
	    result.add(new TilePosition(113, 0));
	    result.add(new TilePosition(114, 2));
	    result.add(new TilePosition(110, 0));
	    result.add(new TilePosition(111, 2));
	    result.add(new TilePosition(107, 0));
	    result.add(new TilePosition(108, 2));
	    result.add(new TilePosition(106, 26));
	    result.add(new TilePosition(108, 29));
	    result.add(new TilePosition(111, 30));
	    result.add(new TilePosition(114, 32));
	    result.add(new TilePosition(117, 33));
	    result.add(new TilePosition(120, 34));
	    result.add(new TilePosition(123, 34));

	    break;
	case FIVE:
	    result.add(new TilePosition(115, 108));
	    result.add(new TilePosition(107, 104));
	    result.add(new TilePosition(114, 116));
	    result.add(new TilePosition(114, 118));
	    result.add(new TilePosition(113, 121));
	    result.add(new TilePosition(116, 121));
	    result.add(new TilePosition(119, 109));
	    result.add(new TilePosition(122, 109));
	    result.add(new TilePosition(125, 109));
	    result.add(new TilePosition(122, 111));
	    result.add(new TilePosition(125, 111));
	    result.add(new TilePosition(114, 112));
	    result.add(new TilePosition(113, 121));
	    result.add(new TilePosition(116, 121));
	    result.add(new TilePosition(113, 123));
	    result.add(new TilePosition(116, 123));
	    result.add(new TilePosition(119, 123));
	    result.add(new TilePosition(110, 125));
	    result.add(new TilePosition(113, 125));
	    result.add(new TilePosition(116, 125));
	    result.add(new TilePosition(119, 125));
	    result.add(new TilePosition(92, 123));
	    result.add(new TilePosition(95, 123));
	    result.add(new TilePosition(94, 125));
	    result.add(new TilePosition(97, 125));
	    result.add(new TilePosition(110, 125));
	    result.add(new TilePosition(122, 124));
	    result.add(new TilePosition(125, 123));
	    result.add(new TilePosition(116, 110));
	    break;
	case SEVEN:
	    result.add(new TilePosition(17, 113));
	    result.add(new TilePosition(23, 110));
	    result.add(new TilePosition(15, 115));
	    result.add(new TilePosition(15, 117));
	    result.add(new TilePosition(2, 124));
	    result.add(new TilePosition(5, 125));
	    result.add(new TilePosition(8, 125));
	    result.add(new TilePosition(11, 125));
	    result.add(new TilePosition(14, 125));
	    result.add(new TilePosition(17, 125));
	    result.add(new TilePosition(10, 123));
	    result.add(new TilePosition(13, 123));
	    result.add(new TilePosition(16, 123));
	    result.add(new TilePosition(13, 121));
	    result.add(new TilePosition(16, 121));
	    result.add(new TilePosition(0, 113));
	    result.add(new TilePosition(0, 111));
	    result.add(new TilePosition(0, 109));
	    result.add(new TilePosition(0, 107));
	    result.add(new TilePosition(0, 105));
	    result.add(new TilePosition(0, 97));
	    result.add(new TilePosition(0, 95));
	    result.add(new TilePosition(0, 93));
	    result.add(new TilePosition(0, 91));
	    result.add(new TilePosition(3, 92));
	    result.add(new TilePosition(6, 92));
	    result.add(new TilePosition(9, 93));
	    result.add(new TilePosition(3, 94));
	    break;
	case ELEVEN:
	    result.add(new TilePosition(10, 18));
	    result.add(new TilePosition(18, 22));
	    result.add(new TilePosition(10, 10));
	    result.add(new TilePosition(0, 17));
	    result.add(new TilePosition(0, 15));
	    result.add(new TilePosition(3, 17));
	    result.add(new TilePosition(3, 15));
	    result.add(new TilePosition(6, 17));
	    result.add(new TilePosition(6, 15));
	    result.add(new TilePosition(6, 13));
	    result.add(new TilePosition(9, 16));
	    result.add(new TilePosition(9, 14));
	    result.add(new TilePosition(0, 0));
	    result.add(new TilePosition(0, 2));
	    result.add(new TilePosition(3, 0));
	    result.add(new TilePosition(3, 2));
	    result.add(new TilePosition(6, 0));
	    result.add(new TilePosition(9, 0));
	    result.add(new TilePosition(12, 0));
	    result.add(new TilePosition(15, 0));
	    result.add(new TilePosition(18, 0));
	    result.add(new TilePosition(21, 0));
	    result.add(new TilePosition(24, 0));
	    result.add(new TilePosition(27, 0));
	    result.add(new TilePosition(30, 0));
	    result.add(new TilePosition(11, 2));
	    result.add(new TilePosition(11, 4));
	    break;
	default:
	    break;
	}

	return result;
    }

    public List<TilePosition> getRefinery() {
	List<TilePosition> result = new ArrayList<>();

	switch (allianceStartLocation) {
	case ONE:
	    result.add(new TilePosition(117, 2));
	    break;
	case FIVE:
	    result.add(new TilePosition(117, 112));
	    break;
	case SEVEN:
	    result.add(new TilePosition(7, 112));
	    break;
	case ELEVEN:
	    result.add(new TilePosition(7, 2));
	    break;
	default:
	    break;
	}

	return result;
    }

    public List<TilePosition> getTurret() {
	List<TilePosition> result = new ArrayList<>();

	switch (allianceStartLocation) {
	case ONE:
	    result.add(new TilePosition(105, 17));
	    result.add(new TilePosition(119, 10));
	    break;
	case FIVE:
	    result.add(new TilePosition(113, 109));
	    result.add(new TilePosition(119, 120));
	    break;
	case SEVEN:
	    result.add(new TilePosition(23, 108));
	    result.add(new TilePosition(7, 120));
	    break;
	case ELEVEN:
	    result.add(new TilePosition(20, 20));
	    result.add(new TilePosition(8, 10));
	    break;
	default:
	    break;
	}

	return result;
    }

    public TilePosition getChokePoint1() {
	TilePosition result = null;

	switch (allianceStartLocation) {
	case ONE:
	    result = new TilePosition(107, 14);
	    break;
	case FIVE:
	    result = new TilePosition(112, 106);
	    break;
	case SEVEN:
	    result = new TilePosition(22, 112);
	    break;
	case ELEVEN:
	    result = new TilePosition(15, 21);
	    break;
	default:
	    break;
	}

	return result;
    }

    public TilePosition getChokePoint2() {
	TilePosition result = null;

	switch (allianceStartLocation) {
	case ONE:
	    result = new TilePosition(93, 18);
	    break;
	case FIVE:
	    result = new TilePosition(110, 97);
	    break;
	case SEVEN:
	    result = new TilePosition(33, 112);
	    break;
	case ELEVEN:
	    result = new TilePosition(34, 26);
	    break;
	default:
	    break;
	}

	return result;
    }

    // 몇시 방향인지 리턴한다.
    public ClockLocation getAllianceStartLocation() {
	return allianceStartLocation;
    }

    // 정찰 순서를 리턴한다.
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

    public TilePosition getEnemyStartLocation() {
	return enemyStartLocation;
    }

    public void setEnemyStartLocation(TilePosition enemyStartLocation) {
	this.enemyStartLocation = enemyStartLocation;
    }
}