import java.util.HashSet;
import java.util.Set;

import bwapi.Position;
import bwapi.Race;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class StrategyManager extends Manager {

    private BuildManager buildManager;
    private LocationManager locationManager;
    private StrategyBase strategy = null; // 현재 전략
    private Set<StrategyItem> strategyItems = new HashSet<>(); // 전략 전술 플래그
    private Set<StrategyStatus> strategyStatus = new HashSet<>(); // 현재 상태
    private TilePosition attackTilePosition = null; // 공격 지점. 공격 지점이 null이면 유닛들은 대기한다. 공격 지점이 설정되면, 유닛들은 해당 지점으로 Attack Position을 수행한다.
    private TilePosition defenceTilePosition = null; // 방어 지점. 아군 유닛이나 건물이 공격 받으면, 그 위치가 방어 지점이 된다.
    private Unit2 headAllianceUnit = null; // 아군의 공격 선두 유닛
    private static int repairCount = 3; // 벙커를 수리할 SCV 개수
    private int lastScanFrameCount = 0; // 마지막으로 스캔을 뿌린 시각

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);

	// 각종 매니져 설정
	this.buildManager = gameStatus.getBuildManager();
	this.locationManager = gameStatus.getLocationManager();

	// 거의 모든 전략에서 사용될 수 있는 범용적인 전략을 세팅한다.
	setPassiveStrategyItem();

	// TODO 상대방의 종족이나 ID에 따라서 전략을 선택한다.
	//strategy = new StrategyDefault();
	//strategy = new StrategyTwoFactory();
	if (gameStatus.isComputer()) {
	    if (gameStatus.getEnemyRace().equals(Race.Terran)) {
		Log.info("User: Computer, Terran");
		strategy = new StrategyTwoFactory();
	    } else {
		Log.info("User: Computer, Not Terran");
		strategy = new StrategyDefault();
	    }
	} else {
	    if (gameStatus.isMatchPlayerByName("JohnVer")) {
		Log.info("User: JohnVer");
		strategy = new StrategyTwoFactory();
	    } else {
		Log.info("User: Default");
		strategy = new StrategyDefault();
	    }
	}

	strategy.onStart(gameStatus);
    }

    @Override
    public void onFrame() {
	super.onFrame();

	checkIfuseScan();
	doBunkerJob();
	doAcademyJob();
	doAttackUnitAutoTrain();
	doDefenceBase();
	doAutoBuildSupply();
	doAutoTrainVulture();
	doAutoBuildFactory();
	doAutoExtension();

	strategy.onFrame();
    }

    @Override
    protected void onUnitComplete(Unit2 unit) {
	super.onUnitComplete(unit);

	if (strategyItems.contains(StrategyItem.SET_BARRACKS_RALLY)) {
	    // 배럭의 랠리 포인트를 설정한다.
	    LocationManager locationManager = gameStatus.getLocationManager();
	    if (null != unit && unit.getType().equals(UnitType.Terran_Barracks)) {
		unit.setRallyPoint(locationManager.getBaseEntranceChokePoint().toPosition());
	    }
	}

	strategy.onUnitComplete(unit);
    }

    @Override
    protected void onUnitDestroy(Unit2 unit) {
	super.onUnitDestroy(unit);

	strategy.onUnitDestroy(unit);
    }

    @Override
    protected void onUnitDiscover(Unit2 unit) {
	super.onUnitDiscover(unit);

	strategy.onUnitDiscover(unit);
    }

    @Override
    protected void onUnitEvade(Unit2 unit) {
	super.onUnitEvade(unit);

	strategy.onUnitEvade(unit);
    }

    private void setPassiveStrategyItem() {
	strategyItems.add(StrategyItem.AUTO_DEFENCE_ALLIANCE_BASE);
	strategyItems.add(StrategyItem.AUTO_REPAIR_BUNKER);
    }

    // 공격갈 위치를 계산한다.
    public TilePosition calcAndGetAttackTilePosition() {
	TilePosition result = null;

	// 내 본진의 위치
	TilePosition allianceStartTilePosition = locationManager.getAllianceBaseLocation();

	// 적 본진의 위치
	Set<Unit2> enemyMainBuildingSet = enemyUnitInfo.getUnitSet(UnitKind.MAIN_BUILDING);

	// 가급적 본진에서 가장 가까운 적 본진부터 공격한다.
	Unit2 closestMainBuilding = enemyUnitInfo.getClosestUnitWithLastTilePosition(enemyMainBuildingSet, allianceStartTilePosition.toPosition());

	if (null != closestMainBuilding) {
	    // 적 메인 건물이 존재할 경우..
	    result = enemyUnitInfo.getLastTilePosition(closestMainBuilding);
	    Log.info("getAttackPosition: 적 메인 건물(%s)", result);
	} else {
	    // 적 메인 건물은 찾지 못했지만, 다른 건물들이 존재할 경우.
	    Set<Unit2> enemyBuildingSet = enemyUnitInfo.getUnitSet(UnitKind.Building);
	    if (!enemyBuildingSet.isEmpty()) {
		// 적 건물이 다수 존재할 경우, 내 본진에서 가장 가까운 상대 건물부터 공격한다.
		Unit2 closestBuilding = enemyUnitInfo.getClosestUnitWithLastTilePosition(enemyBuildingSet, allianceStartTilePosition.toPosition());
		result = enemyUnitInfo.getLastTilePosition(closestBuilding);
		Log.info("getAttackPosition: 적 일반 건물(%s)", result);
	    } else {
		TilePosition enemyStartLocation = locationManager.getEnemyStartLocation();
		if (gameStatus.isExplored(enemyStartLocation)) {
		    Log.info("getAttackPosition: 적 건물이 존재하지 않음.");
		    result = null;
		} else {
		    // 어떠한 적 건물도 찾지 못했지만, 적 본진 위치는 알고 있을 경우 (예를 들어 3곳을 정찰 성공했다면 남은 한 곳은 방문하지 않아도 적 본진이다.)
		    result = enemyStartLocation;
		    Log.info("getAttackPosition: 적 건물을 명시적으로 발견하지는 못했지만, 예측되는 곳(%s)으로 이동한다.", result);
		}
	    }
	}

	if (null == result) {
	    // 공격 지점이 없으면, 탐색 모드로 전환한다.
	    addStrategyStatus(StrategyStatus.SEARCH_FOR_ELIMINATE);
	}

	return result;
    }

    // ///////////////////////////////////////////////////////////
    // StrategyItem 구현부
    // ///////////////////////////////////////////////////////////

    // 자동으로 공격 유닛을 훈련하는 작업을 수행한다.
    private void doAttackUnitAutoTrain() {
	// 1초에 한 번만 수행된다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}
	if (hasStrategyItem(StrategyItem.AUTO_TRAIN_BIONIC_UNIT)) {
	    BuildManager buildManager = gameStatus.getBuildManager();
	    if (0 == buildManager.getQueueSize() && true == buildManager.isInitialBuildFinished()) {
		// 서플 여유가 4개 이하면 서플을 짓는다. (최대 1개를 동시에 지을 수 있음)
		if (1 > allianceUnitInfo.getConstructionCount(UnitType.Terran_Supply_Depot) && gameStatus.getSupplyRemain() <= 4 * 2) {
		    buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
		} else if (gameStatus.getMineral() > 200 && null != allianceUnitInfo.getAnyUnit(UnitKind.Terran_Academy)
			&& 5 > allianceUnitInfo.getUnitSet(UnitKind.Terran_Barracks).size() && 0 == buildManager.getQueueSize()) {
		    // 아카데미가 존재하고, 배럭이 5개 미만이고, BuildOrder Queue가 비어있으면 세 번째 배럭을 짓는다.
		    buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
		} else if (gameStatus.getMineral() >= 50) {
		    Unit2 barracks = allianceUnitInfo.getTrainableBuilding(UnitType.Terran_Barracks, UnitType.Terran_Marine);
		    if (null != barracks) {
			Set<Unit2> medicSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Medic);
			Set<Unit2> marineSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Marine);
			int medicCount = medicSet.size() + allianceUnitInfo.getTrainingQueueUnitCount(UnitType.Terran_Barracks, UnitType.Terran_Medic);
			int marineCount = marineSet.size() + allianceUnitInfo.getTrainingQueueUnitCount(UnitType.Terran_Barracks, UnitType.Terran_Marine);
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
    }

    // 아카데미와 관련된 작업을 수행한다.
    private void doAcademyJob() {
	// 1초에 한 번만 수행된다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}
	Unit2 academy = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Academy);
	if (null != academy) {
	    // 사거리 업그레이드를 한다.
	    if (hasStrategyItem(StrategyItem.AUTO_UPGRADE_U_238_Shells)) {
		if (academy.canUpgrade(UpgradeType.U_238_Shells)) {
		    academy.upgrade(UpgradeType.U_238_Shells);
		}
	    }

	    // Comsat Station Add on
	    if (hasStrategyItem(StrategyItem.AUTO_ADDON_COMSAT_STATION)) {
		allianceUnitInfo.buildAddon(UnitType.Terran_Comsat_Station);
	    }

	    // 스팀팩 업그레이드를 한다.
	    if (hasStrategyItem(StrategyItem.AUTO_UPGRADE_STIMPACK)) {
		if (academy.canResearch(TechType.Stim_Packs)) {
		    academy.research(TechType.Stim_Packs);
		}
	    }
	}
    }

    // 벙커 관련된 작업을 수행한다.
    private void doBunkerJob() {
	// 1초에 한 번만 수행된다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}
	Set<Unit2> bunkerSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Bunker);
	for (Unit2 bunker : bunkerSet) {
	    if (hasStrategyItem(StrategyItem.AUTO_LOAD_MARINE_TO_BUNKER)) {
		if (0 < bunker.getSpaceRemaining()) {
		    marineToBunker(allianceUnitInfo, bunker);
		} else {
		    repairCount = 3;
		}
	    }
	    if (hasStrategyItem(StrategyItem.AUTO_REPAIR_BUNKER)) {
		if (gameStatus.getMineral() > 0) {
		    if (UnitType.Terran_Bunker.maxHitPoints() > bunker.getHitPoints()) {
			repairBunker(allianceUnitInfo, bunker);
		    } else {
			repairCount = 3;
		    }
		}
	    }
	}
    }

    // 벙거를 수리한다.
    private void repairBunker(UnitInfo allianceUnitInfo, Unit2 bunker) {
	WorkerManager workerManager = gameStatus.getWorkerManager();
	Unit2 repairWorker = workerManager.getInterruptableWorker(bunker.getTilePosition());
	if (repairCount > 0) {
	    if (null != repairWorker) {
		ActionUtil.repair(allianceUnitInfo, repairWorker, bunker);
		--repairCount;
	    }
	}
    }

    // 마린을 벙커에 넣는다.
    private void marineToBunker(UnitInfo allianceUnitInfo, Unit2 bunker) {
	Set<Unit2> marineSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Marine);
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

    // 필요할 경우, 스캔을 뿌린다.
    private void checkIfuseScan() {
	// 1초에 한 번만 수행된다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	if (hasStrategyItem(StrategyItem.AUTO_USING_SCAN)) {
	    // 스캔은 3초 이내에는 또 뿌리지 않는다.
	    if (gameStatus.getFrameCount() < lastScanFrameCount + 3 * 42) {
		return;
	    }
	    // 적 클로킹 유닛을 찾는다.
	    Set<Unit2> clockedUnitSet = enemyUnitInfo.getUnitSet(UnitKind.Clocked);
	    Log.trace("Clocked Unit Size: %d", clockedUnitSet.size());
	    for (Unit2 clockedUnit : clockedUnitSet) {
		if (null != clockedUnit && clockedUnit.exists()) {
		    int distance = 300;
		    if (UnitUtil.compareUnitKind(clockedUnit, UnitKind.Protoss_Dark_Templar)) {
			// 다크 템플러는 충분히 가까운 거리에 있을때 스캔을 뿌린다.
			distance = 150;
		    }
		    // 적 클로킹 유닛 distance 거리 미만에 존재하는 아군 유닛이 5기 이상이면 스캔을 뿌린다.
		    Set<Unit2> allianceUnitSet = allianceUnitInfo.getUnitsInRange(clockedUnit.getPosition(), UnitKind.Terran_Marine, distance);
		    Log.info("적 클로킹 유닛 발견: %s. 주변의 마린 수: %d, 거리: %d", clockedUnit, allianceUnitSet.size(), distance);
		    if (5 <= allianceUnitSet.size()) {
			allianceUnitInfo.doScan(clockedUnit.getPosition());
			lastScanFrameCount = gameStatus.getFrameCount();
			break;
		    }
		}
	    }
	}
    }

    // StrategyItem.AUTO_DEFENCE_ALLIANCE_BASE 구현부
    // 본진 주변에 적 유닛이 있으면, 방어한다.
    private void doDefenceBase() {
	// 1초에 한 번만 실행한다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}
	removeStrategyStatus(StrategyStatus.BACK_TO_BASE);
	if (hasStrategyItem(StrategyItem.AUTO_DEFENCE_ALLIANCE_BASE)) {
	    // 커맨드 센터를 가져온다.
	    Set<Unit2> commandCenterSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center);
	    for (Unit2 commandCenter : commandCenterSet) {
		// 커맨드 센터 반경 800 이내의 적 유닛 정보를 하나 가져온다.
		Set<Unit2> enemyUnitSet = enemyUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.ALL, 800);
		// 쳐들어온 적 병력이 없으면 skip 하고 다음 커멘트 센터를 검사한다.
		if (enemyUnitSet.isEmpty()) {
		    continue;
		}

		Position defencePosition = enemyUnitSet.iterator().next().getPosition();
		Set<Unit2> defenceAllianceUnitSet = allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.Combat_Unit, 800);
		if (enemyUnitSet.size() < defenceAllianceUnitSet.size()) {
		    Log.info("본진(%s)에 침입한 적(%d) 발견함. 방어하자.", commandCenter, enemyUnitSet.size());
		    // 커맨드 센터 반경 800 이내의 아군 유닛으로 방어한다.
		    for (Unit2 defenceAllianceUnit : defenceAllianceUnitSet) {
			ActionUtil.attackPosition(allianceUnitInfo, defenceAllianceUnit, defencePosition);
		    }
		} else {
		    Log.info("본진(%s)에 침입한 적(%d)이 아군(%d)보다 많다. 주 병력을 모두 회군시키자.", commandCenter, enemyUnitSet.size(), defenceAllianceUnitSet.size());
		    addStrategyStatus(StrategyStatus.BACK_TO_BASE);
		    for (Unit2 allianceUnit : allianceUnitInfo.getUnitSet(UnitKind.Combat_Unit)) {
			ActionUtil.attackPosition(allianceUnitInfo, allianceUnit, defencePosition);
		    }
		}

	    }
	}
    }

    // StrategyItem.AUTO_TRAIN_VULTURE 구현부
    // 벌쳐를 자동으로 생성해준다.
    private void doAutoTrainVulture() {
	if (hasStrategyItem(StrategyItem.AUTO_TRAIN_VULTURE)) {
	    Set<Unit2> factorySet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Factory);
	    for (Unit2 factory : factorySet) {
		if (!factory.isCompleted()) {
		    continue;
		}
		if (0 == factory.getTrainingQueue().size()) {
		    int trainingRemainSize = buildManager.getBuildOrderQueueItemCount(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture);
		    if (1 > trainingRemainSize) {
			Log.info("벌쳐 생산. 남은 훈련시간: %d, 팩토리: %s", factory.getRemainingTrainTime(), factory);
			buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
		    }
		}
	    }
	}
    }

    // StrategyItem.AUTO_BUILD_FACTORY 구현부
    // 여유가 되면 팩토리를 자동으로 추가한다. 이미 건설 중인 팩토리가 있다면, 건설하지 않는다. 즉 동시에 두 개의 팩토리가 지어지지는 않는다.
    private void doAutoBuildFactory() {
	// 1초에 한 번만 실행한다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	// 돈과 가스가 남으면 팩토리를 지어본다.
	if (hasStrategyItem(StrategyItem.AUTO_BUILD_FACTORY)) {
	    if (1 > allianceUnitInfo.getConstructionCount(UnitType.Terran_Factory)) {
		if (allianceUnitInfo.checkResourceIfCanBuild(UnitType.Terran_Factory)) {
		    int buildFactoryRemainSize = buildManager.getBuildOrderQueueItemCount(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory);
		    if (1 > buildFactoryRemainSize) {
			buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory));
		    }
		}
	    }
	}
    }

    // StrategyItem.AUTO_EXTENSION 구현부
    // 여유가 되면 팩토리를 자동으로 추가한다. 이미 건설 중인 팩토리가 있다면, 건설하지 않는다. 즉 동시에 두 개의 팩토리가 지어지지는 않는다.
    private void doAutoExtension() {
	// 1초에 한 번만 실행한다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	if (!hasStrategyItem(StrategyItem.AUTO_EXTENSION)) {
	    return;
	}

	// 돈과 가스가 남으면 팩토리를 지어본다.
	if (1 > allianceUnitInfo.getConstructionCount(UnitType.Terran_Command_Center)) {
	    if (allianceUnitInfo.checkResourceIfCanBuild(UnitType.Terran_Command_Center)) {
		int buildFactoryRemainSize = buildManager.getBuildOrderQueueItemCount(BuildOrderItem.Order.BUILD, UnitType.Terran_Command_Center);
		if (1 > buildFactoryRemainSize) {
		    buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Command_Center));
		}
	    }
	}
    }

    // StrategyItem.AUTO_BUILD_SUPPLY 구현부
    // 빌드 오더 큐에 들어있는 유닛을 보고, 서플라이 디팟을 짓는 최적 타이밍을 계산한다.
    private void doAutoBuildSupply() {
	// 1초에 한 번만 실행한다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}
	if (!hasStrategyItem(StrategyItem.AUTO_BUILD_SUPPLY)) {
	    return;
	}

	// 서플라이를 짓기 위해서 빌드 오더 큐 재배열.
	buildManager.rearrangeForSupply();
    }

    // ///////////////////////////////////////////////////////////
    // Getter 및 Setter 류 메서드
    // ///////////////////////////////////////////////////////////

    public Unit2 getHeadAllianceUnit() {
	return headAllianceUnit;
    }

    public void setHeadAllianceUnit(Unit2 headAllianceUnit) {
	this.headAllianceUnit = headAllianceUnit;
    }

    public Set<StrategyItem> getStrategyItems() {
	return strategyItems;
    }

    public boolean hasStrategyItem(StrategyItem strategyItem) {
	return strategyItems.contains(strategyItem);
    }

    public void setStrategyItems(Set<StrategyItem> strategyItems) {
	this.strategyItems = strategyItems;
    }

    public boolean containStrategyStatus(StrategyStatus strategyStatus) {
	return this.strategyStatus.contains(strategyStatus);
    }

    public void addStrategyStatus(StrategyStatus strategyStatus) {
	this.strategyStatus.add(strategyStatus);
    }

    public void removeStrategyStatus(StrategyStatus strategyStatus) {
	this.strategyStatus.remove(strategyStatus);
    }

    public boolean hasAttackTilePosition() {
	return null != attackTilePosition ? true : false;
    }

    public TilePosition getAttackTilePositon() {
	return attackTilePosition;
    }

    public void setAttackTilePosition(TilePosition attackTilePosition) {
	Log.info("공격 지점이 %s -> %s로 변경됨.", this.attackTilePosition, attackTilePosition);
	this.attackTilePosition = attackTilePosition;
    }

    public void clearAttackTilePosition() {
	Log.info("공격 지점이 %s -> null 로 변경됨.", attackTilePosition);
	attackTilePosition = null;
    }

    public boolean hasDefenceTilePosition() {
	return null != defenceTilePosition ? true : false;
    }

    public TilePosition getDefenceTilePosition() {
	return defenceTilePosition;
    }

    public void setDefenceTilePosition(TilePosition defenceTilePosition) {
	this.defenceTilePosition = defenceTilePosition;
    }

    public boolean addStrategyItem(StrategyItem strategyItem) {
	return strategyItems.add(strategyItem);
    }

    public boolean removeStrategyItem(StrategyItem strategyItem) {
	return strategyItems.remove(strategyItem);
    }
}