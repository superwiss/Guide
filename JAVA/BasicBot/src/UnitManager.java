import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class UnitManager {

    public static enum Assignment {
	SCOUT, GATHER_GAS
    }

    // 모든 유닛 목록
    private List<Unit> unitList = new ArrayList<>();

    // Unit ID - Unit 매핑
    private Map<Integer, Unit> idUnitMap = new HashMap<>();

    // 유닛의 종류
    private Map<UnitKind, Set<Integer>> unitKindMap = new HashMap<>();

    // 유닛의 마지막 명령
    private Map<Integer, ActionDetail> lastActionMap = new HashMap<>();

    // 유닛의 마지막 상태
    private Map<Integer, UnitStatus> lastStatusMap = new HashMap<>();

    // Key: Command Center의 ID, Value: Key 주변의 미네랄 목록
    private Map<Integer, Set<Integer>> mineralMap = new HashMap<>();

    // Key: 미네랄, Value: 미네랄에 일꾼이 할당되었는지 여부
    private Map<Integer, Boolean> assignedMineralMap = new HashMap<>();

    // 유닛의 마지막 위치
    private Map<Integer, TilePosition> lastTilePositoin = new HashMap<>();

    // 생성자
    public UnitManager() {
	// unitFilter를 초기화 한다.
	for (UnitKind unitKind : UnitKind.values()) {
	    Set<Integer> set = new HashSet<>();
	    unitKindMap.put(unitKind, set);
	}
    }

    // 유닛을 추가한다.
    public void add(Unit unit) {
	addOrRemove(unit, true);
    }

    // 유닛을 삭제한다.
    public void remove(Unit unit) {
	addOrRemove(unit, false);
    }

    // UnitKind에 unit을 추가한다.
    public void addUnitKind(UnitKind unitKind, Unit unit) {
	if (null != unitKind && null != unit) {
	    unitKindMap.get(unitKind).add(unit.getID());
	} else {
	    Log.warn("putUnitKind(): invalid paramter. unitKind: %s, unit: %s", unitKind, unit);
	}
    }

    // UnitKind에 unit을 추가한다.
    public void removeUnitKind(UnitKind unitKind, Unit unit) {
	if (null != unitKind && null != unit) {
	    unitKindMap.get(unitKind).remove(Integer.valueOf(unit.getID()));
	} else {
	    Log.warn("removeUnitKind(): invalid paramter. unitKind: %s, unit: %s", unitKind, unit);
	}
    }

    // Unit ID에 해당하는 유닛 객체를 리턴한다.
    public Unit getUnit(int id) {
	return idUnitMap.get(id);
    }

    public Set<Integer> getAllUnits() {
	return idUnitMap.keySet();
    }

    public Set<Integer> getUnitIdSetByUnitKind(UnitKind unitKind) {
	return unitKindMap.get(unitKind);
    }

    public Set<Integer> getUnitIdSetByUnitKind(UnitType unitType) {
	return unitKindMap.get(UnitUtil.getUnitKindByUnitType(unitType));
    }

    // unitKind 유닛 중에서 아무거나 하나 가져온 뒤, 그 유닛의 ID를 리턴한다.
    public Integer getFirstUnitIdByUnitKind(UnitKind unitKind) {
	Integer result = null;

	if (0 != unitKindMap.get(unitKind).size()) {
	    result = unitKindMap.get(unitKind).iterator().next();
	}

	return result;
    }

    // unitKind 유닛 중에서 아무거나 하나 가져온 뒤, 그 유닛을 리턴한다.
    public Unit getFirstUnitByUnitKind(UnitKind unitKind) {
	Unit result = null;

	Integer unitId = getFirstUnitIdByUnitKind(unitKind);
	if (null != unitId) {
	    result = getUnit(unitId);
	}

	return result;
    }

    // unitKind 유닛 중에서 아무거나 하나 가져온 뒤, 그 유닛의 tilePosition을 리턴한다.
    public TilePosition getFirstUnitTilePositionByUnitKind(UnitKind unitKind) {
	TilePosition result = null;

	Unit unit = getFirstUnitByUnitKind(unitKind);
	if (null != unit) {
	    result = unit.getTilePosition();
	}

	return result;
    }

    // 현재 존재하는 커맨드센터 중 하나를 리턴한다.
    public Unit getFirstCommandCenter() {
	Unit result = null;

	Set<Integer> commandCenters = unitKindMap.get(UnitKind.Terran_Command_Center);
	if (commandCenters.size() > 0) {
	    result = getUnit(commandCenters.iterator().next());
	}

	return result;
    }

    // 유닛의 마지막 Action 정보를 리틴한다.
    public ActionDetail getLastAction(Unit unit) {
	return lastActionMap.get(unit.getID());
    }

    // 유닛의 Action 정보를 Update 한다.
    public void updateLastAction(Unit unit, ActionDetail lastAction) {
	lastActionMap.put(unit.getID(), lastAction);
    }

    // 유닛의 마지막 Status 정보를 리틴한다.
    public UnitStatus getLastStatus(Unit unit) {
	return lastStatusMap.get(unit.getID());
    }

    // 유닛의 Status 정보를 Update 한다.
    public void updateLastStatus(Unit unit, UnitStatus status) {
	lastStatusMap.put(unit.getID(), status);
    }

    private void addOrRemove(Unit unit, boolean isAddMode) {
	Integer id = unit.getID();
	Set<UnitKind> unitKinds = UnitUtil.getUnitKinds(unit);

	if (true == isAddMode) {
	    idUnitMap.put(id, unit);

	    for (UnitKind unitKind : unitKinds) {
		unitKindMap.get(unitKind).add(id);
	    }

	    // 유닛의 초기 상태는 Idle이다.
	    lastStatusMap.put(id, UnitStatus.IDLE);

	    // 건물일 경우, 유닛의 마지막 위치를 기록한다.
	    if (unit.getType().isBuilding()) {
		lastTilePositoin.put(id, unit.getTilePosition());
	    }

	    unitList.add(unit);

	} else {
	    idUnitMap.remove(id);

	    for (UnitKind unitKind : unitKinds) {
		unitKindMap.get(unitKind).remove(id);
	    }

	    lastActionMap.remove(id);
	    lastStatusMap.remove(id);

	    if (lastTilePositoin.containsKey(id)) {
		lastTilePositoin.remove(id);
	    }

	    unitList.remove(unit);
	}
    }

    @Override
    public String toString() {
	String result = "";

	result += "\nUnits Size: " + unitList.size();

	for (Unit unit : unitList) {
	    result += "\n\t(" + UnitUtil.toString(unit) + ") ";
	}

	return result;
    }

    public Unit getCloseCommandCenter(Unit worker) {
	Unit result = null;

	int minDistance = Integer.MAX_VALUE;
	for (Integer commandCenter : unitKindMap.get(UnitKind.Terran_Command_Center)) {
	    int distance = worker.getDistance(getUnit(commandCenter));
	    if (distance < minDistance) {
		minDistance = distance;
		result = getUnit(commandCenter);
	    }
	}

	return result;
    }

    public void setScoutUnit(Unit unit) {
	// 유닛을 관리 대상에서 삭제하고 SCOUT 타입으로 변경한다.
	Set<UnitKind> unitKinds = UnitUtil.getUnitKinds(unit);
	Integer id = unit.getID();
	for (UnitKind unitKind : unitKinds) {
	    unitKindMap.get(unitKind).remove(id);
	}
	unitKindMap.get(UnitKind.Scouting_Unit).add(id);
    }

    public void releaseScoutUnit(Unit unit) {
	if (null != unit) {
	    // 유닛을 SCOUT 타입에서 원래 타입으로 원복한다.
	    Set<UnitKind> unitKinds = UnitUtil.getUnitKinds(unit);
	    Integer id = unit.getID();
	    for (UnitKind unitKind : unitKinds) {
		unitKindMap.get(unitKind).add(id);
	    }
	    unitKindMap.get(UnitKind.Scouting_Unit).remove(id);
	} else {
	    Log.trace("정찰 유닛이 죽어버렸음..");
	}
    }

    // 메모리에 저장된 unitSet 중에서 position에 제일 가까운 unit을 리턴한다.
    public Unit getClosestUnitWithLastTilePosition(Set<Integer> unitSet, Position position) {
	return getClosestUnitWithLastTilePosition(unitSet, position, null);
    }

    // 메모리에 저장된 unitSet 중에서 position에 제일 가까운 unit을 리턴한다. excludeUnitType은 계산에서 제외한다.
    public Unit getClosestUnitWithLastTilePosition(Set<Integer> unitSet, Position position, Set<UnitType> excludeUnitType) {
	Unit result = null;

	if (null != unitSet && null != position) {
	    int minDistance = Integer.MAX_VALUE;
	    for (Integer unitId : unitSet) {
		Unit unit = getUnit(unitId);
		if (null != unit) {
		    if (null != excludeUnitType && excludeUnitType.contains(unit.getType())) {
			continue;
		    }
		    TilePosition lastTilePosition = lastTilePositoin.get(unitId);
		    if (null != lastTilePosition) {
			int distance = UnitUtil.getDistance(lastTilePosition.toPosition(), position);
			if (distance < minDistance) {
			    minDistance = distance;
			    result = unit;
			}
		    }
		} else {
		    Log.warn("getClosestUnit(): Failed to getting unit by unitId(%d)", unitId);
		}
	    }
	} else {
	    Log.warn("Invalid Parameter: unitset: %s, position: %s", unitSet, position);
	}

	return result;
    }

    public TilePosition getLastTilePosition(Integer unitId) {
	return lastTilePositoin.get(unitId);
    }

    public String toBuildingString() {
	String result = "";

	Set<Integer> buildings = getUnitIdSetByUnitKind(UnitKind.Building);

	result += "\nBuilding size: " + buildings.size();

	for (Integer buildingId : buildings) {
	    Unit building = getUnit(buildingId);
	    if (null != building) {
		result += "\n\t(" + UnitUtil.toString(building) + ") ";
	    }
	}

	return result;
    }

    // 미네랄 정보를 assignedMineralMap에 추가한다. isAssignedSCV <- 미네랄에 SCV가 할당되어 있는지 여부 (일꾼을 미네랄에 골고루 분배하기 위한 목적)
    public void addAssignedMineralMap(int mineralId, boolean isAssignedSCV) {
	assignedMineralMap.put(mineralId, false);
    }

    public boolean isAssignedWorkerToMiniral(Integer mineralId) {
	return assignedMineralMap.get(mineralId);
    }

    // 커맨드 센터에 대해서, 주변에 존재하는 미네랄 set을 할당한다.
    public void assignMineralToCommandCenter(Unit commandCenter, Set<Integer> mineralIdSet) {
	Log.trace("assign miniralSet near command center: %d", commandCenter.getID());
	mineralMap.put(commandCenter.getID(), mineralIdSet);
    }

    // 커맨드 센터가 할당 받은 미네랄 set을 릴리즈 한다.
    public void releaseMineralToCommandCenter(Unit commandCenter) {
	Log.trace("release miniralSet near command center: %d", commandCenter.getID());
	mineralMap.remove(commandCenter.getID());
    }

    public Set<Integer> getMineralIdSetAssignedByCommandCenter(Unit commandCenter) {
	return mineralMap.get(commandCenter.getID());
    }

    // mineralId에 일꾼을 할당한다. (모든 미네랄에 일꾼이 할당되기 전까지, 이 미네랄을 새로운 일꾼에게 할당되지 않기 위한 목적)
    public void assignWorkerToMiniral(int mineralId) {
	assignedMineralMap.put(mineralId, true);
    }

    // 건설 중인 건물이 몇 개나 있는지 확인한다.
    public int getConstructionCount(UnitType underConstructBuildingType) {
	int result = 0;

	Set<Integer> unitIdSet = getUnitIdSetByUnitKind(underConstructBuildingType);
	for (Integer unitId : unitIdSet) {
	    Unit unit = getUnit(unitId);
	    if (false == unit.isCompleted()) {
		result += 1;
	    }
	}

	return result;
    }

    // unitIdSet 중에서 position에 가장 가까운 유닛 하나를 리턴한다. 유닛 타입이 excludeUnitType일 경우는 제외한다.
    public Unit getClosestUnit(Set<Integer> unitIdSet, Position position, Set<UnitType> excludeUnitType) {
	Unit result = null;

	if (null != unitIdSet && null != position) {
	    int minDistance = Integer.MAX_VALUE;
	    for (Integer unitId : unitIdSet) {
		Unit unit = getUnit(unitId);
		if (null != unit) {
		    if (null != excludeUnitType && excludeUnitType.contains(unit.getType())) {
			continue;
		    }
		    int distance = unit.getDistance(position);
		    if (distance < minDistance) {
			minDistance = distance;
			result = unit;
		    }
		} else {
		    Log.warn("getClosestUnit(): Failed to getting unit by unitId(%d)", unitId);
		}
	    }
	} else {
	    Log.warn("Invalid Parameter: unitset= %s, position=%s, excludeUnitType=%s", unitIdSet, position, excludeUnitType);
	}

	return result;
    }

    // unitManager 소속의 unitIdSet 중에서 position에 가장 가까운 유닛 하나를 리턴한다. (exclude 없는 버전)
    public Unit getClosestUnit(Set<Integer> unitIdSet, Position position) {
	return getClosestUnit(unitIdSet, position, null);
    }

    // buildingType 건물에서 훈련 중인 unitType의 개수를 리턴한다.
    public int getTrainingQueueUnitCount(UnitType buildingType, UnitType unitType) {
	int result = 0;

	Set<Integer> buildingSet = getUnitIdSetByUnitKind(buildingType);
	for (Integer buildingId : buildingSet) {
	    Unit building = getUnit(buildingId);
	    List<UnitType> trainingQueue = building.getTrainingQueue();
	    for (UnitType trainingUnitType : trainingQueue) {
		if (unitType.equals(trainingUnitType)) {
		    result++;
		}
	    }
	}

	return result;
    }

    // unitType을 훈련할 수 있는 buildingType 건물 중, 적절한 건물을 리턴한다. 예를 들면, 마린을 뽑을 때 트레이닝 큐가 제일 짧은 배럭이 우선 순위가 높다라던가 등등...
    public Unit getTrainableBuilding(UnitType buildingType, UnitType unitType) {
	Unit targetBuilding = null;

	int minQueueSize = Integer.MAX_VALUE;
	Set<Integer> candidateSet = getUnitIdSetByUnitKind(buildingType);
	// 마린 훈련이 가능한 배럭 중에서 TrainingQueue가 가장 적은 배럭을 선택
	// TrainingQueue는 최대 2개까지만 허용
	for (Integer buildingId : candidateSet) {
	    Unit building = getUnit(buildingId);
	    if (building.canTrain(unitType)) {
		if (building.getTrainingQueue().size() < 2) {
		    if (minQueueSize > building.getTrainingQueue().size()) {
			minQueueSize = building.getTrainingQueue().size();
			targetBuilding = building;
		    }
		}
	    }
	}

	return targetBuilding;
    }
}