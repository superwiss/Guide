import java.util.List;

import bwapi.Game;
import bwapi.Unit;
import bwapi.UnitType;

// MagiBot을 빠르게 연습시키기 위해서, 유즈맵으로 미션을 만들어 MagiBot이 미션을 해결하는 방식으로 훈련한다.
public class TrainingManager {
    private static TrainingManager instance = new TrainingManager();

    public static TrainingManager Instance() {
	return instance;
    }

    private static Game game = MyBotModule.Broodwar;

    private TrainingData trainingData;

    private boolean isTrainingMode = true;

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

    public void init() {
	String mapName = game.mapFileName();
	TrainingData.TrainingDataBuilder builder = TrainingData.builder();
	switch (mapName) {
	case "Marine1_vs_Zergling1.scx":
	    // 마린 1마리 vs 저글링 1마리 전투. 저글링이 Attack Move Location 으로 이동하던 도중에 마린을 만난 상황.
	    // 500 프레임 이내에 아군 마린은 죽지 않고 적 저글링을 죽여야 한다.
	    builder.addAllianceUnitType(UnitType.Terran_Marine).addEnemyUnitType(UnitType.Zerg_Zergling).frameLimitFrom(50).frameLimitTo(500).allianceKillCount(0).enemyKillCount(1)
		    .scoreType(TrainingData.SCORE_TYPE.ALLIANCE_HP_AND_TIME);
	    break;
	default:
	    isTrainingMode = false;
	    break;
	}
	trainingData = builder.build();
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

	if (true == result) {
	    printResult();
	}

	return result;
    }

    // 유닛이 없어질 때 마다 호출된다.
    public void onUnitEvade(Unit unit) {
	List<UnitType> targetUnitTypeList;
	if (game.self().isEnemy(unit.getPlayer())) {
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
    private void printResult() {
	Log.info("Traning finished.");
	Log.info("\t Result: %s", isSuccess ? "Success" : "Failed");
	Log.info("\t Score: %d", getScore());
	Log.info("\t Frame count: %d", currentFrameCount);
	Log.info("\t Remain alliance HP: %d", allianceUnitHp);
	Log.info("\t Remain enemy HP: %d", enemyUnitHp);
	Log.info("\t Killed alliance units count: %d", allianceUnitKilledCount);
	Log.info("\t Killed enemy units count: %d", enemyUnitKilledCount);
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
	currentFrameCount = game.getFrameCount();
	calcHp();
    }

    // Aliance 유닛들이 HP 합과 Enemy 유닛들의 HP 합을 계산한다.
    private void calcHp() {
	allianceUnitHp = 0;
	enemyUnitHp = 0;

	List<Unit> allUnits = game.getAllUnits();
	for (Unit unit : allUnits) {
	    if (game.self().isEnemy(unit.getPlayer())) {
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

    // unit의 타입이 targetUnitTypeList 중의 하나와 일치하면, result + unit의 hp를 리턴한다. 
    private long getHp(long result, List<UnitType> targetUnitTypeList, Unit unit) {
	if (true == isTargetUnitType(targetUnitTypeList, unit)) {
	    result += unit.getHitPoints();
	}
	return result;
    }

    // unit의 타입이 targetUnitTypeList 중의 하나와 일치하면 true를 리턴.
    private boolean isTargetUnitType(List<UnitType> targetUnitTypeList, Unit unit) {
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
}