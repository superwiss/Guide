import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import bwapi.TilePosition;

public class EliminateManager extends Manager {
    private StrategyManager strategyManager;
    private Queue<TilePosition> eliminateQueue = new LinkedList<>();

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);
	this.strategyManager = gameStatus.getStrategyManager();
    }

    @Override
    protected void onFrame() {
	super.onFrame();

	if (gameStatus.isMatchedInterval(3)) {

	    // 게임 10분이 넘은 시점부터 동작한다.
	    if (gameStatus.getFrameCount() < 24 * 60 * 10) {
		return;
	    }

	    if (strategyManager.hasStrategyStatus(StrategyStatus.SEARCH_FOR_ELIMINATE)) {
		// 적 건물이 하나도 없으면 맵 전체를 탐색한다.
		if (0 == enemyUnitInfo.getUnitSet(UnitKind.Building).size()) {
		    Log.info("Eliminate mode working. EnemyBase: %s", gameStatus.getLocationManager().getEnemyStartLocation());
		    search();
		} else {
		    Log.info("Eliminate mode finish.");
		    strategyManager.removeStrategyStatus(StrategyStatus.SEARCH_FOR_ELIMINATE);
		}
	    }

	}
    }

    public void search() {
	if (eliminateQueue.isEmpty()) {
	    initQueue();
	}
	Set<Unit2> combatUnitSet = allianceUnitInfo.getUnitSet(UnitKind.Combat_Unit);
	for (Unit2 combatUnit : combatUnitSet) {
	    if (null != combatUnit && combatUnit.isIdle() && !eliminateQueue.isEmpty()) {
		TilePosition tilePosition = eliminateQueue.poll();
		ActionUtil.moveToPosition(allianceUnitInfo, combatUnit, tilePosition.toPosition());
		Log.info(" MagiEliminateManager.search(). Unit(%s) -> tilePosition(%s)", combatUnit, tilePosition);
	    }
	}
    }

    private void initQueue() {
	for (int i = 0; i < 128; i += 16) {
	    for (int j = 0; j < 128; j += 16) {
		eliminateQueue.add(new TilePosition(i, j));
	    }
	}

    }
}
