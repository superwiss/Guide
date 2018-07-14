import java.util.HashSet;
import java.util.Set;

import bwapi.Unit;
import bwapi.UnitType;

public class GameStatusManager extends Manager {

    private UnitManager allianceUnitManager = null;
    private UnitManager enemyUnitManager = null;

    // Single Thread에서 동작하는 BWAPI 특성을 고려해서, multi-thread 고려 없이 아래와 같이 적극 활용한다.
    private Set<Integer> unitIdSetCache = new HashSet<>();

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);

	this.allianceUnitManager = gameStatus.getAllianceUnitManager();
	this.enemyUnitManager = gameStatus.getEnemyUnitManager();

    }

    @Override
    protected void onFrame() {
	super.onFrame();

	if (1 == gameStatus.getFrameCount()) {
	    // 최초 프레임에서는 커맨드 센터 주변의 미네랄을 가져와서 초기화 한다.
	    // TODO 일꾼이 골고루 미네랄에 퍼지도록 하기 위한 목적인데, 리펙토링 필요하다.
	    updateMineralsNearCommandCenterInfo(true);
	}
    }

    @Override
    protected void onUnitComplete(Unit unit) {
	super.onUnitComplete(unit);

	// 커맨드 센터일 경우, 커맨드 센터에 할당된 미네랄 정보를 할당한다.
	// 0 프레임은 게임 시작 직후 onStart() 단계이다. 본 메서드가 호출되는 시점에는 아직 미네랄 정보가 모두 로드되기 전일 수 있다.
	// 따라서 0 프레임일 경우에는 일꾼을 미네랄로 보내는 작업을 하지 않고, 이후 일꾼이 생산되는 시점에만 일꾼을 미네랄로 보낸다.
	// 일꾼은 1프레임에서 미네랄보 보낸다.
	if (0 != gameStatus.getFrameCount() && unit.getType().equals(UnitType.Terran_Command_Center)) {
	    updateMineralsNearCommandCenterInfo(true);
	}
    }

    @Override
    protected void onUnitDestroy(Unit unit) {
	super.onUnitDestroy(unit);

	if (true == UnitUtil.isAllianceUnit(unit)) {
	    allianceUnitManager.remove(unit);
	    // 커맨드 센터일 경우, 커맨드 센터에 할당된 미네랄 정보를 릴리즈한다.
	    if (unit.getType().equals(UnitType.Terran_Command_Center)) {
		updateMineralsNearCommandCenterInfo(false);
	    }
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

    // command center 주변의 미네랄 정보를 업데이트 한다.
    // isCommandCenterCreated == true: 커맨드 센터가 추가되었다. 커맨드 센터에 주변 미네랄을 할당한다.
    // isCommandCenterCreated == false: 커맨드 센터가 파괴되었다. 커맨드 센터 주변의 미네랄을 해제한다. 
    private void updateMineralsNearCommandCenterInfo(boolean isCommandCenterCreated) {
	// 게임 시작 직후 onStart 단계에서는 아직 미네랄 정보가 BWAPI에 구성되기도 전에 Command Center 정보가 먼저 구성될 경우가 있다.
	// 게임 시작 후 1프레임 이상부터는 모든 미네랄 정보를 BWAPI에서 가져올 수 있으므로, 1프레임 이상일 경우만 로직을 동작시킨다.
	if (1 <= gameStatus.getFrameCount()) {
	    for (Integer commandCenterId : allianceUnitManager.getUnitIdSetByUnitKind(UnitKind.Terran_Command_Center)) {
		Unit commandCenter = allianceUnitManager.getUnit(commandCenterId);
		if (null == commandCenter) {
		    Log.warn("updateMineralMap(): id가 %d인 커맨드 센터가 존재하지 않습니다.", commandCenterId);
		    continue;
		} else {
		    if (true == isCommandCenterCreated) {
			assignMineralToCommandCenter(commandCenter);
		    } else {
			releaseMineralToCommandCenter(commandCenter);
		    }
		}
	    }
	}
    }

    // commandCenterId 주위의 미네랄 배치 정보를 업데이트
    private void assignMineralToCommandCenter(Unit commandCenter) {
	unitIdSetCache.clear(); // Command Center 주변의 미네랄 ID를 저장할 UnitIdSet

	for (Unit mineral : gameStatus.getGame().getMinerals()) {
	    if (commandCenter.getDistance(mineral) < 500) {
		unitIdSetCache.add(mineral.getID());
		allianceUnitManager.addAssignedMineralMap(mineral.getID(), false);
	    }
	}
	allianceUnitManager.assignMineralToCommandCenter(commandCenter, unitIdSetCache);
    }

    private void releaseMineralToCommandCenter(Unit commandCenter) {
	allianceUnitManager.releaseMineralToCommandCenter(commandCenter);
    }
}
