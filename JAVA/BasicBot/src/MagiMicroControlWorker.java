import java.util.Set;

import bwapi.TilePosition;
import bwapi.Unit;

// 매딕을 컨트롤 한다.
public class MagiMicroControlWorker extends Manager {
    private static MagiMicroControlWorker instance = new MagiMicroControlWorker();

    public static MagiMicroControlWorker Instance() {
	return instance;
    }

    private LocationManager locationManager = null;;
    private MagiWorkerManager workerManager = MagiWorkerManager.Instance();
    private Unit enemyWorker = null;
    private Unit enemyWorkerKiller = null;

    @Override
    protected void onStart(GameStatus gameStatus) {
	locationManager = gameStatus.getLocationManager();
	super.onStart(gameStatus);
    }

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

	TilePosition allianceBaseTilePosition = locationManager.getAllianceBaseLocation();

	Set<Integer> enemyWorkerIdSet = enemyUnitManager.getUnitIdSetByUnitKind(UnitKind.Worker);
	Unit closestEnemyWorker = enemyUnitManager.getClosestUnit(enemyWorkerIdSet, allianceBaseTilePosition.toPosition());
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
		Unit allianceWorker = workerManager.getInterruptableWorker(closestEnemyWorker.getTilePosition());
		if (null != allianceWorker) {
		    Log.info("아군 일꾼(%d)이 본진에 들어온 적군 일꾼(%d)를 공격한다.", allianceWorker.getID(), closestEnemyWorker.getID());
		    assignEnemyWorkerKiller(closestEnemyWorker, allianceWorker);
		}
	    }
	}
    }

    // 본진에 침입한 적군 일꾼을 아군 일꾼이 공격한다.
    private void assignEnemyWorkerKiller(Unit closestEnemyWorker, Unit allianceWorker) {
	enemyWorker = closestEnemyWorker;
	ActionUtil.attackEnemyUnit(allianceUnitManager, allianceWorker, closestEnemyWorker);
	enemyWorkerKiller = allianceWorker;
	allianceUnitManager.removeUnitKind(UnitKind.Worker, enemyWorkerKiller);
    }

    // 본진에 침입한 적군 일꾼을 아군 일꾼이 그만 공격한다.
    private void releaseEnemyWorkerKiller() {
	allianceUnitManager.addUnitKind(UnitKind.Worker, enemyWorkerKiller);
	ActionUtil.stop(allianceUnitManager, enemyWorkerKiller);
	enemyWorkerKiller = null;
	enemyWorker = null;
    }
}