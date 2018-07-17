import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import bwapi.TilePosition;

public class EliminateManager extends Manager {
    private Queue<TilePosition> eliminateQueue = new LinkedList<>();

    public void search(UnitManager allianceUnitManager) {
	if (eliminateQueue.isEmpty()) {
	    initQueue();
	}
	Set<Unit2> combatUnitSet = allianceUnitManager.getUnitSet(UnitKind.Combat_Unit);
	for (Unit2 combatUnit : combatUnitSet) {
	    if (null != combatUnit && combatUnit.isIdle() && !eliminateQueue.isEmpty()) {
		TilePosition tilePosition = eliminateQueue.poll();
		ActionUtil.moveToPosition(allianceUnitManager, combatUnit, tilePosition.toPosition());
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
