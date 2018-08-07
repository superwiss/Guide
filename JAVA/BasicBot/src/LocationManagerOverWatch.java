import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bwapi.TilePosition;

// 오버워치 맵
public class LocationManagerOverWatch extends LocationManager {
    private static final int ONE_CLOCK = 0;
    private static final int FIVE_CLOCK = 1;
    private static final int SEVEN_CLOCK = 2;
    private static final int ELEVEN_CLOCK = 3;

    @Override
    public List<TilePosition> initBaseLocations() {
	List<TilePosition> result = new ArrayList<>(4);

	result.add(new TilePosition(117, 7));
	result.add(new TilePosition(117, 117));
	result.add(new TilePosition(7, 117));
	result.add(new TilePosition(7, 7));

	return result;
    }

    // 정찰할 위치(순서)를 설정한다.
    @Override
    public List<TilePosition> initSearchSequence() {
	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시: 5시, 11시, 7시 순서로 정찰
	    result.add(getBaseLocations(FIVE_CLOCK));
	    result.add(getBaseLocations(ELEVEN_CLOCK));
	    result.add(getBaseLocations(SEVEN_CLOCK));
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시: 7시, 1시, 11시 순서로 정찰
	    result.add(getBaseLocations(SEVEN_CLOCK));
	    result.add(getBaseLocations(ONE_CLOCK));
	    result.add(getBaseLocations(ELEVEN_CLOCK));
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시: 11시, 5시, 1시 순서로 정찰
	    result.add(getBaseLocations(ELEVEN_CLOCK));
	    result.add(getBaseLocations(FIVE_CLOCK));
	    result.add(getBaseLocations(ONE_CLOCK));
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시: 1시, 7시, 5시 순서로 정찰
	    result.add(getBaseLocations(ONE_CLOCK));
	    result.add(getBaseLocations(SEVEN_CLOCK));
	    result.add(getBaseLocations(FIVE_CLOCK));
	}

	return result;
    }

    // 배럭, 팩토리, 스타포트와 같은 병력 훈련용 타일의 위치를 지정한다. Add on 건물 위치까지 고려해야 한다.
    @Override
    public List<TilePosition> initTrainingBuildings() {
	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
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
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
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
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
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
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
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
	}

	return result;
    }

    // 본진 입구 벙커를 지을 위치를 설정한다.
    @Override
    public List<TilePosition> initBaseEntranceBunker() {
	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result.add(new TilePosition(105, 15));
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(112, 107));
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result.add(new TilePosition(18, 111));
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(13, 19));
	}

	return result;
    }

    // 3*2 사이즈 건물을 짓기 위한 위치들을 설정한다. (서플라이 디팟, 아마데미 등)
    public List<TilePosition> init3by2SizeBuildings() {
	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
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
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
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
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
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
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
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
	    result.add(new TilePosition(9, 16));
	}

	return result;
    }

    // 본진 가스를 짓기 위한 위치를 설정한다.
    @Override
    public List<TilePosition> initBaseRefinery() {
	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result.add(new TilePosition(117, 2));
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(117, 112));
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result.add(new TilePosition(7, 112));
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(7, 2));
	}

	return result;
    }

    // 본진 입구에 위치한 터렛의 위치를 설정한다.
    @Override
    public List<TilePosition> initBaseTurret() {
	// TODO 아직 미 구현됨.
	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	}

	return result;
    }

    // 본진 입구 방어를 위한 위치를 설정한다.
    @Override
    public TilePosition initBaseEntranceChokePoint() {
	TilePosition result = null;

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result = new TilePosition(107, 14);
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result = new TilePosition(112, 106);
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result = new TilePosition(22, 111);
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result = new TilePosition(15, 21);
	}

	return result;
    }

    // 앞마당 입구 방어를 위한 위치를 설정한다.
    @Override
    public TilePosition initFirstExtensionChokePoint() {
	TilePosition result = null;

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result = new TilePosition(93, 18);
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result = new TilePosition(110, 97);
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result = new TilePosition(33, 112);
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result = new TilePosition(34, 26);
	}

	return result;
    }

    @Override
    public List<TilePosition> initEngineeringBay() {
	return new ArrayList<TilePosition>();
    }

    @Override
    public List<TilePosition> initFirstExpansionTurret() {
	return new ArrayList<TilePosition>();
    }

    @Override
    public Set<TilePosition> getHillTilePosition() {
	Set<TilePosition> result = new HashSet<>();

	return result;
    }

    // 조이기 상태에서 적을 기다리고 있을 위치를 리턴한다.
    @Override
    public TilePosition getBlockingChokePoint() {
	return null;
    }

    @Override
    public List<TilePosition> getExtentionPosition() {
	List<TilePosition> result = new ArrayList<>();

	return result;
    }

    @Override
    public List<TilePosition> getBlockingEntranceBuilding() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<TilePosition> getSecondEntranceBuilding() {
	// TODO Auto-generated method stub
	return null;
    }
}