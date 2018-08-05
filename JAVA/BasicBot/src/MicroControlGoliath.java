import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.UnitType;

public class MicroControlGoliath extends Manager {
    private final Set<UnitType> goliathUnitTypeSet = new HashSet<>();

    private StrategyManager strategyManager = null;
    private LocationManager locationManager = null;

    public MicroControlGoliath() {
	goliathUnitTypeSet.add(UnitType.Terran_Goliath);
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
		//		waitMechanicUnit();
	    }
	}

	if (gameStatus.isMatchedInterval(3)) {
	    if (strategyManager.hasStrategyItem(StrategyItem.ALLOW_PHASE)) {
		//페이즈에 따라 목표지점을 달리한다.
		followPhase();
	    }
	}

	if (gameStatus.isMatchedInterval(1)) {
	    //	    followMechanicUnit();
	}

	fullyAttack();
    }

    private void followPhase() {

	Set<Unit2> goliathSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Goliath);

	for (Unit2 goliath : goliathSet) {
	    if (!goliath.exists()) {
		Log.warn("[MicroControlMarine:checkIfUsingStimPack] 골리앗(%s)이 exist 하지 않습니다.", goliath);
	    } else {
		Position attackPosition = null;
		if (strategyManager.getPhase() == 0) {
		    attackPosition = locationManager.getBaseEntranceChokePoint().toPosition();
		} else if (strategyManager.getPhase() == 1) {
		    attackPosition = locationManager.getSecondExtensionChokePoint().toPosition();
		} else if (strategyManager.getPhase() == 2) {
		    attackPosition = locationManager.getTwoPhaseChokePoint().toPosition();
		} else if (strategyManager.getPhase() == 3) {
		    attackPosition = locationManager.getThreePhaseChokePointForMech().toPosition();
		}

		if (attackPosition != null && !strategyManager.hasStrategyStatus(StrategyStatus.BACK_TO_BASE)) {
		    strategyManager.setAttackTilePosition(attackPosition.toTilePosition());
		    if (goliath.getDistance(attackPosition) > 300) {
			ActionUtil.moveToPosition(allianceUnitInfo, goliath, attackPosition);
		    } else {
			ActionUtil.attackPosition(allianceUnitInfo, goliath, attackPosition);
		    }
		}
	    }
	}
    }

    private void fullyAttack() {
	if (!strategyManager.hasStrategyStatus(StrategyStatus.ATTACK)) {
	    return;
	}

	if (strategyManager.hasStrategyStatus(StrategyStatus.BACK_TO_BASE)) {
	    return;
	}

	// 아군 유닛들 목록
	Set<Unit2> allianceUnitSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Goliath);

	// key: 적군 유닛, value: key를 공격할 수 있는 아군 유닛들 목록
	Map<Unit2, List<Unit2>> attackableUnitMap = new HashMap<>();

	for (Unit2 enemyUnit : enemyUnitInfo.getUnitSet(UnitKind.Combat_Unit)) {
	    // 적이 존재하기 않거나 보이지 않는다면 skip하고 다음 적군 유닛 계산
	    if (false == enemyUnit.isVisible() || false == enemyUnit.exists()) {
		continue;
	    }

	    // 적군을 공격할 수 있는 아군 유닛 리스트 초기화
	    List<Unit2> attackableUnitList = new ArrayList<>(allianceUnitSet.size());
	    attackableUnitMap.put(enemyUnit, attackableUnitList);

	    for (Unit2 allianceUnit : allianceUnitSet) {
		int distance = UnitUtil.getDistance(allianceUnit, enemyUnit);
		int groundWeaponDistance = allianceUnit.getType().groundWeapon().maxRange();
		if (allianceUnit.canAttack(enemyUnit) && allianceUnit.isInWeaponRange(enemyUnit)) {
//		    Log.trace("아군(%s)이 적군(%s)를 공격할 수 있음. distance=%d, weaponDistance=%d, enemyHP=%d", allianceUnit, enemyUnit, distance, groundWeaponDistance,
//			    enemyUnit.getHitPoints());
		    attackableUnitList.add(allianceUnit);
		}
	    }

	    if (attackableUnitList.isEmpty()) {
		attackableUnitMap.remove(enemyUnit);
	    }
	}
	Comparator<Unit2> comparatorByHP = (u1, u2) -> u1.getHitPoints() - u2.getHitPoints();
	List<Unit2> sortedEnemyUnitList = Util.<Unit2, Integer>mapSortByValue(attackableUnitMap, comparatorByHP);

	Comparator<Unit2> comparatorByUnitDistance = (u1, u2) -> UnitUtil.getDistance(u1, u2);
	Set<Unit2> attackFinishSet = new HashSet<>();
	for (Unit2 enemyUnit : sortedEnemyUnitList) {
//	    Log.trace("enemy unit 순서 : unit=%s, hp=%d", enemyUnit, enemyUnit.getHitPoints());
	    List<Unit2> attackableAllianceUnitList = attackableUnitMap.get(enemyUnit);
	    Collections.sort(attackableAllianceUnitList, comparatorByUnitDistance);
	    int damage = 0;
	    for (Unit2 allianceUnit : attackableAllianceUnitList) {
		if (!attackFinishSet.contains(allianceUnit)) {
		    if (enemyUnit.getHitPoints() - damage >= 0) {
//			Log.trace("\t공격 시도: 아균=%s, 적군=%s, 적군체력=%d, 아군 누적 예상 공격력=%d", allianceUnit, enemyUnit, enemyUnit.getHitPoints(), damage);
			attackFinishSet.add(allianceUnit);
			ActionUtil.attackEnemyUnit(allianceUnitInfo, allianceUnit, enemyUnit);
			damage += UnitUtil.getDamage(allianceUnit, enemyUnit);
		    } else {
			Log.trace("\t공격 안함: 아균=%s, 적군=%s, 적군체력=%d, 아군 누적 예상 공격력=%d", allianceUnit, enemyUnit, enemyUnit.getHitPoints(), damage);
			break;
		    }
		}
	    }
	}
    }

}