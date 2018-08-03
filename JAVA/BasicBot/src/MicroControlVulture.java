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

public class MicroControlVulture extends Manager {
    private StrategyManager strategyManager = null;
    private LocationManager locationManager = null;

    public MicroControlVulture() {
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
	fullyAttack();
    }

    private void followPhase() {

	Set<Unit2> vultureSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Vulture);

	for (Unit2 vulture : vultureSet) {
	    if (!vulture.exists()) {
		Log.warn("[MicroControlMarine:checkIfUsingStimPack] 벌쳐(%s)가 exist 하지 않습니다.", vulture);
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
		if (attackPosition != null) {
		    if (vulture.getDistance(attackPosition) > 500) {
			ActionUtil.moveToPosition(allianceUnitInfo, vulture, attackPosition);
		    } else {
			ActionUtil.attackPosition(allianceUnitInfo, vulture, attackPosition);
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

    private void fullyAttack() {
	if (!strategyManager.hasStrategyStatus(StrategyStatus.ATTACK)) {
	    return;
	}

	if (strategyManager.hasStrategyStatus(StrategyStatus.BACK_TO_BASE)) {
	    return;
	}

	// 아군 유닛들 목록
	Set<Unit2> allianceUnitSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Vulture);

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
		    Log.trace("아군(%s)이 적군(%s)를 공격할 수 있음. distance=%d, weaponDistance=%d, enemyHP=%d", allianceUnit, enemyUnit, distance, groundWeaponDistance,
			    enemyUnit.getHitPoints());
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
	    Log.trace("enemy unit 순서 : unit=%s, hp=%d", enemyUnit, enemyUnit.getHitPoints());
	    List<Unit2> attackableAllianceUnitList = attackableUnitMap.get(enemyUnit);
	    Collections.sort(attackableAllianceUnitList, comparatorByUnitDistance);
	    int damage = 0;
	    for (Unit2 allianceUnit : attackableAllianceUnitList) {
		if (!attackFinishSet.contains(allianceUnit)) {
		    if (enemyUnit.getHitPoints() - damage >= 0) {
			Log.trace("\t공격 시도: 아균=%s, 적군=%s, 적군체력=%d, 아군 누적 예상 공격력=%d", allianceUnit, enemyUnit, enemyUnit.getHitPoints(), damage);
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