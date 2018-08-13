import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bwapi.Position;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.UnitType;

// 매딕을 컨트롤 한다.
public class MicroControlMarine extends Manager {
    private final Set<UnitType> medicUnitTypeSet = new HashSet<>();
    private StrategyManager strategyManager = null;
    private LocationManager locationManager = null;

    public MicroControlMarine() {
	medicUnitTypeSet.add(UnitType.Terran_Medic);
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

	if (strategyManager.isSkipMicroControl()) {
	    return;
	}

	if (gameStatus.isMatchedInterval(1)) {
	    checkIfUsingStimPack();
	}

	if (gameStatus.isMatchedInterval(2)) {
	    // 선두 유닛이 너무 앞서가면, 뒤따로 오는 유닛을 기다린다.
	    waitBionicUnit();
	}

	aggressiveMoveAttack();
	fullyAttack();
    }

    // 스팀팩을 사용할지 여부를 판단하고, 필요할 경우 스팀팩을 사용한다.
    private void checkIfUsingStimPack() {
	Set<Unit2> marineSet = allianceUnitInfo.getUnitSet(UnitType.Terran_Marine);
	for (Unit2 marine : marineSet) {
	    if (!marine.exists()) {
		Log.warn("[MicroControlMarine:checkIfUsingStimPack] 마린(%s)이 exist 하지 않습니다.", marine);
	    } else {
		// 마린 주변에 매딕이 1마리 이상 존재하고, 적군이 3마리 이상 존재하면 스팀팩을 사용한다.
		Set<Unit2> medicSet = allianceUnitInfo.getUnitsInRange(marine.getPosition(), UnitKind.Terran_Medic, 300);
		int medicCount = medicSet.size();
		int medicTotalEnergy = 0;
		for (Unit2 medic : medicSet) {
		    medicTotalEnergy += medic.getEnergy();
		}
		int enemyCount = enemyUnitInfo.getUnitsInRange(marine.getPosition(), UnitKind.Combat_Unit, 300).size();
		// 마린의 반경 300 주위에 매딕이 1마리 이상, 적군이 3마리 이상, 매딕의 에너지 총 합이 20 이상, 마린의 체력이 15 이상이면 스팀팩을 사용한다.
		boolean isUsingStimPack = false;
		if (medicCount >= 1 && enemyCount >= 3 && medicTotalEnergy >= 20 && marine.getHitPoints() >= 25) {
		    if (marine.canUseTech(TechType.Stim_Packs)) {
			marine.useTech(TechType.Stim_Packs);
			isUsingStimPack = true;
		    }
		}
		Log.debug("마린(%s) 스팀팩 조건: 매딕수=%d, 적군수=%d, 매딕 에너지: %d, 마린 체력: %d. 스팀팩 사용? : %b", marine, medicCount, enemyCount, medicTotalEnergy, marine.getHitPoints(),
			isUsingStimPack);
	    }
	}
    }

    // 선두 바이오닉 유닛 400 주변에 마린이 20마리 미만이라면, 모든 유닛이 적군으로의 진군을 일단 멈추고 선두 유닛쪽에 모인다.
    private void waitBionicUnit() {
	//공격을 갈 지점이 있을 경우에만 컨트롤을 한다.
	if (true == strategyManager.hasAttackTilePosition()) {
	    Position attackPosition = strategyManager.getAttackTilePositon().toPosition();
	    // 공격 지점을 이미 정복했고, 적 건물이 존재하지 않으면 아무것도 하지 않는다.
	    if (gameStatus.isExplored(attackPosition.toTilePosition()) && 0 == enemyUnitInfo.getUnitSet(UnitKind.Building).size()) {
		return;
	    }

	    Unit2 headBionicUnit = allianceUnitInfo.getHeadAllianceUnit(UnitKind.Bionic_Attackable, attackPosition);
	    if (null != headBionicUnit) {
		// 선두 바이오닉 유닛 300 주변의 마린 개수
		int headGroupSize = allianceUnitInfo.getUnitsInRange(headBionicUnit.getPosition(), UnitKind.Terran_Marine, 300).size();
		if (20 > headGroupSize) {
		    Position headPosition = headBionicUnit.getPosition();
		    Log.info("선두 마린(%s) 주변에 다른 마린이 %d명 밖에 없어서, 선두 위치로 모든 유닛이 이동한다. 선두 마린 위치: %s, 집결 위치: %s", headBionicUnit, headGroupSize, headBionicUnit.getPosition(),
			    headPosition);
		    attackAll(headPosition);
		} else {
		    Log.info("선두 마린(%s) 주변에 다른 마린이 충분하게도 %d명이나 있다. 총 공격한다.", headBionicUnit, headGroupSize);
		    attackAll(attackPosition);
		}
	    } else {
		int bionicSize = 0;
		Set<Unit2> bionicUnits = allianceUnitInfo.getUnitSet(UnitKind.Bionic_Attackable);
		if (null != bionicUnits) {
		    bionicSize = bionicUnits.size();
		}
		Log.info("선두 마린이 없습니다. 공격 바이오닉 공격 유닛 개수: %d", bionicSize);
	    }
	}
    }

