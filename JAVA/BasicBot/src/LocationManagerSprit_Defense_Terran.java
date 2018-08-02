import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bwapi.TilePosition;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;

// 서킷 브레이커 맵
public class LocationManagerSprit_Defense_Terran extends LocationManager {
    private static final int ONE_CLOCK = 0;
    private static final int FIVE_CLOCK = 1;
    private static final int SEVEN_CLOCK = 2;
    private static final int ELEVEN_CLOCK = 3;

    @Override
    public List<TilePosition> initBaseLocations() {
	List<TilePosition> result = new ArrayList<>(4);

	result.add(new TilePosition(117, 7));
	result.add(new TilePosition(117, 117));
	result.add(new TilePosition(7, 116));
	result.add(new TilePosition(7, 6));

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

    // 정찰할 위치(순서)를 설정한다.
    @Override
    public List<TilePosition> initEnemyBaseSearchSequence() {
	if (enemyStartLocation != null) {
	    List<TilePosition> result = new ArrayList<>();
	    if (enemyStartLocation.equals(getBaseLocations(ONE_CLOCK))) {
		result.add(new TilePosition(104, 0));
		result.add(new TilePosition(125, 1));
		result.add(new TilePosition(125, 24));
		result.add(new TilePosition(105, 18));
	    } else if (enemyStartLocation.equals(getBaseLocations(FIVE_CLOCK))) {
		result.add(new TilePosition(125, 104));
		result.add(new TilePosition(125, 124));
		result.add(new TilePosition(102, 124));
		result.add(new TilePosition(104, 103));
	    } else if (enemyStartLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
		result.add(new TilePosition(20, 124));
		result.add(new TilePosition(1, 124));
		result.add(new TilePosition(0, 102));
		result.add(new TilePosition(20, 105));
	    } else if (enemyStartLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
		result.add(new TilePosition(1, 20));
		result.add(new TilePosition(1, 1));
		result.add(new TilePosition(20, 1));
		result.add(new TilePosition(20, 20));
	    }
	    return result;
	} else {
	    return null;
	}
    }

    // 배럭, 팩토리, 스타포트와 같은 병력 훈련용 타일의 위치를 지정한다. Add on 건물 위치까지 고려해야 한다.
    @Override
    public List<TilePosition> initTrainingBuildings() {
	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result.add(new TilePosition(111, 1));
	    result.add(new TilePosition(105, 1));
	    result.add(new TilePosition(99, 1));

	    result.add(new TilePosition(107, 6));
	    result.add(new TilePosition(107, 9));
	    result.add(new TilePosition(111, 9));
	    result.add(new TilePosition(111, 12));
	    result.add(new TilePosition(111, 15));

	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시

	    result.add(new TilePosition(121, 107));
	    result.add(new TilePosition(121, 104));
	    result.add(new TilePosition(121, 99));
	    result.add(new TilePosition(121, 110));

	    result.add(new TilePosition(110, 104));
	    result.add(new TilePosition(114, 104));
	    result.add(new TilePosition(110, 107));
	    result.add(new TilePosition(114, 107));
	    result.add(new TilePosition(106, 107));
	    result.add(new TilePosition(106, 104));

	    //애드온 건물

	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {

	    result.add(new TilePosition(13, 124));
	    result.add(new TilePosition(19, 124));
	    result.add(new TilePosition(25, 124));

	    result.add(new TilePosition(13, 116));
	    result.add(new TilePosition(13, 113));
	    result.add(new TilePosition(17, 113));
	    result.add(new TilePosition(17, 110));

	    result.add(new TilePosition(7, 124));

	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(0, 14));
	    result.add(new TilePosition(0, 17));
	    result.add(new TilePosition(0, 20));
	    result.add(new TilePosition(0, 23));

	    result.add(new TilePosition(9, 20));
	    result.add(new TilePosition(9, 17));
	    result.add(new TilePosition(9, 14));
	    result.add(new TilePosition(17, 14));
	    result.add(new TilePosition(13, 17));
	    result.add(new TilePosition(13, 14));
	}

	return result;
    }

