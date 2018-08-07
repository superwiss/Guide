import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import bwapi.TilePosition;

/// 게임 초반에 일꾼 유닛 중에서 정찰 유닛을 하나 지정하고, 정찰 유닛을 이동시켜 정찰을 수행하는 class<br>
/// 적군의 BaseLocation 위치를 알아내는 것까지만 개발되어있습니다
public class ScoutManager extends Manager {
    private Queue<TilePosition> searchQueue = new LinkedList<>();

    private LocationManager locationManager;
    private StrategyManager strategyManager;
    private boolean searchEdege;
    private int searchFlag;

    // TODO 정찰은 1개만 가능하도록 구현됨. 다중 유닛 정찰 구현하기.
    @Override
    public void onFrame() {
	super.onFrame();

	// 1초에 한 번씩 수행된다.
	if (gameStatus.isMatchedInterval(1)) {
	    return;
	}

	locationManager = gameStatus.getLocationManager();
	strategyManager = gameStatus.getStrategyManager();

	Unit2 scoutUnit = allianceUnitInfo.getAnyUnit(UnitKind.Scouting_Unit);

	if (null == scoutUnit) {
	    return;
	}

	// 정찰을 완료했으면, 정찰 유닛을 릴리즈 한다.
	if (true == searchQueue.isEmpty()) {
	    if (scoutUnit.exists()) {
		if (strategyManager.hasStrategyItem(StrategyItem.ENEMY_BASE_EDGE_SCOUT)) {
		    if (searchEdege == true && locationManager.enemyStartLocation != null) {
			searchEnemyBase(scoutUnit);
		    }
		} else {
		    allianceUnitInfo.releaseScoutUnit(scoutUnit);
		    scoutUnit.stop();
		}
	    }
	    Log.info("정찰을 완료했다.");
	} else {
	    TilePosition target = searchQueue.peek();
	    searchEdege = false;
	    searchFlag = 1;

	    if (gameStatus.isVisible(target)) {
		// 정찰 위치의 fog of war가 사라지면 Queue에서 제거하고 다음 위치로 이동한다.
		Log.info("위치(%s) 정찰 완료.", target);
		searchQueue.poll();
		if (null == locationManager.getEnemyStartLocation()) {
		    // 다음 지점으로 이동한다.
		    onFrame();
		}
	    } else {
		// 정찰을 계속한다.
		ActionUtil.moveToPosition(allianceUnitInfo, scoutUnit, target.toPosition());
	    }

	}
    }

    @Override
    protected void onUnitDiscover(Unit2 unit) {
	super.onUnitDiscover(unit);

	if (0 == gameStatus.getFrameCount()) {
	    return;
	}

	// 적 본진을 찾았으면 중단한다. 
	if (null != locationManager.getEnemyStartLocation()) {
	    return;
	}

	// 적 유닛에 대해서만 처리한다.
	if (UnitUtil.isEnemyUnit(unit)) {

	    // 적 메인 건물을 명시적으로 찾았을 경우를 처리
	    Set<Unit2> enemyMainBuildingSet = enemyUnitInfo.getUnitSet(UnitKind.MAIN_BUILDING);
	    for (Unit2 enemyMainBuilding : enemyMainBuildingSet) {
		for (TilePosition tilePosition : locationManager.getSearchSequence()) {
		    if (tilePosition.equals(locationManager.getAllianceBaseLocation())) {
			// 내 본진은 계산에 포함하지 않는다.
			continue;
		    }
		    if (enemyMainBuilding.getTilePosition().equals(tilePosition)) {
			gameStatus.sendText("Found Enemy Base. (explicit)");
			foundEnemyBaseLocation(locationManager, tilePosition);
			return;
		    }
		}

	    }
	}
    }

