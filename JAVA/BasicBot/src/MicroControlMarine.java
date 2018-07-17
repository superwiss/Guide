import java.util.HashSet;
import java.util.Set;

import bwapi.Position;
import bwapi.TechType;
import bwapi.UnitType;

// 매딕을 컨트롤 한다.
public class MicroControlMarine extends Manager {
    private final Set<UnitType> medicUnitTypeSet = new HashSet<>();
    private StrategyManager strategyManager = null;

    public MicroControlMarine() {
	medicUnitTypeSet.add(UnitType.Terran_Medic);
    }

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);

	strategyManager = gameStatus.getStrategyManager();
    }

    @Override
    protected void onFrame() {
	super.onFrame();

	// 84프레임(2초)에 1번만 컨트롤 한다.
	if (0 != gameStatus.getFrameCount() && 0 == gameStatus.getFrameCount() % (42 * 2)) {
	    // 선두 유닛이 너무 앞서가면, 뒤따로 오는 유닛을 기다린다.
	    waitBionicUnit();
	    checkIfUsingStimPack();
	}
    }

    // 스팀팩을 사용할지 여부를 판단하고, 필요할 경우 스팀팩을 사용한다.
    private void checkIfUsingStimPack() {
	Set<Unit2> marineSet = allianceUnitInfo.getUnitSet(UnitType.Terran_Marine);
	for (Unit2 marine : marineSet) {
	    // 마린 주변에 매딕이 1마리 이상 존재하고, 적군이 3마리 이상 존재하면 스팀팩을 사용한다.
	    int madicCount = allianceUnitInfo.getUnitsInRange(marine.getPosition(), UnitKind.Terran_Medic, 300).size();
	    int enemyCount = enemyUnitInfo.getUnitsInRange(marine.getPosition(), UnitKind.Combat_Unit, 300).size();
	    Log.debug("아군(%d) 주변 매딕 수(%d), 적군 수(%d)", marine.getID(), madicCount, enemyCount);
	    if (madicCount >= 1 && enemyCount >= 1) {
		if (marine.canUseTech(TechType.Stim_Packs)) {
		    marine.useTech(TechType.Stim_Packs);
		}
	    }
	}
    }

    // 선두 바이오닉 유닛 400 주변에 마린이 20마리 미만이라면, 모든 유닛이 적군으로의 진군을 일단 멈추고 선두 유닛쪽에 모인다.
    private void waitBionicUnit() {
	if (true == strategyManager.hasAttackTilePosition()) {
	    Position attackPosition = strategyManager.getAttackTilePositon().toPosition();
	    Unit2 headBionicUnit = allianceUnitInfo.getHeadAllianceUnit(UnitKind.Bionic_Attackable, attackPosition);
	    // 선두 바이오닉 유닛 300 주변의 마린 개수
	    int headGroupSize = allianceUnitInfo.getUnitsInRange(headBionicUnit.getPosition(), UnitKind.Terran_Marine, 300).size();
	    if (20 > headGroupSize) {
		Position headPosition = headBionicUnit.getPosition();
		Log.info("선두 마린(%d) 주변에 다른 마린이 %d명 밖에 없어서, 선두 위치로 모든 유닛이 이동한다. 선두 마린 위치: %s, 모일 위치: %s", headBionicUnit.getID(), headGroupSize, headBionicUnit.getPosition(),
			headPosition);
		attackAll(headPosition);
	    } else {
		Log.info("선두 마린(%d) 주변에 다른 마린이 충분하게도 %d명이나 있어서, 총 공격한다.", headBionicUnit.getID(), headGroupSize);
		attackAll(attackPosition);
	    }
	}
    }

    private void attackAll(Position headPosition) {
	Set<Unit2> attackableUnitSet = allianceUnitInfo.getUnitSet(UnitKind.Combat_Unit);
	for (Unit2 attackableUnit : attackableUnitSet) {
	    ActionUtil.attackPosition(allianceUnitInfo, attackableUnit, headPosition);
	}
    }
}