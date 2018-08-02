import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bwapi.TilePosition;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;

// 서킷 브레이커 맵
public class LocationManagerCircuitBreaker_Defense_Terran extends LocationManager {
    private static final int ONE_CLOCK = 0;
    private static final int FIVE_CLOCK = 1;
    private static final int SEVEN_CLOCK = 2;
    private static final int ELEVEN_CLOCK = 3;

    @Override
    public List<TilePosition> initBaseLocations() {
	List<TilePosition> result = new ArrayList<>(4);

	result.add(new TilePosition(117, 9)); // 1시
	result.add(new TilePosition(117, 118)); // 5시
	result.add(new TilePosition(7, 118)); // 7시
	result.add(new TilePosition(7, 9)); // 11시

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
	    result.add(new TilePosition(118, 17));
	    result.add(new TilePosition(111, 21));
	    result.add(new TilePosition(111, 15));
	    result.add(new TilePosition(111, 11));
	    result.add(new TilePosition(111, 7));
	    result.add(new TilePosition(105, 7));
	    result.add(new TilePosition(105, 11));
	    result.add(new TilePosition(105, 15));
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(118, 106));
	    result.add(new TilePosition(111, 105));
	    result.add(new TilePosition(105, 109));
	    result.add(new TilePosition(118, 110));
	    result.add(new TilePosition(111, 109));

	    result.add(new TilePosition(111, 113));
	    result.add(new TilePosition(105, 113));
	    result.add(new TilePosition(105, 116));
	    result.add(new TilePosition(111, 118));
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result.add(new TilePosition(0, 107));
	    result.add(new TilePosition(7, 105));
	    result.add(new TilePosition(14, 105));
	    result.add(new TilePosition(7, 109));
	    result.add(new TilePosition(0, 111));
	    result.add(new TilePosition(14, 109));
	    result.add(new TilePosition(14, 113));
	    result.add(new TilePosition(14, 117));
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(0, 20));
	    result.add(new TilePosition(7, 20));
	    result.add(new TilePosition(14, 20));
	    result.add(new TilePosition(0, 16));
	    result.add(new TilePosition(7, 16));
	    result.add(new TilePosition(14, 16));
	    result.add(new TilePosition(14, 12));
	    result.add(new TilePosition(14, 8));
	}

	return result;
    }

    @Override
    public List<TilePosition> initEntranceBuildings() {
	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result.add(new TilePosition(123, 24));
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(118, 102));
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result.add(new TilePosition(0, 103));
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(0, 24));
	}
	return result;
    }

    @Override
    public List<TilePosition> initSecondEntranceBuildings() {
	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result.add(new TilePosition(110, 36));
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(110, 90));
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result.add(new TilePosition(14, 90));
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(16, 31));
	}
	return result;
    }

    // 본진 입구 벙커를 지을 위치를 설정한다.
    @Override
    public List<TilePosition> initBaseEntranceBunker() {
	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result.add(new TilePosition(114, 35));
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(114, 92));
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result.add(new TilePosition(11, 92));
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(15, 32));
	}

	return result;
    }

    // 3*2 사이즈 건물을 짓기 위한 위치들을 설정한다. (서플라이 디팟, 아마데미 등)
    public List<TilePosition> init3by2SizeBuildings() {
	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result.add(new TilePosition(118, 24));
	    result.add(new TilePosition(121, 22));
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
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(125, 100));
	    result.add(new TilePosition(122, 102));
	    result.add(new TilePosition(125, 102));
	    result.add(new TilePosition(120, 125));
	    result.add(new TilePosition(120, 123));

	    result.add(new TilePosition(117, 125));
	    result.add(new TilePosition(117, 123));
	    result.add(new TilePosition(114, 125));
	    result.add(new TilePosition(114, 123));
	    result.add(new TilePosition(111, 125));

	    result.add(new TilePosition(111, 123));
	    result.add(new TilePosition(108, 125));
	    result.add(new TilePosition(108, 123));
	    result.add(new TilePosition(105, 125));
	    result.add(new TilePosition(105, 123));

	    result.add(new TilePosition(102, 125));
	    result.add(new TilePosition(102, 123));
	    result.add(new TilePosition(99, 125));
	    result.add(new TilePosition(96, 125));
	    result.add(new TilePosition(99, 123));

	    result.add(new TilePosition(96, 123));
	    result.add(new TilePosition(114, 121));
	    result.add(new TilePosition(111, 121));
	    result.add(new TilePosition(108, 121));
	    result.add(new TilePosition(105, 121));

	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result.add(new TilePosition(7, 102));
	    result.add(new TilePosition(4, 102));
	    result.add(new TilePosition(8, 125));
	    result.add(new TilePosition(11, 125));
	    result.add(new TilePosition(14, 125));

	    result.add(new TilePosition(17, 125));
	    result.add(new TilePosition(20, 125));
	    result.add(new TilePosition(23, 125));
	    result.add(new TilePosition(26, 125));
	    result.add(new TilePosition(26, 123));

	    result.add(new TilePosition(29, 125));
	    result.add(new TilePosition(29, 123));
	    result.add(new TilePosition(23, 123));
	    result.add(new TilePosition(20, 123));
	    result.add(new TilePosition(17, 123));

	    result.add(new TilePosition(14, 123));
	    result.add(new TilePosition(11, 123));
	    result.add(new TilePosition(8, 123));
	    result.add(new TilePosition(23, 121));
	    result.add(new TilePosition(20, 121));
	    result.add(new TilePosition(20, 119));

	    result.add(new TilePosition(20, 117));
	    result.add(new TilePosition(20, 115));
	    result.add(new TilePosition(20, 113));
	    result.add(new TilePosition(20, 111));

	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(7, 24));
	    result.add(new TilePosition(4, 24));
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
	}

	return result;
    }

    // 본진 가스를 짓기 위한 위치를 설정한다.
    @Override
    public List<TilePosition> initBaseRefinery() {
	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result.add(new TilePosition(116, 4));
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(117, 113));
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result.add(new TilePosition(7, 113));
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(7, 4));
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
	    result = new TilePosition(119, 23);
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result = new TilePosition(116, 104);
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result = new TilePosition(11, 104);
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result = new TilePosition(11, 24);
	}
	return result;
    }

    // 본진 입구 방어를 위한 위치를 설정한다.
    @Override
    public List<TilePosition> initBaseTankPoint() {

	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result.add(new TilePosition(108, 23));
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(109, 103));
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result.add(new TilePosition(17, 103));
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(17, 22));
	}
	return result;
    }

    // 앞마당 입구 방어를 위한 위치를 설정한다.
    @Override
    public TilePosition initFirstExtensionChokePoint() {
	TilePosition result = null;

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result = new TilePosition(112, 33);
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result = new TilePosition(112, 96);
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result = new TilePosition(15, 95);
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result = new TilePosition(15, 36);
	}

	return result;
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

    @Override
    public List<TilePosition> getFirstExpansionLocation() {
	return allianceFirstExpansionLocation;
    }

    @Override
    public List<TilePosition> getEnemyFirstExpansionLocation() {
	return enemyFirstExpansionLocation;
    }

    @Override
    public TilePosition initSecondExtensionChokePoint() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<TilePosition> initEnemyBaseSearchSequence() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public TilePosition initTwoPhaseChokePoint() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public TilePosition initThreePhaseChokePointForSiege() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public TilePosition initThreePhaseChokePointForMech() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public TilePosition getThreePhaseChokePointForMech() {
	// TODO Auto-generated method stub
	return null;
    }

}