    @Override
    public void onUnitDestroy(Unit2 unit) {
	super.onUnitDestroy(unit);

	UnitInfo allianceUnitInfo = gameStatus.getAllianceUnitInfo();
	UnitInfo enemyUnitInfo = gameStatus.getEnemyUnitInfo();

	// 정찰중인 유닛이 죽었을 경우를 처리...
	if (allianceUnitInfo.isKindOf(unit, UnitKind.Scouting_Unit)) {
	    Log.info("정찰 유닛(%s)이 죽음.", unit);

	    gameStatus.sendText("Scout Unit Dead.");
	    allianceUnitInfo.releaseScoutUnit(unit);

	    // 적 본진을 찾고 유닛이 죽었을 경우를 처리...
	    if (null != locationManager.getEnemyStartLocation()) {
		// 적 본진을 찾았으므로 더 이상 정찰을 하지 않는다.
		return;
	    }

	    // 정찰 가지 않은 곳이 1곳 뿐이라면 그 곳이 적 본진이라고 유추할 수 있다.
	    TilePosition guessTilePosition = checkIfguessSearchSuccess(locationManager);
	    if (null != guessTilePosition) {
		// 적 본진을 유추했으므로 더 이상 정찰을 하지 않는다.
		gameStatus.sendText("Found Enemy Base. (guess)");
		foundEnemyBaseLocation(locationManager, guessTilePosition);
		return;
	    }

	    // 현재까지 발견된 적 건물을 기반으로 적 위치를 유추한다.
	    Set<Unit2> enemyBuildingUnitSet = enemyUnitInfo.getUnitSet(UnitKind.Building);
	    for (Unit2 enemyBuildingUnit : enemyBuildingUnitSet) {

		// 발견한 적 건물의 위치와 Starting Location이 가까우면, 적의 본진을 발견한 것으로 유추할 수 있다.
		for (TilePosition tilePosition : locationManager.getSearchSequence()) {
		    if (tilePosition.equals(locationManager.getAllianceBaseLocation())) {
			// 내 본진은 계산에 포함하지 않는다.
			continue;
		    }
		    int distance = Integer.MAX_VALUE;
		    if (enemyBuildingUnit.isVisible()) {
			distance = UnitUtil.getDistance(tilePosition, enemyBuildingUnit);
		    } else {
			TilePosition lastTilePosition = allianceUnitInfo.getLastTilePosition(enemyBuildingUnit);
			distance = UnitUtil.getDistance(tilePosition, lastTilePosition);
		    }
		    Log.debug("적 건물: %s, 스타팅 포인트 위치: %s, 거리: %d", enemyBuildingUnit, tilePosition, distance);
		    if (1024 >= distance) {
			// 적 본진을 유추했으므로 더 이상 정찰을 하지 않는다.
			gameStatus.sendText("Found Enemy Base. (guess)");
			Log.info("적 본진을 찾았습니다. 발견한 적 건물=%s, 적 본진의 Tile Position=%s, 발견한 적 건물과 적 본진의 거리: %d", enemyBuildingUnit, tilePosition, distance);
			foundEnemyBaseLocation(locationManager, tilePosition);
			return;
		    }
		}
	    }

	    // 현재 정보로는 적 본진 위치를 알 수 없다. 어쩔 수 없이 정찰을 다시 한다. (가보지 않은 곳을 위주로...)
	    searchQueue.clear();
	    for (TilePosition tilePosition : locationManager.getSearchSequence()) {
		if (tilePosition.equals(locationManager.getAllianceBaseLocation())) {
		    // 내 본진은 계산에 포함하지 않는다.
		    continue;
		}
		if (unit.getTargetPosition().equals(tilePosition.toPosition())) {
		    // 방금 정찰하다 죽은 위치로는 정찰하지 않는다.
		    continue;
		}
		if (gameStatus.isExplored(tilePosition)) {
		    // 이미 정찰한 곳은 정찰하지 않는다.
		    continue;
		}
		searchQueue.add(tilePosition);
		gameStatus.sendText("Scout Again.");
		Log.info("정찰을 완료하기 전에 정찰 유닛(%d)이 죽었다. 다시 정찰하자.", unit.getID());
		allianceUnitInfo.releaseScoutUnit(unit);
		doFirstSearch();
	    }
	}
    }

