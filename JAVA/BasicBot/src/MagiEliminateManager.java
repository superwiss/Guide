import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import bwapi.TilePosition;
import bwapi.Unit;

/// 게임 초반에 일꾼 유닛 중에서 정찰 유닛을 하나 지정하고, 정찰 유닛을 이동시켜 정찰을 수행하는 class<br>
/// 적군의 BaseLocation 위치를 알아내는 것까지만 개발되어있습니다
public class MagiEliminateManager {

    private static MagiEliminateManager instance = new MagiEliminateManager();

    /// static singleton 객체를 리턴합니다
    public static MagiEliminateManager Instance() {
	return instance;
    }

    private Queue<TilePosition> eliminateQueue = new LinkedList<>();

    public void onFrame(GameData gameData) {
    }

    public void search(UnitManager allianceUnitManager) {
	if (eliminateQueue.isEmpty()) {
	    initQueue();
	}
	Set<Integer> combatUnitIds = allianceUnitManager.getUnitsIdByUnitKind(UnitKind.Combat_Unit);
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