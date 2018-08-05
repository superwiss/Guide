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
	    result.add(new TilePosition(121, 17));
	    result.add(new TilePosition(114, 17));
	    result.add(new TilePosition(107, 17));
	    result.add(new TilePosition(111, 21));

	    result.add(new TilePosition(107, 13));
	    result.add(new TilePosition(107, 10));

	    result.add(new TilePosition(114, 13));
	    result.add(new TilePosition(111, 7));
	    result.add(new TilePosition(107, 7));

	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(107, 106));
	    result.add(new TilePosition(114, 105));
	    result.add(new TilePosition(121, 105));
	    result.add(new TilePosition(107, 109));
	    result.add(new TilePosition(121, 109));
	    result.add(new TilePosition(114, 109));

	    result.add(new TilePosition(109, 113));
	    result.add(new TilePosition(105, 113));
	    result.add(new TilePosition(105, 116));
	    result.add(new TilePosition(109, 116));
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
	    result.add(new TilePosition(14, 16));
	    result.add(new TilePosition(7, 20));
	    result.add(new TilePosition(0, 20));
	    result.add(new TilePosition(0, 16));
	    result.add(new TilePosition(7, 16));
	    result.add(new TilePosition(14, 20));
	    result.add(new TilePosition(14, 12));
	    result.add(new TilePosition(14, 9));
	    result.add(new TilePosition(18, 12));
	    result.add(new TilePosition(18, 9));
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
	    result.add(new TilePosition(108, 90));
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result.add(new TilePosition(15, 90));
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(17, 31));
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
	    result.add(new TilePosition(110, 4));
	    result.add(new TilePosition(101, 4));
	    result.add(new TilePosition(98, 0));
	    result.add(new TilePosition(98, 2));
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(125, 100));
	    result.add(new TilePosition(122, 102));
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
	    result.add(new TilePosition(99, 123));
	    result.add(new TilePosition(96, 125));

	    result.add(new TilePosition(111, 121));
	    result.add(new TilePosition(111, 119));
	    result.add(new TilePosition(108, 119));
	    result.add(new TilePosition(105, 119));
	    
	    result.add(new TilePosition(108, 121));
	    result.add(new TilePosition(105, 121));

	    result.add(new TilePosition(102, 121));
	    result.add(new TilePosition(121, 113));
	    result.add(new TilePosition(124, 113));

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

	    result.add(new TilePosition(17, 121));
	    result.add(new TilePosition(14, 121));
	    result.add(new TilePosition(11, 121));
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

    // 본진에 위치한 터렛의 위치를 설정한다.
    @Override
    public List<TilePosition> initBaseTurret() {
	// TODO 아직 미 구현됨.
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
	    result.add(new TilePosition(10, 9));
	    result.add(new TilePosition(2, 30));
	    result.add(new TilePosition(11, 5));
	    result.add(new TilePosition(15, 20));
	    result.add(new TilePosition(15, 22));
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
	    result = new TilePosition(119, 23);
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result = new TilePosition(117, 103);
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result = new TilePosition(5, 105);
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
	    result.add(new TilePosition(18, 23));
	}
	return result;
    }

    // 앞마당 입구 방어를 위한 위치를 설정한다.
    @Override
    public TilePosition initFirstExtensionTankPoint() {
	TilePosition result = null;

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result = new TilePosition(102, 38);
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result = new TilePosition(102, 91);
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result = new TilePosition(27, 90);
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result = new TilePosition(27, 37);
	}

	return result;
    }

    @Override
    public TilePosition initSecondExtensionChokePoint() {
	TilePosition result = null;

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result = new TilePosition(113, 34);
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result = new TilePosition(111, 95);
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result = new TilePosition(16, 96);
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result = new TilePosition(15, 36);
	}

	return result;
    }

    @Override
    public TilePosition initTwoPhaseChokePoint() {
	TilePosition result = null;

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result = new TilePosition(98, 38);
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result = new TilePosition(97, 90);
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result = new TilePosition(32, 89);
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result = new TilePosition(30, 38);
	}

	return result;
    }

    @Override
    public TilePosition initThreePhaseChokePointForSiege() {
	TilePosition result = null;

	if (enemyStartLocation != null) {
	    if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {

		if (enemyStartLocation.equals(getBaseLocations(FIVE_CLOCK))) {
		    result = new TilePosition(95, 94); //ok!!
		} else if (enemyStartLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
		    result = new TilePosition(38, 96); //ok!!
		} else if (enemyStartLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
		    result = new TilePosition(28, 40); //ok!!
		}

	    } else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
		//작업중
		if (enemyStartLocation.equals(getBaseLocations(ONE_CLOCK))) {
		    result = new TilePosition(94, 34); //ok!!
		} else if (enemyStartLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
		    result = new TilePosition(35, 92); //ok!!
		} else if (enemyStartLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
		    result = new TilePosition(34, 35); //ok
		}

	    } else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {

		if (enemyStartLocation.equals(getBaseLocations(ONE_CLOCK))) {
		    result = new TilePosition(92, 32); //ok!!
		} else if (enemyStartLocation.equals(getBaseLocations(FIVE_CLOCK))) {
		    result = new TilePosition(95, 85); //ok!!
		} else if (enemyStartLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
		    result = new TilePosition(37, 36); //ok!!
		}

	    } else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {

		if (enemyStartLocation.equals(getBaseLocations(ONE_CLOCK))) {
		    result = new TilePosition(91, 33); //ok!!
		} else if (enemyStartLocation.equals(getBaseLocations(FIVE_CLOCK))) {
		    result = new TilePosition(94, 93); //ok!!
		} else if (enemyStartLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
		    result = new TilePosition(34, 95); //ok!!
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
		    result = new TilePosition(88, 80); //ok!!
		} else if (enemyStartLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
		    result = new TilePosition(44, 81); //ok!!
		} else if (enemyStartLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
		    result = new TilePosition(43, 47); //ok!!
		}

	    } else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
		//작업중
		if (enemyStartLocation.equals(getBaseLocations(ONE_CLOCK))) {
		    result = new TilePosition(86, 49); //ok!!
		} else if (enemyStartLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
		    result = new TilePosition(44, 79); //ok!!
		} else if (enemyStartLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
		    result = new TilePosition(44, 48); //ok
		}

	    } else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {

		if (enemyStartLocation.equals(getBaseLocations(ONE_CLOCK))) {
		    result = new TilePosition(82, 46); //ok!!
		} else if (enemyStartLocation.equals(getBaseLocations(FIVE_CLOCK))) {
		    result = new TilePosition(79, 83); //ok!!
		} else if (enemyStartLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
		    result = new TilePosition(41, 51); //ok
		}

	    } else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
		//작업중
		if (enemyStartLocation.equals(getBaseLocations(ONE_CLOCK))) {
		    result = new TilePosition(83, 47); //ok!!
		} else if (enemyStartLocation.equals(getBaseLocations(FIVE_CLOCK))) {
		    result = new TilePosition(86, 79); //ok!!
		} else if (enemyStartLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
		    result = new TilePosition(40, 79); //ok!!
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

    @Override
    public List<TilePosition> initMineralExpansion() {

	List<TilePosition> result = new ArrayList<>();

	if (allianceBaseLocation.equals(getBaseLocations(ONE_CLOCK))) {
	    // 1시
	    result.add(new TilePosition(89, 15));
	} else if (allianceBaseLocation.equals(getBaseLocations(FIVE_CLOCK))) {
	    // 5시
	    result.add(new TilePosition(89, 110));
	} else if (allianceBaseLocation.equals(getBaseLocations(SEVEN_CLOCK))) {
	    // 7시
	    result.add(new TilePosition(35, 110));
	} else if (allianceBaseLocation.equals(getBaseLocations(ELEVEN_CLOCK))) {
	    // 11시
	    result.add(new TilePosition(35, 15));
	}

	return result;
    }

}