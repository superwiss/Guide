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
    protected TilePosition enemyBaseLocation = null; // 적군 본진 위치
    protected List<TilePosition> allianceFirstExpansionLocation = null; // 아군 확장 위치
    protected List<TilePosition> baseLocations = null; // 맵 전체의 스타팅 포인트 위치들.
    private List<TilePosition> searchSequence = null; // 정찰할 위치(순서)
    protected List<TilePosition> trainingBuildings = null; // 배럭, 팩토리, 스타포트와 같은 병력 훈련용 타일의 위치
    protected List<TilePosition> baseEntranceBunkers = null; // 본진 입구 벙커를 지을 위치
    protected List<TilePosition> size3by2Buildings = null; // 3*2 사이즈 건물을 짓기 위한 위치들 (서플라이 디팟, 아마데미 등)
    protected List<TilePosition> baseRefineries = null; // 본진 가스를 짓기 위한 위치
    protected List<TilePosition> baseTurrets = null; // 본진에 위치한 터렛의 위치
    protected List<TilePosition> firstExpansionTurrets = null; // 본진에 위치한 터렛의 위치
    protected List<TilePosition> engineeringBay = null; // 엔지니어링 베이 타일의 위치
    private TilePosition baseEntranceChokePoint = null; // 본진 입구 방어를 위한 위치
    private TilePosition firstExtensionChokePoint = null; // 앞마당 입구 방어를 위한 위치

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
	
//	allianceFirstExpansionLocation = initFirstExpansionLocaion();
//	baseLocations = initBaseLocations();
//	searchSequence = initSearchSequence();
//	trainingBuildings = initTrainingBuildings();
//	baseEntranceBunkers = initBaseEntranceBunker();
//	size3by2Buildings = init3by2SizeBuildings();
//	baseRefineries = initBaseRefinery();
//	baseTurrets = initBaseTurret();
//	firstExpansionTurrets = initFirstExpansionTurret();
//	engineeringBay = initEngineeringBay();
//	baseEntranceChokePoint = initBaseEntranceChokePoint();
//	firstExtensionChokePoint = initFirstExtensionChokePoint();
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
	engineeringBay = initEngineeringBay();
	baseEntranceChokePoint = initBaseEntranceChokePoint();
	firstExtensionChokePoint = initFirstExtensionChokePoint();
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

    // 아군 확장기지의 위치를 리턴한다.
    @Override
    public List<TilePosition> getFirstExpansionLocation() {
	return allianceFirstExpansionLocation;
    }

    // 적군 본진의 위치에 대한 Getter
    @Override
    public TilePosition getEnemyBaseLocation() {
	return enemyBaseLocation;
    }

    // 적군 본진의 위치에 대한 Setter
    @Override
    public void setEnemyStartLocation(TilePosition enemyBaseLocation) {
	Log.info("LocationManager:setEnemyStartLocation() - ", enemyBaseLocation);
	this.enemyBaseLocation = enemyBaseLocation;
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

    // 엔지니어링 베이의 위치를 리턴한다.
    @Override
    public List<TilePosition> getEngineeringBay() {
	return engineeringBay;
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

    @Override
    public void setMapName(String mapName) {
	this.mapName = mapName;
    }

    @Override
    public String getMapName() {
	return this.mapName;
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
}
