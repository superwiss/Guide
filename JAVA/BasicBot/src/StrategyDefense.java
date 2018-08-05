import java.util.Set;

import bwapi.TilePosition;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class StrategyDefense extends StrategyBase {

    private StrategyManager strategyManager = null;
    private LocationManager locationManager = null;
    private EliminateManager eliminateManager = null;

    private int totalAttackFrame = 0;

    @Override
    public void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);

	strategyManager = gameStatus.getStrategyManager();
	locationManager = gameStatus.getLocationManager();
	eliminateManager = gameStatus.getEliminateManager();
    }

    @Override
    public void onFrame() {
	super.onFrame();

	if (strategyManager.isSkipMicroControl()) {
	    return;
	}

	if (strategyManager.isSkipMicroControl()) {
	    return;
	}

	// 공격 시점과 장소를 체크한다.
	checkAttackTimingAndPosition();
    }

    private void checkAttackTimingAndPosition() {
	if (!gameStatus.isMatchedInterval(1)) {
	    // 1초에 한 번만 수행한다.
	    return;
	}

	if (strategyManager.hasStrategyStatus(StrategyStatus.BACK_TO_BASE)) {
	    return;
	}

	// 일꾼을 제외한 인구수를 구한다.
	//	int supplyUsed = allianceUnitInfo.getSupplyUsedExceptWorker();
	Set<Unit2> attackableUnitSet = allianceUnitInfo.getUnitSet(UnitKind.Combat_Unit);
	int supplyUsed = attackableUnitSet.size();

	if (strategyManager.hasStrategyStatus(StrategyStatus.FULLY_ATTACK)) {
	    TilePosition attackTilePosition = strategyManager.calcAndGetAttackTilePosition();

	    // 공격 위치 반경 1200 이내에 아군 유닛이 없다면.. 공격이 막힌 것으로 봐야한다.
	    if (null != attackTilePosition) {
		Set<Unit2> attackUnitSet = allianceUnitInfo.getUnitsInRange(attackTilePosition.toPosition(), UnitKind.Combat_Unit, 1500);
		if (attackUnitSet.isEmpty() || supplyUsed < 20) {
		    strategyManager.removeStrategyStatus(StrategyStatus.FULLY_ATTACK);
		    strategyManager.removeStrategyStatus(StrategyStatus.ATTACK);
		    strategyManager.setPhase(1);
		    Log.info("총 공격이 실패했다. 병력을 모아서 다시 공격가자.");
		} else {
		    strategyManager.setPhase(5);
		    strategyManager.setAttackTilePosition(attackTilePosition);
		    Log.info("총 공격을 유지한다. 인구수: %d, 위치: %s", supplyUsed, attackTilePosition);
		}
	    }
	} else if (supplyUsed >= 62) {
	    //	} else if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center).size() > 6) {
	    TilePosition attackTilePosition = strategyManager.calcAndGetAttackTilePosition();
	    strategyManager.setAttackTilePosition(attackTilePosition);
	    strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
	    strategyManager.addStrategyStatus(StrategyStatus.FULLY_ATTACK);
	    Log.info("총 공격을 간다. 인구수: %d, 위치: %s", supplyUsed, attackTilePosition);
	}
    }

    @Override
    public void initialBuildOrder() {
	BuildManager buildManager = gameStatus.getBuildManager();

	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));

	//9서플
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.SCOUTING));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));

	//11배럭, 가스
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Refinery));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));

	//12서치
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));

	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.GATHER_GAS));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.GATHER_GAS));

	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));

	//15팩
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));

	//20커맨드 18가스
	//애드온

	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Siege_Tank_Tank_Mode));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));

	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Siege_Tank_Tank_Mode));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Command_Center));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Siege_Tank_Tank_Mode));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Siege_Tank_Tank_Mode));
	//	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Bunker));

	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.INITIAL_BUILDORDER_FINISH));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.SET_STRATEGY_ITEM, StrategyItem.AUTO_BUILD_SUPPLY));
    }

    @Override
    public void initialStrategyItem(Set<StrategyItem> strategyItems) {

	strategyItems.add(StrategyItem.AUTO_LOAD_MARINE_TO_BUNKER);
	strategyItems.add(StrategyItem.AUTO_REPAIR_BUNKER);

	strategyItems.add(StrategyItem.AUTO_TRAIN_SCV);
	strategyItems.add(StrategyItem.AUTO_BALANCE_SCV);

	strategyItems.add(StrategyItem.AUTO_RESEARCH_SIEGE_MODE);
	strategyItems.add(StrategyItem.AUTO_RESEARCH_CHARON_BOOSTERS);

	//strategyItems.add(StrategyItem.AUTO_TRAIN_BIONIC_UNIT);
	//strategyItems.add(StrategyItem.SET_BARRACKS_RALLY);
	//strategyItems.add(StrategyItem.AUTO_UPGRADE_BIONIC_UNIT);

	strategyItems.add(StrategyItem.AUTO_ADDON_COMSAT_STATION);

	strategyItems.add(StrategyItem.AUTO_USING_SCAN);

	//strategyItems.add(StrategyItem.AGGRESSIVE_MOVE_ATTACK);

	strategyItems.add(StrategyItem.AUTO_BUILD_TWO_ARMORY);
	strategyItems.add(StrategyItem.AUTO_ADDON_MACHINE_SHOP);
	strategyItems.add(StrategyItem.AUTO_BUILD_EXPANSION);
	strategyItems.add(StrategyItem.AUTO_BUILD_TURRET);

	strategyItems.add(StrategyItem.AUTO_UPGRADE_MECHANIC_UNIT);
	strategyItems.add(StrategyItem.AUTO_TRAIN_MECHANIC_UNIT);

	strategyItems.add(StrategyItem.AUTO_DEFENCE_ALLIANCE_BASE);
	strategyItems.add(StrategyItem.AUTO_LIFT_COMMAND_CENTER);
	strategyItems.add(StrategyItem.BLOCK_ENTRANCE);
	strategyItems.add(StrategyItem.AUTO_ASSIGN_GAS_SCV);
	strategyItems.add(StrategyItem.ALLOW_PHASE);

	strategyItems.add(StrategyItem.USE_SCIENCE_VESSEL);

	//마이크로 매니저를 통해 위치를 지정하기 때문에 렐리포인트는 더이상 사용하지 않는다.
	//strategyItems.add(StrategyItem.SET_FACTORY_RALLY);
    }

}
