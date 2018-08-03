import java.util.HashSet;
import java.util.Set;

import bwapi.Position;
import bwapi.TechType;
import bwapi.UnitType;

public class MicroControlVessel extends Manager {
    private final Set<UnitType> vesselUnitTypeSet = new HashSet<>();
    private StrategyManager strategyManager = null;
    private LocationManager locationManager = null;

    private final static double vesselSpeed = UnitType.Terran_Science_Vessel.topSpeed() * 8;
    private final static int vesselRadius = UnitType.Terran_Science_Vessel.sightRange();
    private boolean avoidFlag;

    public MicroControlVessel() {
	vesselUnitTypeSet.add(UnitType.Terran_Science_Vessel);
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

	// 42프레임(1초)에 1번만 컨트롤 한다.
	if (gameStatus.isMatchedInterval(1)) {
	    followMechanicUnit();
	}

	avoidEnemyUnit();
	useDefenseive();
    }

    private void useDefenseive() {

	Set<Unit2> vesselSet = allianceUnitInfo.getUnitSet(UnitType.Terran_Science_Vessel);
	for (Unit2 vessel : vesselSet) {
	    if (vessel.getEnergy() >= 100) {

		Set<Unit2> mechanicNearVessel = null;
		mechanicNearVessel = allianceUnitInfo.findUnitSetNearTile(vessel.getTilePosition(), UnitKind.Mechanic_Unit, vesselRadius);

		for (Unit2 mechanic : mechanicNearVessel) {
		    
		    if(mechanic.getType() == UnitType.Terran_Vulture){
			//벌쳐에게 쓰긴 아깝다
			continue;
		    }
		    
		    if (mechanic.isUnderAttack() && mechanic.getHitPoints() < mechanic.getType().maxHitPoints() * 0.7 && !mechanic.isDefenseMatrixed()
			    && mechanic.getHitPoints() > mechanic.getType().maxHitPoints() * 0.15) {
			vessel.useTech(TechType.Defensive_Matrix, mechanic);
			return;
		    }
		}
	    }
	}
    }

    private void avoidEnemyUnit() {

	Set<Unit2> vesselSet = allianceUnitInfo.getUnitSet(UnitType.Terran_Science_Vessel);
	for (Unit2 vessel : vesselSet) {

	    Set<Unit2> antiVesselSet = enemyUnitInfo.getUnitSet(UnitKind.AntiVessel_Unit);
	    Unit2 mostDangerUnit = null;
	    double mostDangerDistance = -9999;
	    for (Unit2 dangerUnit : antiVesselSet) {

		double dangerDistance = dangerUnit.getType().airWeapon().maxRange() - dangerUnit.getDistance(vessel.getPosition());
		if (dangerDistance > mostDangerDistance) {
		    mostDangerUnit = dangerUnit;
		    mostDangerDistance = dangerDistance;
		}
	    }

	    avoidFlag = false;
	    if (mostDangerUnit != null) {

		double vSpeed = 0;
		if (!mostDangerUnit.getType().isBuilding()) {
		    vSpeed = vesselSpeed * 3;
		}

		if (mostDangerUnit.isInWeaponRange(vessel) || (vessel.getDistance(mostDangerUnit) <= mostDangerUnit.getType().airWeapon().maxRange() + vSpeed)) {
		    Position backPosition = UnitUtil.getBackPosition(vessel.getPosition(), mostDangerUnit.getPosition());
		    ActionUtil.moveToPosition(allianceUnitInfo, vessel, backPosition, 100);
		    avoidFlag = true;
		}
	    }
	}
    }

    private void followMechanicUnit() {

	if (strategyManager.isSkipMicroControl()) {
	    return;
	}

	Position newPosition = null;
	Set<Unit2> mechanicSet = null;
	if (true == strategyManager.hasAttackTilePosition()) {
	    Position attackPosition = strategyManager.getAttackTilePositon().toPosition();
	    mechanicSet = allianceUnitInfo.getUnitSet(UnitKind.Mechanic_Unit);
	    // 공격 목표 지점에서 가장 가까운 선두 유닛을 구한다.
	    Unit2 headUnit = allianceUnitInfo.getClosestUnit(mechanicSet, attackPosition, vesselUnitTypeSet);

	    if (null == headUnit) {
		Log.warn("선두 유닛이 존재하지 않아서 베슬을 컨트롤 할 수 없습니다.");
		return;
	    } else if (headUnit.getPosition().equals(attackPosition)) {
		// 선두 유닛이 이미 공격 지점에 위치하고 있다면 베슬도 그 지점으로 이동한다.
		newPosition = attackPosition;
	    } else if (null != headUnit) {
		// TODO 적절한 베슬의 위치선정 필요
		newPosition = UnitUtil.getPositionAsDistance(headUnit.getPosition(), attackPosition, 5);
	    }

	    if (null == newPosition) {
		Log.warn("베슬이 이동할 수 있는 거리를 계산하지 못했습니다. 바이오닉 유닛 개수=%d, 바이오닉 선두 위치=%s, 바이오닉 공격 대상 위치=%s, 베슬 이동 위치: null", mechanicSet.size(),
			null != headUnit ? headUnit.getTilePosition() : "null", attackPosition);
		//본진으로 복귀
		newPosition = locationManager.getFirstExtensionChokePoint().toPosition();
	    }

	    Set<Unit2> vesselSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Science_Vessel);
	    for (Unit2 vessel : vesselSet) {
		if (avoidFlag == false) {
		    boolean updated = ActionUtil.moveToPosition(allianceUnitInfo, vessel, newPosition);
		    if (updated) {
			Log.debug("베슬(%s)을 %s 위치로 이동한다.", vessel, newPosition);
		    }
		}
	    }
	}
    }
}