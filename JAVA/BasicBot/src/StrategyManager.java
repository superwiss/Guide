import java.util.HashSet;
import java.util.Set;

import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class StrategyManager extends Manager {

    private StrategyBase strategy = null; // 현재 전략
    private Set<StrategyItem> strategyItems = new HashSet<>(); // 전략 전술 플래그
    private TilePosition attackTilePosition = null; // 공격 지점. 공격 지점이 null이면 유닛들은 대기한다. 공격 지점이 설정되면, 유닛들은 해당 지점으로 Attack Position을 수행한다.
    private TilePosition defenceTilePosition = null; // 방어 지점. 아군 유닛이나 건물이 공격 받으면, 그 위치가 방어 지점이 된다.
    private Unit2 headAllianceUnit = null; // 아군의 공격 선두 유닛
    private static int repairCount = 3; // 벙커를 수리할 SCV 개수
    private int lastScanFrameCount = 0; // 마지막으로 스캔을 뿌린 시각
    private static int multiCount = 0;

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);

	// TODO 상대방의 종족이나 ID에 따라서 전략을 선택한다.
	strategy = new StrategyDefense();

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
	doEngineeringBayJob();
	doFactoryJob();
	doCommandJob();

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

	if (strategyItems.contains(StrategyItem.SET_FACTORY_RALLY)) {
	    // 배럭의 랠리 포인트를 설정한다.
	    LocationManager locationManager = gameStatus.getLocationManager();
	    if (null != unit && unit.getType().equals(UnitType.Terran_Factory)) {

		if (multiCount == 0) {
		    unit.setRallyPoint(locationManager.getBaseEntranceChokePoint().toPosition());
		} else if (multiCount == 1) {
		    unit.setRallyPoint(locationManager.getFirstExtensionChokePoint().toPosition());
		}

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

    // ///////////////////////////////////////////////////////////
    // StrategyImte 구현부
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
		    buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
		} else if (gameStatus.getMineral() > 200 && null != allianceUnitInfo.getAnyUnit(UnitKind.Terran_Academy)
			&& 5 > allianceUnitInfo.getUnitSet(UnitKind.Terran_Barracks).size() && 0 == buildManager.getQueueSize()) {
		    // 아카데미가 존재하고, 배럭이 5개 미만이고, BuildOrder Queue가 비어있으면 세 번째 배럭을 짓는다.
		    buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
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

	if (hasStrategyItem(StrategyItem.AUTO_TRAIN_MECHANIC_UNIT)) {
	    BuildManager buildManager = gameStatus.getBuildManager();
	    if (0 == buildManager.getQueueSize() && true == buildManager.isInitialBuildFinished()) {
		// 서플 여유가 6개 이하면 서플을 짓는다. (최대 1개를 동시에 지을 수 있음)
		if (1 > allianceUnitInfo.getConstructionCount(UnitType.Terran_Supply_Depot) && gameStatus.getSupplyRemain() <= 6 * 2) {
		    buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
		} else if (gameStatus.getMineral() > 200 && gameStatus.getGas() > 100 && 4 > allianceUnitInfo.getUnitSet(UnitKind.Terran_Factory).size()
			&& 0 == buildManager.getQueueSize()) {
		    // 팩토리가 4개 미만이고, BuildOrder Queue가 비어있으면 팩토리를 짓는다.
		    buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory));
		} else if (gameStatus.getMineral() >= 75) {
		    Unit2 barracks = allianceUnitInfo.getTrainableBuilding(UnitType.Terran_Factory, UnitType.Terran_Vulture);
		    if (null != barracks) {
			Set<Unit2> tankSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Siege_Tank_Tank_Mode);
			Set<Unit2> vultureSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Vulture);
			int tankCount = tankSet.size() + allianceUnitInfo.getTrainingQueueUnitCount(UnitType.Terran_Factory, UnitType.Terran_Siege_Tank_Tank_Mode);
			int vultureCount = vultureSet.size() + allianceUnitInfo.getTrainingQueueUnitCount(UnitType.Terran_Factory, UnitType.Terran_Vulture);

			// 벌쳐4마리당 탱크 1대
			Log.info("탱크/벌쳐 생산. 탱크 수: %d, 벌쳐 수: %d", tankCount, vultureCount);
			if (tankCount * 4 < vultureCount) {
			    barracks.train(UnitType.Terran_Siege_Tank_Tank_Mode);
			} else {
			    barracks.train(UnitType.Terran_Vulture);
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
	    if (hasStrategyItem(StrategyItem.AUTO_RESEARCH_U_238_Shells)) {
		if (academy.canUpgrade(UpgradeType.U_238_Shells)) {
		    academy.upgrade(UpgradeType.U_238_Shells);
		}
	    }

	    // Comsat Station Add on
	    if (hasStrategyItem(StrategyItem.AUTO_ADDON_COMSAT_STATION)) {
		allianceUnitInfo.buildAddon(UnitType.Terran_Comsat_Station);
	    }

	    // 스팀팩 업그레이드를 한다.
	    if (hasStrategyItem(StrategyItem.AUTO_RESEARCH_STIMPACK)) {
		if (academy.canResearch(TechType.Stim_Packs)) {
		    academy.research(TechType.Stim_Packs);
		}
	    }
	}
    }

    // 아카데미와 관련된 작업을 수행한다.
    private void doFactoryJob() {
	// 1초에 한 번만 수행된다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	Unit2 factory = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Factory);
	if (null != factory) {
	    // Machine Shop Add on
	    if (hasStrategyItem(StrategyItem.AUTO_ADDON_MACHINE_SHOP)) {
		allianceUnitInfo.buildAddon(UnitType.Terran_Machine_Shop);
	    }
	}
    }

    private void doCommandJob() {

	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	if (hasStrategyItem(StrategyItem.AUTO_LIFT_COMMAND_CENTER) && multiCount == 0) {

	    Set<Unit2> commandCenterUnitSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center);
	    LocationManager locationManager = gameStatus.getLocationManager();

	    if (commandCenterUnitSet.size() == 2) {

		Unit2 firstExpansionCommandCenter = null;
		TilePosition baseLocation = locationManager.getAllianceBaseLocation();
		TilePosition firstExpansionLocation = locationManager.getFirstExpansionLocation().get(0);

		for (Unit2 unit : commandCenterUnitSet) {

		    if (unit.getTilePosition().getX() == baseLocation.getX() && unit.getTilePosition().getY() == baseLocation.getY()) {
			continue;
		    } else {
			firstExpansionCommandCenter = unit;
			break;
		    }
		}

		if (firstExpansionCommandCenter != null) {
		    if (firstExpansionCommandCenter.isLifted() == false) {
			if (firstExpansionCommandCenter.getTilePosition().getX() != firstExpansionLocation.getX()
				|| firstExpansionCommandCenter.getTilePosition().getY() != firstExpansionLocation.getY()) {
			    firstExpansionCommandCenter.lift();
			}
		    } else {
			firstExpansionCommandCenter.land(new TilePosition(firstExpansionLocation.getX(), firstExpansionLocation.getY()));
		    }
		    if (firstExpansionCommandCenter.isLifted() == false && firstExpansionCommandCenter.getTilePosition().getX() == firstExpansionLocation.getX()
			    && firstExpansionCommandCenter.getTilePosition().getY() == firstExpansionLocation.getY()) {
			multiCount = 1;
		    }
		}
	    }

	}

    }

    // 엔지니어링 베이와 관련된 작업을 수행한다.
    private void doEngineeringBayJob() {

	// 1초에 한 번만 수행된다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	Unit2 engineeringBay = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Engineering_Bay);

	if (null != engineeringBay) {

	    if (hasStrategyItem(StrategyItem.AUTO_UPGRADE_BIONIC_UNIT)) {
		// 공격력, 방어력 1레벨 업그레이드를 한다.
		if (engineeringBay.getPlayer().getUpgradeLevel(UpgradeType.Terran_Infantry_Weapons) == 0 && engineeringBay.canUpgrade(UpgradeType.Terran_Infantry_Weapons)) {
		    engineeringBay.upgrade(UpgradeType.Terran_Infantry_Weapons);
		} else if (engineeringBay.getPlayer().getUpgradeLevel(UpgradeType.Terran_Infantry_Armor) == 0 && engineeringBay.canUpgrade(UpgradeType.Terran_Infantry_Armor)) {
		    engineeringBay.upgrade(UpgradeType.Terran_Infantry_Armor);
		}

		// 아군이 바이오닉 체제일 경우(마린 두부대 이상) 계속해서 공격력 방어력 업그레이드를 한다.
		// *참고 : 이영호는 1/1업 후 체제 변환 시 엔지니어링 베이를 정찰용으로 띄운다.
		Set<Unit2> marineSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Marine);
		int marineCount = marineSet.size() + allianceUnitInfo.getTrainingQueueUnitCount(UnitType.Terran_Barracks, UnitType.Terran_Marine);
		Unit2 scienceFacility = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Science_Facility);

		if (marineCount > 24 && null != scienceFacility) {
		    if (engineeringBay.getPlayer().getUpgradeLevel(UpgradeType.Terran_Infantry_Weapons) < 3 && engineeringBay.canUpgrade(UpgradeType.Terran_Infantry_Weapons)) {
			engineeringBay.upgrade(UpgradeType.Terran_Infantry_Weapons);
		    } else if (engineeringBay.getPlayer().getUpgradeLevel(UpgradeType.Terran_Infantry_Armor) < 3 && engineeringBay.canUpgrade(UpgradeType.Terran_Infantry_Armor)) {
			engineeringBay.upgrade(UpgradeType.Terran_Infantry_Armor);
		    }
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

    // 본진 주변에 적 유닛이 있으면, 방어한다.
    private void doDefenceBase() {
	// 1초에 한 번만 실행한다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}
	if (hasStrategyItem(StrategyItem.AUTO_DEFENCE_ALLIANCE_BASE)) {
	    // 커맨드 센터를 가져온다.
	    Set<Unit2> commandCenterSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center);
	    for (Unit2 commandCenter : commandCenterSet) {
		// 커맨드 센터 반경 800 이내의 적 유닛 정보를 하나 가져온다.
		Unit2 enemyUnit = enemyUnitInfo.getAnyUnitInRange(commandCenter.getPosition(), UnitKind.ALL, 800);
		if (null != enemyUnit) {
		    Log.info("본진(%s)에 침입한 적(%s) 발견함. 방어하자.", commandCenter, enemyUnit);
		    // 커맨드 센터 반경 800 이내의 아군 유닛으로 방어한다.
		    Set<Unit2> defenceAllianceUnitSet = allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.Combat_Unit, 800);
		    for (Unit2 defenceAllianceUnit : defenceAllianceUnitSet) {
			ActionUtil.attackPosition(allianceUnitInfo, defenceAllianceUnit, enemyUnit.getPosition());
		    }
		}
	    }
	}
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
}