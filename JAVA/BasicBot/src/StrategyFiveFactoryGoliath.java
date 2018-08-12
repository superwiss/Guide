import java.util.List;
import java.util.Set;

import bwapi.Order;
import bwapi.Position;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.UnitType;
import bwapi.UpgradeType;
import bwta.BWTA;
import bwta.BaseLocation;

public class StrategyFiveFactoryGoliath extends StrategyBase {

    private StrategyManager strategyManager = null;
    private LocationManager locationManager = null;
    private BuildManager buildManager = null;
    private WorkerManager workerManager = null;
    private boolean isAttackReady;
    private int scanCount = 0;
    private TilePosition currentExpandPosition = null;

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
	isAttackReady = false;
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
		if (gameStatus.getMineral() >= 200 && gameStatus.getGas() >= 100) {
		    strategyManager.addStrategyItem(StrategyItem.AUTO_BUILD_FACTORY);
		} else {
		    strategyManager.removeStrategyItem(StrategyItem.AUTO_BUILD_FACTORY);
		}
	    } else if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Factory).size() >= 5 && allianceUnitInfo.getUnitSet(UnitKind.Terran_Factory).size() < 9) {
		if (gameStatus.getMineral() >= 400 && gameStatus.getGas() >= 200) {
		    strategyManager.addStrategyItem(StrategyItem.AUTO_BUILD_FACTORY);
		} else {
		    strategyManager.removeStrategyItem(StrategyItem.AUTO_BUILD_FACTORY);
		}
	    } else {

	    }
	}

	// 공격 시점과 장소를 체크한다.
	checkAttackTimingAndPosition();

	//확장을 시도한다.
	doExpansion();

	// 골리앗의 방어력과 공격력을 업그레이드 한다.
	doArmoryJob();

	// 아카데미를 건설한다.
	doBuildAcademy();

	// 엔지니어링 베이를 건설한다.
	doBuildEngineeringBay();

	// 본진에 있는 골리앗을 수리한다.
	doRepairGoliath();

	// 입구에 있는 배럭을 자동으로 열고 닫는다.
	doAutoLiftBarracks();

	// 입구의 건물을 자동으로 수리한다.
	doBlockEntrance();

	// 스캔 사용 여부에 따라 본진의 움직임을 결정한다.
	checkComsat();

	//멀티 예정지에 적군이 있다면 공격한다.
	autoDefenceExpansion();

	//적의 확장기지를 찾아낸다.
	searchEnemyExpansion();

	autoBuildDefenceExpansion();

	doBunkerJob();

	//서플라이가 따라가지 못해 임시로 추가한 메소드
	autoBuildSupply();

	// 본진의 커맨드 센터를 확장으로 옮긴다.
	//doAutoLiftCommandCenter();
    }

    private void doBunkerJob() {

	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	Set<Unit2> bunkerSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Bunker);
	int bunkerCount = 0;
	for (Unit2 bunker : bunkerSet) {

	    //주변에 미네랄이 없는 벙커는 수리하거나 마린을 넣지 않는다.
	    if (allianceUnitInfo.getUnitsInRange(bunker.getPosition(), UnitKind.Resource_Mineral_Field, 400).size() == 0) {
		//미네랄을 다 캔 지역의 마린을 벙커에서 꺼낸다.
		bunker.unloadAll();
		continue;
	    }

	    bunkerCount++;

	    if (hasStrategyItem(StrategyItem.AUTO_DEFENCE_EXPANSION)) {
		if (0 < bunker.getSpaceRemaining()) {
		    marineToBunker(allianceUnitInfo, bunker);
		} else {
		    repairCount = 3;
		}
	    }
	    if (hasStrategyItem(StrategyItem.AUTO_DEFENCE_EXPANSION)) {

		if (gameStatus.getMineral() > 0) {
		    if (UnitType.Terran_Bunker.maxHitPoints() > bunker.getHitPoints()) {
			repairBuilding(allianceUnitInfo, bunker);
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
	    Log.info("마린 생산. 마린 수: %d, 벙커 수: %d", marineCount, bunkerCount);
	    if (bunkerCount * 4 > marineCount) {
		barracks.train(UnitType.Terran_Marine);
	    }
	    if (marineCount < 4 && buildManager.isInitialBuildFinished()) {
		//마린4마리 항시 유지
		barracks.train(UnitType.Terran_Marine);
	    }
	}
    }

    // 마린을 벙커에 넣는다.
    private void marineToBunker(UnitInfo allianceUnitInfo, Unit2 bunker) {
	System.out.println("벙커에 넣자");
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
	    System.out.println("벙커에 넣자");
	    targetMarine.load(bunker);
	}
    }

    private void doBuildEngineeringBay() {

	// 2초에 한 번만 수행된다.
	if (!gameStatus.isMatchedInterval(2)) {
	    return;
	}

	Unit2 engineeringBay = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Engineering_Bay);
	if (null == engineeringBay) {
	    if (gameStatus.getMineral() > 125 && 0 == buildManager.getQueueSize()) {
		if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Engineering_Bay).size() == 0) {
		    buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Engineering_Bay));
		}
	    }
	} else {
	    if (!engineeringBay.isLifted()) {
		engineeringBay.lift();
	    }
	}
    }

    private void autoBuildDefenceExpansion() {

	if (buildManager.isInitialBuildFinished() && hasStrategyItem(StrategyItem.AUTO_DEFENCE_EXPANSION)) {

	    for (BaseLocation targetBaseLocation : BWTA.getBaseLocations()) {

		//아군 본진의 경우 제외한다.
		if (targetBaseLocation.getTilePosition().equals(locationManager.allianceBaseLocation)) {
		    continue;
		}

		//앞마당도 제외한다.
		if (targetBaseLocation.getTilePosition().equals(locationManager.getExtentionPosition().get(0))) {
		    continue;
		}

		//미네랄이 없는 멀티는 제외한다.
		if (allianceUnitInfo.getUnitsInRange(targetBaseLocation.getPosition(), UnitKind.Resource_Mineral_Field, 320).size() == 0) {
		    continue;
		}

		//아군의 커맨드 센터가 지어져 있을 경우, 벙커를 건설한다.
		if (allianceUnitInfo.getUnitsInRange(targetBaseLocation.getPosition(), UnitKind.Terran_Command_Center, 320).size() > 0) {

		    if (allianceUnitInfo.getUnitsInRange(targetBaseLocation.getPosition(), UnitKind.Terran_Bunker, 320).size() == 0) {
			if (0 == buildManager.getQueueSize()) {
			    if (1 > allianceUnitInfo.getConstructionCount(UnitType.Terran_Bunker)) {
				TilePosition goodPosition = needBunkerPlace(targetBaseLocation.getTilePosition());
				buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Bunker, goodPosition));
			    }
			}
		    }

		    if (allianceUnitInfo.getUnitsInRange(targetBaseLocation.getPosition(), UnitKind.Terran_Missile_Turret, 320).size() < 2) {

			Unit2 bunker = null;
			for (Unit2 bunkers : allianceUnitInfo.getUnitsInRange(targetBaseLocation.getPosition(), UnitKind.Terran_Bunker, 300)) {
			    bunker = bunkers;
			}

			if (0 == buildManager.getQueueSize() && bunker != null) {
			    if (1 > allianceUnitInfo.getConstructionCount(UnitType.Terran_Missile_Turret)) {
				TilePosition goodPosition = needTurretPlace(bunker.getTilePosition());
				buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Missile_Turret, goodPosition));
			    }
			}
		    }

		    //확장 주변의 건물을 수리한다.
		    Set<Unit2> buildingSet = allianceUnitInfo.getUnitsInRange(targetBaseLocation.getPosition(), UnitKind.Building, 320);
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
	}
    }

    private TilePosition needBunkerPlace(TilePosition tilePosition) {

	int currentX = tilePosition.getX();
	int currentY = tilePosition.getY();
	int bunkerSizeX = 3;
	int bunkerSizeY = 3;
	boolean canBuildHere = false;
	TilePosition goodPosition = null;

	for (int x_position = 0; x_position < 2; x_position++) {
	    for (int y_position = 0; y_position < 2; y_position++) {

		canBuildHere = canBuildHere(new TilePosition(currentX, currentY));

		if (canBuildHere == true) {
		    goodPosition = new TilePosition(currentX, currentY);
		    break;
		}
		currentY = currentY + bunkerSizeY;
	    }

	    if (canBuildHere) {
		break;
	    }
	    currentY = tilePosition.getY();
	    currentX = currentX + bunkerSizeX;
	}

	return goodPosition;
    }

    private TilePosition needTurretPlace(TilePosition tilePosition) {

	int currentX = tilePosition.getX() - 2;
	int currentY = tilePosition.getY();
	int bunkerSizeX = 6;
	boolean canBuildHere = false;
	TilePosition goodPosition = null;

	for (int x_position = 0; x_position < 6; x_position++) {

	    canBuildHere = canBuildHere(new TilePosition(currentX, currentY));
	    if (canBuildHere == true) {
		goodPosition = new TilePosition(currentX, currentY);
		break;
	    }

	    if (canBuildHere) {
		break;
	    }
	    currentX = currentX + bunkerSizeX;
	}
	return goodPosition;
    }

    private boolean canBuildHere(TilePosition tilePosition) {

	boolean alreadyBuilt = false;
	Set<Unit2> buildingSet = allianceUnitInfo.getUnitSet(UnitKind.Building);
	for (Unit2 building : buildingSet) {
	    if (building.getTilePosition().equals(tilePosition)) {
		alreadyBuilt = true;
		continue;
	    }
	}
	if (true == alreadyBuilt) {
	    return false;
	} else {
	    return true;
	}
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

	    Position defencePosition = locationManager.getBaseEntranceChokePoint().toPosition();
	    Set<Unit2> defenceAllianceUnitSet = allianceUnitInfo.getUnitsInRange(defencePosition, UnitKind.Combat_Unit, 500);
	    Set<Unit2> enemyUnitSet = enemyUnitInfo.getUnitsInRange(defencePosition, UnitKind.ALL, 500);

	    if (enemyUnitSet.size() > 0) {
		strategyManager.addStrategyStatus(StrategyStatus.BACK_TO_BASE);
		for (Unit2 defenceAllianceUnit : defenceAllianceUnitSet) {
		    ActionUtil.attackPosition(allianceUnitInfo, defenceAllianceUnit, locationManager.getBlockingChokePoint());
		}
	    }

	} else if (goliathCount >= 24 && goliathCount < 70) {

	    //	     공격 유닛 인구수가 50 ~ 80이면 적 입구를 조인다.

	    TilePosition attackTilePosition = strategyManager.calcAndGetAttackTilePosition();
	    if (!isAttackReady) {
		List<BaseLocation> enemyBase = strategyManager.getOccupiedBaseLocation();
		if (enemyBase.size() != 0) {
		    for (BaseLocation target : enemyBase) {
			attackTilePosition = target.getTilePosition();
		    }
		    strategyManager.setAttackTilePosition(attackTilePosition);
		    strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
		} else {
		    strategyManager.setAttackTilePosition(locationManager.getBlockingChokePoint());
		    strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
		}
	    } else {
		strategyManager.setAttackTilePosition(strategyManager.calcAndGetAttackTilePosition());
		strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
		strategyManager.addStrategyStatus(StrategyStatus.FULLY_ATTACK);
	    }
	    // 조이기 시점에 적이 5마리 이상 보이면 총 공격을 한다.
	    //	    if (enemyUnitInfo.getUnitSet(UnitKind.Combat_Unit).size() > 5) {
	    //		TilePosition attackTilePosition = strategyManager.calcAndGetAttackTilePosition();
	    //		strategyManager.setAttackTilePosition(attackTilePosition);
	    //		strategyManager.addStrategyStatus(StrategyStatus.FULLY_ATTACK);
	    //		strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
	    //		Log.info("총 공격을 간다. 인구수: %d, 위치: %s", goliathCount, attackTilePosition);
	    //	    }
	} else if (goliathCount >= 70) {
	    //	    	     공격 유닛 인구수가 80이 넘으면 총 공격을 한다.
	    TilePosition attackTilePosition = strategyManager.calcAndGetAttackTilePosition();
	    if (!isAttackReady) {
		List<BaseLocation> enemyBase = strategyManager.getOccupiedBaseLocation();
		if (enemyBase.size() != 0) {
		    for (BaseLocation target : enemyBase) {
			attackTilePosition = target.getTilePosition();
		    }
		    strategyManager.setAttackTilePosition(attackTilePosition);
		    strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
		} else {
		    strategyManager.setAttackTilePosition(locationManager.getBlockingChokePoint());
		    strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
		}
	    } else {
		strategyManager.setAttackTilePosition(strategyManager.calcAndGetAttackTilePosition());
		strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
		strategyManager.addStrategyStatus(StrategyStatus.FULLY_ATTACK);
	    }

	    //	    // 공격과 동시에 추가 확장을 시도한다.
	    //	    Log.info("총 공격을 간다. 인구수: %d, 위치: %s", goliathCount, attackTilePosition);
	}
    }

    private void doExpansion() {

	if (hasStrategyItem(StrategyItem.AUTO_SAFE_EXTENSION)) {

	    // 3초에 한 번만 수행된다.
	    if (!gameStatus.isMatchedInterval(3)) {
		return;
	    }

	    int currentMultiCount = 0;
	    //현재 확장 갯수를 업데이트 한다
	    for (Unit2 commandCenter : allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Command_Center)) {
		//커맨드 센터 주변에 미네랄이 6개 이상 있을 경우 운영중인 확장이라고 생각한다.
		if (allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.Resource_Mineral_Field, 400).size() > 6) {
		    if (allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.Resource_Vespene_Geyser, 400).size() > 0) {
			currentMultiCount++;
		    }
		}
	    }
	    strategyManager.setLiveMultiCount(currentMultiCount);

	    // 현재 건설중인 커맨드 센터가 있다면 취소
	    if (allianceUnitInfo.getConstructionCount(UnitType.Terran_Command_Center) > 0 || buildManager.getQueueSize() > 0) {
		return;
	    }

	    // 최대로 운영하려는 확장의 갯수
	    int maxExpansion = 3;

	    //현재 운영중인 확장의 갯수가 최대 수치를 넘는다면 더이상 건설하지 않는다.
	    if (currentMultiCount >= maxExpansion) {
		strategyManager.setTryExpansion(false);
		return;
	    }

	    //전체 커맨드 센터 숫자를 가져온다.
	    int totalCommandCount = allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center).size();
	    //앞마당까지 지어진 상황에서 추가 확장을 가져가는 메소드
	    if (totalCommandCount >= 2) {

		TilePosition nextExpansionPoint = strategyManager.getNextExpansionPoint();
		if (nextExpansionPoint == null) {
		    Log.warn("더이상 지을 확장 기지가 없습니다");
		    isAttackReady = true;
		    return;
		}

		//다음확장이 발견되어 있지 않다면 스캔을 뿌려본다.
		if (allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Comsat_Station).size() > 0) {
		    if (nextExpansionPoint != null) {
			if (!gameStatus.isExplored(nextExpansionPoint)) {
			    allianceUnitInfo.doScan(nextExpansionPoint.toPosition());
			}
		    }
		}

		//TODO 확장을 가져가는 다양한 조건들이 추가될 예정이다.
		//메카닉 유닛이 여유가 있을 경우 확장을 가져간다.
		if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Goliath).size() > 25) {
		    if (0 == buildManager.getQueueSize()) {
			strategyManager.setTryExpansion(true);
			strategyManager.setNextExpansionPosition(nextExpansionPoint);
			buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Command_Center, nextExpansionPoint));
		    }
		}
		//미네랄이 과도하게 남을 경우 확장을 시도한다?
		if (gameStatus.getMineral() > 1000) {
		    if (0 == buildManager.getQueueSize()) {
			strategyManager.setTryExpansion(true);
			currentExpandPosition = nextExpansionPoint;
			strategyManager.setNextExpansionPosition(nextExpansionPoint);
			buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Command_Center, nextExpansionPoint));
		    }
		}
	    }
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

	// 1초에 한 번만 수행된다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	Unit2 academy = allianceUnitInfo.getAnyUnit(UnitKind.Terran_Academy);
	if (null == academy) {
	    if (gameStatus.getMineral() > 100 && 0 == buildManager.getQueueSize()) {
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

	if (gameStatus.getFrameCount() > 15000) {
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
	    Unit2 scv = allianceUnitInfo.getAnyUnitInRange(locationManager.getBlockingEntranceBuilding().get(0).toPosition(), UnitKind.Terran_SCV, 100);
	    if (scv != null) {

		//scv가 건설중이면 띄우지 않는다.
		Unit2 factory = allianceUnitInfo.getAnyUnitInRange(locationManager.getBlockingEntranceBuilding().get(0).toPosition(), UnitKind.Terran_Factory, 100);
		if (factory != null && !factory.isCompleted()) {
		    return;
		}

		//착지 상태의 배럭에 대해
		if (!entranceBarrack.isLifted()) {
		    //적 유닛이 근처에 없을 경우에만 띄운다.
		    if (enemyUnitInfo.getUnitsInRange(locationManager.getBaseEntranceChokePoint().toPosition(), UnitKind.ALL, 500).size() == 0) {
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
		TilePosition secondPosition = new TilePosition(locationManager.getSecondEntranceBuilding().get(0).getX(),
			locationManager.getSecondEntranceBuilding().get(0).getY());
		if (!entranceBarrack.isLifted() && !entranceBarrack.getTilePosition().equals(secondPosition)) {
		    entranceBarrack.lift();
		} else {
		    entranceBarrack.land(secondPosition);
		}
	    }

	    //배럭이 없다면 배럭을 건설한다.
	    if (entranceBarrack == null) {
		if (gameStatus.getMineral() > 150 && 0 == buildManager.getQueueSize()) {
		    if (allianceUnitInfo.getUnitSet(UnitKind.Terran_Barracks).size() == 0) {
			buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
		    }
		}
	    }
	}
    }

    private void doBlockEntrance() {

	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	if (hasStrategyItem(StrategyItem.BLOCK_ENTRANCE_ZERG) && gameStatus.getFrameCount() < 10000) {

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

    private void checkComsat() {

	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

	if (allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Science_Vessel).size() == 0
		&& (allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Comsat_Station).size() == 0)) {
	    for (Unit2 unit : enemyUnitInfo.getUnitSet(UnitKind.Combat_Unit)) {
		if (unit.isVisible() && (!unit.isDetected() || unit.getOrder() == Order.Burrowing) && unit.getPosition().isValid() && unit.isFlying() == false) {
		    strategyManager.addStrategyStatus(StrategyStatus.BACK_TO_BASE);
		    strategyManager.removeStrategyStatus(StrategyStatus.FULLY_ATTACK);
		    strategyManager.removeStrategyStatus(StrategyStatus.ATTACK);
		    for (Unit2 goliath : allianceUnitInfo.getUnitSet(UnitKind.Terran_Goliath)) {
			ActionUtil.attackPosition(allianceUnitInfo, goliath, locationManager.getBaseEntranceChokePoint());
		    }
		}
	    }
	} else if (allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Comsat_Station).size() > 0) {

	    boolean canUseScan = false;
	    boolean isCloakUnit = false;

	    for (Unit2 unit : enemyUnitInfo.getUnitSet(UnitKind.Combat_Unit)) {
		if (unit.isVisible() && (!unit.isDetected() || unit.getOrder() == Order.Burrowing) && unit.getPosition().isValid() && unit.isFlying() == false) {
		    isCloakUnit = true;
		}
	    }

	    if (isCloakUnit) {
		for (Unit2 comsat : allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Comsat_Station)) {
		    if (comsat.getEnergy() > 50) {
			canUseScan = true;
		    }
		}
		if (canUseScan == false) {
		    strategyManager.addStrategyStatus(StrategyStatus.BACK_TO_BASE);
		    strategyManager.removeStrategyStatus(StrategyStatus.FULLY_ATTACK);
		    strategyManager.removeStrategyStatus(StrategyStatus.ATTACK);
		    for (Unit2 goliath : allianceUnitInfo.getUnitSet(UnitKind.Terran_Goliath)) {
			ActionUtil.attackPosition(allianceUnitInfo, goliath, locationManager.getBaseEntranceChokePoint());
		    }
		}
	    }
	}
    }

    private void autoDefenceExpansion() {

	// 1초에 한 번만 실행한다.
	if (!gameStatus.isMatchedInterval(1)) {
	    return;
	}

    }

    private void searchEnemyExpansion() {

	if (hasStrategyItem(StrategyItem.SEARCH_ENEMY_EXPANSION_BY_SCAN)) {

	    Unit2 targetComsat = null;
	    Set<Unit2> comsatSet = allianceUnitInfo.getUnitSet(UnitType.Terran_Comsat_Station);

	    //컴셋의 마나가 200일 경우에 서칭을 시작한다.
	    for (Unit2 comsat : comsatSet) {
		if (comsat.getEnergy() == 200) {
		    targetComsat = comsat;
		}
	    }

	    if (targetComsat == null) {
		return;
	    }

	    List<BaseLocation> scanLocation = strategyManager.getScanLocation();
	    int maxScan = scanLocation.size();
	    if (scanCount >= maxScan) {
		scanCount = 0;
		return;
	    }

	    BaseLocation scanTarget = scanLocation.get(scanCount);
	    if (scanTarget != null && targetComsat.canUseTechPosition(TechType.Scanner_Sweep)) {
		targetComsat.useTech(TechType.Scanner_Sweep, scanTarget.getPosition());
		scanCount++;
	    }
	}
    }

    private void autoBuildSupply() {

	if (true == buildManager.isInitialBuildFinished() && 1 >= buildManager.getQueueSize()) {
	    // 서플 여유가 8개 이하면 서플을 짓는다. (최대 2개를 동시에 지을 수 있음) 
	    if (1 > allianceUnitInfo.getConstructionCount(UnitType.Terran_Supply_Depot) && gameStatus.getSupplyRemain() <= 8 * 2 && gameStatus.getSupplyTotal() < 400) {
		buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
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
	//	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.SCOUTING));
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
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory));
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
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.INITIAL_BUILDORDER_FINISH));
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
	strategyItems.add(StrategyItem.AUTO_SAFE_EXTENSION);
	strategyItems.add(StrategyItem.SEARCH_ENEMY_EXPANSION_BY_SCAN);
	strategyItems.add(StrategyItem.AUTO_DEFENCE_EXPANSION);
    }

}