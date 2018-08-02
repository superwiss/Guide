import java.util.Set;

import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class StrategyTwoFactory extends StrategyBase {

    private StrategyManager strategyManager = null;
    private LocationManager locationManager = null;

    public StrategyTwoFactory() {
	strategyName = "TowFactory";
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

	if (gameStatus.getFrameCount() == 0) {
	    return;
	}

	if (strategyManager.isSkipMicroControl()) {
	    return;
	}

	// 팩토리는 여유가 있을 때마다 하나씩 늘려준다.
	// 팩토리는 확장 이후에 늘려준다.
	if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center).size() >= 2) {
	    if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Factory).size() < locationManager.getTrainingBuildings().size() - 0) {
		if (gameStatus.getMineral() >= 300) {
		    strategyManager.addStrategyItem(StrategyItem.AUTO_BUILD_FACTORY);
		} else {
		    strategyManager.removeStrategyItem(StrategyItem.AUTO_BUILD_FACTORY);
		}
	    } else {
		strategyManager.removeStrategyItem(StrategyItem.AUTO_BUILD_FACTORY);
	    }
	}

	// 확장은 최대 2곳을 가져간다.
	if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center).size() >= 3) {
	    strategyManager.removeStrategyItem(StrategyItem.AUTO_EXTENSION);
	}

	// 벌탱 비율은 벌쳐 4마링 당 탱크 1대 비율로 뽑는다.
	if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Siege_Tank_Tank_Mode).size() * 4 >= allianceUnitInfo.getUnitSet(UnitKind.Terran_Vulture).size()) {
	    strategyManager.removeStrategyItem(StrategyItem.AUTO_TRAIN_TANK);
	} else {
	    strategyManager.addStrategyItem(StrategyItem.AUTO_TRAIN_TANK);
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
	int supplyUsed = allianceUnitInfo.getSupplyUsedExceptWorker();

	if (strategyManager.hasStrategyStatus(StrategyStatus.FULLY_ATTACK)) {
	    TilePosition attackTilePosition = strategyManager.calcAndGetAttackTilePosition();

	    // 공격 위치 반경 1200 이내에 아군 유닛이 없다면.. 공격이 막힌 것으로 봐야한다.
	    if (null != attackTilePosition) {
		Set<Unit2> attackUnitSet = allianceUnitInfo.getUnitsInRange(attackTilePosition.toPosition(), UnitKind.Combat_Unit, 1200);
		if (attackUnitSet.isEmpty()) {
		    strategyManager.removeStrategyStatus(StrategyStatus.FULLY_ATTACK);
		    Log.info("총 공격이 실패했다. 병력을 모아서 다시 공격가자.");
		} else {
		    strategyManager.setAttackTilePosition(attackTilePosition);
		    Log.info("총 공격을 유지한다. 인구수: %d, 위치: %s", supplyUsed, attackTilePosition);
		}
	    }
	} else if (supplyUsed < 15) {
	    // 공격 유닛 인구수가 15 미만이라면 본진에서 대기한다.
	    // 마린은 앞마당에 대기하자.
	    for (Unit2 marine : allianceUnitInfo.getUnitSet(UnitKind.Terran_Marine)) {
		ActionUtil.moveToPosition(allianceUnitInfo, marine, locationManager.getExtentionPosition().get(0));
	    }
	} else if (supplyUsed >= 14 && supplyUsed < 100) {
	    // 공격 유닛 인구수가 7 ~ 50이면 본진 앞마당 입구로 나온다.
	    strategyManager.setAttackTilePosition(locationManager.getFirstExtensionChokePoint());
	    strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
	    Log.info("본진 앞마당으로 내려온다. 인구수: %d. 위치: %s", supplyUsed, strategyManager.getAttackTilePositon());

	    // 앞마당으로 내려가는 시점에 커맨드 센터를 더 늘린다.
	    strategyManager.addStrategyItem(StrategyItem.AUTO_EXTENSION);

	} else if (supplyUsed >= 100 && supplyUsed < 160) {
	    // 공격 유닛 인구수가 50 ~ 80이면 적 입구를 조인다.
	    strategyManager.setAttackTilePosition(locationManager.getBlockingChokePoint());
	    strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
	    Log.info("적 본진 근처에서 조이기를 한다. 인구수: %d, 위치: %s", supplyUsed, strategyManager.getAttackTilePositon());

	    // 조이기 시점에 적이 5마리 이상 보이면 총 공격을 한다.
	    if (enemyUnitInfo.getUnitSet(UnitKind.Combat_Unit).size() > 5) {
		TilePosition attackTilePosition = strategyManager.calcAndGetAttackTilePosition();
		strategyManager.setAttackTilePosition(attackTilePosition);
		strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
		strategyManager.addStrategyStatus(StrategyStatus.FULLY_ATTACK);
		Log.info("총 공격을 간다. 인구수: %d, 위치: %s", supplyUsed, attackTilePosition);
	    }
	} else if (supplyUsed >= 160) {
	    // 공격 유닛 인구수가 80이 넘으면 총 공격을 한다.
	    TilePosition attackTilePosition = strategyManager.calcAndGetAttackTilePosition();
	    strategyManager.setAttackTilePosition(attackTilePosition);
	    strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
	    strategyManager.addStrategyStatus(StrategyStatus.FULLY_ATTACK);
	    Log.info("총 공격을 간다. 인구수: %d, 위치: %s", supplyUsed, attackTilePosition);
	}
    }

    @Override
    public void initialBuildOrder() {
	// 초기 빌드 오더
	BuildManager buildManager = gameStatus.getBuildManager();

	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 5
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 6
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 7
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 8
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 9
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 10
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 11
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Refinery));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 12
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 13
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.SCOUTING));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 14
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.GATHER_GAS));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.GATHER_GAS));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 15
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine)); // 16
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 17
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine)); // 18
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 19
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 20
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.ADD_ON, UnitType.Terran_Machine_Shop));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 21
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Siege_Tank_Tank_Mode)); // 23
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 24
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.ADD_ON, UnitType.Terran_Machine_Shop));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 25
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Siege_Tank_Tank_Mode)); // 27
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Siege_Tank_Tank_Mode)); // 29
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.UPGRADE, UpgradeType.Ion_Thrusters));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.UPGRADE, TechType.Spider_Mines));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture)); // 31
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture)); // 33
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.SET_STRATEGY_ITEM, StrategyItem.AUTO_BUILD_SUPPLY));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.SET_STRATEGY_ITEM, StrategyItem.AUTO_TRAIN_VULTURE));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.SET_STRATEGY_ITEM, StrategyItem.AUTO_TRAIN_TANK));
    }

    @Override
    public void initialStrategyItem(Set<StrategyItem> strategyItems) {
	strategyItems.add(StrategyItem.AUTO_REBALANCE_WORKER);
    }

}