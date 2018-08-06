import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import bwapi.TilePosition;
import bwapi.UnitType;

public class EliminateManager extends Manager {
    private static final String TAG = "[EliminateManager]";
    private StrategyManager strategyManager;
    private BuildManager buildManager;
    private Queue<TilePosition> eliminateQueue = new LinkedList<>();

    private boolean hasFactory = false;
    private boolean hasStarport = false;

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);
	this.strategyManager = gameStatus.getStrategyManager();
	this.buildManager = gameStatus.getBuildManager();
    }

    @Override
    protected void onFrame() {
	super.onFrame();

	if (gameStatus.isMatchedInterval(1)) {
	    checkEnemyBuildingLocation();
	}

	if (gameStatus.isMatchedInterval(20)) {
	    checkEnemyBuildingLocation();
	    searchByWraith();
	}

	if (gameStatus.isMatchedInterval(3)) {

	    // 게임 10분이 넘은 시점부터 동작한다.
	    if (gameStatus.getFrameCount() < 24 * 60 * 10) {
		return;
	    }

	    if (strategyManager.hasStrategyStatus(StrategyStatus.SEARCH_FOR_ELIMINATE)) {
		// 적 건물이 하나도 없으면 맵 전체를 탐색한다.
		if (false == enemyUnitInfo.hasLandedBuilding()) {
		    Log.info("%s Eliminate mode working. EnemyBase: %s", TAG, gameStatus.getLocationManager().getEnemyStartLocation());
		    searchByGroundUnit();
		    buildStartWraith();
		} else {
		    Log.info("%s Eliminate mode finish.", TAG);
		    strategyManager.removeStrategyStatus(StrategyStatus.SEARCH_FOR_ELIMINATE);
		}
	    }
	}
    }

    public void searchByGroundUnit() {
	if (eliminateQueue.isEmpty()) {
	    initQueue();
	}
	Set<Unit2> combatUnitSet = allianceUnitInfo.getUnitSet(UnitKind.Combat_Unit);
	for (Unit2 combatUnit : combatUnitSet) {
	    if (null != combatUnit && combatUnit.isIdle() && !eliminateQueue.isEmpty() && false == combatUnit.isFlying()) {
		TilePosition tilePosition = eliminateQueue.poll();
		ActionUtil.moveToPosition(allianceUnitInfo, combatUnit, tilePosition.toPosition());
		Log.info("%s MagiEliminateManager.search(). Unit(%s) -> tilePosition(%s)", TAG, combatUnit, tilePosition);
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

    private void buildStartWraith() {
	if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Factory).size() <= 0 && false == hasFactory) {
	    BuildOrderItem buildOrderItem = new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory);
	    if (!buildManager.hasBuildOrderItem(buildOrderItem)) {
		buildManager.addFirst(buildOrderItem);
		Log.info("%s 팩토리를 건설한다.: %s", TAG, buildOrderItem);
		hasFactory = true;
	    }

	} else {
	    if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Starport).size() <= 0 && false == hasStarport) {
		BuildOrderItem buildOrderItem = new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Starport);
		if (!buildManager.hasBuildOrderItem(buildOrderItem)) {
		    buildManager.addFirst(buildOrderItem);
		    Log.info("%s 스타포트를 건설한다.: %s", TAG, buildOrderItem);
		    hasStarport = true;
		}
	    } else {
		Unit2 startPort = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Starport);
		if (null != startPort && startPort.canTrain(UnitType.Terran_Wraith) && startPort.getTrainingQueue().size() < 1) {
		    startPort.train(UnitType.Terran_Wraith);
		    Log.info("%s 스타포트에서 레이스를 생산한다.%s", TAG, startPort);
		}
	    }
	}
    }

    private void checkEnemyBuildingLocation() {
	enemyUnitInfo.updateLastTilePosition();
    }

    private void searchByWraith() {
	Set<Unit2> wraithSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Wraith);
	for (Unit2 wraith : wraithSet) {
	    TilePosition currentTilePosition = wraith.getTilePosition();
	    int nextTilePositionX = currentTilePosition.getX();
	    int nextTilePositionY = currentTilePosition.getY();
	    if (currentTilePosition.getX() >= 125 && currentTilePosition.getY() >= 125) {
		nextTilePositionX = 0;
		nextTilePositionY = 0;
	    } else if (currentTilePosition.getX() <= 125) {
		nextTilePositionX = 127;
	    } else if (currentTilePosition.getX() > 125) {
		nextTilePositionX = 0;
		nextTilePositionY += 10;
	    }
	    TilePosition tilePosition = new TilePosition(nextTilePositionX, nextTilePositionY);
	    ActionUtil.attackPosition(allianceUnitInfo, wraith, tilePosition);
	    Log.debug("%s wraith(%s , %s)을 타일(%s)로 이동.", TAG, wraith, wraith.getTilePosition(), tilePosition);
	}
    }

}