    // position 위치로 모든 바이오닉 유닛이 공격간다.
    private void attackAll(Position position) {
	Set<Unit2> attackableUnitSet = allianceUnitInfo.getUnitSet(UnitKind.Bionic_Attackable);
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

	Set<Unit2> unitSet = allianceUnitInfo.getUnitSet(UnitKind.Bionic_Attackable);
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

    private void fullyAttack() {
	if (!strategyManager.hasStrategyStatus(StrategyStatus.ATTACK)) {
	    return;
	}

	if (strategyManager.hasStrategyStatus(StrategyStatus.BACK_TO_BASE)) {
	    return;
	}

	if (strategyManager.hasStrategyItem(StrategyItem.BLOCK_ENTRANCE_ZERG) && strategyManager.getAttackTilePositon() == locationManager.getFirstExtensionChokePoint()) {

	    if (gameStatus.isMatchedInterval(1)) {
		return;
	    }

	    //	    Unit2 marine1 = allianceUnitInfo.getAnyUnit(UnitKind.Defence_Unit1);
	    //	    TilePosition attackTilePositon = strategyManager.getAttackTilePositon();
	    //	    if (marine1 != null) {
	    //		Unit2 enemy = enemyUnitInfo.getAnyUnitInRange(attackTilePositon.toPosition(), UnitKind.Combat_Unit, 100);
	    //		if (enemy != null) {
	    //		    //		    ActionUtil.attackEnemyUnit(allianceUnitInfo, marine1, enemy);
	    //		    ActionUtil.moveToPosition(allianceUnitInfo, marine1, attackTilePositon);
	    //		} else {
	    //		    ActionUtil.moveToPosition(allianceUnitInfo, marine1, attackTilePositon);
	    //		}
	    //	    }
	    //
	    //	    Unit2 marine2 = allianceUnitInfo.getAnyUnit(UnitKind.Defence_Unit2);
	    //	    attackTilePositon = locationManager.getFirstExtensionChokePoint2();
	    //	    if (marine2 != null) {
	    //		Unit2 enemy = enemyUnitInfo.getAnyUnitInRange(attackTilePositon.toPosition(), UnitKind.Combat_Unit, 100);
	    //		if (enemy != null) {
	    //		    //		    ActionUtil.attackEnemyUnit(allianceUnitInfo, marine2, enemy);
	    //		    ActionUtil.moveToPosition(allianceUnitInfo, marine2, attackTilePositon);
	    //		} else {
	    //		    ActionUtil.moveToPosition(allianceUnitInfo, marine2, attackTilePositon);
	    //		}
	    //	    }

	    //	    for (Unit2 marine : allianceUnitInfo.getUnitSet(UnitKind.Terran_Marine)) {
	    //
	    //		System.out.println("?");
	    //		System.out.println("방어마린1" + allianceUnitInfo.getAnyUnit(UnitKind.Defence_Unit1));
	    //		System.out.println("방어마린2" + allianceUnitInfo.getAnyUnit(UnitKind.Defence_Unit2));
	    //
	    //		int marinCount = allianceUnitInfo.getUnitsInRange(attackTilePositon.toPosition(), UnitKind.Terran_Marine, 60).size();
	    //		if (marinCount >= 1) {
	    //		    attackTilePositon = locationManager.getFirstExtensionChokePoint2();
	    //		}
	    //
	    //		Unit2 enemy = enemyUnitInfo.getAnyUnitInRange(attackTilePositon.toPosition(), UnitKind.Combat_Unit, 100);
	    //		if (enemy != null) {
	    //		    ActionUtil.attackEnemyUnit(allianceUnitInfo, marine, enemy);
	    //		} else {
	    //		    ActionUtil.moveToPosition(allianceUnitInfo, marine, attackTilePositon);
	    //		}
	    //
	    //		//		if (marine.getDistance(attackTilePositon.toPosition()) > 50) {
	    //		//		    ActionUtil.moveToPosition(allianceUnitInfo, marine, attackTilePositon);
	    //		//		}
	    //
	    //	    }
	    return;
	}

	// 아군 유닛들 목록
	Set<Unit2> allianceUnitSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Marine);

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

	for (Unit2 unit : allianceUnitInfo.getUnitSet(UnitKind.Terran_Marine)) {
	    TilePosition attackTilePositon = strategyManager.getAttackTilePositon();
	    // 총 공격 지점이 visible 상태라면... 점령한 것으로 판단하고, 공격을 너무 자주하지 않고 1초에 1번만 공격한다.
	    if (gameStatus.isVisible(attackTilePositon)) {
		if (false == gameStatus.isMatchedInterval(1)) {
		    break;
		}
	    }
	    if (!attackFinishSet.contains(unit)) {
		ActionUtil.attackPosition(allianceUnitInfo, unit, attackTilePositon);
	    }
	}
    }

}