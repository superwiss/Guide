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

	// 5초에 한 번씩 수행한다.
	if (gameStatus.isMatchedInterval(5)) {
	    doWholeAttack();
	}
    }

    // 전체 공격을 처리한다.
    void doWholeAttack() {
	// 모든 공격 가능한 유닛셋을 가져온다.
	Set<Unit2> attackableUnitSet = allianceUnitInfo.getUnitSet(UnitKind.Combat_Unit);
	// 총 공격 전이고, 공격 유닛이 60마리 이상이고, 적 본진을 발견했으면 총 공격 모드로 변환한다.
	Log.debug("총 공격 조건 확인. 공격 위치: %s, 아군 공격 가능한 유닛 수: %d, 적 본진 위치: %s", strategyManager.getAttackTilePositon(), attackableUnitSet.size(),
		locationManager.getEnemyStartLocation());

	if (true == strategyManager.hasAttackTilePosition() || (attackableUnitSet.size() > 60 && null != locationManager.getEnemyStartLocation())) {
	    Log.info("총 공격 모드로 전환. 아군 유닛 수: %d", attackableUnitSet.size());
	    TilePosition attackTilePosition = calcAttackPosition();
	    strategyManager.setPhase(5);

	    if (null != attackTilePosition) {
		strategyManager.setAttackTilePosition(attackTilePosition);

		Log.info("총 공격! 공격할 위치: %s", attackTilePosition);
		for (Unit2 attackableUnit : attackableUnitSet) {

		    if (attackableUnit.getDistance(attackTilePosition.toPosition()) > 300) {
			if (attackableUnit.canUnsiege()) {
			    attackableUnit.unsiege();
			}
		    }
		    ActionUtil.attackPosition(allianceUnitInfo, attackableUnit, attackTilePosition.toPosition());
		}
	    }
	}
    }

    // 공격갈 위치를 계산한다.
    private TilePosition calcAttackPosition() {
	TilePosition result = null;

	if (0 == totalAttackFrame) {
	    result = locationManager.getFirstExtensionChokePoint();
	    // 10초 동안 앞마당으로 집결한 뒤 총 공격을 간다.
	    totalAttackFrame = gameStatus.getFrameCount() + 10 * 42;
	    Log.info("앞으로 10초 뒤에 총 공격을 시작한다. 일단 앞마당에 집결하자.");
	} else {
	    if (totalAttackFrame < gameStatus.getFrameCount()) {
		// 내 본진의 위치
		TilePosition allianceStartTilePosition = locationManager.getAllianceBaseLocation();

		// 적 본진의 위치
		Set<Unit2> enemyMainBuildingSet = enemyUnitInfo.getUnitSet(UnitKind.MAIN_BUILDING);

		// 가급적 본진에서 가장 가까운 적 본진부터 공격한다.
		Unit2 closestMainBuilding = enemyUnitInfo.getClosestUnitWithLastTilePosition(enemyMainBuildingSet, allianceStartTilePosition.toPosition());

		if (null != closestMainBuilding) {
		    // 적 메인 건물이 존재할 경우..
		    result = enemyUnitInfo.getLastTilePosition(closestMainBuilding);
		} else {
		    // 적 메인 건물은 찾지 못했지만, 다른 건물들이 존재할 경우.
		    Set<Unit2> enemyBuildingSet = enemyUnitInfo.getUnitSet(UnitKind.Building);
		    if (!enemyBuildingSet.isEmpty()) {
			// 적 건물이 다수 존재할 경우, 내 본진에서 가장 가까운 상대 건물부터 공격한다.
			Unit2 closestBuilding = enemyUnitInfo.getClosestUnitWithLastTilePosition(enemyBuildingSet, allianceStartTilePosition.toPosition());
			if (null != closestBuilding) {
			    result = enemyUnitInfo.getLastTilePosition(closestBuilding);
			}
		    } else {
			TilePosition enemyBaseLocation = locationManager.getEnemyStartLocation();
			if (gameStatus.isExplored(enemyBaseLocation)) {
			    Log.info("적 건물을 찾을 수 없다. 탐색하자");
			    result = null;
			} else {
			    // 어떠한 적 건물도 찾지 못했지만, 적 본진 위치는 알고 있을 경우 (예를 들어 3곳을 정찰 성공했다면 남은 한 곳은 방문하지 않아도 적 본진이다.)
			    Log.info("적 건물은 없지만, 적 위치는 알고 있다. 적 위치로 공격을 가자: %s", enemyBaseLocation);
			    result = enemyBaseLocation;
			}
		    }
		}
		Log.info("총 공격한다: %s", result);
	    } else {
		result = locationManager.getFirstExtensionChokePoint();
		Log.info("내 본진 앞마당으로 집결한다");
	    }
	}

	return result;
    }

    @Override
    public void initialBuildOrder() {
	BuildManager buildManager = gameStatus.getBuildManager();

	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));

	//9서플
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));

	//11배럭, 가스
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Refinery));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));

	//12서치
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.SCOUTING));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.GATHER_GAS));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.GATHER_GAS));

	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));

	//15팩
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));

	//20커맨드 18가스
	//애드온

	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Siege_Tank_Tank_Mode));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));

	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Siege_Tank_Tank_Mode));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Command_Center));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Siege_Tank_Tank_Mode));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Siege_Tank_Tank_Mode));
	//	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Bunker));

	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.INITIAL_BUILDORDER_FINISH));
    }

    @Override
    public void initialStrategyItem(Set<StrategyItem> strategyItems) {

	strategyItems.add(StrategyItem.AUTO_LOAD_MARINE_TO_BUNKER);
	strategyItems.add(StrategyItem.AUTO_REPAIR_BUNKER);
	strategyItems.add(StrategyItem.AUTO_TRAIN_MECHANIC_UNIT);
	strategyItems.add(StrategyItem.AUTO_TRAIN_SCV);
	strategyItems.add(StrategyItem.AUTO_BALANCE_SCV);
	strategyItems.add(StrategyItem.AUTO_ASSIGN_GAS_SCV);
	strategyItems.add(StrategyItem.SET_FACTORY_RALLY);
	strategyItems.add(StrategyItem.BLOCK_ENTRANCE);

	strategyItems.add(StrategyItem.AUTO_RESEARCH_SIEGE_MODE);
	strategyItems.add(StrategyItem.AUTO_RESEARCH_CHARON_BOOSTERS);

	strategyItems.add(StrategyItem.AUTO_BUILD_TWO_ARMORY);
	strategyItems.add(StrategyItem.AUTO_UPGRADE_MECHANIC_UNIT);

	//strategyItems.add(StrategyItem.AUTO_TRAIN_BIONIC_UNIT);
	//strategyItems.add(StrategyItem.SET_BARRACKS_RALLY);
	//strategyItems.add(StrategyItem.AUTO_RESEARCH_U_238_Shells);
	//strategyItems.add(StrategyItem.AUTO_RESEARCH_STIMPACK);
	//strategyItems.add(StrategyItem.AUTO_UPGRADE_BIONIC_UNIT);

	strategyItems.add(StrategyItem.AUTO_ADDON_COMSAT_STATION);
	strategyItems.add(StrategyItem.AUTO_ADDON_MACHINE_SHOP);
	strategyItems.add(StrategyItem.AUTO_USING_SCAN);
	strategyItems.add(StrategyItem.AUTO_DEFENCE_ALLIANCE_BASE);
	strategyItems.add(StrategyItem.AUTO_LIFT_COMMAND_CENTER);

	strategyItems.add(StrategyItem.ALLOW_PHASE);
	//strategyItems.add(StrategyItem.AGGRESSIVE_MOVE_ATTACK);
    }

}