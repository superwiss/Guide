import java.util.HashSet;
import java.util.Set;

import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;

/// 상황을 판단하여, 정찰, 빌드, 공격, 방어 등을 수행하도록 총괄 지휘를 하는 class <br>
/// InformationManager 에 있는 정보들로부터 상황을 판단하고, <br>
/// BuildManager 의 buildQueue에 빌드 (건물 건설 / 유닛 훈련 / 테크 리서치 / 업그레이드) 명령을 입력합니다.<br>
/// 정찰, 빌드, 공격, 방어 등을 수행하는 코드가 들어가는 class
public class MagiStrategyManager {

    private static MagiStrategyManager instance = new MagiStrategyManager();

    /// static singleton 객체를 리턴합니다
    public static MagiStrategyManager Instance() {
	return instance;
    }

    private Set<StrategyItem> strategyItems = new HashSet<>();

    private MagiBuildManager buildManager = MagiBuildManager.Instance();
    private LocationManager locationManager = LocationManager.Instance();
    private MicroControlManager microControlManager = MicroControlManager.Instance();
    private MagiEliminateManager magiEliminateManager = MagiEliminateManager.Instance();
    // 벙커는 SCV 4마리만 수리한다.
    private static int repairCount = 4;

    public MagiStrategyManager() {
	strategyItems.add(StrategyItem.MARINE_INTO_BUNKER);
	strategyItems.add(StrategyItem.REPAIR_BUNKER);
	strategyItems.add(StrategyItem.MARINE_AUTO_TRAIN);
	strategyItems.add(StrategyItem.SET_BARRACKS_RALLY);
    }

    /// 경기가 시작될 때 일회적으로 전략 초기 세팅 관련 로직을 실행합니다
    public void onStart() {
	defense6droneBuildOrder();
    }

