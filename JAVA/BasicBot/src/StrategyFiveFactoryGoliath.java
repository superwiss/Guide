import java.util.Set;

import bwapi.TilePosition;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class StrategyFiveFactoryGoliath extends StrategyBase {

    private StrategyManager strategyManager = null;
    private LocationManager locationManager = null;
    private BuildManager buildManager = null;
    private WorkerManager workerManager = null;

    private static int repairCount = 3; //골리앗을 수리할 scv의 갯수

    public StrategyFiveFactoryGoliath() {
	strategyName = "TowFactory";
    }

    @Override
    public void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);

	strategyManager = gameStatus.getStrategyManager();
	locationManager = gameStatus.getLocationManager();
	buildManager = gameStatus.getBuildManager();
	workerManager = gameStatus.getWorkerManager();
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
	    if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Factory).size() < 5) {
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

	// 본진에 있는 골리앗을 수리한다.
	doRepairGoliath();

	// 입구에 있는 배럭을 자동으로 열고 닫는다.
	doAutoLiftBarracks();

	// 입구의 건물을 자동으로 수리한다.
	doBlockEntrance();

	// 본진의 커맨드 센터를 확장으로 옮긴다.
	//	doAutoLiftCommandCenter();
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
	    strategyManager.setAttackTilePosition(locationManager.getFirstExtensionChokePoint());
	    strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
	} else if (goliathCount >= 4 && goliathCount < 24) {
	    // 공격 유닛 인구수가 7 ~ 50이면 본진 앞마당 입구로 나온다.
	    strategyManager.setAttackTilePosition(locationManager.getBaseEntranceChokePoint());
	    strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
	    Log.info("본진 앞마당으로 내려온다. 인구수: %d. 위치: %s", goliathCount, strategyManager.getAttackTilePositon());

	} else if (goliathCount >= 24 && goliathCount < 28) {
	    // 공격 유닛 인구수가 50 ~ 80이면 적 입구를 조인다.
	    strategyManager.setAttackTilePosition(locationManager.getBlockingChokePoint());
	    strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
	    Log.info("적 본진 근처에서 조이기를 한다. 인구수: %d, 위치: %s", goliathCount, strategyManager.getAttackTilePositon());
	    //	    strategyManager.addStrategyItem(StrategyItem.AUTO_EXTENSION);

	    // 조이기 시점에 적이 5마리 이상 보이면 총 공격을 한다.
	    if (enemyUnitInfo.getUnitSet(UnitKind.Combat_Unit).size() > 5) {
		TilePosition attackTilePosition = strategyManager.calcAndGetAttackTilePosition();
		strategyManager.setAttackTilePosition(attackTilePosition);
		strategyManager.addStrategyStatus(StrategyStatus.FULLY_ATTACK);
		strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
		Log.info("총 공격을 간다. 인구수: %d, 위치: %s", goliathCount, attackTilePosition);
	    }
	} else if (goliathCount >= 28) {
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
	    if (gameStatus.getMineral() > 150 && 0 == buildManager.getQueueSize() && goliathCount >= 8) {
		if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Academy).size() == 0) {
		    buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Academy));
		}
	    }
	}
    }

    // 골리앗 수리와 관련된 작업을 수행한다.
    private void doRepairGoliath() {

	// 1초에 한 번만 수행된다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	Set<Unit2> commandCenterSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center);
	for (Unit2 commandCenter : commandCenterSet) {

	    // 커맨드 센터 반경 800 이내의 골리앗 정보를 가져온다.
	    Set<Unit2> goliathSet = allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.Terran_Goliath, 800);

	    // 골리앗이 없으면 다음 커맨드 센터 검사
	    if (goliathSet.isEmpty()) {
		continue;
	    }

	    //골리앗이 많으면 더이상 수리할 필요 없다.
	    if (goliathSet.size() >= 6) {
		return;
	    }

	    for (Unit2 goliath : goliathSet) {

		if (gameStatus.getMineral() > 50) {
		    if (UnitType.Terran_Goliath.maxHitPoints() > goliath.getHitPoints()) {
			repairUnit(allianceUnitInfo, goliath);
		    } else {
			repairCount = 3;
		    }
		}
	    }
	}
    }

    // 유닛을 수리한다.
    private void repairUnit(UnitInfo allianceUnitInfo, Unit2 unit) {
	Unit2 repairWorker = workerManager.getInterruptableWorker(unit.getTilePosition());
	if (repairCount > 0) {
	    if (null != repairWorker) {
		ActionUtil.repair(allianceUnitInfo, repairWorker, unit);
		--repairCount;
	    }
	}
    }

    private void doAutoLiftBarracks() {

	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	//멀티가 아직 지어지지 않았을 경우
	if (hasStrategyItem(StrategyItem.BLOCK_ENTRANCE_ZERG) && allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Command_Center).size() == 1
		&& allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Barracks).size() > 0) {

	    Unit2 entranceBarrack = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Barracks);

	    //확장기지 근처에 아군 scv가 있을 경우 배럭을 띄운다.
	    Unit2 scv = allianceUnitInfo.getAnyUnitInRange(locationManager.getBaseEntranceChokePoint().toPosition(), UnitKind.Terran_SCV, 100);
	    if (scv != null) {

		//scv가 건설중이면 띄우지 않는다.
		if (scv.isConstructing()) {
		    return;
		}

		//착지 상태의 배럭에 대해
		if (!entranceBarrack.isLifted()) {
		    //적 유닛이 근처에 없을 경우에만 띄운다.
		    if (enemyUnitInfo.getUnitsInRange(locationManager.getBaseEntranceChokePoint().toPosition(), UnitKind.Terran_SCV, 500).size() == 0) {
			entranceBarrack.lift();
		    }
		}
	    } else {
		//배럭이 떠있을 경우 다시 착지시킨다.
		if (entranceBarrack.isLifted()) {

		    TilePosition landPosition = locationManager.getBlockingEntranceBuilding().get(0);
		    TilePosition checkTile = new TilePosition(landPosition.getX() + 2, landPosition.getY() + 1);
		    for (Unit2 unit2 : allianceUnitInfo.getUnitsInRange(checkTile.toPosition(), UnitKind.Combat_Unit, 100)) {
			unit2.move(locationManager.allianceBaseLocation.toPosition());
			ActionUtil.moveToPosition(allianceUnitInfo, unit2, locationManager.allianceBaseLocation.toPosition());
		    }

		    entranceBarrack
			    .land(new TilePosition(locationManager.getBlockingEntranceBuilding().get(0).getX(), locationManager.getBlockingEntranceBuilding().get(0).getY()));
		}
	    }
	}

	//멀티가 지어진 경우
	if (hasStrategyItem(StrategyItem.BLOCK_ENTRANCE_ZERG) && allianceUnitInfo.getUnitSet(UnitKind.Terran_Goliath).size() >= 4) {

	    Unit2 entranceBarrack = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Barracks);

	    if (entranceBarrack != null) {
		//착지 상태의 배럭이 확장 위치가 아닐경우 띄운다
		if (!entranceBarrack.isLifted()) {
		    entranceBarrack.lift();
		}
	    }
	}
    }

    private void doBlockEntrance() {

	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	if (hasStrategyItem(StrategyItem.BLOCK_ENTRANCE_ZERG)) {

	    TilePosition blockPosition = locationManager.getBaseEntranceChokePoint();
	    Set<Unit2> buildingSet = allianceUnitInfo.getUnitsInRange(blockPosition.toPosition(), UnitKind.Building, 320);

	    for (Unit2 buidling : buildingSet) {
		if (gameStatus.getMineral() > 0) {
		    if (buidling.getType().maxHitPoints() > buidling.getHitPoints()) {
			repairBuilding(allianceUnitInfo, buidling);
		    } else {
			repairCount = 3;
		    }
		}
	    }
	}
    }

    // 벙거를 수리한다.
    private void repairBuilding(UnitInfo allianceUnitInfo, Unit2 building) {
	WorkerManager workerManager = gameStatus.getWorkerManager();
	Unit2 repairWorker = workerManager.getInterruptableWorker(building.getTilePosition());
	if (repairCount > 0) {
	    if (null != repairWorker) {
		ActionUtil.repair(allianceUnitInfo, repairWorker, building);
		--repairCount;
	    }
	}
    }

    private void doAutoLiftCommandCenter() {

	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	//멀티가 지어져 있지 않은 상황
	if (hasStrategyItem(StrategyItem.BLOCK_ENTRANCE_ZERG) && allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Command_Center).size() == 2) {

	    Set<Unit2> commandCenterUnitSet = allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Command_Center);

	    if (commandCenterUnitSet.size() == 2) {

		Unit2 firstExpansionCommandCenter = null;
		TilePosition baseLocation = locationManager.getAllianceBaseLocation();
		TilePosition firstExpansionLocation = locationManager.getExtentionPosition().get(0);

		for (Unit2 unit : commandCenterUnitSet) {
		    //본진의 커맨드 센터는 무시
		    if (unit.getTilePosition().getX() == baseLocation.getX() && unit.getTilePosition().getY() == baseLocation.getY()) {
			continue;
		    } else {
			firstExpansionCommandCenter = unit;
			break;
		    }
		}

		if (firstExpansionCommandCenter != null) {
		    //멀티용 커맨드가 멀티 위치에 있지 않다면 띄운다
		    if (firstExpansionCommandCenter.isLifted() == false) {
			if (firstExpansionCommandCenter.getTilePosition().getX() != firstExpansionLocation.getX()
				|| firstExpansionCommandCenter.getTilePosition().getY() != firstExpansionLocation.getY()) {
			    firstExpansionCommandCenter.lift();
			}
		    } else {
			//떠있을 경우 멀티 위치에 착지시킨다.
			firstExpansionCommandCenter.land(new TilePosition(firstExpansionLocation.getX(), firstExpansionLocation.getY()));
		    }
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
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.SCOUTING));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 10
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 11
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Refinery));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 12
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 13
	//	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.SCOUTING));
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
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Command_Center));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 21
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture)); // 23
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
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
	strategyItems.add(StrategyItem.BLOCK_ENTRANCE_ZERG);
	strategyItems.add(StrategyItem.ENEMY_BASE_EDGE_SCOUT);
    }

}