import java.util.List;
import java.util.Set;

import bwapi.Color;
import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;

/// 봇 프로그램 개발의 편의성 향상을 위해 게임 화면에 추가 정보들을 표시하는 class<br>
/// 여러 Manager 들로부터 정보를 조회하여 Screen 혹은 Map 에 정보를 표시합니다
public class MagiUXManager extends Manager {
    private StrategyManager strategyManager = null;
    private UnitInfo allianceUnitInfo = null;
    private LocationManager locationManager = null;

    private final char white = '';
    private final char teal = '';

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);
	strategyManager = gameStatus.getStrategyManager();
	locationManager = gameStatus.getLocationManager();
	allianceUnitInfo = gameStatus.getAllianceUnitInfo();
    }

    @Override
    protected void onFrame() {
	super.onFrame();

	// 0 Frame은 LocationManager가 초기화 안되었으므로 skip 한다.
	if (1 > gameStatus.getFrameCount()) {
	    return;
	}

	drawUnitId();
	drawHeadAllianceUnit();
	drawBaseDefenceArea();
	drawPositionAtMousePoint();
	//drawMapGrid();

	//배럭 & 팩토리 건설 위치
	List<TilePosition> tilePositionList = null;
	tilePositionList = locationManager.getTrainingBuildings();
	drawBuildingBoxMap(tilePositionList, UnitType.Terran_Barracks, "Barracks");

	//서플라이 건설 위치
	tilePositionList = locationManager.get3by2SizeBuildings();
	drawBuildingBoxMap(tilePositionList, UnitType.Terran_Supply_Depot, "Supply");

	//입구 벙커 건설 위치
	tilePositionList = locationManager.getBaseEntranceBunker();
	drawBuildingBoxMap(tilePositionList, UnitType.Terran_Bunker, "Bunker");

	//터렛 건설 위치
	tilePositionList = locationManager.getBaseTurret();
	drawBuildingBoxMap(tilePositionList, UnitType.Terran_Missile_Turret, "Turret");

	//확장 터렛 건설 위치
	tilePositionList = locationManager.getFirstExpansionTurret();
	drawBuildingBoxMap(tilePositionList, UnitType.Terran_Missile_Turret, "ExTurret");

	//확장 기지 위치
	tilePositionList = locationManager.getFirstExpansionLocation();
	drawBuildingBoxMap(tilePositionList, UnitType.Terran_Command_Center, "First Expansion Location");

	//입구막기용 배럭 위치
	tilePositionList = locationManager.getEntranceBuilding();
	drawBuildingBoxMap(tilePositionList, UnitType.Terran_Barracks, "Block Building");

	//확장 배럭 위치
	tilePositionList = locationManager.getSecondEntranceBuilding();
	drawBuildingBoxMap(tilePositionList, UnitType.Terran_Barracks, "Second Block Building");

	//탱크 위치
	tilePositionList = locationManager.getBaseTankPoint();
	drawBuildingBoxMap(tilePositionList, UnitType.Terran_Missile_Turret, "Tank");

	//베이스 초크포인트 위치
	TilePosition tilePos = locationManager.getBaseEntranceChokePoint();
	MyBotModule.Broodwar.drawCircleMap(tilePos.getX() * 32, tilePos.getY() * 32, 30, Color.Red);
	MyBotModule.Broodwar.drawTextMap(tilePos.getX() * 32 - 40, tilePos.getY() * 32 - 7, "base ent choke point");

	//확장 초크포인트 위치
	tilePos = locationManager.getFirstExtensionChokePoint();
	MyBotModule.Broodwar.drawCircleMap(tilePos.getX() * 32, tilePos.getY() * 32, 450, Color.Red);
	MyBotModule.Broodwar.drawTextMap(tilePos.getX() * 32 - 40, tilePos.getY() * 32 - 7, "first ex choke point");

	//확장 초크포인트 위치
	tilePos = locationManager.getSecondExtensionChokePoint();
	MyBotModule.Broodwar.drawCircleMap(tilePos.getX() * 32, tilePos.getY() * 32, 200, Color.Red);
	MyBotModule.Broodwar.drawTextMap(tilePos.getX() * 32 - 40, tilePos.getY() * 32 - 7, "Second ex choke point");

	//확장 초크포인트 위치
	tilePos = locationManager.getTwoPhaseChokePoint();
	MyBotModule.Broodwar.drawCircleMap(tilePos.getX() * 32, tilePos.getY() * 32, 200, Color.Blue);
	MyBotModule.Broodwar.drawTextMap(tilePos.getX() * 32 - 40, tilePos.getY() * 32 - 7, "2 phase choke point");

	if (locationManager.getEnemyStartLocation() != null) {

	    tilePos = locationManager.getThreePhaseChokePointForSiege();
	    MyBotModule.Broodwar.drawCircleMap(tilePos.getX() * 32, tilePos.getY() * 32, 600, Color.Blue);
	    MyBotModule.Broodwar.drawTextMap(tilePos.getX() * 32 - 40, tilePos.getY() * 32 - 7, "3 phase Tank point");

	    tilePos = locationManager.getThreePhaseChokePointForMech();
	    MyBotModule.Broodwar.drawCircleMap(tilePos.getX() * 32, tilePos.getY() * 32, 200, Color.Purple);
	    MyBotModule.Broodwar.drawTextMap(tilePos.getX() * 32 - 40, tilePos.getY() * 32 - 7, "3 phase Mech point");

	}

	//현재 확장의 상황을 표시합니다.
	drawCurrentMultiInfoOnScreen(40, 60);

    }

    public void drawCurrentMultiInfoOnScreen(int x, int y) {
	MyBotModule.Broodwar.drawTextScreen(x, y, white + " <Current Strategy>");
	MyBotModule.Broodwar.drawTextScreen(x, y + 10, teal + "MultiCount = " + strategyManager.multiCount);
	MyBotModule.Broodwar.drawTextScreen(x, y + 20, teal + "Mechanic Count = " + allianceUnitInfo.getSupplyUsedExceptWorker());
	MyBotModule.Broodwar.drawTextScreen(x, y + 30, teal + "Phase = " + strategyManager.getPhase());
	MyBotModule.Broodwar.drawTextScreen(x, y + 40, teal + "Enemy Base = " + locationManager.getEnemyStartLocation());

	int seq = 1;
	for (StrategyStatus status : strategyManager.getStrategyStatus()) {	    
	    MyBotModule.Broodwar.drawTextScreen(x, y + 40 + seq * 10, teal + "Strategy status = " + status);
	    seq++;
	}

    }

    private void drawBuildingBoxMap(List<TilePosition> tilePositionList, UnitType unitType, String name) {

	int sequence = 0;
	for (TilePosition tilePosition : tilePositionList) {
	    sequence++;
	    int x = tilePosition.getX();
	    int y = tilePosition.getY();
	    int x1 = x * 32 + 8;
	    int y1 = y * 32 + 8;
	    int x2 = (x + unitType.tileSize().getX()) * 32 - 8;
	    int y2 = (y + unitType.tileSize().getY()) * 32 - 8;
	    MyBotModule.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Green, false);
	    MyBotModule.Broodwar.drawTextMap(x1 + 5, y1 + 2, name + " " + sequence);
	}
    }

    /// 해당 BaseLocation 에 player의 건물이 존재하는지 리턴합니다
    /// @param baseLocation 대상 BaseLocation
    /// @param player 아군 / 적군
    /// @param radius TilePosition 단위
    public boolean hasBuildingAroundBaseLocation(TilePosition baseLocation, int radius) {

	// invalid regions aren't considered the same, but they will both be null
	if (baseLocation == null) {
	    return false;
	}

	if (allianceUnitInfo.getUnitsInRange(baseLocation.toPosition(), UnitKind.MAIN_BUILDING, radius).size() > 0) {
	    return true;
	}

	if (enemyUnitInfo.getUnitsInRange(baseLocation.toPosition(), UnitKind.MAIN_BUILDING, radius).size() > 0) {
	    return true;
	}

	return false;
    }

    // Unit의 ID를 표시한다.
    public void drawUnitId() {
	Game game = gameStatus.getGame();
	for (Unit2 unit : Unit2.get(game.self().getUnits())) {
	    gameStatus.drawTextMap(unit.getPosition(), "" + unit.getID());
	}
	for (Unit2 unit : Unit2.get(game.enemy().getUnits())) {
	    gameStatus.drawTextMap(unit.getPosition(), "" + unit.getID());
	}
    }

    // 아군의 선두 유닛과 그 반경 300을 원으로 표시한다.
    private void drawHeadAllianceUnit() {
	if (null != strategyManager.getAttackTilePositon()) {
	    Position attackPosition = strategyManager.getAttackTilePositon().toPosition();
	    Unit2 headAllianceUnit = allianceUnitInfo.getHeadAllianceUnit(UnitKind.Bionic_Attackable, attackPosition);
	    if (null != headAllianceUnit) {
		gameStatus.drawCircleMap(headAllianceUnit.getPosition().getX(), headAllianceUnit.getPosition().getY(), 300, Color.Blue);
	    }
	}
    }

    // 아군 본진의 방어 영역을 표시한다.
    private void drawBaseDefenceArea() {
	Set<Unit2> commandCenterSet = allianceUnitInfo.getUnitSet(UnitKind.Terran_Command_Center);
	for (Unit2 commandCenter : commandCenterSet) {
	    gameStatus.drawCircleMap(commandCenter, 800, Color.Green);
	}
    }

    // 마우스 포인터의 Tile Position 위치를 표시한다.
    private void drawPositionAtMousePoint() {
	int mouseX = gameStatus.getMousePosition().getX() + gameStatus.getScreenPosition().getX();
	int mouseY = gameStatus.getMousePosition().getY() + gameStatus.getScreenPosition().getY();
	gameStatus.drawTextMap(mouseX + 20, mouseY, "(" + (int) (mouseX / Config.TILE_SIZE) + ", " + (int) (mouseY / Config.TILE_SIZE) + ")");
    }

    /// Tile Position 그리드를 Map 에 표시합니다
    public void drawMapGrid() {
	int cellSize = 32;
	int mapWidth = 4096;
	int mapHeight = 4096;
	int cols = 128;
	int rows = 128;

	for (int i = 0; i < cols; i++) {
	    MyBotModule.Broodwar.drawLineMap(i * cellSize, 0, i * cellSize, mapHeight, Color.Blue);
	}

	for (int j = 0; j < rows; j++) {
	    MyBotModule.Broodwar.drawLineMap(0, j * cellSize, mapWidth, j * cellSize, Color.Blue);
	}

	for (int r = 0; r < rows; r += 2) {
	    for (int c = 0; c < cols; c += 2) {
		MyBotModule.Broodwar.drawTextMap(c * 32, r * 32, c + "," + r);
	    }
	}

    }

}