    public void onFrame(GameData gameData) {
	UnitManager allianceUnitManager = gameData.getAllianceUnitManager();
	Set<Integer> bunkerSet = allianceUnitManager.getUnitsIdByUnitKind(UnitKind.Terran_Bunker);
	for (Integer bunkerId : bunkerSet) {
	    Unit bunker = allianceUnitManager.getUnit(bunkerId);
	    if (strategyItems.contains(StrategyItem.MARINE_INTO_BUNKER)) {
		if (0 < bunker.getSpaceRemaining()) {
		    marineToBunker(allianceUnitManager, bunker);
		}
	    }
	    if (strategyItems.contains(StrategyItem.REPAIR_BUNKER)) {
		if (gameData.getMineral() > 0) {
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
		// 서플 여유가 4개 이하면 서플을 짓는다.
		if (false == buildManager.isBuildingSupply() && gameData.getSupplyRemain() <= 4 * 2) {
		    buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
		} else if (gameData.getMineral() > 200 && null != allianceUnitManager.getFirstUnitIdByUnitKind(UnitKind.Terran_Academy)
			&& 5 > allianceUnitManager.getUnitsIdByUnitKind(UnitKind.Terran_Barracks).size() && 0 == buildManager.getQueueSize()) {
		    // 아카데미가 존재하고, 배럭이 5개 미만이고, BuildOrder Queue가 비어있으면 세 번째 배럭을 짓는다.
		    buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
		} else if (gameData.getMineral() >= 50) {
		    Unit barracks = buildManager.getTrainableBarracks(allianceUnitManager);
		    if (null != barracks) {
			Set<Integer> medicIds = allianceUnitManager.getUnitsIdByUnitKind(UnitKind.Terran_Medic);
			Set<Integer> marineIds = allianceUnitManager.getUnitsIdByUnitKind(UnitKind.Terran_Marine);
			int medicCount = medicIds.size() + buildManager.getTrainingQueueUnitCount(allianceUnitManager, UnitType.Terran_Medic);
			int marineCount = marineIds.size() + buildManager.getTrainingQueueUnitCount(allianceUnitManager, UnitType.Terran_Marine);
			// 마린4마리당 매딕 1마리
			Log.info("마린/매딕 생산. 마린 수: %d, 메딕 수: %d", marineCount, medicCount);
			if (medicCount * 4 < marineCount) {
			    barracks.train(UnitType.Terran_Medic);
			} else {
			    barracks.train(UnitType.Terran_Marine);
			}
		    }
		}
	    }
	}
	Integer academyId = allianceUnitManager.getFirstUnitIdByUnitKind(UnitKind.Terran_Academy);
	if (null != academyId) {
	    Unit academy = allianceUnitManager.getUnit(academyId);
	    if (academy.canUpgrade(UpgradeType.U_238_Shells)) {
		academy.upgrade(UpgradeType.U_238_Shells);
	    }
	}

	// 모든 공격 가능한 유닛셋을 가져온다.
	Set<Integer> attackableUnits = allianceUnitManager.getUnitsIdByUnitKind(UnitKind.Combat_Unit);
	// 총 공격 전이고, 공격 유닛이 60마리 이상이고, 적 본진을 발견했으면 총 공격 모드로 변환한다.
	if (false == microControlManager.hasAttackTilePosition() && attackableUnits.size() > 60 && null != locationManager.getEnemyStartTilePosition()) {
	    // 5초에 한 번만 수행한다.
	    if (0 != gameData.getFrameCount() % (42 * 5)) {
		return;
	    }
	    Log.info("총 공격 모드로 전환. 아군 유닛 수: %d", attackableUnits.size());
	    TilePosition attackTilePosition = null;
	    UnitManager enemyUnitManager = gameData.getEnemyUnitManager();

	    // 내 본진의 위치
	    TilePosition allianceStartTilePosition = locationManager.getAllianceStartTilePosition();

	    // 적 본진의 위치
	    Set<Integer> enemyMainBuildingIds = enemyUnitManager.getUnitsIdByUnitKind(UnitKind.MAIN_BUILDING);

	    // 가급적 본진에서 가장 가까운 적 본진부터 공격한다.
	    Unit closestMainBuilding = enemyUnitManager.getClosestUnitWithLastTilePosition(enemyMainBuildingIds, allianceStartTilePosition.toPosition());

	    if (null != closestMainBuilding) {
		attackTilePosition = enemyUnitManager.getLastTilePosition(closestMainBuilding.getID());
	    } else {
		// 적 건물들의 위치를 가져온다.
		Set<Integer> enemyBuildingIds = enemyUnitManager.getUnitsIdByUnitKind(UnitKind.Building);
		// 내 본진에서 가장 가까운 상대 건물부터 공격한다.
		Unit closestBuilding = enemyUnitManager.getClosestUnitWithLastTilePosition(enemyBuildingIds, allianceStartTilePosition.toPosition());
		if (null != closestBuilding) {
		    attackTilePosition = enemyUnitManager.getLastTilePosition(closestBuilding.getID());
		}
	    }

	    Log.info("공격할 위치: %s", attackTilePosition);
	    if (null != attackTilePosition) {
		for (Integer unitId : attackableUnits) {
		    Unit unit = allianceUnitManager.getUnit(unitId);
		    if (unit.isIdle()) {
			ActionUtil.attackPosition(allianceUnitManager, unit, attackTilePosition.toPosition());
		    }
		}
	    } else {
		magiEliminateManager.search(allianceUnitManager);
		Log.info("Eliminate Manager 동작 시작.");
	    }
	}
    }

    // 벙거를 수리한다.
    private void repairBunker(UnitManager allianceUnitManager, Unit bunker) {
	Set<Integer> workerSet = allianceUnitManager.getUnitsIdByUnitKind(UnitKind.Worker);
	int minDistance = Integer.MAX_VALUE;
	Unit targetWorker = null;
	for (Integer workerId : workerSet) {
	    Unit worker = allianceUnitManager.getUnit(workerId);
	    if (!allianceUnitManager.isinterruptableWorker(workerId)) {
		// 동원 가능한 일꾼이 아니면 Skip한다.
		continue;
	    }
	    int distance = bunker.getDistance(worker);
	    if (distance < minDistance) {
		minDistance = distance;
		targetWorker = worker;
	    }
	}
	if (repairCount > 0) {
	    if (null != targetWorker) {
		ActionUtil.repair(allianceUnitManager, targetWorker, bunker);
		--repairCount;
	    }
	}
    }

    // 마린을 벙커에 넣는다.
    private void marineToBunker(UnitManager allianceUnitManager, Unit bunker) {
	Set<Integer> marineSet = allianceUnitManager.getUnitsIdByUnitKind(UnitKind.Terran_Marine);
	int minDistance = Integer.MAX_VALUE;
	Unit targetMarine = null;
	for (Integer marineId : marineSet) {
	    Unit marine = allianceUnitManager.getUnit(marineId);
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

    ///  경기가 종료될 때 일회적으로 전략 결과 정리 관련 로직을 실행합니다
    public void onEnd(boolean isWinner) {
    }

    /// 경기 진행 중 매 프레임마다 경기 전략 관련 로직을 실행합니다
    public void update() {
    }

    public void defense6droneBuildOrder() {
	// 초기 빌드 오더
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.SCOUTING));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.BUILD, UnitType.Terran_Bunker));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_MARINE));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_MARINE));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.BUILD, UnitType.Terran_Refinery));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.BUILD, UnitType.Terran_Academy));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.GATHER_GAS));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.INITIAL_BUILDORDER_FINISH));
    }

    public void onUnitComplete(Unit unit, GameData gameData) {
	if (null != unit && unit.getType().equals(UnitType.Terran_Barracks)) {
	    unit.setRallyPoint(locationManager.getChokePoint1().toPosition());
	}
    }
}