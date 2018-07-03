import java.util.Set;

import bwapi.TilePosition;
import bwapi.Unit;

/// 게임 초반에 일꾼 유닛 중에서 정찰 유닛을 하나 지정하고, 정찰 유닛을 이동시켜 정찰을 수행하는 class<br>
/// 적군의 BaseLocation 위치를 알아내는 것까지만 개발되어있습니다
public class MagiScoutManager {

    private static MagiScoutManager instance = new MagiScoutManager();

    /// static singleton 객체를 리턴합니다
    public static MagiScoutManager Instance() {
	return instance;
    }

    private LocationManager locationManager = LocationManager.Instance();
    private boolean scouting = false;

    public void onFrame(GameData gameData) {
	// 50프레임에 한 번씩 수행된다.
	if (0 != gameData.getFrameCount() % 50) {
	    return;
	}

	UnitManager allianceUnitManager = gameData.getAllianceUnitManager();
	UnitManager enemyUnitManager = gameData.getEnemyUnitManager();
	Set<Integer> scoutUnits = allianceUnitManager.getUnitsByUnitKind(UnitKind.SCOUT);
	// 적 건물을 발견했으면 일꾼을 릴리즈 한다.
	if (true == scouting && 0 < enemyUnitManager.getUnitsByUnitKind(UnitKind.MAIN_BUILDING).size()) {
	    for (Integer scoutUnitId : scoutUnits) {
		Unit scoutUnit = allianceUnitManager.getUnit(scoutUnitId);
		allianceUnitManager.releaseScoutUnit(scoutUnit);
		scoutUnit.stop();
	    }
	    Log.info("정찰을 완료했다.");
	    scouting = false;
	} else if (false == scouting && gameData.getFrameCount() > 1) {
	    // 정찰을 시작하지 않았으면, 정찰을 시작한다.
	    // TODO 정찰은 1개만 가능하도록 구현됨. 다중 유닛 정찰 구현하기.
	    if (scoutUnits.size() > 0) {
		Integer scoutUnitId = scoutUnits.iterator().next();
		Unit scoutUnit = allianceUnitManager.getUnit(scoutUnitId);
		LocationManager.ClockLocation myStarting = locationManager.getClockLocation();
		scouting = true;
		switch (myStarting) {
		case ONE:
		    // 1시면 5시부터 정찰
		    // 5시
		    scoutUnit.move(new TilePosition(117, 117).toPosition());
		    // 7시
		    scoutUnit.move(new TilePosition(33, 110).toPosition(), true);
		    scoutUnit.move(new TilePosition(29, 117).toPosition(), true);
		    scoutUnit.move(new TilePosition(7, 117).toPosition(), true);
		    // 11시
		    scoutUnit.move(new TilePosition(7, 7).toPosition(), true);
		    break;
		case FIVE:
		    // 5시면 7시부터 정찰
		    // 7시
		    scoutUnit.move(new TilePosition(33, 110).toPosition());
		    scoutUnit.move(new TilePosition(29, 117).toPosition(), true);
		    scoutUnit.move(new TilePosition(7, 117).toPosition(), true);
		    // 11시
		    scoutUnit.move(new TilePosition(7, 7).toPosition(), true);
		    // 1시
		    scoutUnit.move(new TilePosition(117, 7).toPosition(), true);
		    break;
		case SEVEN:
		    // 7시면 11시부터 정찰
		    // 11시
		    scoutUnit.move(new TilePosition(7, 7).toPosition());
		    // 1시
		    scoutUnit.move(new TilePosition(117, 7).toPosition(), true);
		    // 5시
		    scoutUnit.move(new TilePosition(117, 117).toPosition(), true);
		    break;
		case ELEVEN:
		    // 시작 위치가 11시일 경우,
		    // 1시
		    scoutUnit.move(new TilePosition(117, 7).toPosition());
		    // 5시
		    scoutUnit.move(new TilePosition(117, 117).toPosition(), true);
		    // 7시
		    scoutUnit.move(new TilePosition(33, 110).toPosition(), true);
		    scoutUnit.move(new TilePosition(29, 117).toPosition(), true);
		    scoutUnit.move(new TilePosition(7, 117).toPosition(), true);
		    break;
		default:
		    break;
		}
	    }
	}
    }
}