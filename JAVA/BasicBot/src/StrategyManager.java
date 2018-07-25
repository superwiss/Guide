import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bwapi.Position;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import bwta.BWTA;

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
<<<<<<< HEAD
	strategy = new StrategyDefense();
=======
	strategy = new StrategyDefault();
	//strategy = new StrategyTwoFactory();
>>>>>>> refs/remotes/origin/Development

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
	doBarrackJob();
	doSCVAutoTrain();
	doFactoryRally();
	doRefineryJob();

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

	BuildManager buildManager = gameStatus.getBuildManager();
	if (unit.getType().equals(UnitType.Terran_SCV) || unit.getType().equals(UnitType.Terran_Command_Center)) {
	    if (buildManager.isInitialBuildFinished()) {
		rebalanceWorkers();
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

    public void doFactoryRally() {

	if (strategyItems.contains(StrategyItem.SET_FACTORY_RALLY)) {
	    // 팩토리의 랠리 포인트를 설정한다.
	    LocationManager locationManager = gameStatus.getLocationManager();

	    for (Unit2 factory : allianceUnitInfo.getUnitSet(UnitKind.Terran_Factory)) {
		if (multiCount == 0) {
		    factory.setRallyPoint(locationManager.getBaseEntranceChokePoint().toPosition());
		} else if (multiCount == 1) {
		    factory.setRallyPoint(locationManager.getFirstExtensionChokePoint().toPosition());
		}
	    }
	}
    }

    private void doRefineryJob() {

	//이니셜 빌드오더가 끝난 상황에서, 가스 일꾼이 3기 미만인 경우, 자동으로 일꾼 3마리를 할당시켜준다.
	if (hasStrategyItem(StrategyItem.AUTO_ASSIGN_GAS_SCV)) {

	    BuildManager buildManager = gameStatus.getBuildManager();

	    //이니셜 빌드오더가 끝났을 경우
	    if (buildManager.isInitialBuildFinished()) {

		//5초에 한번만 시행한다.
		if (!gameStatus.isMatchedInterval(5)) {
		    return;
		}

		System.out.println("가스 채워넣자");
		//아군의 모든 커맨드 센터를 대상으로
		for (Unit2 commandCenter : allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center)) {

		    //대상 커맨드 센터에 할당된 가스 일꾼이 3기 미만일 경우,
		    System.out.println("할당된 가스 일꾼 " + findUnitSetNear(commandCenter, UnitKind.Worker_Gather_Gas, 320).size());
		    if (findUnitSetNear(commandCenter, UnitKind.Worker_Gather_Gas, 320).size() < 3) {

			//대상 커맨드 센터에 할당된 리파이너리를 가져온다.
			Unit2 refinery = findOneUnitNear(commandCenter, UnitKind.Terran_Refinery, 320);

			//리파이너를 찾았을 경우
			System.out.println("리파이너리를 찾아보자");
			if (refinery != null) {

			    System.out.println("리파이너리를 찾았다");
			    //대상 커맨드 센터 주변의 미네랄 일꾼을 찾는다.
			    //미네랄 일꾼이 3기 이상이고, 리파이너리가 지어져 있으면 미네랄 일꾼을 가스에 할당한다.
			    System.out.println("현재 미네랄 일꾼 " + findMineralWorkerSetNear(commandCenter, UnitKind.Terran_SCV, 320).size());
			    if (findMineralWorkerSetNear(commandCenter, UnitKind.Terran_SCV, 320).size() >= 3 && refinery.isCompleted()) {
				buildManager.add(new BuildOrderItem(BuildOrderItem.Order.GATHER_GAS, refinery));
			    } else {
				//미네랄 일꾼이 부족하거나 리파이너리가 건설 중이다.
				System.out.println("미네랄 일꾼이 부족하다 or 건설중이다.");
			    }

			} else {
			    //리파이너리가 없어서 건설이 필요하다.
			    //큐에 아무것도 없고, 대상 커맨드 센터가 완성되었을 경우 리파이너리 건설
			    if (0 == buildManager.getQueueSize() && commandCenter.isCompleted() && !commandCenter.isLifted()) {
				System.out.println("건설ㅋㅋ");
				//대상 커맨드 센터 주변의 베스핀 가스를 가져온다.
				//가져온 베스핀 가스 위치에 리파이너리를 건설한다.
				Unit2 vespene = findOneUnitNear(commandCenter, UnitKind.Resource_Vespene_Geyser, 320);
				if (vespene != null) {
				    buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Refinery, vespene.getTilePosition()));
				} else {
				    return;
				}
			    } else {
				System.out.println("큐가 찼다 or 커맨드가 건설중이다.");
			    }
			    return;
			}

		    } else {
			//대상 커맨드 센터는 정상적으로 가스 채취가 이루어지고 있다.
			System.out.println("훌륭하다");
		    }
		}
	    }
	}
    }

    public void rebalanceWorkers() {

	if (hasStrategyItem(StrategyItem.AUTO_BALANCE_SCV)) {

	    //모든 커맨드 센터의 미네랄을 캐는 일꾼 숫자를 가져온다.
	    int total_scv = 0;
	    for (Unit2 commandCenter : allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center)) {
		total_scv += findUnitSetNear(commandCenter, UnitKind.Terran_SCV, 320).size();
	    }

	    //모든 커맨드 센터 근처의 미네랄 덩이 수를 가져온다.
	    int total_mineral = 0;
	    for (Unit2 commandCenter : allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center)) {
		total_mineral += findUnitSetNear(commandCenter, UnitKind.Resource_Mineral_Field, 320).size();
	    }

	    System.out.println("토탈 scv " + total_scv);
	    System.out.println("총 미네랄 " + total_mineral);

	    //각 커맨드 센터의 일꾼 부족 현황을 가져온다.
	    for (Unit2 commandCenter : allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center)) {

		int result = checkMineralBalance(commandCenter, total_scv, total_mineral);

		//부족한 커맨드 센터에 대해, 여유량 만큼 scv를 이동시킨다.
		if (result < 0 && commandCenter.isCompleted() && !commandCenter.isLifted()) {

		    //여유가 있는 커맨드 센터를 가져온다.
		    Unit2 enoughCommand = null;
		    for (Unit2 commandCenter2 : allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center)) {
			int result2 = checkMineralBalance(commandCenter2, total_scv, total_mineral);
			if (result2 > 0) {
			    enoughCommand = commandCenter;
			}
		    }

		    //여유가 있는 커맨드 센터의 scv를 가져온다.
		    Set<Unit2> scvCandidate = null;
		    if (enoughCommand == null) {
			return;
		    } else {
			scvCandidate = findMineralWorkerSetNear(enoughCommand, UnitKind.Terran_SCV, 320);
		    }

		    //부족한 숫자만큼 scv를 stop시켜 다른 커맨드 센터에 할당되게 한다.
		    int seq = 0;
		    for (Unit2 scv : scvCandidate) {
			scv.stop();
			seq++;
			if (seq == Math.abs(result)) {
			    return;
			}
		    }
		}
	    }
	}
    }

    //대상 유닛 근처에 있는 유닛셋 전체를 리턴한다.
    public Set<Unit2> findUnitSetNear(Unit2 baseUnit, UnitKind wantFind, int findRange) {

	Set<Unit2> targetUnitSet = new HashSet<>(allianceUnitInfo.getUnitSet(wantFind));
	Set<Unit2> nearUnitSet = new HashSet<>();

	for (Unit2 targetUnit : targetUnitSet) {
	    if (targetUnit.getDistance(baseUnit) < findRange) {
		nearUnitSet.add(targetUnit);
	    }
	}

	return nearUnitSet;
    }

    //대상 유닛 근처에 있는 유닛 하나를 리턴한다.
    public Unit2 findOneUnitNear(Unit2 baseUnit, UnitKind wantFind, int findRange) {

	Set<Unit2> targetUnitSet = findUnitSetNear(baseUnit, wantFind, findRange);
	Unit2 findUnit = null;

	for (Unit2 targetUnit : targetUnitSet) {
	    findUnit = targetUnit;
	}

	return findUnit;
    }

    //대상 유닛 근처에 있는 미네랄 일꾼을 리턴한다.
    public Set<Unit2> findMineralWorkerSetNear(Unit2 baseUnit, UnitKind wantFind, int findRange) {

	Set<Unit2> scvUnitSet = findUnitSetNear(baseUnit, wantFind, findRange);
	Set<Unit2> findUnitSet = new HashSet<>();
	WorkerManager workerManager = gameStatus.getWorkerManager();

	for (Unit2 scv : scvUnitSet) {
	    if (workerManager.isinterruptableWorker(scv)) {
		findUnitSet.add(scv);
	    }
	}

	return findUnitSet;
    }

    private int checkMineralBalance(Unit2 commandCenter, int total_scv, int total_mineral) {

	int mineralCount = findUnitSetNear(commandCenter, UnitKind.Resource_Mineral_Field, 320).size();
	System.out.println("현재 미네랄 숫자 " + mineralCount);
	int scvCount = findMineralWorkerSetNear(commandCenter, UnitKind.Terran_SCV, 320).size();
	System.out.println("현재 scv 숫자 " + scvCount);

	double d = (mineralCount / (double) total_mineral);
	System.out.println("d" + d);
	int goodNum = (int) (total_scv * (d));
	System.out.println("적정 숫자 " + goodNum);

	int result = scvCount - goodNum;
	System.out.println("결과 값 " + result);
	return result;
    }

    public int getMineralNearCommandCenter(Unit2 commandCenter) {
	int mineralCount = 0;
	Set<Unit2> mineralSet = new HashSet<>(allianceUnitInfo.getUnitSet(UnitKind.Resource_Mineral_Field));
	for (Unit2 mineral : mineralSet) {
	    if (mineral.getDistance(commandCenter) < 320) {
		mineralCount++;
	    }
	}
	return mineralCount;
    }

    // 자동으로 SCV를 훈련하는 작업을 수행한다
    private void doSCVAutoTrain() {

	// 1초에 한 번만 수행된다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	BuildManager buildManager = gameStatus.getBuildManager();

	if (hasStrategyItem(StrategyItem.AUTO_TRAIN_SCV) && buildManager.isInitialBuildFinished()) {

	    if (MyBotModule.Broodwar.self().supplyTotal() - MyBotModule.Broodwar.self().supplyUsed() < 2) {
		return;
	    }

	    int mineral_count = 0;

	    Set<Unit2> commandCenters = allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center);

	    for (Unit2 commandCenter : commandCenters) {

		int minerals = getMineralNearCommandCenter(commandCenter);
		System.out.println("커맨드 센터 근처 미네랄" + minerals);
		if (minerals > 0) {
		    mineral_count += minerals;
		}
	    }

	    System.out.println("큐사이즈 " + buildManager.getQueueSize());
	    if (0 == buildManager.getQueueSize()) {

		if (gameStatus.getMineral() >= 50) {

		    System.out.println("커맨드 센터 수 " + commandCenters.size());
		    int maxworkerCount = mineral_count * 2 + 8 * commandCenters.size();

		    Set<Unit2> scvSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_SCV);
		    int scvCount = scvSet.size() + allianceUnitInfo.getTrainingQueueUnitCount(UnitType.Terran_Command_Center, UnitType.Terran_SCV);

		    int maxscv = 60;

		    System.out.println("현재 scv 카운트 " + scvCount);
		    System.out.println("맥스 워커 카운트 " + maxworkerCount);
		    if (scvCount < maxscv && scvCount < maxworkerCount) {
			Unit2 commandCenter = allianceUnitInfo.getTrainableBuilding(UnitType.Terran_Command_Center, UnitType.Terran_SCV);
			if (null != commandCenter) {
			    Log.info("SCV 생산. SCV 수: %d,", scvCount);
			    System.out.println("생 산 ");
			    commandCenter.train(UnitType.Terran_SCV);
			}
		    }
		}
	    }
	}
    }

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

    private void doBarrackJob() {

	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	LocationManager locaionManager = gameStatus.getLocationManager();

	if (hasStrategyItem(StrategyItem.AUTO_LIFT_COMMAND_CENTER) && multiCount == 1) {

	    Set<Unit2> commandCenterUnitSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center);
	    Set<Unit2> barrackUnitSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Barracks);
	    LocationManager locationManager = gameStatus.getLocationManager();

	    if (commandCenterUnitSet.size() == 2) {

		Unit2 entranceBarrack = null;
		TilePosition firstExpansionLocation = locationManager.getSecondEntranceBuilding().get(0);

		for (Unit2 unit : barrackUnitSet) {
		    //		    if (unit.getTilePosition().getX() == locaionManager.getEntranceBuilding().get(0).getX()
		    //			    && unit.getTilePosition().getY() == locaionManager.getEntranceBuilding().get(0).getY()) {
		    entranceBarrack = unit;
		    continue;
		    //		    } 
		}

		if (entranceBarrack != null) {
		    if (entranceBarrack.isLifted() == false) {
			if (entranceBarrack.getTilePosition().getX() != locaionManager.getSecondEntranceBuilding().get(0).getX()
				|| entranceBarrack.getTilePosition().getY() != locaionManager.getSecondEntranceBuilding().get(0).getY()) {
			    entranceBarrack.lift();
			}
		    } else {
			entranceBarrack.land(new TilePosition(firstExpansionLocation.getX(), firstExpansionLocation.getY()));
		    }
		    if (entranceBarrack.isLifted() == false && entranceBarrack.getTilePosition().getX() == firstExpansionLocation.getX()
			    && entranceBarrack.getTilePosition().getY() == firstExpansionLocation.getY()) {
			return;
		    }
		}
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

    public final TilePosition getRefineryPositionNear(TilePosition seedPosition) {

	TilePosition closestGeyser = TilePosition.None;
	double minGeyserDistanceFromSeedPosition = 100000000;

	// 전체 geyser 중에서 seedPosition 으로부터 16 TILE_SIZE 거리 이내에 있는 것을 찾는다
	for (Unit geyser : MyBotModule.Broodwar.getStaticGeysers()) {
	    // geyser->getPosition() 을 하면, Unknown 으로 나올 수 있다.
	    // 반드시 geyser->getInitialPosition() 을 사용해야 한다

	    Position geyserPos = geyser.getInitialPosition();
	    TilePosition geyserTilePos = geyser.getInitialTilePosition();

	    // if it is not connected fron seedPosition, it is located in another island
	    if (!BWTA.isConnected(seedPosition, geyserTilePos)) {
		continue;
	    }

	    // 이미 지어져 있는가
	    boolean refineryAlreadyBuilt = false;
	    List<Unit> alreadyBuiltUnits = MyBotModule.Broodwar.getUnitsInRadius(geyserPos, 4 * Config.TILE_SIZE);
	    for (Unit u : alreadyBuiltUnits) {
		if (u.getType().isRefinery() && u.exists()) {
		    refineryAlreadyBuilt = true;
		}
	    }

	    //std::cout << " geyser TilePos is not reserved, is connected, is not refineryAlreadyBuilt" << std::endl;

	    if (refineryAlreadyBuilt == false) {
		//double thisDistance = BWTA.getGroundDistance(geyserPos.toTilePosition(), seedPosition);

		double thisDistance = geyserPos.getDistance(seedPosition.toPosition());

		if (thisDistance < minGeyserDistanceFromSeedPosition) {
		    //std::cout << " selected " << std::endl;

		    minGeyserDistanceFromSeedPosition = thisDistance;
		    closestGeyser = geyser.getInitialTilePosition();
		}
	    }
	}
	return closestGeyser;
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
	    //	    Log.trace("Clocked Unit Size: %d", clockedUnitSet.size());
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