import bwapi.Unit;

public class MagiGameStatusManager extends Manager {

    private static MagiGameStatusManager instance = new MagiGameStatusManager();

    public static MagiGameStatusManager Instance() {
	return instance;
    }

    private UnitManager allianceUnitManager = null;
    private UnitManager enemyUnitManager = null;

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);

	this.allianceUnitManager = gameStatus.getAllianceUnitManager();
	this.enemyUnitManager = gameStatus.getEnemyUnitManager();
    }

    @Override
    protected void onUnitDestroy(Unit unit) {
	super.onUnitDestroy(unit);

	if (true == UnitUtil.isAllianceUnit(unit)) {
	    allianceUnitManager.remove(unit);
	} else if (true == UnitUtil.isEnemyUnit(unit)) {
	    enemyUnitManager.remove(unit);
	} else {
	    // else 상황 = 즉 중립 건물, 중립 동물에 대해서는 아무런 처리도 하지 않는다.
	}
    }

    @Override
    protected void onUnitDiscover(Unit unit) {
	super.onUnitDiscover(unit);

	if (true == UnitUtil.isAllianceUnit(unit)) {
	    allianceUnitManager.add(unit);
	} else if (true == UnitUtil.isEnemyUnit(unit)) {
	    enemyUnitManager.add(unit);
	} else {
	    // else 상황 = 즉 중립 건물, 중립 동물에 대해서는 아무런 처리도 하지 않는다.
	}
	if (unit.getType().isMineralField()) {
	    allianceUnitManager.add(unit);
	}
    }
}