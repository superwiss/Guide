import java.util.List;
import java.util.Set;

import bwapi.Color;
import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.UnitType;
import bwta.BaseLocation;

/// 봇 프로그램 개발의 편의성 향상을 위해 게임 화면에 추가 정보들을 표시하는 class<br>
/// 여러 Manager 들로부터 정보를 조회하여 Screen 혹은 Map 에 정보를 표시합니다
public class MagiUXManager extends Manager {
    private StrategyManager strategyManager = null;
    private UnitInfo allianceUnitInfo = null;

    private final char white = '';
    private final char teal = '';

    @Override
    protected void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);

	strategyManager = gameStatus.getStrategyManager();
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

	LocationManager locationManager = gameStatus.getLocationManager();

	List<TilePosition> tilePositionList = null; // 건물을 지을 위치
	tilePositionList = locationManager.getTrainingBuildings();
	drawBuildingBoxMap(tilePositionList, UnitType.Terran_Barracks, "Barracks");

	tilePositionList = locationManager.getBlockingEntranceBuilding();
	if (tilePositionList != null) {
	    drawBuildingBoxMap(tilePositionList, UnitType.Terran_Barracks, "Block Building");
	}

	tilePositionList = locationManager.getSecondEntranceBuilding();
	if (tilePositionList != null) {
	    drawBuildingBoxMap(tilePositionList, UnitType.Terran_Barracks, "Second Block Building");
	}

	tilePositionList = locationManager.get3by2SizeBuildings();
	drawBuildingBoxMap(tilePositionList, UnitType.Terran_Supply_Depot, "Supply");

	tilePositionList = locationManager.getBaseEntranceBunker();
	drawBuildingBoxMap(tilePositionList, UnitType.Terran_Bunker, "Bunker");

	tilePositionList = locationManager.getBaseTurret();
	drawBuildingBoxMap(tilePositionList, UnitType.Terran_Missile_Turret, "Turret");

	tilePositionList = locationManager.getFirstExpansionTurret();
	drawBuildingBoxMap(tilePositionList, UnitType.Terran_Missile_Turret, "ExTurret");

	tilePositionList = locationManager.getExtentionPosition();
	drawBuildingBoxMap(tilePositionList, UnitType.Terran_Command_Center, "Expansion Point");

	if (locationManager.getEnemyStartLocation() != null) {
	    tilePositionList = locationManager.getEnemyBaseSearchSequence();
	    if (tilePositionList != null) {
		drawBuildingBoxMap(tilePositionList, UnitType.Terran_Missile_Turret, "EnemyBase");
	    }
	}

	//베이스 초크포인트 위치
	TilePosition tilePos = locationManager.getBaseEntranceChokePoint();
	MyBotModule.Broodwar.drawCircleMap(tilePos.getX() * 32, tilePos.getY() * 32, 100, Color.Red);
	MyBotModule.Broodwar.drawTextMap(tilePos.getX() * 32 - 40, tilePos.getY() * 32 - 7, "base ent choke point");

	//확장 초크포인트 위치
	tilePos = locationManager.getFirstExtensionChokePoint();
	MyBotModule.Broodwar.drawCircleMap(tilePos.getX() * 32, tilePos.getY() * 32, 30, Color.Red);
	MyBotModule.Broodwar.drawTextMap(tilePos.getX() * 32 - 40, tilePos.getY() * 32 - 7, "first ex choke point");

	tilePos = locationManager.getFirstExtensionChokePoint2();
	if (tilePos != null) {
	    MyBotModule.Broodwar.drawCircleMap(tilePos.getX() * 32, tilePos.getY() * 32, 30, Color.Red);
	    MyBotModule.Broodwar.drawTextMap(tilePos.getX() * 32 - 40, tilePos.getY() * 32 - 7, "first ex choke point2");
	}

	//아군의 모든 완성된 커맨드 센터를 대상으로 한다.
	for (Unit2 commandCenter : allianceUnitInfo.getCompletedUnitSet(UnitKind.Terran_Command_Center)) {

	    int gasWorker = allianceUnitInfo.getUnitsInRange(commandCenter.getPosition(), UnitKind.Worker_Gather_Gas, 320).size();

	    int x = commandCenter.getPosition().getX() - 64;
	    int y = commandCenter.getPosition().getY() - 32;

	    MyBotModule.Broodwar.drawBoxMap(x - 2, y - 1, x + 85, y + 14, Color.Black, true);
	    MyBotModule.Broodwar.drawTextMap(x, y, white + "Gas Workers: " + gasWorker);
	}

	//게임정보를 맵에 표시합니다.
	drawCurrentMultiInfoOnScreen(40, 60);
    }

    // Unit의 ID와 일꾼의 역할을 표시한다.
    public void drawUnitId() {
	Game game = gameStatus.getGame();
	for (Unit2 unit : Unit2.get(game.self().getUnits())) {
	    gameStatus.drawTextMap(unit.getPosition(), "" + unit.getID());

	    if (unit.getType() == UnitType.Terran_SCV) {
		if (allianceUnitInfo.isKindOf(unit, UnitKind.Worker_Gather_Gas)) {
		    gameStatus.drawCircleMap(unit, 5, Color.Green, true);
		} else if (allianceUnitInfo.isKindOf(unit, UnitKind.Worker_Construct)) {
		    gameStatus.drawCircleMap(unit, 5, Color.Yellow, true);
		} else if (allianceUnitInfo.isKindOf(unit, UnitKind.Scouting_Unit)) {
		    gameStatus.drawCircleMap(unit, 5, Color.Purple, true);
		} else {
		    gameStatus.drawCircleMap(unit, 5, Color.Cyan, true);
		}
	    }
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

    public void drawCurrentMultiInfoOnScreen(int x, int y) {
	MyBotModule.Broodwar.drawTextScreen(x, y, white + " <Current Strategy>");
	MyBotModule.Broodwar.drawTextScreen(x, y + 10, teal + "CombatUnit Count = " + allianceUnitInfo.getSupplyUsedExceptWorker());
	MyBotModule.Broodwar.drawTextScreen(x, y + 20, teal + "Goliath Count = " + allianceUnitInfo.getUnitSet(UnitKind.Terran_Goliath).size());
	MyBotModule.Broodwar.drawTextScreen(x, y + 30, teal + "SCV Count = " + allianceUnitInfo.getUnitSet(UnitKind.Terran_SCV).size());
	MyBotModule.Broodwar.drawTextScreen(x, y + 40, teal + "Multi Count = " + strategyManager.getLiveMultiCount());

	int seq = 1;
	for (StrategyStatus status : strategyManager.getStrategyStatus()) {
	    MyBotModule.Broodwar.drawTextScreen(x, y + 40 + seq * 10, teal + "Strategy Status = " + status);
	    seq++;
	}

	seq = 1;
	for (BaseLocation baselocation : strategyManager.getOccupiedBaseLocation()) {
	    MyBotModule.Broodwar.drawTextScreen(x + 340, y + seq * 10, teal + "Enemy Base List = " + baselocation.getTilePosition());
	    seq++;
	}

	seq = 1;
	for (BaseLocation baselocation : strategyManager.getScanLocation()) {
	    MyBotModule.Broodwar.drawTextScreen(x + 340, y + 100 + seq * 10, teal + "Scan List = " + baselocation.getTilePosition());
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

}