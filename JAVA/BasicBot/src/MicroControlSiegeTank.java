import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// 매딕을 컨트롤 한다.
public class MicroControlSiegeTank extends Manager {

    private StrategyManager strategyManager;
    private LocationManager locationManager;

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);

	strategyManager = gameStatus.getStrategyManager();
	locationManager = gameStatus.getLocationManager();
    }

    @Override
    protected void onFrame() {
	super.onFrame();
	fullyAttack();
    }

    private void fullyAttack() {
	if (!strategyManager.containStrategyStatus(StrategyStatus.ATTACK)) {
	    return;
	}

	if (strategyManager.containStrategyStatus(StrategyStatus.SEARCH_FOR_ELIMINATE)) {
	    return;
	}

	// 아군 유닛들 목록
	Set<Unit2> allianceUnitSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Siege_Tank_Tank_Mode);

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

	for (Unit2 unit : allianceUnitInfo.getUnitSet(UnitKind.Terran_Siege_Tank_Tank_Mode)) {
	    if (!attackFinishSet.contains(unit)) {
		ActionUtil.attackPosition(allianceUnitInfo, unit, strategyManager.getAttackTilePositon());
	    }
	}
    }

}