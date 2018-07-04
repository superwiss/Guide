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
	    result.add(new TilePosition(113, 6));
	    result.add(new TilePosition(113, 9));
	    result.add(new TilePosition(113, 12));
	    result.add(new TilePosition(113, 15));
	    result.add(new TilePosition(113, 3));
	    break;
	case FIVE:
	    result.add(new TilePosition(110, 117));
	    result.add(new TilePosition(110, 120));
	    result.add(new TilePosition(106, 117));
	    result.add(new TilePosition(106, 120));
	    result.add(new TilePosition(102, 117));
	    break;
	case SEVEN:
	    result.add(new TilePosition(11, 119));
	    result.add(new TilePosition(11, 116));
	    result.add(new TilePosition(11, 113));
	    result.add(new TilePosition(11, 110));
	    result.add(new TilePosition(11, 107));
	    break;
	case ELEVEN:
	    result.add(new TilePosition(11, 9));
	    result.add(new TilePosition(11, 6));
	    result.add(new TilePosition(15, 9));
	    result.add(new TilePosition(15, 6));
	    result.add(new TilePosition(19, 9));
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
	    result.add(new TilePosition(123, 14));
	    result.add(new TilePosition(117, 16));
	    result.add(new TilePosition(120, 16));
	    result.add(new TilePosition(123, 16));
	    result.add(new TilePosition(117, 18));
	    result.add(new TilePosition(120, 18));
	    result.add(new TilePosition(123, 18));
	    result.add(new TilePosition(117, 20));
	    result.add(new TilePosition(120, 20));
	    result.add(new TilePosition(123, 20));
	    result.add(new TilePosition(124, 0));
	    result.add(new TilePosition(121, 0));
	    result.add(new TilePosition(118, 0));
	    result.add(new TilePosition(115, 0));
	    result.add(new TilePosition(112, 0));
	    result.add(new TilePosition(109, 0));
	    result.add(new TilePosition(115, 2));
	    result.add(new TilePosition(112, 2));
	    result.add(new TilePosition(109, 2));
	    result.add(new TilePosition(124, 2));
	    result.add(new TilePosition(121, 2));
	    result.add(new TilePosition(115, 4));
	    result.add(new TilePosition(112, 4));
	    result.add(new TilePosition(109, 4));

	    break;
	case FIVE:
	    result.add(new TilePosition(114, 115));
	    result.add(new TilePosition(111, 115));
	    result.add(new TilePosition(114, 113));
	    result.add(new TilePosition(111, 113));
	    result.add(new TilePosition(114, 111));
	    result.add(new TilePosition(111, 111));
	    result.add(new TilePosition(124, 124));
	    result.add(new TilePosition(121, 124));
	    result.add(new TilePosition(118, 124));
	    result.add(new TilePosition(115, 124));
	    result.add(new TilePosition(112, 124));
	    result.add(new TilePosition(109, 124));
	    result.add(new TilePosition(106, 124));
	    result.add(new TilePosition(103, 124));
	    result.add(new TilePosition(100, 124));
	    result.add(new TilePosition(97, 124));
	    result.add(new TilePosition(124, 122));
	    result.add(new TilePosition(121, 122));
	    result.add(new TilePosition(118, 122));
	    result.add(new TilePosition(115, 122));
	    result.add(new TilePosition(112, 122));
	    result.add(new TilePosition(109, 122));
	    result.add(new TilePosition(106, 122));
	    result.add(new TilePosition(103, 122));
	    result.add(new TilePosition(100, 122));
	    result.add(new TilePosition(97, 122));
	    break;
	case SEVEN:
	    result.add(new TilePosition(7, 122));
	    result.add(new TilePosition(10, 122));
	    result.add(new TilePosition(1, 114));
	    result.add(new TilePosition(4, 114));
	    result.add(new TilePosition(1, 112));
	    result.add(new TilePosition(4, 112));
	    result.add(new TilePosition(1, 110));
	    result.add(new TilePosition(4, 110));
	    result.add(new TilePosition(1, 108));
	    result.add(new TilePosition(4, 108));
	    result.add(new TilePosition(1, 106));
	    result.add(new TilePosition(4, 106));
	    result.add(new TilePosition(1, 104));
	    result.add(new TilePosition(4, 104));
	    result.add(new TilePosition(1, 102));
	    result.add(new TilePosition(4, 102));
	    result.add(new TilePosition(1, 100));
	    result.add(new TilePosition(4, 100));
	    result.add(new TilePosition(7, 100));
	    result.add(new TilePosition(10, 100));
	    result.add(new TilePosition(1, 98));
	    result.add(new TilePosition(4, 98));
	    result.add(new TilePosition(7, 98));
	    result.add(new TilePosition(10, 98));
	    result.add(new TilePosition(1, 96));
	    result.add(new TilePosition(4, 96));
	    result.add(new TilePosition(7, 96));
	    result.add(new TilePosition(10, 96));

	    break;
	case ELEVEN:
	    result.add(new TilePosition(5, 14));
	    result.add(new TilePosition(5, 12));
	    result.add(new TilePosition(8, 14));
	    result.add(new TilePosition(8, 12));
	    result.add(new TilePosition(1, 3));
	    result.add(new TilePosition(4, 3));
	    result.add(new TilePosition(1, 0));
	    result.add(new TilePosition(4, 0));
	    result.add(new TilePosition(7, 0));
	    result.add(new TilePosition(10, 0));
	    result.add(new TilePosition(13, 0));
	    result.add(new TilePosition(16, 0));
	    result.add(new TilePosition(19, 0));
	    result.add(new TilePosition(22, 0));
	    result.add(new TilePosition(25, 0));
	    result.add(new TilePosition(11, 2));
	    result.add(new TilePosition(14, 2));
	    result.add(new TilePosition(17, 2));
	    result.add(new TilePosition(20, 2));
	    result.add(new TilePosition(23, 2));
	    result.add(new TilePosition(26, 2));
	    result.add(new TilePosition(11, 4));
	    result.add(new TilePosition(14, 4));
	    result.add(new TilePosition(17, 4));
	    result.add(new TilePosition(20, 4));
	    result.add(new TilePosition(23, 4));
	    result.add(new TilePosition(26, 4));
	    break;
	default:
	    break;
	}

	return result;
    }

    public List<TilePosition> getRefinery() {
	List<TilePosition> result = new ArrayList<>();

	switch (clockLocation) {
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

    public TilePosition getChokePoint1() {
	TilePosition result = null;

	switch (clockLocation) {
	case ONE:
	    result = new TilePosition(106, 13);
	    break;
	case FIVE:
	    result = new TilePosition(113, 105);
	    break;
	case SEVEN:
	    result = new TilePosition(22, 112);
	    break;
	case ELEVEN:
	    result = new TilePosition(15, 20);
	    break;
	default:
	    break;
	}

	return result;
    }

    // 몇시 방향인지 리턴한다.
    public ClockLocation getClockLocation() {
	return clockLocation;
    }
}