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
import bwta.BaseLocation;

public class StrategyManager extends Manager {

    private StrategyBase strategy = null; // 현재 전략
    private Set<StrategyItem> strategyItems = new HashSet<>(); // 전략 전술 플래그
    private TilePosition attackTilePosition = null; // 공격 지점. 공격 지점이 null이면 유닛들은 대기한다. 공격 지점이 설정되면, 유닛들은 해당 지점으로 Attack Position을 수행한다.
    private TilePosition defenceTilePosition = null; // 방어 지점. 아군 유닛이나 건물이 공격 받으면, 그 위치가 방어 지점이 된다.
    private Unit2 headAllianceUnit = null; // 아군의 공격 선두 유닛
    private static int repairCount = 3; // 벙커를 수리할 SCV 개수
    private int lastScanFrameCount = 0; // 마지막으로 스캔을 뿌린 시각
    public static int multiCount = 0;
    private int phase = 0;

    BuildManager buildManager = null;
    LocationManager locationManager = null;
    WorkerManager workerManager = null;

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);

	// TODO 상대방의 종족이나 ID에 따라서 전략을 선택한다.
	strategy = new StrategyDefense();
	strategy.onStart(gameStatus);

	buildManager = gameStatus.getBuildManager();
	locationManager = gameStatus.getLocationManager();
	workerManager = gameStatus.getWorkerManager();
    }

    @Override
    public void onFrame() {
	super.onFrame();

	doExpansion();
	checkIfuseScan();

	doAttackUnitAutoTrain();
	doSCVAutoTrain();
	doAcademyJob();
	doArmoryJob();
	doEngineeringBayJob();
	doMachineShopJob();

	doBunkerJob();
	doFactoryAddOnJob();
	doRefineryJob();

	doDefenceBase();
	liftCommandJob();
	liftBarrackJob();

	doPhaseCheck();
	doScienceFacility();
	doRebalance();
	//doFactoryRally();

	strategy.onFrame();
    }

    private void doRebalance() {

	//5초에 한번만 시행한다. 
	if (!gameStatus.isMatchedInterval(10)) {
	    return;
	}

	rebalanceWorkers();

    }

    private void doScienceFacility() {

	if (allianceUnitInfo.getUnitSet(UnitKind.Mechanic_Unit).size() > 30) {

	    BuildManager buildManager = gameStatus.getBuildManager();

	    //사이언스 퍼실리티 건설
	    Unit2 starport = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Starport);
	    if (null == starport) {
		if (hasStrategyItem(StrategyItem.AUTO_BUILD_TWO_ARMORY)) {
		    if (gameStatus.getMineral() > 150 && gameStatus.getGas() > 100 && 0 == buildManager.getQueueSize() && buildManager.isInitialBuildFinished()) {
			if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Starport).size() == 0) {
			    // 첫번째 아머리는 바로 짓는다.
			    buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Starport));
			}
		    }
		}
	    }

	    Unit2 scienceFacility = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Science_Facility);
	    if (null == scienceFacility) {
		if (hasStrategyItem(StrategyItem.AUTO_BUILD_TWO_ARMORY)) {
		    if (gameStatus.getMineral() > 150 && gameStatus.getGas() > 150 && 0 == buildManager.getQueueSize() && buildManager.isInitialBuildFinished()) {
			if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Science_Facility).size() == 0) {
			    // 첫번째 아머리는 바로 짓는다.
			    buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Science_Facility));
			}
		    }
		}
	    }
	}
    }

    private void doPhaseCheck() {

	if (strategyItems.contains(StrategyItem.ALLOW_PHASE)) {

	    int completeCommand = 0;
	    for (Unit2 commandCenter : allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center)) {
		if (commandCenter.isCompleted()) {
		    completeCommand++;
		}
	    }

	    if (completeCommand == 1 && phase != 5) {
		setPhase(0);
	    } else if (completeCommand == 2 && phase != 5) {
		setPhase(1);
	    }

	    //	    System.out.println("메카닉 유닛 수 " + allianceUnitInfo.getUnitSet(UnitKind.Mechanic_Unit).size());

	    if (allianceUnitInfo.getUnitSet(UnitKind.Mechanic_Unit).size() > 30 && phase != 5) {
		setPhase(2);
	    }

//	    if (allianceUnitInfo.getUnitSet(UnitKind.Mechanic_Unit).size() > 45 && phase != 5) {
//		setPhase(3);
//	    }

	}

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
		if (multiCount == 1) {
		    factory.setRallyPoint(locationManager.getBaseEntranceChokePoint().toPosition());
		} else if (multiCount == 2) {
		    factory.setRallyPoint(locationManager.getFirstExtensionChokePoint().toPosition());
		}
	    }

	    for (Unit2 factory : allianceUnitInfo.getUnitSet(UnitKind.Terran_Barracks)) {
		if (multiCount == 1) {
		    factory.setRallyPoint(locationManager.getBaseEntranceChokePoint().toPosition());
		} else if (multiCount == 2) {
		    factory.setRallyPoint(locationManager.getFirstExtensionChokePoint().toPosition());
		}
	    }
	}
    }

    //이니셜 빌드오더가 끝난 상황에서, 가스 일꾼이 3기 미만인 경우, 자동으로 일꾼 3마리를 할당시켜준다.
    private void doRefineryJob() {

	if (hasStrategyItem(StrategyItem.AUTO_ASSIGN_GAS_SCV) && buildManager.isInitialBuildFinished()) {

	    //5초에 한번만 시행한다. 
	    if (!gameStatus.isMatchedInterval(8)) {
		return;
	    }

	    //아군의 모든 커맨드 센터를 대상으로
	    for (Unit2 commandCenter : allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Command_Center)) {

		//대상 커맨드 센터에 할당된 가스 일꾼이 3기 미만일 경우,
		if (allianceUnitInfo.findUnitSetNear(commandCenter, UnitKind.Worker_Gather_Gas, 320).size() < 3) {

		    //대상 커맨드 센터에 할당된 리파이너리를 가져온다.
		    Unit2 refinery = allianceUnitInfo.findOneUnitNear(commandCenter, UnitKind.Terran_Refinery, 320);

		    //리파이너를 찾았을 경우
		    if (refinery != null) {

			//대상 커맨드 센터 주변의 미네랄 일꾼을 찾는다.
			//미네랄 일꾼이 3기 이상이고, 리파이너리가 지어져 있으면 미네랄 일꾼을 가스에 할당한다.
			//			System.out.println("현재 미네랄 일꾼 " + workerManager.findMineralWorkerSetNear(commandCenter, UnitKind.Terran_SCV, 320).size());
			if (workerManager.findMineralWorkerSetNear(commandCenter, UnitKind.Terran_SCV, 320).size() >= 3 && refinery.isCompleted()) {
			    buildManager.add(new BuildOrderItem(BuildOrderItem.Order.GATHER_GAS, refinery));
			} else {
			    //미네랄 일꾼이 부족하거나 리파이너리가 건설 중이다.
			    //			    System.out.println("미네랄 일꾼이 부족하다 or 건설중이다.");
			}

		    } else {
			//리파이너리가 없어서 건설이 필요하다.
			//큐에 아무것도 없고, 대상 커맨드 센터가 완성되었을 경우 리파이너리 건설
			if (0 == buildManager.getQueueSize() && commandCenter.isCompleted() && !commandCenter.isLifted()) {
			    //대상 커맨드 센터 주변의 베스핀 가스를 가져온다.
			    //가져온 베스핀 가스 위치에 리파이너리를 건설한다.
			    Unit2 vespene = allianceUnitInfo.findOneUnitNear(commandCenter, UnitKind.Resource_Vespene_Geyser, 320);
			    if (vespene != null) {
				buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Refinery, vespene.getTilePosition()));
			    } else {
				//				System.out.println("여긴 베스핀이 없어");
				continue;
			    }
			} else {
			    //			    System.out.println("큐가 찼다 or 커맨드가 건설중이다.");
			}
			return;
		    }

		}

		//대상 커맨드 센터에 할당된 가스 일꾼이 3기 미만일 경우,
		Set<Unit2> gasScv = allianceUnitInfo.findUnitSetNear(commandCenter, UnitKind.Worker_Gather_Gas, 320);
		if (gasScv.size() > 3) {
		    System.out.println("가스 scv가 넘쳐난다" + commandCenter.getID() + " " + gasScv.size());

		    int seq = 0;
		    int releaseNum = gasScv.size() - 3;
		    for (Unit2 scv : gasScv) {
			allianceUnitInfo.releaseGasUnit(scv);
			scv.stop();
			System.out.println(scv.getID());
			seq++;
			if (seq == releaseNum) {
			    return;
			}
		    }

		}
	    }
	}
    }

    private void doExpansion() {

	if (hasStrategyItem(StrategyItem.AUTO_BUILD_EXPANSION)) {

	    // 3초에 한 번만 수행된다.
	    if (!gameStatus.isMatchedInterval(3)) {
		return;
	    }

	    int currentMultiCount = 0;
	    //현재 확장 갯수를 업데이트 한다
	    for (Unit2 commandCenter : allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Command_Center)) {
		//커맨드 센터 주변에 미네랄이 6개 이상 있을 경우 운영중인 확장이라고 생각한다.
		if (allianceUnitInfo.findUnitSetNear(commandCenter, UnitKind.Resource_Mineral_Field, 320).size() > 6) {
		    multiCount++;
		    currentMultiCount++;
		}
	    }
	    multiCount = currentMultiCount;

	    // 현재 건설중인 커맨드 센터가 있다면 취소
	    if (allianceUnitInfo.getConstructionCount(UnitType.Terran_Command_Center) > 0 || buildManager.getQueueSize() > 0) {
		return;
	    }

	    // 최대로 운영하려는 확장의 갯수
	    int maxExpansion = 3;

	    //현재 운영중인 확장의 갯수가 최대 수치를 넘는다면 더이상 건설하지 않는다.
	    if (multiCount >= maxExpansion) {
		return;
	    }

	    //전체 커맨드 센터 숫자를 가져온다.
	    int totalCommandCount = allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center).size();

	    //앞마당까지 지어진 상황에서 추가 확장을 가져가는 메소드
	    if (totalCommandCount >= 2) {

		//다음확장이 발견되어 있지 않다면 스캔을 뿌려본다.
		TilePosition nextExpansionPoint = locationManager.getNextExpansionPoint();
		if (!gameStatus.isExplored(nextExpansionPoint)) {
		    allianceUnitInfo.doScan(nextExpansionPoint.toPosition());
		}

		//TODO 확장을 가져가는 다양한 조건들이 추가될 예정이다.
		//메카닉 유닛이 여유가 있을 경우 확장을 가져간다.
		if (gameStatus.getMineral() > 600 && allianceUnitInfo.getUnitSet(UnitKind.Mechanic_Unit).size() > 40) {
		    if (0 == buildManager.getQueueSize()) {
			buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Command_Center, locationManager.getNextExpansionPoint()));
		    }
		}
		//미네랄이 과도하게 남을 경우 확장을 시도한다?
		if (gameStatus.getMineral() > 2000) {
		    if (0 == buildManager.getQueueSize()) {
			buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Command_Center, locationManager.getNextExpansionPoint()));
		    }
		}
	    }
	}
    }

    public void rebalanceWorkers() {

	if (hasStrategyItem(StrategyItem.AUTO_BALANCE_SCV)) {

	    WorkerManager workerManager = gameStatus.getWorkerManager();

	    //모든 커맨드 센터의 미네랄을 캐는 일꾼 숫자를 가져온다.
	    int total_scv = 0;
	    for (Unit2 commandCenter : allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center)) {
		if (commandCenter.isCompleted()) {
		    total_scv += allianceUnitInfo.findUnitSetNear(commandCenter, UnitKind.Terran_SCV, 320).size();
		}
	    }

	    //모든 커맨드 센터 근처의 미네랄 덩이 수를 가져온다.
	    int total_mineral = 0;
	    for (Unit2 commandCenter : allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center)) {
		if (commandCenter.isCompleted()) {
		    total_mineral += allianceUnitInfo.findUnitSetNear(commandCenter, UnitKind.Resource_Mineral_Field, 320).size();
		}
	    }

	    System.out.println("토탈 scv " + total_scv);
	    System.out.println("총 미네랄 " + total_mineral);

	    //각 커맨드 센터의 일꾼 부족 현황을 가져온다.
	    for (Unit2 commandCenter : allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center)) {

		if (commandCenter.isCompleted()) {

		    if (allianceUnitInfo.findUnitSetNear(commandCenter, UnitKind.Resource_Mineral_Field, 320).size() == 0) {
			continue;
		    }

		    //이미 적정 숫자의 일꾼이 있는 커맨드 센터 제외
		    if (workerManager.findMineralWorkerSetNear(commandCenter, UnitKind.Terran_SCV, 320).size() > 10) {
			System.out.println("밸런스를 맞출 필요가 없습니다.");
			continue;
		    }

		    int result = workerManager.checkMineralBalance(commandCenter, total_scv, total_mineral);

		    //부족한 커맨드 센터에 대해, 여유량 만큼 scv를 이동시킨다.
		    if (result < 0 && commandCenter.isCompleted() && !commandCenter.isLifted()) {

			//여유가 있는 커맨드 센터를 가져온다.
			Unit2 enoughCommand = null;
			for (Unit2 commandCenter2 : allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center)) {

			    if (!commandCenter2.isCompleted()) {
				continue;
			    }

			    if (allianceUnitInfo.findUnitSetNear(commandCenter2, UnitKind.Resource_Mineral_Field, 320).size() == 0) {
				continue;
			    }

			    int result2 = workerManager.checkMineralBalance(commandCenter2, total_scv, total_mineral);
			    if (result2 > 0) {
				enoughCommand = commandCenter2;
			    }
			}

			//여유가 있는 커맨드 센터의 scv를 가져온다.
			Set<Unit2> scvCandidate = null;
			if (enoughCommand == null) {
			    return;
			} else {
			    scvCandidate = workerManager.findMineralWorkerSetNear(enoughCommand, UnitKind.Terran_SCV, 500);
			}

			System.out.println("여유량" + scvCandidate.size());
			//부족한 숫자만큼 scv를 stop시켜 다른 커맨드 센터에 할당되게 한다.
			if (Math.abs(result) > 4) {
			    result = 4;
			}
			int seq = 0;
			for (Unit2 scv : scvCandidate) {
			    //			    scv.stop();
			    System.out.println("아이디" + scv.getID());
			    ActionUtil.stop(allianceUnitInfo, scv);
			    seq++;
			    if (seq == Math.abs(result)) {
				return;
			    }
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

	    if (true == buildManager.isInitialBuildFinished() && 1 >= buildManager.getQueueSize()) {
		// 서플 여유가 8개 이하면 서플을 짓는다. (최대 2개를 동시에 지을 수 있음) 
		// TODO 최적화 필요
		if (1 > allianceUnitInfo.getConstructionCount(UnitType.Terran_Supply_Depot) && gameStatus.getSupplyRemain() <= 8 * 2 && gameStatus.getSupplyTotal() < 400) {
		    buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
		} else if (gameStatus.getMineral() > 200 && gameStatus.getGas() > 100 && 0 == buildManager.getQueueSize() && gameStatus.getFrameCount() > 10000) {
		    if (4 > allianceUnitInfo.getUnitSet(UnitKind.Terran_Factory).size()) {
			// 팩토리가 4개 미만이고, BuildOrder Queue가 비어있으면 팩토리를 짓는다.
			buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory));
		    } else if (6 > allianceUnitInfo.getUnitSet(UnitKind.Terran_Factory).size() && multiCount > 2) {
			//팩토리가 3개 지어지고 난 후 부터는 멀티가 있을 때 팩토리를 건설한다.
			buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory));
		    }
		}

		//머신샵이 없는 건물에서 탱크를 생산할 경우 데드락이 걸리는 문제 해결 필요
		Unit2 factory = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Factory);
		if (factory != null) {

		    Set<Unit2> tankSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Siege_Tank);
		    Set<Unit2> vultureSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Vulture);
		    Set<Unit2> goliathSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Goliath);

		    int tankCount = tankSet.size() + allianceUnitInfo.getTrainingQueueUnitCount(UnitType.Terran_Factory, UnitType.Terran_Siege_Tank_Tank_Mode);
		    int vultureCount = vultureSet.size() + allianceUnitInfo.getTrainingQueueUnitCount(UnitType.Terran_Factory, UnitType.Terran_Vulture);
		    int goliathCount = goliathSet.size() + allianceUnitInfo.getTrainingQueueUnitCount(UnitType.Terran_Factory, UnitType.Terran_Goliath);

		    //TODO 상대 종족과 전략에 따라 메카닉의 비율을 다르게 가져갈 필요가 있다.
		    Log.info("메카닉 생산. 탱크 수: %d, 벌쳐 수: %d, 골리앗 수: %d", tankCount, vultureCount, goliathCount);

		    if (tankCount < vultureCount) {
			Unit2 bestSiegeFactory = allianceUnitInfo.getTrainableBuilding(UnitType.Terran_Factory, UnitType.Terran_Siege_Tank_Tank_Mode);
			if (bestSiegeFactory != null) {
			    if (bestSiegeFactory.canTrain(UnitType.Terran_Siege_Tank_Tank_Mode)) {
				bestSiegeFactory.train(UnitType.Terran_Siege_Tank_Tank_Mode);
			    }
			}

		    } else if (goliathCount < vultureCount) {
			Unit2 bestGoliathFactory = allianceUnitInfo.getTrainableBuilding(UnitType.Terran_Factory, UnitType.Terran_Goliath);
			if (bestGoliathFactory != null) {
			    if (bestGoliathFactory.canTrain(UnitType.Terran_Goliath)) {
				bestGoliathFactory.train(UnitType.Terran_Goliath);
			    }
			}
		    } else {
			Unit2 bestVultureFactory = allianceUnitInfo.getTrainableBuilding(UnitType.Terran_Factory, UnitType.Terran_Vulture);
			if (bestVultureFactory != null) {
			    bestVultureFactory.train(UnitType.Terran_Vulture);
			}
		    }
		}
	    }
	}
    }

    // 자동으로 SCV를 훈련하는 작업을 수행한다
    private void doSCVAutoTrain() {

	// 1초에 한 번만 수행된다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	if (hasStrategyItem(StrategyItem.AUTO_TRAIN_SCV) && buildManager.isInitialBuildFinished()) {

	    if (MyBotModule.Broodwar.self().supplyTotal() - MyBotModule.Broodwar.self().supplyUsed() < 2) {
		return;
	    }

	    int mineral_count = 0;

	    Set<Unit2> commandCenters = allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center);

	    for (Unit2 commandCenter : commandCenters) {

		int minerals = allianceUnitInfo.getMineralNearCommandCenter(commandCenter);
		if (minerals > 0) {
		    mineral_count += minerals;
		}
	    }

	    if (0 == buildManager.getQueueSize()) {

		if (gameStatus.getMineral() >= 50) {

		    int maxworkerCount = mineral_count * 2 + 6 * commandCenters.size();

		    Set<Unit2> scvSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_SCV);
		    int scvCount = scvSet.size() + allianceUnitInfo.getTrainingQueueUnitCount(UnitType.Terran_Command_Center, UnitType.Terran_SCV);

		    int maxscv = 60;

		    System.out.println("현재 scv 카운트 " + scvCount);
		    System.out.println("맥스 워커 카운트 " + maxworkerCount);
		    if (scvCount < maxscv && scvCount < maxworkerCount) {
			Unit2 commandCenter = allianceUnitInfo.getTrainableBuilding(UnitType.Terran_Command_Center, UnitType.Terran_SCV);
			if (null != commandCenter) {
			    Log.info("SCV 생산. SCV 수: %d,", scvCount);
			    commandCenter.train(UnitType.Terran_SCV);
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
	} else {
	    //아카데미가 지어져 있지 않을 경우 12000프레임 후에 건설한다.
	    if (gameStatus.getMineral() > 150 && buildManager.getQueueSize() == 0 && gameStatus.getFrameCount() > 10000) {
		buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Academy));
	    }
	}
    }

    // 아머리와 관련된 작업을 수행한다.
    private void doArmoryJob() {
	// 2초에 한 번만 수행된다.
	if (!gameStatus.isMatchedInterval(2)) {
	    return;
	}

	//아머리 건설
	Unit2 factory = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Factory);
	if (null != factory) {
	    if (hasStrategyItem(StrategyItem.AUTO_BUILD_TWO_ARMORY)) {
		BuildManager buildManager = gameStatus.getBuildManager();
		if (gameStatus.getMineral() > 100 && gameStatus.getGas() > 50 && 0 == buildManager.getQueueSize() && buildManager.isInitialBuildFinished()) {
		    if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Armory).size() == 0) {
			// 첫번째 아머리는 바로 짓는다.
			buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Armory));
		    } else if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Armory).size() < 2 && allianceUnitInfo.getUnitSet(UnitKind.Mechanic_Unit).size() > 24) {
			//두번째 아머리는 어느정도 메카닉 유닛이 갖춰졌을 때 짓는다
			buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Armory));
		    }
		}
	    }
	}

	//아머리 업그레이드
	if (hasStrategyItem(StrategyItem.AUTO_UPGRADE_MECHANIC_UNIT)) {

	    Unit2 isArmory = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Armory);

	    if (null != isArmory) {

		for (Unit2 armory : allianceUnitInfo.getUnitSet(UnitKind.Terran_Armory)) {

		    // 공격력, 방어력 1레벨 업그레이드를 한다.
		    if (armory.getPlayer().getUpgradeLevel(UpgradeType.Terran_Vehicle_Weapons) == 0 && armory.canUpgrade(UpgradeType.Terran_Vehicle_Weapons)) {
			armory.upgrade(UpgradeType.Terran_Vehicle_Weapons);
		    } else if (armory.getPlayer().getUpgradeLevel(UpgradeType.Terran_Vehicle_Plating) == 0 && armory.canUpgrade(UpgradeType.Terran_Vehicle_Plating)) {
			armory.upgrade(UpgradeType.Terran_Vehicle_Plating);
		    }

		    // 메카닉 병력이 24마리 이상일 경우 계속해서 공격력 방어력 업그레이드를 한다.
		    int mechanicCount = allianceUnitInfo.getUnitSet(UnitKind.Mechanic_Unit).size();
		    if (mechanicCount >= 24 && null != allianceUnitInfo.getAnyUnit(UnitKind.Terran_Science_Facility)) {
			if (armory.getPlayer().getUpgradeLevel(UpgradeType.Terran_Vehicle_Weapons) < 3 && armory.canUpgrade(UpgradeType.Terran_Vehicle_Weapons)) {
			    armory.upgrade(UpgradeType.Terran_Vehicle_Weapons);
			}
			if (armory.getPlayer().getUpgradeLevel(UpgradeType.Terran_Vehicle_Plating) < 3 && armory.canUpgrade(UpgradeType.Terran_Vehicle_Plating)) {
			    armory.upgrade(UpgradeType.Terran_Vehicle_Plating);
			}
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
		Set<Unit2> marineSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Marine);
		int marineCount = marineSet.size() + allianceUnitInfo.getTrainingQueueUnitCount(UnitType.Terran_Barracks, UnitType.Terran_Marine);
		Unit2 scienceFacility = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Science_Facility);

		if (marineCount > 24 && null != scienceFacility) {
		    if (engineeringBay.getPlayer().getUpgradeLevel(UpgradeType.Terran_Infantry_Weapons) < 3 && engineeringBay.canUpgrade(UpgradeType.Terran_Infantry_Weapons)) {
			engineeringBay.upgrade(UpgradeType.Terran_Infantry_Weapons);
		    }
		    if (engineeringBay.getPlayer().getUpgradeLevel(UpgradeType.Terran_Infantry_Armor) < 3 && engineeringBay.canUpgrade(UpgradeType.Terran_Infantry_Armor)) {
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

	//건설된 벙커 수만큼 마린을 생산한다
	Unit2 barracks = allianceUnitInfo.getTrainableBuilding(UnitType.Terran_Barracks, UnitType.Terran_Marine);
	if (null != barracks) {
	    Set<Unit2> marineSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Marine);
	    int marineCount = marineSet.size() + allianceUnitInfo.getTrainingQueueUnitCount(UnitType.Terran_Barracks, UnitType.Terran_Marine);
	    int bunkerCount = allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Bunker).size();
	    Log.info("마린 생산. 마린 수: %d, 벙커 수: %d", marineCount, bunkerCount);
	    if (bunkerCount * 4 > marineCount) {
		barracks.train(UnitType.Terran_Marine);
	    }
	}
    }

    // 머신샵과 관련된 작업을 수행한다.
    private void doMachineShopJob() {
	// 1초에 한 번만 수행된다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}
	Unit2 machineShop = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Machine_Shop);

	if (null != machineShop) {

	    if (hasStrategyItem(StrategyItem.AUTO_RESEARCH_SIEGE_MODE)) {
		if (machineShop.canResearch(TechType.Tank_Siege_Mode)) {
		    machineShop.research(TechType.Tank_Siege_Mode);
		}
	    }

	    if (hasStrategyItem(StrategyItem.AUTO_RESEARCH_ION_THRUSTERS)) {
		if (machineShop.canUpgrade(UpgradeType.Ion_Thrusters)) {
		    machineShop.upgrade(UpgradeType.Ion_Thrusters);
		}
	    }

	    if (hasStrategyItem(StrategyItem.AUTO_RESEARCH_CHARON_BOOSTERS) && gameStatus.getFrameCount() > 10000) {
		if (machineShop.canUpgrade(UpgradeType.Charon_Boosters)) {
		    machineShop.upgrade(UpgradeType.Charon_Boosters);
		}
	    }

	    if (hasStrategyItem(StrategyItem.AUTO_RESEARCH_SPIDER_MINES)) {
		if (machineShop.canResearch(TechType.Spider_Mines)) {
		    machineShop.research(TechType.Spider_Mines);
		}
	    }
	}
    }

    // 아카데미와 관련된 작업을 수행한다.
    private void doFactoryAddOnJob() {
	// 1초에 한 번만 수행된다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	if (hasStrategyItem(StrategyItem.AUTO_ADDON_MACHINE_SHOP)) {

	    Unit2 factory = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Factory);
	    if (null != factory) {
		// Machine Shop Add on
		int machineShopCount = allianceUnitInfo.getUnitSet(UnitKind.Terran_Machine_Shop).size();
		int maxMachineShopCount = 3;

		if (machineShopCount < maxMachineShopCount) {
		    for (Unit2 forAddonFactory : allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Factory)) {
			if (forAddonFactory.canBuildAddon()) {
			    forAddonFactory.buildAddon(UnitType.Terran_Machine_Shop);
			}
		    }
		}
		//		allianceUnitInfo.buildAddon(UnitType.Terran_Machine_Shop);
	    }
	}
    }

    //본진에 건설된 커맨드 센터를 멀티로 이동시키는 메소드
    private void liftCommandJob() {

	//멀티가 지어져 있지 않은 상황
	if (hasStrategyItem(StrategyItem.AUTO_LIFT_COMMAND_CENTER) && multiCount == 1) {

	    Set<Unit2> commandCenterUnitSet = allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Command_Center);

	    if (commandCenterUnitSet.size() == 2) {

		Unit2 firstExpansionCommandCenter = null;
		TilePosition baseLocation = locationManager.getAllianceBaseLocation();
		TilePosition firstExpansionLocation = locationManager.getFirstExpansionLocation().get(0);

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

    //상황에 맞게 배럭을 띄우는 메소드
    private void liftBarrackJob() {

	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	//멀티가 아직 지어지지 않았을 경우
	if (hasStrategyItem(StrategyItem.BLOCK_ENTRANCE) && multiCount == 1 && allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Barracks).size() > 0) {

	    Unit2 entranceBarrack = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Barracks);

	    //확장기지 근처에 아군 scv가 있을 경우 배럭을 띄운다.
	    if (allianceUnitInfo.findUnitSetNearTile(locationManager.getFirstExpansionLocation().get(0), UnitKind.Terran_SCV, 300).size() > 0) {
		//착지 상태의 배럭에 대해
		if (!entranceBarrack.isLifted()) {
		    //적 유닛이 근처에 없을 경우에만 띄운다.
		    if (enemyUnitInfo.findUnitSetNearTile(locationManager.getFirstExpansionLocation().get(0), UnitKind.ALL, 500).size() == 0) {
			entranceBarrack.lift();
		    }
		}
	    } else {
		//배럭이 떠있을 경우 다시 착지시킨다.
		if (entranceBarrack.isLifted()) {
		    entranceBarrack.land(new TilePosition(locationManager.getEntranceBuilding().get(0).getX(), locationManager.getEntranceBuilding().get(0).getY()));
		}
	    }
	}

	//멀티가 지어진 경우
	if (hasStrategyItem(StrategyItem.AUTO_LIFT_COMMAND_CENTER) && multiCount == 2 && gameStatus.getFrameCount() < 15000) {

	    Unit2 entranceBarrack = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Barracks);
	    TilePosition firstExpansionBarrackLocation = locationManager.getSecondEntranceBuilding().get(0);

	    if (entranceBarrack != null) {
		//착지 상태의 배럭이 확장 위치가 아닐경우 띄운다
		if (!entranceBarrack.isLifted()) {
		    if (entranceBarrack.getTilePosition().getX() != locationManager.getSecondEntranceBuilding().get(0).getX()
			    || entranceBarrack.getTilePosition().getY() != locationManager.getSecondEntranceBuilding().get(0).getY()) {
			entranceBarrack.lift();
		    }
		} else {
		    //떠있을 경우 확장 위치로 배럭을 착지시킨다. 
		    entranceBarrack.land(new TilePosition(firstExpansionBarrackLocation.getX(), firstExpansionBarrackLocation.getY()));
		}
		if (entranceBarrack.isLifted() == false && entranceBarrack.getTilePosition().getX() == firstExpansionBarrackLocation.getX()
			&& entranceBarrack.getTilePosition().getY() == firstExpansionBarrackLocation.getY()) {
		    return;
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
		    Set<Unit2> mechanicUnitSet = allianceUnitInfo.getUnitsInRange(clockedUnit.getPosition(), UnitKind.Mechanic_Unit, distance);
		    Log.info("적 클로킹 유닛 발견: %s. 주변의 마린 수: %d, 거리: %d", clockedUnit, allianceUnitSet.size(), distance);
		    if (5 <= allianceUnitSet.size() || 5 <= mechanicUnitSet.size()) {
			allianceUnitInfo.doScan(clockedUnit.getPosition());
			lastScanFrameCount = gameStatus.getFrameCount();
			break;
		    }
		}
	    }
	}

	for (BaseLocation targetBaseLocation : BWTA.getStartLocations()) {
	    //시작지역이 발견되지 않으면 스캔을 뿌려본다.
	    TilePosition startPoint = targetBaseLocation.getTilePosition();
	    if (!gameStatus.isExplored(startPoint)) {
		allianceUnitInfo.doScan(startPoint.toPosition());
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
		    Set<Unit2> defenceAllianceUnitSet = null;
		    //공중 유닛의 경우 공중공격이 가능한 유닛을 파견한다.
		    if (enemyUnit.isFlying()) {
			defenceAllianceUnitSet = allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.AntiAir_Unit, 800);
		    } else {
			defenceAllianceUnitSet = allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.Combat_Unit, 800);
		    }

		    // 커맨드 센터 반경 800 이내의 아군 유닛으로 방어한다.
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

    public int getPhase() {
	return phase;
    }

    public void setPhase(int phase) {
	this.phase = phase;
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