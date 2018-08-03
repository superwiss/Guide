import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bwapi.TilePosition;
import bwta.BWTA;
import bwta.BaseLocation;

public abstract class LocationManager extends Manager implements MapInfo {

    protected String mapName; // 지도 이름
    protected TilePosition allianceBaseLocation = null; // 아군 본진 위치
    protected TilePosition enemyStartLocation = null; // 적군 본진 위치
    protected List<TilePosition> allianceFirstExpansionLocation = null; // 아군 확장 위치
    protected List<TilePosition> enemyFirstExpansionLocation = null; // 적군 확장 위치
    protected List<TilePosition> baseLocations = null; // 맵 전체의 스타팅 포인트 위치들.
    private List<TilePosition> searchSequence = null; // 정찰할 위치(순서)
    protected List<TilePosition> trainingBuildings = null; // 배럭, 팩토리, 스타포트와 같은 병력 훈련용 타일의 위치
    protected List<TilePosition> baseEntranceBunkers = null; // 본진 입구 벙커를 지을 위치
    protected List<TilePosition> size3by2Buildings = null; // 3*2 사이즈 건물을 짓기 위한 위치들 (서플라이 디팟, 아마데미 등)
    protected List<TilePosition> baseRefineries = null; // 본진 가스를 짓기 위한 위치
    protected List<TilePosition> baseTurrets = null; // 본진에 위치한 터렛의 위치
    protected List<TilePosition> firstExpansionTurrets = null; // 본진에 위치한 터렛의 위치
    protected List<TilePosition> entranceBuilding = null; // 본진 입구막기용 배럭의 위치
    protected List<TilePosition> secondEntranceBuilding = null; // 확장 입구막기용 배럭의 위치
    private TilePosition baseEntranceChokePoint = null; // 본진 입구 방어를 위한 위치
    private TilePosition firstExtensionChokePoint = null; // 앞마당 입구 방어를 위한 위치
    private TilePosition secondExtensionChokePoint = null; // 두번째 확장 입구 방어를 위한 위치
    private TilePosition twoPhaseChokePoint = null;
    private TilePosition threePhaseChokePointForSiege = null;
    private TilePosition threePhaseChokePointForMech = null;
    private List<TilePosition> baseTankPoint = null; // 본진 탱크 배치를 위한 위치

