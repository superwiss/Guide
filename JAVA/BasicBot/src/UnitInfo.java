import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bwapi.Position;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class UnitInfo {

    public static enum Assignment {
	SCOUT, GATHER_GAS
    }

    // 모든 아군 유닛 셋
    private Set<Unit2> unitSet = new HashSet<>();

    // 유닛의 종류
    private Map<UnitKind, Set<Unit2>> unitKindMap = new HashMap<>();

    // 유닛의 마지막 명령
    private Map<Unit2, ActionDetail> lastActionMap = new HashMap<>();

    // 유닛의 마지막 상태
    private Map<Unit2, UnitStatus> lastStatusMap = new HashMap<>();

    // 유닛의 마지막 위치
    private Map<Unit2, TilePosition> lastTilePositoin = new HashMap<>();

    // 생성자
    public UnitInfo() {
	// unitFilter를 초기화 한다.
	for (UnitKind unitKind : UnitKind.values()) {
	    Set<Unit2> set = new HashSet<>();
	    unitKindMap.put(unitKind, set);
	}
    }

    // 유닛을 추가한다.
    public void add(Unit2 unit) {
	addOrRemove(unit, true);
    }

    // 유닛을 삭제한다.
    public void remove(Unit2 unit) {
	addOrRemove(unit, false);
    }

    // UnitKind에 unit을 추가한다.
    public void addUnitKind(UnitKind unitKind, Unit2 unit) {
	if (null != unitKind && null != unit) {
	    unitKindMap.get(unitKind).add(unit);
	} else {
	    Log.warn("putUnitKind(): invalid paramter. unitKind: %s, unit: %s", unitKind, unit);
	}
    }

    // UnitKind에서 unit을 삭제한다.
    public void removeUnitKind(UnitKind unitKind, Unit2 unit) {
	if (null != unitKind && null != unit) {
	    unitKindMap.get(unitKind).remove(unit);
	} else {
	    Log.warn("removeUnitKind(): invalid paramter. unitKind: %s, unit: %s", unitKind, unit);
	}
    }

    public Set<Unit2> getAllUnits() {
	return unitSet;
    }

    // 주의: 속도가 느리므로, 디버깅 용도로만 사용할 것...
    public Unit2 getUnit(int id) {
	Unit2 result = null;
	for (Unit2 unit : unitSet) {
	    if (id == unit.getID()) {
		result = unit;
		break;
	    }
	}
	return result;
    }

    public Set<Unit2> getUnitSet(UnitKind unitKind) {
	return unitKindMap.get(unitKind);
    }

    public Set<Unit2> getUnitSet(UnitType unitType) {
	return unitKindMap.get(UnitUtil.getUnitKindByUnitType(unitType));
    }

    public boolean isKindOf(Unit2 unit, UnitKind unitKind) {
	return getUnitSet(unitKind).contains(unit);
    }

    // unitKind 유닛 중에서 아무거나 하나 가져온 뒤, 그 유닛의 ID를 리턴한다.
    public Unit2 getAnyUnit(UnitKind unitKind) {
	Unit2 result = null;

	if (0 != unitKindMap.get(unitKind).size()) {
	    result = unitKindMap.get(unitKind).iterator().next();
	}

	return result;
    }

    // 유닛의 마지막 Action 정보를 리틴한다.
    public ActionDetail getLastAction(Unit2 unit) {
	return lastActionMap.get(unit);
    }

    // 유닛의 Action 정보를 Update 한다.
    public void updateLastAction(Unit2 unit, ActionDetail lastAction) {
	lastActionMap.put(unit, lastAction);
    }

    // 유닛의 마지막 Status 정보를 리틴한다.
    public UnitStatus getLastStatus(Unit2 unit) {
	return lastStatusMap.get(unit);
    }

    // 유닛의 Status 정보를 Update 한다.
    public void updateLastStatus(Unit2 unit, UnitStatus status) {
	lastStatusMap.put(unit, status);
    }

    private void addOrRemove(Unit2 unit, boolean isAddMode) {
	Set<UnitKind> unitKinds = UnitUtil.getUnitKinds(unit);

	// Add on 건물일 경우는 skip 한다.
	if (UnitUtil.isEnemyUnit(unit) && unitKinds.contains(UnitKind.Addon)) {
	    return;
	}

	if (true == isAddMode) {
	    unitSet.add(unit);

	    for (UnitKind unitKind : unitKinds) {
		unitKindMap.get(unitKind).add(unit);
	    }

	    // 유닛의 초기 상태는 Idle이다.
	    lastStatusMap.put(unit, UnitStatus.IDLE);

	    // 건물일 경우, 유닛의 마지막 위치를 기록한다.
	    if (unit.getType().isBuilding()) {
		lastTilePositoin.put(unit, unit.getTilePosition());
	    }
	} else {
	    unitSet.remove(unit);

	    for (UnitKind unitKind : unitKinds) {
		unitKindMap.get(unitKind).remove(unit);
	    }

	    lastActionMap.remove(unit);
	    lastStatusMap.remove(unit);

	    if (lastTilePositoin.containsKey(unit)) {
		lastTilePositoin.remove(unit);
	    }
	}
    }

    @Override
    public String toString() {
	String result = "";

	result += "\nUnits Size: " + unitSet.size();

	for (Unit2 unit : unitSet) {
	    result += "\n\t(" + UnitUtil.toString(unit) + ") ";
	}

	return result;
    }

    public Unit2 getCloseCommandCenter(Unit2 worker) {
	Unit2 result = null;

	int minDistance = Integer.MAX_VALUE;
	for (Unit2 commandCenter : getUnitSet(UnitKind.Terran_Command_Center)) {
	    int distance = worker.getDistance(commandCenter);
	    if (distance < minDistance) {
		minDistance = distance;
		result = commandCenter;
	    }
	}

	return result;
    }

    public void setScoutUnit(Unit2 unit) {
	// 유닛을 관리 대상에서 삭제하고 SCOUT 타입으로 변경한다.
	Set<UnitKind> unitKinds = UnitUtil.getUnitKinds(unit);
	for (UnitKind unitKind : unitKinds) {
	    unitKindMap.get(unitKind).remove(unit);
	}
	unitKindMap.get(UnitKind.Scouting_Unit).add(unit);
    }

    public void releaseScoutUnit(Unit2 unit) {
	if (null != unit) {
	    // 유닛을 SCOUT 타입에서 원래 타입으로 원복한다.
	    Set<UnitKind> unitKinds = UnitUtil.getUnitKinds(unit);
	    for (UnitKind unitKind : unitKinds) {
		unitKindMap.get(unitKind).add(unit);
	    }
	    unitKindMap.get(UnitKind.Scouting_Unit).remove(unit);
	} else {
	    Log.trace("정찰 유닛이 죽어버렸음..");
	}
    }

    // 메모리에 저장된 unitSet 중에서 position에 제일 가까운 unit을 리턴한다.
    public Unit2 getClosestUnitWithLastTilePosition(Set<Unit2> unitSet, Position position) {
	return getClosestUnitWithLastTilePosition(unitSet, position, null);
    }

    // 메모리에 저장된 unitSet 중에서 position에 제일 가까운 unit을 리턴한다. excludeUnitType은 계산에서 제외한다.
    public Unit2 getClosestUnitWithLastTilePosition(Set<Unit2> unitSet, Position position, Set<UnitType> excludeUnitType) {
	Unit2 result = null;

	if (null != unitSet && null != position) {
	    int minDistance = Integer.MAX_VALUE;
	    for (Unit2 unit : unitSet) {
		if (null != excludeUnitType && excludeUnitType.contains(unit.getType())) {
		    continue;
		}
		TilePosition lastTilePosition = lastTilePositoin.get(unit);
		if (null != lastTilePosition) {
		    int distance = UnitUtil.getDistance(lastTilePosition, position);
		    if (distance < minDistance) {
			minDistance = distance;
			result = unit;
		    }
		}
	    }
	} else {
	    Log.warn("Invalid Parameter: unitset: %s, position: %s", unitSet, position);
	}

	return result;
    }

    public TilePosition getLastTilePosition(Unit2 unit) {
	return lastTilePositoin.get(unit);
    }

    public String toBuildingString() {
	String result = "";

	Set<Unit2> buildings = getUnitSet(UnitKind.Building);

	result += "\nBuilding size: " + buildings.size();

	for (Unit2 building : buildings) {
	    if (null != building) {
		result += "\n\t(" + UnitUtil.toString(building) + ") ";
	    }
	}

	return result;
    }

    // 건설 중인 건물이 몇 개나 있는지 확인한다.
    public int getConstructionCount(UnitType underConstructBuildingType) {
	int result = 0;

	Set<Unit2> unitSet = getUnitSet(underConstructBuildingType);
	for (Unit2 unit : unitSet) {
	    if (false == unit.isCompleted()) {
		result += 1;
	    }
	}

	return result;
    }

    // unitSet 중에서 position에 가장 가까운 유닛 하나를 리턴한다. 유닛 타입이 excludeUnitType일 경우는 제외한다.
    public Unit2 getClosestUnit(Set<Unit2> unitSet, Position position, Set<UnitType> excludeUnitType) {
	Unit2 result = null;

	if (null != unitSet && null != position) {
	    int minDistance = Integer.MAX_VALUE;
	    for (Unit2 unit : unitSet) {
		if (null != excludeUnitType && excludeUnitType.contains(unit.getType())) {
		    continue;
		}
		int distance = unit.getPoint().getApproxDistance(position);
		//int distance = BWTA.getGroundDistance2(unit.getTilePosition(), position.toTilePosition());
		if (distance < minDistance) {
		    minDistance = distance;
		    result = unit;
		}
	    }
	} else {
	    Log.warn("Invalid Parameter: unitset= %s, position=%s, excludeUnitType=%s", unitSet, position, excludeUnitType);
	}

	return result;
    }

    // unitManager 소속의 unitSet 중에서 position에 가장 가까운 유닛 하나를 리턴한다. (exclude 없는 버전)
    public Unit2 getClosestUnit(Set<Unit2> unitSet, Position position) {
	return getClosestUnit(unitSet, position, null);
    }

    // buildingType 건물에서 훈련 중인 unitType의 개수를 리턴한다.
    public int getTrainingQueueUnitCount(UnitType buildingType, UnitType unitType) {
	int result = 0;

	Set<Unit2> buildingSet = getUnitSet(buildingType);
	for (Unit2 building : buildingSet) {
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
    public Unit2 getTrainableBuilding(UnitType buildingType, UnitType unitType) {
	Unit2 targetBuilding = null;

	int minQueueSize = Integer.MAX_VALUE;
	Set<Unit2> candidateSet = getUnitSet(buildingType);
	// 훈련이 가능한 건물 중에서 TrainingQueue가 가장 적은 건물을 선택
	// TrainingQueue는 최대 2개까지만 허용
	for (Unit2 building : candidateSet) {
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

    public boolean trainingUnit(UnitType targetUnitType) {
	boolean result = false;

	UnitType trainableBuildingType = getTrainableBuildingType(targetUnitType);

	Unit2 trainableBuilding = getTrainableBuilding(trainableBuildingType, targetUnitType);
	// 훈련하기
	if (null != trainableBuilding) {
	    if (trainableBuilding.canTrain(targetUnitType)) {
		int beforeQueueSize = trainableBuilding.getTrainingQueue().size();
		trainableBuilding.train(targetUnitType);
		int afterQueueSize = trainableBuilding.getTrainingQueue().size();
		if (afterQueueSize > beforeQueueSize) {
		    result = true;
		}

	    }
	}

	return result;
    }

    private UnitType getTrainableBuildingType(UnitType targetUnitType) {
	UnitType result = null;
	switch (targetUnitType.toString()) {
	case "Terran_SCV":
	    result = UnitType.Terran_Command_Center;
	    break;
	case "Terran_Marine":
	case "Terran_Firebat":
	case "Terran_Ghost":
	case "Terran_Medic":
	    result = UnitType.Terran_Barracks;
	    break;
	case "Terran_Vulture":
	case "Terran_Siege_Tank_Siege_Mode":
	case "Terran_Siege_Tank_Tank_Mode":
	case "Terran_Goliath":
	    result = UnitType.Terran_Factory;
	    break;
	case "Terran_Wraith":
	case "Terran_Dropship":
	case "Terran_Science_Vessel":
	case "Terran_Battlecruiser":
	case "Terran_Valkyrie":
	    result = UnitType.Terran_Starport;
	    break;
	default:
	    break;

	}
	return result;
    }

    // 애드온 건물을 건설한다.
    public boolean buildAddon(UnitType addOnUnitType) {
	boolean result = false;

	UnitType addonableBuildingtType = null;
	switch (addOnUnitType.toString()) {
	case "Terran_Comsat_Station":
	case "Terran_Nuclear_Silo":
	    addonableBuildingtType = UnitType.Terran_Command_Center;
	    break;
	case "Terran_Machine_Shop":
	    addonableBuildingtType = UnitType.Terran_Factory;
	    break;
	case "Terran_Control_Tower":
	    addonableBuildingtType = UnitType.Terran_Starport;
	    break;
	case "Terran_Physics_Lab":
	case "Terran_Covert_Ops":
	    addonableBuildingtType = UnitType.Terran_Science_Facility;
	    break;
	default:
	    break;

	}

	Set<Unit2> addonableBuildingSet = getUnitSet(addonableBuildingtType);
	for (Unit2 addonableBuilding : addonableBuildingSet) {
	    if (null != addonableBuilding && addonableBuilding.canBuildAddon(addOnUnitType)) {
		addonableBuilding.buildAddon(addOnUnitType);
		result = true;
	    }
	}

	return result;
    }

    // 스캔을 뿌린다.
    public boolean doScan(Position position) {
	boolean result = false;

	int maxEnergy = Integer.MIN_VALUE;
	Unit2 targetComsat = null;

	Set<Unit2> comsatSet = getUnitSet(UnitType.Terran_Comsat_Station);
	for (Unit2 comsat : comsatSet) {
	    if (maxEnergy < comsat.getEnergy()) {
		maxEnergy = comsat.getEnergy();
		targetComsat = comsat;
	    }
	}

	Log.debug("target comsat: %s", targetComsat);
	if (null != targetComsat && targetComsat.canUseTechPosition(TechType.Scanner_Sweep)) {
	    Log.info("스캔 뿌림: %s", position);
	    targetComsat.useTech(TechType.Scanner_Sweep, position);
	    result = true;
	}

	return result;
    }

    // 업그레이드를 한다.
    public boolean upgrade(UpgradeType upgradeType) {
	boolean result = false;

	if (null != upgradeType) {
	    UnitType upgradableBuildingtType = null;
	    switch (upgradeType.toString()) {
	    case "Ion_Thrusters":
		upgradableBuildingtType = UnitType.Terran_Machine_Shop;
		break;
	    default:
		break;

	    }

	    Set<Unit2> upgradableBuildingSet = getUnitSet(upgradableBuildingtType);
	    for (Unit2 upgradableBuilding : upgradableBuildingSet) {
		if (null != upgradableBuilding && upgradableBuilding.canUpgrade(upgradeType)) {
		    upgradableBuilding.upgrade(upgradeType);
		    result = true;
		}
	    }
	}

	return result;
    }

    // 업그레이드를 한다. (테크)
    public boolean upgrade(TechType upgradeType) {
	boolean result = false;

	if (null != upgradeType) {
	    UnitType upgradableBuildingtType = null;
	    switch (upgradeType.toString()) {
	    case "Spider_Mines":
		upgradableBuildingtType = UnitType.Terran_Machine_Shop;
		break;
	    default:
		break;

	    }

	    Set<Unit2> upgradableBuildingSet = getUnitSet(upgradableBuildingtType);
	    for (Unit2 upgradableBuilding : upgradableBuildingSet) {
		if (null != upgradableBuilding && upgradableBuilding.canResearch(upgradeType)) {
		    upgradableBuilding.research(upgradeType);
		    result = true;
		}
	    }
	}

	return result;
    }

    // position 주변 distance 범위의 unitKind를 구한다.
    public Set<Unit2> getUnitsInRange(Position position, UnitKind unitKind, int range) {
	Set<Unit2> result = new HashSet<>();

	if (null == position || null == unitKind) {
	    Log.warn("UnitInfo.getUnitsInRange: Invalid parameters; position: %s, unitKind; %s", position, unitKind);
	}

	Set<Unit2> unitSet = getUnitSet(unitKind);
	for (Unit2 unit : unitSet) {
	    if (range >= unit.getDistance(position)) {
		result.add(unit);
	    }
	}

	return result;
    }

    // position 주변 distance 범위의 unitKind 중 아무거나 하나를 리턴한다.
    public Unit2 getAnyUnitInRange(Position position, UnitKind unitKind, int range) {
	Unit2 result = null;

	if (null == position || null == unitKind) {
	    Log.warn("UnitInfo.getAnyUnitInRange: Invalid parameters; position: %s, unitKind; %s", position, unitKind);
	}

	Set<Unit2> unitSet = getUnitSet(unitKind);
	for (Unit2 unit : unitSet) {
	    if (range >= unit.getDistance(position)) {
		result = unit;
		break;
	    }
	}

	return result;
    }

    // position에서 가장 가까운 선두 유닛의 위치를 구한다.
    public Unit2 getHeadAllianceUnit(UnitKind targetUnitKind, Position position) {
	Unit2 result = null;

	Set<Unit2> unitSet = null;
	if (null != position) {
	    unitSet = getUnitSet(targetUnitKind);
	    // 공격 목표 지점에서 가장 가까운 선두 유닛을 구한다.
	    result = getClosestUnit(unitSet, position);
	}

	return result;
    }
}