    @Override
    public List<TilePosition> initEntranceBuildings() {
	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result.add(new TilePosition(102, 9));
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(114, 101));
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result.add(new TilePosition(23, 120));
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(4, 28));
	}
	return result;
    }

    @Override
    public List<TilePosition> initSecondEntranceBuildings() {
	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result.add(new TilePosition(88, 20));
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(104, 87));
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result.add(new TilePosition(36, 105));
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(20, 39));
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
	    result.add(new TilePosition(100, 7));
	    result.add(new TilePosition(97, 5));
	    result.add(new TilePosition(125, 0));
	    result.add(new TilePosition(122, 0));
	    result.add(new TilePosition(125, 2));
	    result.add(new TilePosition(122, 2));
	    result.add(new TilePosition(125, 14));
	    result.add(new TilePosition(122, 14));
	    result.add(new TilePosition(125, 16));
	    result.add(new TilePosition(122, 16));
	    result.add(new TilePosition(125, 18));
	    result.add(new TilePosition(122, 18));
	    result.add(new TilePosition(125, 20));
	    result.add(new TilePosition(122, 20));
	    result.add(new TilePosition(125, 22));
	    result.add(new TilePosition(122, 22));
	    result.add(new TilePosition(125, 24));
	    result.add(new TilePosition(122, 24));
	    result.add(new TilePosition(117, 14));
	    result.add(new TilePosition(119, 16));
	    result.add(new TilePosition(119, 18));
	    result.add(new TilePosition(119, 20));
	    result.add(new TilePosition(119, 22));
	    result.add(new TilePosition(116, 16));
	    result.add(new TilePosition(116, 18));
	    result.add(new TilePosition(116, 20));
	    result.add(new TilePosition(125, 26));
	    result.add(new TilePosition(125, 28));
	    result.add(new TilePosition(122, 26));
	    result.add(new TilePosition(122, 28));
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(118, 98));
	    result.add(new TilePosition(118, 100));
	    result.add(new TilePosition(119, 125));
	    result.add(new TilePosition(119, 123));
	    result.add(new TilePosition(116, 125));
	    result.add(new TilePosition(116, 123));
	    result.add(new TilePosition(113, 125));
	    result.add(new TilePosition(113, 123));
	    result.add(new TilePosition(110, 125));
	    result.add(new TilePosition(110, 123));
	    result.add(new TilePosition(107, 125));
	    result.add(new TilePosition(107, 123));
	    result.add(new TilePosition(104, 125));
	    result.add(new TilePosition(104, 123));
	    result.add(new TilePosition(110, 121));
	    result.add(new TilePosition(107, 121));
	    result.add(new TilePosition(104, 121));
	    result.add(new TilePosition(107, 119));
	    result.add(new TilePosition(104, 119));
	    result.add(new TilePosition(107, 117));
	    result.add(new TilePosition(104, 117));
	    result.add(new TilePosition(107, 115));
	    result.add(new TilePosition(104, 115));
	    result.add(new TilePosition(107, 113));
	    result.add(new TilePosition(104, 113));
	    result.add(new TilePosition(110, 119));
	    result.add(new TilePosition(110, 117));
	    result.add(new TilePosition(110, 115));
	    result.add(new TilePosition(110, 113));
	    result.add(new TilePosition(110, 111));
	    result.add(new TilePosition(107, 111));
	    result.add(new TilePosition(104, 111));

	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result.add(new TilePosition(23, 118));
	    result.add(new TilePosition(27, 121));
	    result.add(new TilePosition(0, 124));
	    result.add(new TilePosition(3, 124));
	    result.add(new TilePosition(0, 111));
	    result.add(new TilePosition(3, 111));
	    result.add(new TilePosition(0, 109));
	    result.add(new TilePosition(3, 109));
	    result.add(new TilePosition(0, 107));
	    result.add(new TilePosition(3, 107));
	    result.add(new TilePosition(0, 105));
	    result.add(new TilePosition(3, 105));
	    result.add(new TilePosition(0, 103));
	    result.add(new TilePosition(3, 103));
	    result.add(new TilePosition(0, 101));
	    result.add(new TilePosition(3, 101));
	    result.add(new TilePosition(6, 101));
	    result.add(new TilePosition(6, 103));
	    result.add(new TilePosition(9, 101));
	    result.add(new TilePosition(9, 103));
	    result.add(new TilePosition(6, 105));
	    result.add(new TilePosition(9, 105));
	    result.add(new TilePosition(12, 103));
	    result.add(new TilePosition(12, 105));
	    result.add(new TilePosition(12, 107));
	    result.add(new TilePosition(9, 107));

	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(7, 26));
	    result.add(new TilePosition(10, 26));
	    result.add(new TilePosition(0, 0));
	    result.add(new TilePosition(0, 2));
	    result.add(new TilePosition(3, 0));
	    result.add(new TilePosition(3, 2));
	    result.add(new TilePosition(11, 0));
	    result.add(new TilePosition(11, 2));
	    result.add(new TilePosition(14, 0));
	    result.add(new TilePosition(14, 2));
	    result.add(new TilePosition(17, 0));
	    result.add(new TilePosition(17, 2));
	    result.add(new TilePosition(20, 0));
	    result.add(new TilePosition(20, 2));
	    result.add(new TilePosition(14, 4));
	    result.add(new TilePosition(17, 4));
	    result.add(new TilePosition(20, 4));
	    result.add(new TilePosition(14, 6));
	    result.add(new TilePosition(17, 6));
	    result.add(new TilePosition(20, 6));
	    result.add(new TilePosition(14, 8));
	    result.add(new TilePosition(17, 8));
	    result.add(new TilePosition(20, 8));
	    result.add(new TilePosition(14, 10));
	    result.add(new TilePosition(17, 10));
	    result.add(new TilePosition(20, 10));
	    result.add(new TilePosition(14, 12));
	    result.add(new TilePosition(17, 12));
	    result.add(new TilePosition(20, 12));

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
	    result.add(new TilePosition(7, 111));
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(7, 1));
	}

	return result;
    }

    // 본진에 위치한 터렛의 위치를 설정한다.
    @Override
    public List<TilePosition> initBaseTurret() {

	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result.add(new TilePosition(117, 5));
	    result.add(new TilePosition(120, 12));
	    result.add(new TilePosition(120, 14));
	    result.add(new TilePosition(106, 13));
	    result.add(new TilePosition(106, 15));
	    result.add(new TilePosition(94, 3));
	    result.add(new TilePosition(120, 24));
	    result.add(new TilePosition(117, 22));

	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(116, 120));
	    result.add(new TilePosition(115, 115));
	    result.add(new TilePosition(110, 102));
	    result.add(new TilePosition(112, 102));
	    result.add(new TilePosition(118, 120));
	    result.add(new TilePosition(117, 115));

	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {

	    // 7시 9개
	    result.add(new TilePosition(22, 113));
	    result.add(new TilePosition(7, 119));
	    result.add(new TilePosition(7, 109));
	    result.add(new TilePosition(31, 122));
	    result.add(new TilePosition(7, 107));
	    result.add(new TilePosition(7, 121));
	    result.add(new TilePosition(22, 111));
	    result.add(new TilePosition(6, 99));
	    result.add(new TilePosition(12, 101));

	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {

	    // 11시 11개
	    result.add(new TilePosition(13, 25));
	    result.add(new TilePosition(7, 4));
	    result.add(new TilePosition(13, 20));
	    result.add(new TilePosition(10, 9));
	    result.add(new TilePosition(2, 30));
	    result.add(new TilePosition(11, 5));
	    result.add(new TilePosition(15, 20));
	    result.add(new TilePosition(8, 9));
	    result.add(new TilePosition(23, 0));
	    result.add(new TilePosition(23, 8));
	    result.add(new TilePosition(23, 10));

	}

	return result;
    }

    // 본진 입구 방어를 위한 위치를 설정한다.
    @Override
    public TilePosition initBaseEntranceChokePoint() {
	TilePosition result = null;

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result = new TilePosition(104, 8);
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result = new TilePosition(120, 103);
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result = new TilePosition(22, 120);
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result = new TilePosition(7, 25);
	}

	return result;
    }

    // 본진 입구 방어를 위한 위치를 설정한다.
    @Override
    public List<TilePosition> initBaseTankPoint() {

	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result.add(new TilePosition(101, 18));
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(104, 98));
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result.add(new TilePosition(25, 105));
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(20, 29));
	}
	return result;
    }

    // 앞마당 입구 방어를 위한 위치를 설정한다.
    @Override
    public TilePosition initFirstExtensionChokePoint() {
	TilePosition result = null;

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result = new TilePosition(90, 31);
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result = new TilePosition(96, 90);
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result = new TilePosition(36, 96);
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result = new TilePosition(32, 40);
	}

	return result;
    }

    @Override
    public TilePosition initSecondExtensionChokePoint() {
	TilePosition result = null;

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result = new TilePosition(91, 19);
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result = new TilePosition(106, 91);
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result = new TilePosition(36, 107);
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result = new TilePosition(22, 38);
	}

	return result;
    }

    @Override
    public TilePosition initTwoPhaseChokePoint() {
	TilePosition result = null;

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result = new TilePosition(89, 30);
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result = new TilePosition(95, 87);
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result = new TilePosition(39, 98);
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result = new TilePosition(32, 41);
	}

	return result;
    }

    @Override
    public TilePosition initThreePhaseChokePointForSiege() {
	TilePosition result = null;

	if (enemyStartLocation != null) {
	    if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {

		if (enemyStartLocation.equals(getBaseLocations(FIVE_CLOCK))) {
		    result = new TilePosition(99, 93); //ok
		} else if (enemyStartLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
		    result = new TilePosition(40, 108); //ok
		} else if (enemyStartLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
		    result = new TilePosition(27, 40); //ok
		}

	    } else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
		//작업중
		if (enemyStartLocation.equals(getBaseLocations(ONE_CLOCK))) {
		    result = new TilePosition(88, 21); //ok
		} else if (enemyStartLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
		    result = new TilePosition(32, 98); //ok
		} else if (enemyStartLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
		    result = new TilePosition(31, 33); //ok
		}

	    } else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {

		if (enemyStartLocation.equals(getBaseLocations(ONE_CLOCK))) {
		    result = new TilePosition(90, 24); //ok
		} else if (enemyStartLocation.equals(getBaseLocations(FIVE_CLOCK))) {
		    result = new TilePosition(94, 85); //ok
		} else if (enemyStartLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
		    result = new TilePosition(37, 36); //ok
		}

	    } else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {

		if (enemyStartLocation.equals(getBaseLocations(ONE_CLOCK))) {
		    result = new TilePosition(90, 32); //ok
		} else if (enemyStartLocation.equals(getBaseLocations(FIVE_CLOCK))) {
		    result = new TilePosition(94, 93); //ok
		} else if (enemyStartLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
		    result = new TilePosition(41, 106); //ok
		}
	    }
	}

	return result;
    }

    @Override
    public TilePosition getThreePhaseChokePointForMech() {
	TilePosition result = null;

	if (enemyStartLocation != null) {
	    if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {

		if (enemyStartLocation.equals(getBaseLocations(FIVE_CLOCK))) {
		    result = new TilePosition(90, 78); //ok
		} else if (enemyStartLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
		    result = new TilePosition(46, 92); //ok
		} else if (enemyStartLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
		    result = new TilePosition(42, 46); //ok
		}

	    } else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
		//작업중
		if (enemyStartLocation.equals(getBaseLocations(ONE_CLOCK))) {
		    result = new TilePosition(84, 36); //ok
		} else if (enemyStartLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
		    result = new TilePosition(47, 92); //ok
		} else if (enemyStartLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
		    result = new TilePosition(40, 46); //ok
		}

	    } else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {

		if (enemyStartLocation.equals(getBaseLocations(ONE_CLOCK))) {
		    result = new TilePosition(81, 36); //ok
		} else if (enemyStartLocation.equals(getBaseLocations(FIVE_CLOCK))) {
		    result = new TilePosition(78, 83); //ok
		} else if (enemyStartLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
		    result = new TilePosition(39, 51); //ok
		}

	    } else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
		//작업중
		if (enemyStartLocation.equals(getBaseLocations(ONE_CLOCK))) {
		    result = new TilePosition(77, 38); //ok
		} else if (enemyStartLocation.equals(getBaseLocations(FIVE_CLOCK))) {
		    result = new TilePosition(86, 79); //ok
		} else if (enemyStartLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
		    result = new TilePosition(44, 89); //ok
		}
	    }
	}

	return result;
    }

    // 첫번째 확장에 위치한 터렛의 위치를 설정한다.
    @Override
    public List<TilePosition> initFirstExpansionTurret() {

	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result.add(new TilePosition(86, 10));
	    result.add(new TilePosition(88, 16));
	    result.add(new TilePosition(86, 16));
	    result.add(new TilePosition(90, 10));

	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(114, 86));
	    result.add(new TilePosition(117, 93));
	    result.add(new TilePosition(112, 86));
	    result.add(new TilePosition(115, 92));
	    result.add(new TilePosition(110, 86));

	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result.add(new TilePosition(36, 116));
	    result.add(new TilePosition(41, 110));
	    result.add(new TilePosition(40, 116));
	    result.add(new TilePosition(37, 110));
	    result.add(new TilePosition(39, 110));

	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(14, 36));
	    result.add(new TilePosition(12, 42));
	    result.add(new TilePosition(12, 36));
	    result.add(new TilePosition(14, 41));
	}

	return result;
    }

    @Override
    public Set<TilePosition> getHillTilePosition() {
	Set<TilePosition> result = new HashSet<>();

	// 1시 언덕
	result.add(new TilePosition(96, 11));
	result.add(new TilePosition(96, 12));
	result.add(new TilePosition(96, 13));
	result.add(new TilePosition(97, 10));
	result.add(new TilePosition(97, 11));
	result.add(new TilePosition(97, 12));
	result.add(new TilePosition(97, 13));
	result.add(new TilePosition(97, 14));
	result.add(new TilePosition(98, 9));
	result.add(new TilePosition(98, 10));
	result.add(new TilePosition(98, 11));
	result.add(new TilePosition(98, 12));
	result.add(new TilePosition(98, 13));
	result.add(new TilePosition(98, 14));
	result.add(new TilePosition(99, 9));
	result.add(new TilePosition(99, 10));
	result.add(new TilePosition(99, 11));
	result.add(new TilePosition(99, 12));
	result.add(new TilePosition(100, 9));
	result.add(new TilePosition(100, 10));
	result.add(new TilePosition(100, 11));
	result.add(new TilePosition(101, 9));
	result.add(new TilePosition(101, 10));
	result.add(new TilePosition(101, 11));

	// 5시 언덕
	result.add(new TilePosition(112, 95));
	result.add(new TilePosition(112, 96));
	result.add(new TilePosition(112, 97));
	result.add(new TilePosition(112, 98));
	result.add(new TilePosition(113, 95));
	result.add(new TilePosition(113, 96));
	result.add(new TilePosition(113, 97));
	result.add(new TilePosition(113, 98));
	result.add(new TilePosition(114, 95));
	result.add(new TilePosition(114, 96));
	result.add(new TilePosition(114, 97));
	result.add(new TilePosition(114, 98));
	result.add(new TilePosition(114, 99));
	result.add(new TilePosition(115, 94));
	result.add(new TilePosition(115, 95));
	result.add(new TilePosition(115, 96));
	result.add(new TilePosition(115, 97));
	result.add(new TilePosition(115, 98));
	result.add(new TilePosition(115, 99));
	result.add(new TilePosition(116, 94));
	result.add(new TilePosition(116, 95));
	result.add(new TilePosition(116, 96));
	result.add(new TilePosition(116, 97));
	result.add(new TilePosition(116, 98));
	result.add(new TilePosition(116, 99));
	result.add(new TilePosition(117, 95));
	result.add(new TilePosition(117, 96));
	result.add(new TilePosition(117, 97));
	result.add(new TilePosition(117, 98));
	result.add(new TilePosition(117, 99));

	// 7시 언덕
	result.add(new TilePosition(26, 117));
	result.add(new TilePosition(26, 118));
	result.add(new TilePosition(26, 119));
	result.add(new TilePosition(27, 116));
	result.add(new TilePosition(27, 117));
	result.add(new TilePosition(27, 118));
	result.add(new TilePosition(27, 119));
	result.add(new TilePosition(28, 115));
	result.add(new TilePosition(28, 116));
	result.add(new TilePosition(28, 117));
	result.add(new TilePosition(28, 118));
	result.add(new TilePosition(28, 119));
	result.add(new TilePosition(29, 114));
	result.add(new TilePosition(29, 115));
	result.add(new TilePosition(29, 116));
	result.add(new TilePosition(29, 117));
	result.add(new TilePosition(29, 118));
	result.add(new TilePosition(29, 119));
	result.add(new TilePosition(30, 114));
	result.add(new TilePosition(30, 115));
	result.add(new TilePosition(30, 116));
	result.add(new TilePosition(30, 117));
	result.add(new TilePosition(30, 118));
	result.add(new TilePosition(30, 119));
	result.add(new TilePosition(31, 115));
	result.add(new TilePosition(31, 116));
	result.add(new TilePosition(31, 117));
	result.add(new TilePosition(31, 118));
	result.add(new TilePosition(32, 115));
	result.add(new TilePosition(32, 116));
	result.add(new TilePosition(32, 117));
	result.add(new TilePosition(32, 118));

	// 11시 언덕
	result.add(new TilePosition(8, 29));
	result.add(new TilePosition(8, 30));
	result.add(new TilePosition(9, 28));
	result.add(new TilePosition(9, 29));
	result.add(new TilePosition(9, 30));
	result.add(new TilePosition(10, 27));
	result.add(new TilePosition(10, 28));
	result.add(new TilePosition(10, 29));
	result.add(new TilePosition(10, 30));
	result.add(new TilePosition(10, 31));
	result.add(new TilePosition(10, 32));
	result.add(new TilePosition(11, 27));
	result.add(new TilePosition(11, 28));
	result.add(new TilePosition(11, 29));
	result.add(new TilePosition(11, 30));
	result.add(new TilePosition(11, 31));
	result.add(new TilePosition(11, 32));
	result.add(new TilePosition(11, 33));
	result.add(new TilePosition(12, 27));
	result.add(new TilePosition(12, 28));
	result.add(new TilePosition(12, 29));
	result.add(new TilePosition(12, 30));
	result.add(new TilePosition(12, 31));
	result.add(new TilePosition(12, 32));
	result.add(new TilePosition(12, 33));
	result.add(new TilePosition(13, 30));
	result.add(new TilePosition(13, 31));
	result.add(new TilePosition(13, 32));
	result.add(new TilePosition(14, 30));
	result.add(new TilePosition(14, 31));
	result.add(new TilePosition(14, 32));

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
    public TilePosition initThreePhaseChokePointForMech() {
	// TODO Auto-generated method stub
	return null;
    }

}