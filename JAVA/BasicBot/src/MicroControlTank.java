import java.util.HashSet;
import java.util.Set;

import bwapi.Color;
import bwapi.Position;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.UnitType;

// 매딕을 컨트롤 한다.
public class MicroControlTank extends Manager {
    private StrategyManager strategyManager = null;
    private LocationManager locationManager = null;

    public static final int SIEGE_MODE_MIN_RANGE = UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().minRange(); // 64
    public static final int SIEGE_MODE_MAX_RANGE = UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().maxRange(); // 384

    public MicroControlTank() {
    }

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);
	strategyManager = gameStatus.getStrategyManager();
	locationManager = gameStatus.getLocationManager();
    }

    @Override
    protected void onFrame() {
	super.onFrame();

	if (gameStatus.isMatchedInterval(2)) {
	    // 선두 유닛이 너무 앞서가면, 뒤따로 오는 유닛을 기다린다.
	    if (strategyManager.hasStrategyItem(StrategyItem.AUTO_TRAIN_MECHANIC_UNIT)) {
		waitMechanicUnit();
	    }
	}

	if (gameStatus.isMatchedInterval(1)) {
	    if (strategyManager.hasStrategyItem(StrategyItem.ALLOW_PHASE)) {
		//페이즈에 따라 목표지점을 달리한다.
		followPhase();
	    }
	}

	doSiegeMode();
	doTankMode();

	aggressiveMoveAttack();
    }

    private void doTankMode() {

	boolean needUnSiege = false;
	Set<Unit2> tankSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Siege_Tank_Siege_Mode);

	for (Unit2 tank : tankSet) {

	    Unit2 target = getTarget(tank);

	    if (target == null) {
		needUnSiege = true;
	    }

	    if (needUnSiege && tank.canUnsiege()) {
		tank.unsiege();
	    }
	}

    }

    private void doSiegeMode() {

	boolean needSiege = false;
	Set<Unit2> tankSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Siege_Tank_Tank_Mode);

	for (Unit2 tank : tankSet) {

	    Unit2 target = getTarget(tank);
	    if (target == null) {
		return;
	    }

	    int distanceToTarget = tank.getDistance(target);

	    if (distanceToTarget <= SIEGE_MODE_MAX_RANGE + 5) {
		needSiege = true;
		tank.siege();
	    }

	    if (needSiege) {
		tank.siege();
	    }
	}

    }

    private Unit2 getTarget(Unit2 tank) {

	Set<Unit2> enemeyUnit = enemyUnitInfo.getAllUnits();
	Unit2 bestTarget = null;
	for (Unit2 enemy : enemeyUnit) {

	    if (enemy.isFlying()) {
		continue;
	    }

	    int distanceToTarget = tank.getDistance(enemy);
	    if (distanceToTarget <= SIEGE_MODE_MAX_RANGE + 50) {
		bestTarget = enemy;
	    }

	}
	return bestTarget;
    }

    private void followPhase() {

	Set<Unit2> tankSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Siege_Tank);
	for (Unit2 tank : tankSet) {
	    if (!tank.exists()) {
		Log.warn("[MicroControlMarine:checkIfUsingStimPack] 탱크(%s)가 exist 하지 않습니다.", tank);
	    } else {

		if (strategyManager.getPhase() == 0) {

		    TilePosition tankPosition = locationManager.getBaseTankPoint().get(0);
		    Set<Unit2> tankSet2 = allianceUnitInfo.findUnitSetNearTile(tankPosition, UnitKind.Terran_Siege_Tank, 80);

		    if (tankSet2.size() < 4) {
			tank.attack(tankPosition.toPosition());
		    } else {
			tank.attack(locationManager.getFirstExtensionChokePoint().toPosition());
		    }

		    for (Unit2 tank2 : tankSet2) {
			if (!tank2.isSieged() && tank2.canSiege() && tankSet2.size() <= 4) {
			    tank2.siege();
			}
		    }

		} else if (strategyManager.getPhase() == 1) {

		    TilePosition tankPosition2 = locationManager.getFirstExtensionChokePoint();
		    TilePosition baseTankPosition = locationManager.getBaseTankPoint().get(0);

		    if (tank.getDistance(tankPosition2.toPosition()) > 500) {
			if (tank.getDistance(baseTankPosition.toPosition()) < 80) {

			} else {
			    if (tank.canUnsiege()) {
				tank.unsiege();
			    }
			}
		    }

		    if (tankPosition2 != null) {
			tank.attack(tankPosition2.toPosition());
		    }

		    Set<Unit2> tankSet3 = allianceUnitInfo.findUnitSetNearTile(tankPosition2, UnitKind.Terran_Siege_Tank, 450);
		    for (Unit2 tank3 : tankSet3) {
			if (!tank3.isSieged() && tank3.canSiege()) {
			    tank3.siege();
			}
		    }

		} else if (strategyManager.getPhase() == 2) {

		    TilePosition tankPosition = locationManager.getTwoPhaseChokePoint();

		    if (tankPosition != null) {
			if (tank.getDistance(tankPosition.toPosition()) > 230) {
			    if (tank.canUnsiege()) {
				tank.unsiege();
			    }
			}

			tank.attack(tankPosition.toPosition());

			Set<Unit2> tankSet3 = allianceUnitInfo.findUnitSetNearTile(tankPosition, UnitKind.Terran_Siege_Tank, 230);
			for (Unit2 tank3 : tankSet3) {
			    if (!tank3.isSieged() && tank3.canSiege()) {
				tank3.siege();
			    }
			}
		    }

		} else if (strategyManager.getPhase() == 3) {

		    TilePosition tankPosition = locationManager.getThreePhaseChokePointForSiege();
		    if (tankPosition != null) {

			if (tank.getDistance(tankPosition.toPosition()) > 600) {
			    if (tank.canUnsiege()) {
				tank.unsiege();
			    }
			}

			tank.attack(tankPosition.toPosition());

			Set<Unit2> tankSet3 = allianceUnitInfo.findUnitSetNearTile(tankPosition, UnitKind.Terran_Siege_Tank, 600);
			for (Unit2 tank3 : tankSet3) {
			    if (!tank3.isSieged() && tank3.canSiege()) {
				tank3.siege();
			    }
			}
		    }
		}
	    }
	}
    }

    // 선두 메카닉 유닛 400 주변에 메카닉 유닛이 20마리 미만이라면, 모든 유닛이 적군으로의 진군을 일단 멈추고 선두 유닛쪽에 모인다.
    private void waitMechanicUnit() {
	//공격을 갈 지점이 있을 경우에만 컨트롤을 한다.
	if (true == strategyManager.hasAttackTilePosition()) {
	    Position attackPosition = strategyManager.getAttackTilePositon().toPosition();
	    // 공격 지점을 이미 정복했고, 적 건물이 존재하지 않으면 아무것도 하지 않는다.
	    if (gameStatus.isExplored(attackPosition.toTilePosition()) && 0 == enemyUnitInfo.getUnitSet(UnitKind.Building).size()) {
		return;
	    }

	    Unit2 headMechanicUnit = allianceUnitInfo.getHeadAllianceUnit(UnitKind.Mechanic_Unit, attackPosition);
	    if (null != headMechanicUnit) {
		// 선두 메카닉 유닛 300 주변의 메카닉 갯수
		int headGroupSize = allianceUnitInfo.getUnitsInRange(headMechanicUnit.getPosition(), UnitKind.Mechanic_Unit, 300).size();
		if (20 > headGroupSize) {
		    Position headPosition = headMechanicUnit.getPosition();
		    Log.info("선두 메카닉(%s) 주변에 다른 메카닉이 %d명 밖에 없어서, 선두 위치로 모든 유닛이 이동한다. 선두 메카닉 위치: %s, 집결 위치: %s", headMechanicUnit, headGroupSize, headMechanicUnit.getPosition(),
			    headPosition);
		    attackAll(headPosition);
		} else {
		    Log.info("선두 메카닉(%s) 주변에 다른 메카닉이 충분하게도 %d명이나 있다. 총 공격한다.", headMechanicUnit, headGroupSize);
		    attackAll(attackPosition);
		}
	    } else {
		int mechanicSize = 0;
		Set<Unit2> mechanicUnits = allianceUnitInfo.getUnitSet(UnitKind.Mechanic_Unit);
		if (null != mechanicUnits) {
		    mechanicSize = mechanicUnits.size();
		}
		Log.info("선두 메카닉이 없습니다. 메카닉 공격 유닛 개수: %d", mechanicSize);
	    }
	}
    }

    // position 위치로 모든 유닛이 공격간다.
    private void attackAll(Position position) {
	Set<Unit2> attackableUnitSet = allianceUnitInfo.getUnitSet(UnitKind.Combat_Unit);
	for (Unit2 attackableUnit : attackableUnitSet) {
	    ActionUtil.attackPosition(allianceUnitInfo, attackableUnit, position);
	}
    }

    private void aggressiveMoveAttack() {
	if (!strategyManager.hasStrategyItem(StrategyItem.AGGRESSIVE_MOVE_ATTACK)) {
	    return;
	}

	if (false == strategyManager.hasAttackTilePosition()) {
	    return;
	}

	Set<TilePosition> hillTilePositionSet = gameStatus.getLocationManager().getHillTilePosition();

	TilePosition attackPosition = strategyManager.getAttackTilePositon();

	Set<Unit2> unitSet = allianceUnitInfo.getUnitSet(UnitKind.Mechanic_Unit);
	for (Unit2 unit : unitSet) {
	    if (unit.exists()) {
		if (hillTilePositionSet.contains(unit.getTilePosition())) {
		    if (unit.getGroundWeaponCooldown() < 6) {
			ActionUtil.attackPosition(allianceUnitInfo, unit, attackPosition);
		    } else {
			ActionUtil.moveToPosition(allianceUnitInfo, unit, attackPosition);
		    }
		}
	    }
	}
    }

}