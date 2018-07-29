import java.util.List;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.UnitType;

// MagiBot을 빠르게 연습시키기 위해서, 유즈맵으로 미션을 만들어 MagiBot이 미션을 해결하는 방식으로 훈련한다.
public class TrainingManager extends Manager {
    private TrainingData trainingData;

    private boolean isTrainingMode = true;
    private int exitFrame = -1;

    // 아군 유닛의 전체 HP
    private long allianceUnitHp = 0;
    // 적군 유닛의 전체 HP
    private long enemyUnitHp = 0;
    // 죽은 아군 유닛의 개수
    private int allianceUnitKilledCount = 0;
    // 죽은 적군 유닛의 개수
    private int enemyUnitKilledCount = 0;
    // 현재 Frame 번호
    private int currentFrameCount;
    // 미션 성공 여부
    private boolean isSuccess = false;

    private LocationManager locationManager;
    private StrategyManager strategyManager;

    @Override
    public void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);

	this.locationManager = gameStatus.getLocationManager();
	this.strategyManager = gameStatus.getStrategyManager();

	String mapName = gameStatus.mapFileName();
	TrainingData.TrainingDataBuilder builder = TrainingData.builder();
	switch (mapName) {
	case "Marine1_vs_Zergling1.scx":
	    // 마린 1마리 vs 저글링 1마리 전투. 저글링이 Attack Move Location 으로 이동하던 도중에 마린을 만난 상황.
	    // 500 프레임 이내에 아군 마린은 죽지 않고 적 저글링을 죽여야 한다.
	    builder.addAllianceUnitType(UnitType.Terran_Marine).addEnemyUnitType(UnitType.Zerg_Zergling).frameLimitFrom(50).frameLimitTo(500).allianceKillCount(0).enemyKillCount(1)
		    .scoreType(TrainingData.SCORE_TYPE.ALLIANCE_HP_AND_TIME);
	    break;
	case "Marine1_vs_StrongZergling1.scx":
	    // 마린 1마리 vs 엄청난 체력의 저글링 1마리 전투. 저글링이 Attack Move Location 으로 이동하던 도중에 마린을 만난 상황.
	    // 2000 프레임 이내에 아군 마린은 죽지 않고 적 저글링을 죽여야 한다.
	    builder.addAllianceUnitType(UnitType.Terran_Marine).addEnemyUnitType(UnitType.Zerg_Zergling).frameLimitFrom(50).frameLimitTo(2000).allianceKillCount(0)
		    .enemyKillCount(1).scoreType(TrainingData.SCORE_TYPE.ALLIANCE_HP_AND_TIME);
	    break;
	case "Marine1_vs_Zealot1.scx":
	    // 마린 1마리 vs 질럿 1마리 전투. 질럿이 Attack Move Location 으로 이동하던 도중에 마린을 만난 상황.
	    // 2000 프레임 이내에 아군 마린은 죽지 않고 적 질럿을 죽여야 한다.
	    builder.addAllianceUnitType(UnitType.Terran_Marine).addEnemyUnitType(UnitType.Protoss_Zealot).frameLimitFrom(50).frameLimitTo(2000).allianceKillCount(0)
		    .enemyKillCount(1).scoreType(TrainingData.SCORE_TYPE.ALLIANCE_HP_AND_TIME);
	    break;
	case "Marine2_vs_Zealot1.scx":
	    // 마린 2마리 vs 질럿 1마리 전투. 질럿이 Attack Move Location 으로 이동하던 도중에 마린 둘을 만난 상황.
	    // 2000 프레임 이내에 아군 마린은 1마리까지는 죽어도 되고, 적 질럿을 모두 죽여야 한다.
	    builder.addAllianceUnitType(UnitType.Terran_Marine).addEnemyUnitType(UnitType.Protoss_Zealot).frameLimitFrom(50).frameLimitTo(2000).allianceKillCount(1)
		    .enemyKillCount(1).scoreType(TrainingData.SCORE_TYPE.ALLIANCE_HP_AND_TIME);
	    break;
	case "Zerg_Test.scx":
	    // 저그 테스트용
	    builder.addAllianceUnitType(UnitType.Terran_Marine).addEnemyUnitType(UnitType.Protoss_Zealot).frameLimitFrom(50).frameLimitTo(1000000).allianceKillCount(0)
		    .enemyKillCount(1).scoreType(TrainingData.SCORE_TYPE.ALLIANCE_HP_AND_TIME);
	    break;
	case "Vulture2_vs_Hydralisk1.scx":
	    // 벌쳐 5마리 vs 히드라 4마리: attack으로 싸우면 히드라가 이긴다. 컨트롤 해서 벌쳐가 이겨야 한다.
	    builder.addAllianceUnitType(UnitType.Terran_Vulture).addEnemyUnitType(UnitType.Zerg_Hydralisk).frameLimitFrom(50).frameLimitTo(1000000).allianceKillCount(5)
		    .enemyKillCount(4).scoreType(TrainingData.SCORE_TYPE.ALLIANCE_HP_AND_TIME);
	    //locationManager.setEnemyStartLocation(new TilePosition(80, 65));
	    strategyManager.setAttackTilePosition(new TilePosition(100, 65));
	    strategyManager.addStrategyStatus(StrategyStatus.ATTACK);
	    gameStatus.setScreen(new Position(2048, 2048));
	    break;
	default:
	    isTrainingMode = false;
	    break;
	}
	trainingData = builder.build();
    }

    @Override
    protected void onFrame() {
	super.onFrame();

	if (isTrainingMode()) {

	    if (true == isFinished()) {
		if (-1 == getExitFrame()) {
		    setExitFrame(gameStatus.getFrameCount() + 24);
		    printResult();
		    Log.setLogLevel(Log.Level.NONE);
		}
		if (gameStatus.getFrameCount() >= getExitFrame()) {
		    gameStatus.leaveGame();
		    System.exit(0);
		}
	    }
	}
    }

    // 미션 종료 여부를 리턴한다.
    public boolean isFinished() {
	boolean result = false;

	// 정보를 업데이트 한다.
	updateInformation();

	if (currentFrameCount < trainingData.getFrameLimitFrom()) {
	    // 준비가 완료되기 전에는 미션 종료 여부를 판단하지 않는다.
	    result = false;
	} else {
	    if (currentFrameCount > trainingData.getFrameLimitTo()) {
		// 너무 오래 걸리면 미션 종료.
		result = true;
	    } else if (allianceUnitKilledCount > trainingData.getAllianceKillCount()) {
		// 아군 유닛이 모두 죽었으면 미션 종료.
		result = true;
	    } else if (enemyUnitKilledCount >= trainingData.getEnemyKillCount()) {
		// 적 유닛이 모두 죽었으면 미션 종료.
		isSuccess = true;
		result = true;
	    }
	}

	return result;
    }

    @Override
    public void onUnitEvade(Unit2 unit) {
	super.onUnitEvade(unit);

	// 트레이닝 모드일 경우에만 이벤트를 처리한다.
	if (false == isTrainingMode) {
	    return;
	}

	List<UnitType> targetUnitTypeList;
	if (UnitUtil.isEnemyUnit(unit)) {
	    // 적 유닛이 죽었을 경우
	    targetUnitTypeList = trainingData.getEnemyUnitList();
	    if (true == isTargetUnitType(targetUnitTypeList, unit)) {
		enemyUnitKilledCount += 1;
	    }
	} else {
	    // 아군 유닛이 죽었을 경우
	    targetUnitTypeList = trainingData.getAllianceUnitList();
	    if (true == isTargetUnitType(targetUnitTypeList, unit)) {
		allianceUnitKilledCount += 1;
	    }
	}

    }

    // 미션 결과를 출력한다.
    public void printResult() {
	Log.info("Traning finished.");
	Log.info("\t Result: %s", isSuccess ? "Success" : "Failed");
	Log.info("\t Score: %d", getScore());
	Log.info("\t Frame count: %d", currentFrameCount);
	Log.info("\t Remain alliance HP: %d", allianceUnitHp);
	Log.info("\t Remain enemy HP: %d", enemyUnitHp);
	Log.info("\t Killed alliance units count: %d", allianceUnitKilledCount);
	Log.info("\t Killed enemy units count: %d", enemyUnitKilledCount);
	Util.writeTrainingResultToFile(gameStatus.mapFileName(), isSuccess, getScore(), currentFrameCount, allianceUnitHp, enemyUnitHp, allianceUnitKilledCount,
		enemyUnitKilledCount, UnitSpecMarine.Instance());
    }

    // 점수를 계산한다.
    private long getScore() {
	long result = -1;

	if (true == isSuccess) {
	    switch (trainingData.getScoreType()) {
	    case ALLIANCE_HP_AND_TIME:
		result = allianceUnitHp * 1000000;
		result += trainingData.getFrameLimitTo() - currentFrameCount;
		break;
	    case TIME_AND_ALLIANCE_HP:
		result = (trainingData.getFrameLimitTo() - currentFrameCount) * 1000000;
		result += allianceUnitHp;
		break;
	    default:
		break;
	    }
	}

	return result;
    }

    // 현재 상황을 업데이트 한다.
    private void updateInformation() {
	currentFrameCount = gameStatus.getFrameCount();
	calcHp();
    }

    // Aliance 유닛들이 HP 합과 Enemy 유닛들의 HP 합을 계산한다.
    private void calcHp() {
	allianceUnitHp = 0;
	enemyUnitHp = 0;

	List<Unit2> allUnits = Unit2.get(gameStatus.getAllUnits());
	for (Unit2 unit : allUnits) {
	    if (UnitUtil.isEnemyUnit(unit)) {
		// Enemy Unit일 경우
		if (true == isTargetUnitType(trainingData.getEnemyUnitList(), unit)) {
		    enemyUnitHp += unit.getHitPoints();
		}
	    } else {
		// Alliance Unit일 경우
		if (true == isTargetUnitType(trainingData.getAllianceUnitList(), unit)) {
		    allianceUnitHp += unit.getHitPoints();
		}
	    }
	}
    }

    // unit의 타입이 targetUnitTypeList 중의 하나와 일치하면 true를 리턴.
    private boolean isTargetUnitType(List<UnitType> targetUnitTypeList, Unit2 unit) {
	boolean result = false;

	for (UnitType unitType : targetUnitTypeList) {
	    if (unit.getType().equals(unitType)) {
		result = true;
		break;
	    }
	}

	return result;
    }

    // 지도 이름을 기반으로 트레이닝 모드 여부를 리턴한다.
    public boolean isTrainingMode() {
	return isTrainingMode;
    }

    public int getExitFrame() {
	return exitFrame;
    }

    public void setExitFrame(int exitFrame) {
	this.exitFrame = exitFrame;
    }
}