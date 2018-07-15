import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import bwapi.TilePosition;
import bwapi.Unit;

/// 게임 초반에 일꾼 유닛 중에서 정찰 유닛을 하나 지정하고, 정찰 유닛을 이동시켜 정찰을 수행하는 class<br>
/// 적군의 BaseLocation 위치를 알아내는 것까지만 개발되어있습니다
public class ScoutManager extends Manager {
    private Queue<TilePosition> searchQueue = new LinkedList<>();

    // TODO 정찰은 1개만 가능하도록 구현됨. 다중 유닛 정찰 구현하기.
    @Override
    public void onFrame() {
	super.onFrame();

	// 42프레임에 한 번씩 수행된다.
	if (0 != gameStatus.getFrameCount() % 42) {
	    return;
	}

	UnitManager allianceUnitManager = gameStatus.getAllianceUnitManager();
	LocationManager locationManager = gameStatus.getLocationManager();
	Unit scoutUnit = allianceUnitManager.getFirstUnitByUnitKind(UnitKind.Scouting_Unit);

	if (null == scoutUnit) {
	    return;
	}

	// 정찰을 완료했으면, 정찰 유닛을 릴리즈 한다.
	if (true == searchQueue.isEmpty()) {
	    if (scoutUnit.exists()) {
		allianceUnitManager.releaseScoutUnit(scoutUnit);
		scoutUnit.stop();
	    }
	    Log.info("정찰을 완료했다.");
	} else {
	    TilePosition target = searchQueue.peek();

	    if (gameStatus.isVisible(target)) {
		// 정찰 위치의 fog of war가 사라지면 Queue에서 제거하고 다음 위치로 이동한다.
		Log.info("위치(%s) 정찰 완료.", target);
		searchQueue.poll();
		checkEnemyStartingLocation(gameStatus.getEnemyUnitManager());
		if (null != locationManager.getEnemyBaseLocation()) {
		    Log.info("적 본진을 발견했으므로, 정찰 일꾼(%d)을 릴리즈 한다.", scoutUnit.getID());
		    allianceUnitManager.releaseScoutUnit(scoutUnit);
		    return;
		} else {
		    // 다음 지점으로 이동한다.
		    onFrame();
		}
	    } else {
		// 정찰을 계속한다.
		ActionUtil.moveToPosition(allianceUnitManager, scoutUnit, target.toPosition());
	    }

	}
    }

    public void addSearchQueue(TilePosition tilePosition) {
	searchQueue.add(tilePosition);
    }

    @Override
    public void onUnitDestroy(Unit unit) {
	super.onUnitDestroy(unit);

	UnitManager allianceUnitManager = gameStatus.getAllianceUnitManager();
	UnitManager enemyUnitManager = gameStatus.getEnemyUnitManager();
	LocationManager locationManager = gameStatus.getLocationManager();

	// 정찰중인 유닛이 죽었을 경우를 처리...
	if (allianceUnitManager.getUnitIdSetByUnitKind(UnitKind.Scouting_Unit).contains(Integer.valueOf(unit.getID()))) {
	    // 적 Main건물(커맨드센터, 넥서스, 해처리 류)을 찾기 전이지만, 적 건물이 존재할 경우, 적 건물의 위치를 기반으로 적 본진을 유추한다. 
	    if (null == locationManager.getEnemyBaseLocation()) {
		checkEnemyStartingLocation(enemyUnitManager);
	    }
	    if (null == locationManager.getEnemyBaseLocation()) {
		Log.info("정찰을 완료하기 전에 정찰 유닛(%d)이 죽었다. 다시 정찰하자.", unit.getID());
		allianceUnitManager.releaseScoutUnit(unit);
		doFirstSearch(gameStatus);
	    }
	}
    }

    private void checkEnemyStartingLocation(UnitManager enemyUnitManager) {
	LocationManager locationManager = gameStatus.getLocationManager();

	int notFoundSize = 0;
	int notFoundIndex = -1;
	for (int i = 0; i < 4; ++i) {
	    TilePosition baseLocation = locationManager.getBaseLocations(i);
	    if (!gameStatus.isExplored(baseLocation)) {
		notFoundSize += 1;
		notFoundIndex = i;
	    }
	}

	if (notFoundSize == 1 && -1 != notFoundIndex) {
	    locationManager.setEnemyStartLocation(locationManager.getBaseLocations(notFoundIndex));
	    return;
	}

	Set<Integer> enemyBuildingUnitIds = enemyUnitManager.getUnitIdSetByUnitKind(UnitKind.Building);
	for (Integer enemyBuildingUnitId : enemyBuildingUnitIds) {
	    // 적 본진을 찾았으면 계산을 중단한다.
	    if (null != locationManager.getEnemyBaseLocation()) {
		break;
	    }

	    // 발견한 적 건물의 위치와 Starting Location이 가까우면, 적의 본진을 발견한 것으로 유추할 수 있다.
	    Unit enemyBuildingUnit = enemyUnitManager.getUnit(enemyBuildingUnitId);
	    for (TilePosition tilePosition : locationManager.getSearchSequence()) {
		double distance = tilePosition.getDistance(enemyBuildingUnit.getTilePosition());
		if (32 >= distance) {
		    Log.info("적 본진을 찾았습니다. 발견한 적 건물의 Tile Position=%s, 적 본진의 Tile Position=%s, 발견한 적 건물과 적 본진의 거리: %f", enemyBuildingUnit.getTilePosition(), tilePosition,
			    distance);
		    locationManager.setEnemyStartLocation(tilePosition);
		    break;
		}
	    }
	}
    }

    public boolean doFirstSearch(GameStatus gameStatus) {
	boolean result = true;
	LocationManager locationManager = gameStatus.getLocationManager();
	WorkerManager workerManager = gameStatus.getWorkerManager();

	UnitManager allianceUnitManager = gameStatus.getAllianceUnitManager();
	Unit unitForScout = workerManager.getInterruptableWorker(locationManager.getFirstExtensionChokePoint());
	if (null != unitForScout) {
	    allianceUnitManager.setScoutUnit(unitForScout);
	    searchQueue.addAll(locationManager.getSearchSequence());
	    Log.info("정찰 시작: unitId=%d", unitForScout.getID());
	} else {
	    Log.warn("정찰 가능한 유닛이 없습니다.");
	    result = false;
	}
	return result;
    }
}
