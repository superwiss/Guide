import java.util.ArrayList;
import java.util.List;

import bwapi.UnitType;

public class TrainingData {

    // frameLimitFrom으로 설정한 프레임보다 현재 프레임이 더 작으면 미션 종료 조건을 판단하지 않는다.
    // default = 10; 10프레임부터 미션 종료 조건을 판단하기 시작한다.
    private int frameLimitFrom;

    // frameLimitTo로 설정한 프레임보다 현재 프레임이 더 크면 미션을 종료한다.
    // default = Integer.MAX_VALUE; 시간 제한이 없이 미션이 진행된다.
    private int frameLimitTo;

    // allianceKillCount로 설정한 것보다 더 많은 아군 유닛이 죽으면 미션을 종료한다.
    // default = 0; 아군 유닛이 하나라도 죽으면 미션이 종료된다.
    private int allianceKillCount;

    // enemyKillCount로 설정한 값 이상의 유닛을 죽여야 미션이 종료된다.
    // default = 1; 적군을 하나라도 죽이면 미션이 종료된다.
    private int enemyKillCount;

    // 점수 계산 방식.
    // default = SCORE_TYPE.ALLIANCE_HP_AND_TIME; 시간이 더 걸리더라도 아군이 적게 얻어 맞는게 더 높은 점수를 받는다.
    private SCORE_TYPE scoreType;

    // 미션 성공/실패 조건을 계산할 때 모니터링 할 동맹 유닛의 종류
    private List<UnitType> allianceUnitList;

    // 미션 성공/실패 조건을 계산할 때 모니터링 할 적 유닛의 종류
    private List<UnitType> enemyUnitList;

    // ////////////////////////////////////////////////////////////////////////
    // Getter
    // ////////////////////////////////////////////////////////////////////////

    public int getFrameLimitFrom() {
	return frameLimitFrom;
    }

    public int getFrameLimitTo() {
	return frameLimitTo;
    }

    public int getAllianceKillCount() {
	return allianceKillCount;
    }

    public int getEnemyKillCount() {
	return enemyKillCount;
    }

    public SCORE_TYPE getScoreType() {
	return scoreType;
    }

    public List<UnitType> getAllianceUnitList() {
	return allianceUnitList;
    }

    public List<UnitType> getEnemyUnitList() {
	return enemyUnitList;
    }

    // ////////////////////////////////////////////////////////////////////////
    // Enum
    // ////////////////////////////////////////////////////////////////////////

    // ALLIANCE_HP_AND_TIME: HP가 시간보다 더 중요하다.
    // TIME_AND_ALLIANCE_HP: 시간이 HP보다 더 중요하다.
    public enum SCORE_TYPE {
	ALLIANCE_HP_AND_TIME, TIME_AND_ALLIANCE_HP
    }

    // ////////////////////////////////////////////////////////////////////////
    // Builder
    // ////////////////////////////////////////////////////////////////////////
    public static TrainingDataBuilder builder() {
	return new TrainingDataBuilder();
    }

    public static class TrainingDataBuilder {
	private int frameLimitFrom = 0;
	private int frameLimitTo = Integer.MAX_VALUE;
	private int allianceKillCount = 0;
	private int enemyKillCount = 1;
	private SCORE_TYPE scoreType = SCORE_TYPE.ALLIANCE_HP_AND_TIME;
	private List<UnitType> allianceUnitList = new ArrayList<>();
	private List<UnitType> enemyUnitList = new ArrayList<>();

	public TrainingDataBuilder frameLimitFrom(int frameLimitFrom) {
	    this.frameLimitFrom = frameLimitFrom;
	    return this;
	}

	public TrainingDataBuilder frameLimitTo(int frameLimitTo) {
	    this.frameLimitTo = frameLimitTo;
	    return this;
	}

	public TrainingDataBuilder allianceKillCount(int allianceKillCount) {
	    this.allianceKillCount = allianceKillCount;
	    return this;
	}

	public TrainingDataBuilder enemyKillCount(int enemyKillCount) {
	    this.enemyKillCount = enemyKillCount;
	    return this;
	}

	public TrainingDataBuilder scoreType(SCORE_TYPE scoreType) {
	    this.scoreType = scoreType;
	    return this;
	}

	public TrainingDataBuilder addAllianceUnitType(UnitType unitType) {
	    this.allianceUnitList.add(unitType);
	    return this;
	}

	public TrainingDataBuilder addEnemyUnitType(UnitType unitType) {
	    this.enemyUnitList.add(unitType);
	    return this;
	}

	public TrainingData build() {
	    TrainingData trainingData = new TrainingData();

	    trainingData.frameLimitFrom = this.frameLimitFrom;
	    trainingData.frameLimitTo = this.frameLimitTo;
	    trainingData.allianceKillCount = this.allianceKillCount;
	    trainingData.enemyKillCount = this.enemyKillCount;
	    trainingData.scoreType = this.scoreType;
	    trainingData.allianceUnitList = this.allianceUnitList;
	    trainingData.enemyUnitList = this.enemyUnitList;

	    return trainingData;
	}
    }

    // ////////////////////////////////////////////////////////////////////////
    // Override
    // ////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
	return "TrainingData(" + "frameLimitFrom=" + frameLimitFrom + ",frameLimitTo=" + frameLimitTo + ",allianceUnitList.size=" + allianceUnitList.size() + ",enemyUnitList.size="
		+ enemyUnitList.size() + ")";
    }
}
