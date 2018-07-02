import java.util.ArrayList;
import java.util.List;

import bwapi.TilePosition;
import bwapi.Unit;

public class LocationManager {

    private static LocationManager instance = new LocationManager();

    public static LocationManager Instance() {
	return instance;
    }

    private enum ClockLocation {
	ONE, FIVE, SEVEN, ELEVEN
    }

    private ClockLocation clockLocation;

    // 최초 커맨드센터의 위치를 기반으로 건물 심시티를 결정한다. 
    public void init(Unit commandCenter) {
	if (commandCenter.getTilePosition().equals(new TilePosition(7, 117))) {
	    clockLocation = ClockLocation.SEVEN;
	} else if (commandCenter.getTilePosition().equals(new TilePosition(117, 117))) {
	    clockLocation = ClockLocation.FIVE;
	} else if (commandCenter.getTilePosition().equals(new TilePosition(117, 7))) {
	    clockLocation = ClockLocation.ONE;
	} else if (commandCenter.getTilePosition().equals(new TilePosition(7, 7))) {
	    clockLocation = ClockLocation.ELEVEN;
	}
    }

    public List<TilePosition> getBarracks() {
	// 가로 4, 세로 3
	List<TilePosition> result = new ArrayList<>();

	switch (clockLocation) {
	case ONE:
	    result.add(new TilePosition(113, 9));
	    result.add(new TilePosition(113, 12));
	    result.add(new TilePosition(113, 15));
	    break;
	case FIVE:
	    result.add(new TilePosition(110, 117));
	    result.add(new TilePosition(110, 120));
	    result.add(new TilePosition(110, 123));
	    break;
	case SEVEN:
	    result.add(new TilePosition(11, 119));
	    result.add(new TilePosition(11, 116));
	    result.add(new TilePosition(11, 113));
	    break;
	case ELEVEN:
	    result.add(new TilePosition(11, 9));
	    result.add(new TilePosition(11, 6));
	    result.add(new TilePosition(15, 6));
	    break;
	default:
	    break;
	}

	return result;
    }

    public List<TilePosition> getBunker() {
	// 가로 4, 세로 3
	List<TilePosition> result = new ArrayList<>();

	switch (clockLocation) {
	case ONE:
	    result.add(new TilePosition(117, 10));
	    break;
	case FIVE:
	    result.add(new TilePosition(114, 117));
	    break;
	case SEVEN:
	    result.add(new TilePosition(8, 120));
	    break;
	case ELEVEN:
	    result.add(new TilePosition(8, 10));
	    break;
	default:
	    break;
	}

	return result;
    }

    public List<TilePosition> getSupplyDepot() {
	// 가로 3, 세로 2
	List<TilePosition> result = new ArrayList<>();

	switch (clockLocation) {
	case ONE:
	    result.add(new TilePosition(117, 12));
	    result.add(new TilePosition(120, 12));
	    result.add(new TilePosition(117, 14));
	    result.add(new TilePosition(120, 14));
	    result.add(new TilePosition(117, 16));
	    result.add(new TilePosition(120, 16));
	    break;
	case FIVE:
	    result.add(new TilePosition(114, 115));
	    result.add(new TilePosition(111, 115));
	    result.add(new TilePosition(114, 113));
	    result.add(new TilePosition(111, 113));
	    result.add(new TilePosition(114, 111));
	    result.add(new TilePosition(111, 111));
	    break;
	case SEVEN:
	    result.add(new TilePosition(7, 122));
	    result.add(new TilePosition(10, 122));
	    result.add(new TilePosition(1, 114));
	    result.add(new TilePosition(4, 114));
	    result.add(new TilePosition(1, 112));
	    result.add(new TilePosition(4, 112));
	    break;
	case ELEVEN:
	    result.add(new TilePosition(5, 14));
	    result.add(new TilePosition(5, 12));
	    result.add(new TilePosition(8, 14));
	    result.add(new TilePosition(8, 12));
	    result.add(new TilePosition(1, 3));
	    result.add(new TilePosition(4, 3));
	    break;
	default:
	    break;
	}

	return result;
    }
}