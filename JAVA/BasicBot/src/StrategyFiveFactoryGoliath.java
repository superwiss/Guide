import java.util.Set;

import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class StrategyFiveFactoryGoliath extends StrategyBase {

    private StrategyManager strategyManager = null;
    private LocationManager locationManager = null;
    private BuildManager buildManager = null;

    public StrategyFiveFactoryGoliath() {
	strategyName = "TowFactory";
    }

    @Override
    public void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);

	strategyManager = gameStatus.getStrategyManager();
	locationManager = gameStatus.getLocationManager();
	buildManager = gameStatus.getBuildManager();
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

	// 팩토리는 최초3개 확장 후 5개까지 늘려준다.
	if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center).size() >= 2) {
	    if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Factory).size() < locationManager.getTrainingBuildings().size() - 5) {
		if (gameStatus.getMineral() >= 250 && gameStatus.getGas() >= 150) {
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

	// 공격 시점과 장소를 체크한다.
	checkAttackTimingAndPosition();

	// 골리앗의 방어력과 공격력을 업그레이드 한다.
	doArmoryJob();

	// 적절한 타이밍에 컴셋을 건설한다.
	doBuildAcademy();
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
	int goliathCount = allianceUnitInfo.getUnitSet(UnitKind.Terran_Goliath).size();

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
		    Log.info("총 공격을 유지한다. 인구수: %d, 위치: %s", goliathCount, attackTilePosition);
		}
	    }
	    
	    if (goliathCount < 20) {
		strategyManager.addStrategyStatus(StrategyStatus.BACK_TO_BASE);
		strategyManager.removeStrategyStatus(StrategyStatus.FULLY_ATTACK);
		Log.info("총 공격이 실패했다. 병력을 모아서 다시 공격가자.");
	    }
	} else if (goliathCount < 4) {
	    strategyManager.setAttackTilePosition(locationManager.getBaseEntranceChokePoint());
	    strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
	} else if (goliathCount >= 4 && goliathCount < 24) {
	    // 공격 유닛 인구수가 7 ~ 50이면 본진 앞마당 입구로 나온다.
	    strategyManager.setAttackTilePosition(locationManager.getFirstExtensionChokePoint());
	    strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
	    Log.info("본진 앞마당으로 내려온다. 인구수: %d. 위치: %s", goliathCount, strategyManager.getAttackTilePositon());

	} else if (goliathCount >= 24 && goliathCount < 30) {
	    // 공격 유닛 인구수가 50 ~ 80이면 적 입구를 조인다.
	    strategyManager.setAttackTilePosition(locationManager.getBlockingChokePoint());
	    strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
	    Log.info("적 본진 근처에서 조이기를 한다. 인구수: %d, 위치: %s", goliathCount, strategyManager.getAttackTilePositon());
	    strategyManager.addStrategyItem(StrategyItem.AUTO_EXTENSION);

	    // 조이기 시점에 적이 5마리 이상 보이면 총 공격을 한다.
	    if (enemyUnitInfo.getUnitSet(UnitKind.Combat_Unit).size() > 5) {
		TilePosition attackTilePosition = strategyManager.calcAndGetAttackTilePosition();
		strategyManager.setAttackTilePosition(attackTilePosition);
		strategyManager.addStrategyStatus(StrategyStatus.FULLY_ATTACK);
		strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
		Log.info("총 공격을 간다. 인구수: %d, 위치: %s", goliathCount, attackTilePosition);
	    }
	} else if (goliathCount >= 30) {
	    // 공격 유닛 인구수가 80이 넘으면 총 공격을 한다.
	    TilePosition attackTilePosition = strategyManager.calcAndGetAttackTilePosition();
	    strategyManager.setAttackTilePosition(attackTilePosition);
	    strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
	    strategyManager.addStrategyStatus(StrategyStatus.FULLY_ATTACK);
	    // 공격과 동시에 추가 확장을 시도한다.
	    
	    Log.info("총 공격을 간다. 인구수: %d, 위치: %s", goliathCount, attackTilePosition);
	}
    }

    // 아머리와 관련된 작업을 수행한다.
    private void doArmoryJob() {
	// 1초에 한 번만 수행된다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}
	Unit2 armory = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Armory);

	if (null != armory) {
	    // 5팩 골리앗 전술에 대해 방어력우선으로 공방 각각1레벨 업그레이드를 한다.
	    if (armory.getPlayer().getUpgradeLevel(UpgradeType.Terran_Vehicle_Plating) == 0 && armory.canUpgrade(UpgradeType.Terran_Vehicle_Plating)) {
		armory.upgrade(UpgradeType.Terran_Vehicle_Plating);
	    } else if (armory.getPlayer().getUpgradeLevel(UpgradeType.Terran_Vehicle_Weapons) == 0 && armory.canUpgrade(UpgradeType.Terran_Vehicle_Weapons)) {
		armory.upgrade(UpgradeType.Terran_Vehicle_Weapons);
	    }
	}
    }

    // 아카데미 건설과 관련된 작업을 수행한다.
    private void doBuildAcademy() {

	// 2초에 한 번만 수행된다.
	if (!gameStatus.isMatchedInterval(5)) {
	    return;
	}

	//골리앗이 한부대 정도 되었을 때 아카데미를 건설한다.
	Unit2 academy = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Academy);
	int goliathCount = allianceUnitInfo.getUnitSet(UnitKind.Terran_Goliath).size();
	if (null == academy) {
	    if (gameStatus.getMineral() > 150 && 0 == buildManager.getQueueSize() && goliathCount >= 12) {
		if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Academy).size() == 0) {
		    buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Academy));
		}
	    }
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
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 17
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 19
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 20
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 21
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture)); // 23
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Command_Center));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 24
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 25
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Armory));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture)); // 27
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.ADD_ON, UnitType.Terran_Machine_Shop));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.SET_STRATEGY_ITEM, StrategyItem.AUTO_BUILD_SUPPLY));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.SET_STRATEGY_ITEM, StrategyItem.AUTO_TRAIN_GOLIATH));
    }

    @Override
    public void initialStrategyItem(Set<StrategyItem> strategyItems) {
	strategyItems.add(StrategyItem.AUTO_REBALANCE_WORKER);
	strategyItems.add(StrategyItem.AUTO_REFINERY_JOB);
	strategyItems.add(StrategyItem.AUTO_UPGRADE_CHARON_BOOSTERS);
	strategyItems.add(StrategyItem.AUTO_ADDON_COMSAT_STATION);
	strategyItems.add(StrategyItem.AUTO_USING_SCAN);
    }

}