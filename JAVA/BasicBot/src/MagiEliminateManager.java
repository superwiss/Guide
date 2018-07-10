import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import bwapi.TilePosition;
import bwapi.Unit;

public class MagiEliminateManager extends Manager {

    private static MagiEliminateManager instance = new MagiEliminateManager();

    public static MagiEliminateManager Instance() {
	return instance;
    }

    private Queue<TilePosition> eliminateQueue = new LinkedList<>();

    public void search(UnitManager allianceUnitManager) {
	if (eliminateQueue.isEmpty()) {
	    initQueue();
	}
	Set<Integer> combatUnitIds = allianceUnitManager.getUnitIdSetByUnitKind(UnitKind.Combat_Unit);
	for (Integer combatUnitId : combatUnitIds) {
	    Unit unit = allianceUnitManager.getUnit(combatUnitId);
	    if (null != unit && unit.isIdle() && !eliminateQueue.isEmpty()) {
		TilePosition tilePosition = eliminateQueue.poll();
		ActionUtil.moveToPosition(allianceUnitManager, unit, tilePosition.toPosition());
		Log.info(" MagiEliminateManager.search(). Unit(%d) -> tilePosition(%s)", unit.getID(), tilePosition);
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