    public LocationManager() {
    }

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);
    }

    @Override
    protected void onFrame() {
	super.onFrame();
	// 0 frame에서는 아군 Command Center의 위치를 아직 알 수 없어서 LocationManager를 초기화할 수 없으므로, 1프레임일 때 초기화를 한다.
	if (1 == gameStatus.getFrameCount()) {
	    init(gameStatus.getAllianceUnitInfo().getAnyUnit(UnitKind.Terran_Command_Center));
	}
    }

    // CommandCenter를 기준으로 아군 본진이 위치를 계산한다.
    @Override
    public void init(Unit2 commandCenter) {
	allianceBaseLocation = commandCenter.getTilePosition();
	allianceFirstExpansionLocation = initFirstExpansionLocaion();
	baseLocations = initBaseLocations();
	searchSequence = initSearchSequence();
	trainingBuildings = initTrainingBuildings();
	baseEntranceBunkers = initBaseEntranceBunker();
	size3by2Buildings = init3by2SizeBuildings();
	baseRefineries = initBaseRefinery();
	baseTurrets = initBaseTurret();
	firstExpansionTurrets = initFirstExpansionTurret();
	baseEntranceChokePoint = initBaseEntranceChokePoint();
	firstExtensionChokePoint = initFirstExtensionChokePoint();
	secondExtensionChokePoint = initSecondExtensionChokePoint();
	twoPhaseChokePoint = initTwoPhaseChokePoint();
	threePhaseChokePointForSiege = initThreePhaseChokePointForSiege();
	threePhaseChokePointForMech = initThreePhaseChokePointForMech();
	entranceBuilding = initEntranceBuildings();
	secondEntranceBuilding = initSecondEntranceBuildings();
	baseTankPoint = initBaseTankPoint();
    }

    // index번째 스타팅 포인트 위치를 리턴한다.
    @Override
    public TilePosition getBaseLocations(int index) {
	return baseLocations.get(index);
    }

    // 아군 본진의 위치를 리턴한다.
    @Override
    public TilePosition getAllianceBaseLocation() {
	return allianceBaseLocation;
    }

    // 적군 본진의 위치에 대한 Getter
    @Override
    public TilePosition getEnemyStartLocation() {
	return enemyStartLocation;
    }

    // 적군 본진의 위치에 대한 Setter
    @Override
    public void setEnemyStartLocation(TilePosition enemyStartLocation) {
	Log.info("LocationManager:setEnemyStartLocation() - ", enemyStartLocation);
	this.enemyStartLocation = enemyStartLocation;
    }

    // 정찰할 위치(순서)를 리턴한다.
    @Override
    public List<TilePosition> getSearchSequence() {
	return searchSequence;
    }

    // 배럭, 팩토리, 스타포트와 같은 병력 훈련용 타일의 위치를 리턴한다.
    @Override
    public List<TilePosition> getTrainingBuildings() {
	return trainingBuildings;
    }

    // 본진 입구 벙커를 지을 위치를 리턴한다.
    @Override
    public List<TilePosition> getBaseEntranceBunker() {
	return baseEntranceBunkers;
    }

    @Override
    public List<TilePosition> getEntranceBuilding() {
	return entranceBuilding;
    }

    @Override
    public List<TilePosition> getSecondEntranceBuilding() {
	return secondEntranceBuilding;
    }

    // 3*2 사이즈 건물을 짓기 위한 위치들을 리턴한다. (서플라이 디팟, 아마데미 등)
    @Override
    public List<TilePosition> get3by2SizeBuildings() {
	return size3by2Buildings;
    }

    // 본진 가스를 짓기 위한 위치를 리턴한다.
    @Override
    public List<TilePosition> getBaseRefinery() {
	return baseRefineries;
    }

    // 본진에 위치한 터렛의 위치를 리턴한다.
    @Override
    public List<TilePosition> getBaseTurret() {
	return baseTurrets;
    }

    // 첫번째 확장에 위치한 터렛의 위치를 리턴한다.
    @Override
    public List<TilePosition> getFirstExpansionTurret() {
	return firstExpansionTurrets;
    }

    // 본진 입구 방어를 위한 위치를 리턴한다.
    @Override
    public TilePosition getBaseEntranceChokePoint() {
	return baseEntranceChokePoint;
    }

    // 앞마당 입구 방어를 위한 위치를 리턴한다.
    @Override
    public TilePosition getFirstExtensionChokePoint() {
	return firstExtensionChokePoint;
    }

    // 두번째 확장 입구 방어를 위한 위치를 리턴한다.
    @Override
    public TilePosition getSecondExtensionChokePoint() {
	return secondExtensionChokePoint;
    }

    @Override
    public TilePosition getTwoPhaseChokePoint() {
	return twoPhaseChokePoint;
    }

    @Override
    public TilePosition getThreePhaseChokePointForSiege() {
	return threePhaseChokePointForSiege;
    }

    @Override
    public TilePosition getThreePhaseChokePointForMech() {
	return threePhaseChokePointForMech;
    }

    // 앞마당 입구 방어를 위한 위치를 리턴한다.
    @Override
    public List<TilePosition> getBaseTankPoint() {
	return baseTankPoint;
    }

    @Override
    public void setMapName(String mapName) {
	this.mapName = mapName;
    }

    @Override
    public String getMapName() {
	return this.mapName;
    }

    @Override
    public List<TilePosition> getFirstExpansionLocation() {
	return allianceFirstExpansionLocation;
    }

    @Override
    public List<TilePosition> getEnemyFirstExpansionLocation() {
	return enemyFirstExpansionLocation;
    }

    // 아군 첫번째 확장의 위치를 리턴한다.
    public List<TilePosition> initFirstExpansionLocaion() {
	double tempDistance;
	double closestDistance = 1000000000;
	TilePosition firstExpansionLocation = null;
	for (BaseLocation targetBaseLocation : BWTA.getBaseLocations()) {
	    if (targetBaseLocation.getTilePosition().equals(allianceBaseLocation))
		continue;
	    tempDistance = BWTA.getGroundDistance(allianceBaseLocation, targetBaseLocation.getTilePosition());
	    if (tempDistance < closestDistance && tempDistance > 0) {
		closestDistance = tempDistance;
		firstExpansionLocation = targetBaseLocation.getTilePosition();
	    }
	}

	List<TilePosition> result = new ArrayList<>();
	result.add(firstExpansionLocation);
	return result;
    }

    // 적군 첫번째 확장의 위치를 리턴한다.
    public void initEnemyFirstExpansionLocaion() {
	double tempDistance;
	double closestDistance = 1000000000;
	TilePosition firstEnemyExpansionLocation = null;
	for (BaseLocation targetBaseLocation : BWTA.getBaseLocations()) {
	    if (targetBaseLocation.getTilePosition().equals(enemyStartLocation))
		continue;
	    tempDistance = BWTA.getGroundDistance(enemyStartLocation, targetBaseLocation.getTilePosition());
	    if (tempDistance < closestDistance && tempDistance > 0) {
		closestDistance = tempDistance;
		firstEnemyExpansionLocation = targetBaseLocation.getTilePosition();
	    }
	}

	List<TilePosition> result = new ArrayList<>();
	result.add(firstEnemyExpansionLocation);
	enemyFirstExpansionLocation = result;
    }

    public void initEnemyBaseSearch() {
	threePhaseChokePointForSiege = initThreePhaseChokePointForSiege();
	threePhaseChokePointForMech = initThreePhaseChokePointForMech();
    }

    // 다음 확장의 위치를 리턴한다.
    public TilePosition getNextExpansionPoint() {

	//	double tempDistance;
	//	double closestDistance = 999999;
	int expansionPoint = 0;
	int tempExpansionPoint = 0;
	TilePosition nextExpansionLocation = null;

	for (BaseLocation targetBaseLocation : BWTA.getBaseLocations()) {
	    //아군 본진의 경우 제외한다.
	    if (targetBaseLocation.getTilePosition().equals(allianceBaseLocation))
		continue;
	    //이미 아군의 커맨드 센터가 지어져 있을 경우 제외한다.
	    if (allianceUnitInfo.findUnitSetNearTile(targetBaseLocation.getTilePosition(), UnitKind.Terran_Command_Center, 100).size() > 0) {
		continue;
	    }

	    //이미 적군의 생산기지 지어져 있을 경우 제외한다.
	    if (enemyUnitInfo.findUnitSetNearTile(targetBaseLocation.getTilePosition(), UnitKind.MAIN_BUILDING, 100).size() > 0) {
		continue;
	    }

	    //중앙의 멀티는 가져가지 않는다?
	    if ((targetBaseLocation.getTilePosition().getX() > 60 && targetBaseLocation.getTilePosition().getX() < 70)
		    && (targetBaseLocation.getTilePosition().getY() > 60 && targetBaseLocation.getTilePosition().getY() < 70)) {
		continue;
	    }

	    //아군 기지에서 가장 가까운 확장부터 하나씩 가져간다
	    //각 포인트에 대해 확장 점수를 부여하여, 가장 중요도가 높은 확장부터 가져간다
	    //아군 기지로부터의 거리 
	    //주변에 적 전투 유닛들이 있을 경우 
	    //적 본진과의 거리
	    int baseDistance = (int) BWTA.getGroundDistance(allianceBaseLocation, targetBaseLocation.getTilePosition());
	    int enemyUnitCount = enemyUnitInfo.findUnitSetNearTile(targetBaseLocation.getTilePosition(), UnitKind.Combat_Unit, 100).size();
	    int enemyBaseDistance = (int) targetBaseLocation.getDistance(enemyStartLocation.toPosition());

	    tempExpansionPoint = (10000 - baseDistance) + enemyUnitCount * 1000 + enemyBaseDistance;

	    if (tempExpansionPoint > expansionPoint) {
		expansionPoint = tempExpansionPoint;
		nextExpansionLocation = targetBaseLocation.getTilePosition();

	    }
	}
	return nextExpansionLocation;
    }

}
