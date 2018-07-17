import java.util.Set;

import bwapi.TilePosition;

// 매딕을 컨트롤 한다.
public class MicroControlWorker extends Manager {
    private Unit2 enemyWorker = null;
    private Unit2 enemyWorkerKiller = null;

    @Override
    protected void onFrame() {
	super.onFrame();

	// SCV는 42프레임(1초)에 1번만 컨트롤 한다.
	if (0 == gameStatus.getFrameCount() || gameStatus.getFrameCount() % 42 != 0) {
	    return;
	}

	// 본진에 들어온 적군 일꾼을 죽인다.
	killEnemyWorker();

    }

    // 본진에 있는 적군 일꾼 하나를 찾아서 마린으로 공격한다.
    private void killEnemyWorker() {
	if (enemyWorkerKiller != null) {
	    if (!enemyWorkerKiller.exists()) {
		// 본진에 들어온 적 일꾼을 공격하던 아군 일꾼이 파괴된 상황...
		Log.info("본진에 들어온 적 일꾼을 공격하던 아군 일꾼(%d)이 파괴되었다.", enemyWorkerKiller.getID());
		enemyWorkerKiller = null;
		return;
	    }
	    if (null != enemyWorker && (!enemyWorker.exists() || !enemyWorker.isVisible())) {
		Log.info("아군 일꾼(%d)이 본진에 들어온 적 일꾼을 파괴하였다.", enemyWorkerKiller.getID());
		releaseEnemyWorkerKiller();
		return;
	    }
	}

	LocationManager locationManager = gameStatus.getLocationManager();
	TilePosition allianceBaseTilePosition = locationManager.getAllianceBaseLocation();

	Set<Unit2> enemyWorkerSet = enemyUnitInfo.getUnitSet(UnitKind.Worker);
	Unit2 closestEnemyWorker = enemyUnitInfo.getClosestUnit(enemyWorkerSet, allianceBaseTilePosition.toPosition());
	if (null != closestEnemyWorker) {
	    int distance = closestEnemyWorker.getDistance(allianceBaseTilePosition.toPosition());
	    Log.debug("적군 일꾼이(%d) 아군 영역에 들어왔다. 거리: %d", closestEnemyWorker.getID(), distance);

	    if (null != enemyWorkerKiller) {
		// 적 일꾼을 추적하는 아군 일꾼이 있는 상태
		if (enemyWorkerKiller.exists()) {
		    Log.debug("아군 일꾼(%d)이 열심히 적 일꾼을 쫓고 있다.", enemyWorkerKiller.getID());
		    if (distance >= 1000) {
			// 적 일꾼이 아군 본진에서 멀리 물러나면, 아군 일꾼도 공격을 멈춘다.
			Log.info("아군 일꾼(%d)이 적 일꾼(%d, 본진과의 거리: %d)을 그만 쫓고 본진으로 회군한다.", enemyWorkerKiller.getID(), closestEnemyWorker.getID(), distance);
			releaseEnemyWorkerKiller();
		    }
		}
	    } else if (1000 > distance) {
		// 적 일꾼이 아군 본진까지 다가왔다. 적에게 가장 가까운 아군 일꾼 하나를 선택한다.
		WorkerManager workerManager = gameStatus.getWorkerManager();
		Unit2 allianceWorker = workerManager.getInterruptableWorker(closestEnemyWorker.getTilePosition());
		if (null != allianceWorker) {
		    Log.info("아군 일꾼(%d)이 본진에 들어온 적군 일꾼(%d)를 공격한다.", allianceWorker.getID(), closestEnemyWorker.getID());
		    assignEnemyWorkerKiller(closestEnemyWorker, allianceWorker);
		}
	    }
	}
    }

    // 본진에 침입한 적군 일꾼을 아군 일꾼이 공격한다.
    private void assignEnemyWorkerKiller(Unit2 closestEnemyWorker, Unit2 allianceWorker) {
	enemyWorker = closestEnemyWorker;
	ActionUtil.attackEnemyUnit(allianceUnitInfo, allianceWorker, closestEnemyWorker);
	enemyWorkerKiller = allianceWorker;
	allianceUnitInfo.removeUnitKind(UnitKind.Worker, enemyWorkerKiller);
    }

    // 본진에 침입한 적군 일꾼을 아군 일꾼이 그만 공격한다.
    private void releaseEnemyWorkerKiller() {
	allianceUnitInfo.addUnitKind(UnitKind.Worker, enemyWorkerKiller);
	ActionUtil.stop(allianceUnitInfo, enemyWorkerKiller);
	enemyWorkerKiller = null;
	enemyWorker = null;
    }
}