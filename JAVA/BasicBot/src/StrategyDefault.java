import java.util.Set;

import bwapi.TilePosition;
import bwapi.UnitType;

public class StrategyDefault extends StrategyBase {

    private StrategyManager strategyManager = null;
    private LocationManager locationManager = null;

    public StrategyDefault() {
	strategyName = "Default";
    }

    @Override
    public void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);

	strategyManager = gameStatus.getStrategyManager();
	locationManager = gameStatus.getLocationManager();
    }

    @Override
    public void onFrame() {
	super.onFrame();

	if (strategyManager.isSkipMicroControl()) {
	    return;
	}

	// 5초에 한 번씩 수행한다.
	if (gameStatus.isMatchedInterval(5)) {
	    doWholeAttack();
	}
    }

    // 전체 공격을 처리한다.
    void doWholeAttack() {
	TilePosition attackTilePosition = strategyManager.calcAndGetAttackTilePosition();

	// 모든 공격 가능한 유닛셋을 가져온다.
	Set<Unit2> attackableUnitSet = allianceUnitInfo.getUnitSet(UnitKind.Combat_Unit);
	// 총 공격 전이고, 공격 유닛이 60마리 이상이고, 적 본진을 발견했으면 총 공격 모드로 변환한다.
	Log.debug("총 공격 조건 확인. 공격 위치: %s, 아군 공격 가능한 유닛 수: %d, 적 본진 위치: %s", attackTilePosition, attackableUnitSet.size(), locationManager.getEnemyStartLocation());
	
	if (!strategyManager.hasStrategyStatus(StrategyStatus.FULLY_ATTACK) && attackableUnitSet.size() > 50) {
	    TilePosition tilePositon = locationManager.getFirstExtensionChokePoint();
	    for (Unit2 attackableUnit : attackableUnitSet) {
		ActionUtil.attackPosition(allianceUnitInfo, attackableUnit, tilePositon.toPosition());
	    }
	}

	if (null != attackTilePosition && (attackableUnitSet.size() > 60)) {
	    Log.info("총 공격 모드로 전환. 아군 유닛 수: %d", attackableUnitSet.size());
	    strategyManager.addStrategyStatus(StrategyStatus.FULLY_ATTACK);
	}

	if (null != attackTilePosition && strategyManager.hasStrategyStatus(StrategyStatus.FULLY_ATTACK)) {
	    strategyManager.setAttackTilePosition(attackTilePosition);
	    Log.info("총 공격! 공격할 위치: %s", attackTilePosition);
	    for (Unit2 attackableUnit : attackableUnitSet) {
		ActionUtil.attackPosition(allianceUnitInfo, attackableUnit, attackTilePosition.toPosition());
	    }
	}
    }

    @Override
    public void initialBuildOrder() {
	BuildManager buildManager = gameStatus.getBuildManager();
	// 초기 빌드 오더
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.SCOUTING));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Bunker));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Refinery));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Academy));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.GATHER_GAS));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.INITIAL_BUILDORDER_FINISH));
    }

    @Override
    public void initialStrategyItem(Set<StrategyItem> strategyItems) {
	strategyItems.add(StrategyItem.AUTO_LOAD_MARINE_TO_BUNKER);
	strategyItems.add(StrategyItem.AUTO_REPAIR_BUNKER);
	strategyItems.add(StrategyItem.AUTO_TRAIN_BIONIC_UNIT);
	strategyItems.add(StrategyItem.SET_BARRACKS_RALLY);
	strategyItems.add(StrategyItem.AUTO_UPGRADE_U_238_Shells);
	strategyItems.add(StrategyItem.AUTO_UPGRADE_STIMPACK);
	strategyItems.add(StrategyItem.AUTO_ADDON_COMSAT_STATION);
	strategyItems.add(StrategyItem.AUTO_USING_SCAN);
	strategyItems.add(StrategyItem.AUTO_DEFENCE_ALLIANCE_BASE);
	//strategyItems.add(StrategyItem.AGGRESSIVE_MOVE_ATTACK);
    }

}