    public void addSearchQueue(TilePosition tilePosition) {
	searchQueue.add(tilePosition);
    }

    // 정찰을 실패했으나, 적 위치를 짐작할 수 있으면 예상된 적 타일 위치를 리턴한다.
    private TilePosition checkIfguessSearchSuccess(LocationManager locationManager) {
	TilePosition result = null;

	// 스타팅 포인트 4곳을 방문 했는지 확인한다.
	int notFoundSize = 0;
	int notFoundIndex = -1;
	for (int i = 0; i < 4; ++i) {
	    TilePosition baseLocation = locationManager.getBaseLocations(i);
	    if (!gameStatus.isExplored(baseLocation)) {
		notFoundSize += 1;
		notFoundIndex = i;
	    }
	}

	// 마지막 한 곳만 정찰을 실패했다면, 그곳이 적 본진이다.
	if (notFoundSize == 1 && -1 != notFoundIndex) {
	    result = locationManager.getBaseLocations(notFoundIndex);
	}

	return result;
    }

    private void foundEnemyBaseLocation(LocationManager locationManager, TilePosition tilePosition) {
	Log.info("적 본진을 찾았습니다. 적 본진의 Tile Position=%s", tilePosition);
	locationManager.setEnemyStartLocation(tilePosition);
	searchEdege = true;
	searchQueue.clear();
	if (strategyManager.hasStrategyStatus(StrategyStatus.SEARCH_FOR_ELIMINATE)) {
	    strategyManager.removeStrategyStatus(StrategyStatus.SEARCH_FOR_ELIMINATE);
	}
    }

    // 최초(상대의 위치를 파악하는게 가장 핵심인) 정찰을 수행한다.
    // 정찰을 시작하면 true, 정찰을 시작할 수 없으면 false를 리턴한다.
    public boolean doFirstSearch() {
	boolean result = true;
	WorkerManager workerManager = gameStatus.getWorkerManager();

	UnitInfo allianceUnitInfo = gameStatus.getAllianceUnitInfo();
	Unit2 unitForScout = workerManager.getInterruptableWorker(locationManager.getFirstExtensionChokePoint());
	if (null != unitForScout) {
	    allianceUnitInfo.setScoutUnit(unitForScout);
	    searchQueue.addAll(locationManager.getSearchSequence());
	    Log.info("정찰 시작: unitId=%d", unitForScout.getID());
	} else {
	    Log.warn("정찰 가능한 유닛이 없습니다.");
	    result = false;
	}
	return result;
    }

    private void searchEnemyBase(Unit2 scout) {

	if (scout == null) {
	    return;
	}

	TilePosition currentTilePosition = scout.getTilePosition();
	TilePosition nextTilePosition = null;
	List<TilePosition> enemyBaseEdge = locationManager.getEnemyBaseSearchSequence();

	if (currentTilePosition.getDistance(enemyBaseEdge.get(0)) <= 2) {
	    searchFlag = 1;
	} else if (currentTilePosition.getDistance(enemyBaseEdge.get(1)) <= 2) {
	    searchFlag = 2;
	} else if (currentTilePosition.getDistance(enemyBaseEdge.get(2)) <= 2) {
	    searchFlag = 3;
	} else if (currentTilePosition.getDistance(enemyBaseEdge.get(3)) <= 2) {
	    searchFlag = 0;
	}

	if (searchFlag == 1) {
	    nextTilePosition = enemyBaseEdge.get(1);
	} else if (searchFlag == 2) {
	    nextTilePosition = enemyBaseEdge.get(2);
	} else if (searchFlag == 3) {
	    nextTilePosition = enemyBaseEdge.get(3);
	} else if (searchFlag == 0) {
	    nextTilePosition = enemyBaseEdge.get(0);
	}

	ActionUtil.moveToPosition(allianceUnitInfo, scout, nextTilePosition);
	Log.debug("%s 정찰 SCV(%s)을 타일(%s)로 이동.", scout, scout.getTilePosition(), nextTilePosition);
    }
}
