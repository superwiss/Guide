import java.util.HashSet;
import java.util.Set;

import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class StrategyDefault extends StrategyBase {
    private Set<StrategyItem> strategyItems = new HashSet<>();

    // 벙커는 SCV 4마리만 수리한다.
    private static int repairCount = 4;

    public StrategyDefault() {
	strategyItems.add(StrategyItem.MARINE_INTO_BUNKER);
	strategyItems.add(StrategyItem.REPAIR_BUNKER);
	strategyItems.add(StrategyItem.MARINE_AUTO_TRAIN);
	strategyItems.add(StrategyItem.SET_BARRACKS_RALLY);
    }

    private int lastScanFrameCount = 0;

    @Override
    public void onFrame() {
	super.onFrame();

	BuildManager buildManager = gameStatus.getBuildManager();
	LocationManager locationManager = gameStatus.getLocationManager();
	EliminateManager magiEliminateManager = gameStatus.getEliminateManager();

	Set<Unit2> bunkerSet = allianceUnitManager.getUnitSet(UnitKind.Terran_Bunker);
	for (Unit2 bunker : bunkerSet) {
	    if (strategyItems.contains(StrategyItem.MARINE_INTO_BUNKER)) {
		if (0 < bunker.getSpaceRemaining()) {
		    marineToBunker(allianceUnitManager, bunker);
		}
	    }
	    if (strategyItems.contains(StrategyItem.REPAIR_BUNKER)) {
		if (gameStatus.getMineral() > 0) {
		    if (UnitType.Terran_Bunker.maxHitPoints() > bunker.getHitPoints()) {
			repairBunker(allianceUnitManager, bunker);
		    } else {
			repairCount = 4;
		    }
		}
	    }
	}

	if (strategyItems.contains(StrategyItem.MARINE_AUTO_TRAIN)) {
	    if (0 == buildManager.getQueueSize() && true == buildManager.isInitialBuildFinished()) {
		// 서플 여유가 4개 이하면 서플을 짓는다. (최대 1개를 동시에 지을 수 있음)
		if (1 > allianceUnitManager.getConstructionCount(UnitType.Terran_Supply_Depot) && gameStatus.getSupplyRemain() <= 4 * 2) {
		    buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
		} else if (gameStatus.getMineral() > 200 && null != allianceUnitManager.getAnyUnit(UnitKind.Terran_Academy)
			&& 5 > allianceUnitManager.getUnitSet(UnitKind.Terran_Barracks).size() && 0 == buildManager.getQueueSize()) {
		    // 아카데미가 존재하고, 배럭이 5개 미만이고, BuildOrder Queue가 비어있으면 세 번째 배럭을 짓는다.
		    buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
		} else if (gameStatus.getMineral() >= 50) {
		    Unit2 barracks = allianceUnitManager.getTrainableBuilding(UnitType.Terran_Barracks, UnitType.Terran_Marine);
		    if (null != barracks) {
			Set<Unit2> medicSet = allianceUnitManager.getUnitSet(UnitKind.Terran_Medic);
			Set<Unit2> marineSet = allianceUnitManager.getUnitSet(UnitKind.Terran_Marine);
			int medicCount = medicSet.size() + allianceUnitManager.getTrainingQueueUnitCount(UnitType.Terran_Barracks, UnitType.Terran_Medic);
			int marineCount = marineSet.size() + allianceUnitManager.getTrainingQueueUnitCount(UnitType.Terran_Barracks, UnitType.Terran_Marine);
			// 마린6마리당 매딕 1마리
			Log.info("마린/매딕 생산. 마린 수: %d, 메딕 수: %d", marineCount, medicCount);
			if (medicCount * 6 < marineCount) {
			    barracks.train(UnitType.Terran_Medic);
			} else {
			    barracks.train(UnitType.Terran_Marine);
			}
		    }
		}
	    }
	}

	Unit2 academy = allianceUnitManager.getAnyUnit(UnitKind.Terran_Academy);
	if (null != academy) {
	    // 사거리 업그레이드를 한다.
	    if (academy.canUpgrade(UpgradeType.U_238_Shells)) {
		academy.upgrade(UpgradeType.U_238_Shells);
	    }

	    // Comsat Station Add on
	    allianceUnitManager.buildAddon(UnitType.Terran_Comsat_Station);

	    // 스팀팩 업그레이드를 한다.
	    if (academy.canResearch(TechType.Stim_Packs)) {
		academy.research(TechType.Stim_Packs);
	    }
	}

	checkIfuseScan();

	// 모든 공격 가능한 유닛셋을 가져온다.
	Set<Unit2> attackableUnitSet = allianceUnitManager.getUnitSet(UnitKind.Combat_Unit);
	// 총 공격 전이고, 공격 유닛이 60마리 이상이고, 적 본진을 발견했으면 총 공격 모드로 변환한다.
	if (true == gameStatus.hasAttackTilePosition() || (attackableUnitSet.size() > 60 && null != locationManager.getEnemyBaseLocation())) {
	    // 5초에 한 번만 수행한다.
	    if (0 != gameStatus.getFrameCount() % (42 * 5)) {
		return;
	    }
	    Log.info("총 공격 모드로 전환. 아군 유닛 수: %d", attackableUnitSet.size());
	    TilePosition attackTilePosition = null;

	    // 내 본진의 위치
	    TilePosition allianceStartTilePosition = locationManager.getAllianceBaseLocation();

	    // 적 본진의 위치
	    Set<Unit2> enemyMainBuildingIds = enemyUnitManager.getUnitSet(UnitKind.MAIN_BUILDING);

	    // 가급적 본진에서 가장 가까운 적 본진부터 공격한다.
	    Unit2 closestMainBuilding = enemyUnitManager.getClosestUnitWithLastTilePosition(enemyMainBuildingIds, allianceStartTilePosition.toPosition());

	    if (null != closestMainBuilding) {
		attackTilePosition = enemyUnitManager.getLastTilePosition(closestMainBuilding);
	    } else {
		// 적 건물들의 위치를 가져온다.
		Set<Unit2> enemyBuildingIds = enemyUnitManager.getUnitSet(UnitKind.Building);
		// 내 본진에서 가장 가까운 상대 건물부터 공격한다.
		Unit2 closestBuilding = enemyUnitManager.getClosestUnitWithLastTilePosition(enemyBuildingIds, allianceStartTilePosition.toPosition());
		if (null != closestBuilding) {
		    attackTilePosition = enemyUnitManager.getLastTilePosition(closestBuilding);
		}
	    }

	    if (null != attackTilePosition) {
		gameStatus.setAttackTilePosition(attackTilePosition);
		Log.info("총 공격! 공격할 위치: %s", attackTilePosition);
		for (Unit2 attackableUnit : attackableUnitSet) {
		    ActionUtil.attackPosition(allianceUnitManager, attackableUnit, attackTilePosition.toPosition());
		}
	    } else {
		gameStatus.clearAttackTilePosition();
		magiEliminateManager.search(allianceUnitManager);
		Log.info("Eliminate Manager 동작 시작.");
	    }
	}
    }

    @Override
    public void onUnitComplete(Unit2 unit) {
	super.onFrame();

	LocationManager locationManager = gameStatus.getLocationManager();

	if (null != unit && unit.getType().equals(UnitType.Terran_Barracks)) {
	    unit.setRallyPoint(locationManager.getBaseEntranceChokePoint().toPosition());
	}
    }

    @Override
    public void initialBuildOrder() {
	BuildManager buildManager = gameStatus.getBuildManager();
	// 초기 빌드 오더
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.SCOUTING));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Bunker));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Refinery));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Academy));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.GATHER_GAS));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.INITIAL_BUILDORDER_FINISH));
    }

    private void checkIfuseScan() {
	// 스캔은 3초 이내에는 또 뿌리지 않는다.
	if (gameStatus.getFrameCount() < lastScanFrameCount + 3 * 42) {
	    return;
	}
	// 적 클로킹 유닛을 찾는다.
	Set<Unit2> clockedUnitSet = enemyUnitManager.getUnitSet(UnitKind.Clocked);
	Log.trace("Clocked Unit Size: %d", clockedUnitSet.size());
	for (Unit2 clockedUnit : clockedUnitSet) {
	    if (null != clockedUnit && clockedUnit.exists()) {
		// 적 클로킹 유닛 150 거리 미만에 존재하는 아군 유닛이 5기 이상이면 스캔을 뿌린다.
		Set<Unit2> allianceUnitSet = allianceUnitManager.getUnitsInRange(clockedUnit.getPosition(), UnitKind.Terran_Marine, 150);
		Log.info("적 클로킹 유닛 발견: %s. 주변의 마린 수: %d", clockedUnit, allianceUnitSet.size());
		if (5 <= allianceUnitSet.size()) {
		    Log.info("스캔 뿌림: %s", clockedUnit.getPosition());
		    allianceUnitManager.doScan(clockedUnit.getPosition());
		    lastScanFrameCount = gameStatus.getFrameCount();
		    break;
		}
	    }
	}
    }

    // 벙거를 수리한다.
    private void repairBunker(UnitManager allianceUnitManager, Unit2 bunker) {
	WorkerManager workerManager = gameStatus.getWorkerManager();
	Unit2 repairWorker = workerManager.getInterruptableWorker(bunker.getTilePosition());
	if (repairCount > 0) {
	    if (null != repairWorker) {
		ActionUtil.repair(allianceUnitManager, repairWorker, bunker);
		--repairCount;
	    }
	}
    }

    // 마린을 벙커에 넣는다.
    private void marineToBunker(UnitManager allianceUnitManager, Unit2 bunker) {
	Set<Unit2> marineSet = allianceUnitManager.getUnitSet(UnitKind.Terran_Marine);
	int minDistance = Integer.MAX_VALUE;
	Unit2 targetMarine = null;
	for (Unit2 marine : marineSet) {
	    if (marine.isLoaded()) {
		// 벙커나 수송선에 이미 타고 있으면 skip한다.
		continue;
	    }
	    int distance = bunker.getDistance(marine);
	    if (distance < minDistance) {
		minDistance = distance;
		targetMarine = marine;
	    }
	}
	if (null != targetMarine) {
	    targetMarine.load(bunker);
	}
    }
}