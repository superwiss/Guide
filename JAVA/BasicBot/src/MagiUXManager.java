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
	//배럭스 & 팩토리 건설 위치
	tilePositionList = locationManager.getTrainingBuildings();
	int sequence = 0;
	for (TilePosition tilePosition : tilePositionList) {
	    sequence++;
	    int x = tilePosition.getX();
	    int y = tilePosition.getY();
	    int x1 = x * 32 + 8;
	    int y1 = y * 32 + 8;
	    int x2 = (x + UnitType.Terran_Barracks.tileSize().getX()) * 32 - 8;
	    int y2 = (y + UnitType.Terran_Barracks.tileSize().getY()) * 32 - 8;
	    MyBotModule.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Green, false);
	    MyBotModule.Broodwar.drawTextMap(x1 + 5, y1 + 2, "Barracks");
	    MyBotModule.Broodwar.drawTextMap(x1 + 5, y1 + 13, "Factory  " + sequence);
	}

	//서플라이 건설 위치
	tilePositionList = locationManager.get3by2SizeBuildings();
	sequence = 0;
	for (TilePosition tilePosition : tilePositionList) {
	    sequence++;
	    int x = tilePosition.getX();
	    int y = tilePosition.getY();
	    int x1 = x * 32 + 8;
	    int y1 = y * 32 + 8;
	    int x2 = (x + UnitType.Terran_Supply_Depot.tileSize().getX()) * 32 - 8;
	    int y2 = (y + UnitType.Terran_Supply_Depot.tileSize().getY()) * 32 - 8;
	    MyBotModule.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Green, false);
	    MyBotModule.Broodwar.drawTextMap(x1 + 5, y1 + 2, "Supply  " + sequence);
	}

	//입구 벙커 건설 위치
	tilePositionList = locationManager.getBaseEntranceBunker();
	sequence = 0;
	for (TilePosition tilePosition : tilePositionList) {
	    sequence++;
	    int x = tilePosition.getX();
	    int y = tilePosition.getY();
	    int x1 = x * 32 + 8;
	    int y1 = y * 32 + 8;
	    int x2 = (x + UnitType.Terran_Bunker.tileSize().getX()) * 32 - 8;
	    int y2 = (y + UnitType.Terran_Bunker.tileSize().getY()) * 32 - 8;
	    MyBotModule.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Green, false);
	    MyBotModule.Broodwar.drawTextMap(x1 + 5, y1 + 2, "Bunker  " + sequence);
	}

	//터렛 건설 위치
	tilePositionList = locationManager.getBaseTurret();
	sequence = 0;
	for (TilePosition tilePosition : tilePositionList) {
	    sequence++;
	    int x = tilePosition.getX();
	    int y = tilePosition.getY();
	    int x1 = x * 32 + 8;
	    int y1 = y * 32 + 8;
	    int x2 = (x + UnitType.Terran_Missile_Turret.tileSize().getX()) * 32 - 8;
	    int y2 = (y + UnitType.Terran_Missile_Turret.tileSize().getY()) * 32 - 8;
	    MyBotModule.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Green, false);
	    MyBotModule.Broodwar.drawTextMap(x1 + 5, y1 + 2, "Turret " + sequence);
	}

	//가스 건설 위치
	tilePositionList = locationManager.getBaseRefinery();
	sequence = 0;
	for (TilePosition tilePosition : tilePositionList) {
	    sequence++;
	    int x = tilePosition.getX();
	    int y = tilePosition.getY();
	    int x1 = x * 32 + 8;
	    int y1 = y * 32 + 8;
	    int x2 = (x + UnitType.Terran_Refinery.tileSize().getX()) * 32 - 8;
	    int y2 = (y + UnitType.Terran_Refinery.tileSize().getY()) * 32 - 8;
	    MyBotModule.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Green, false);
	    MyBotModule.Broodwar.drawTextMap(x1 + 5, y1 + 2, "Refinery  " + sequence);
	}

	//베이스 초크포인트 위치
	TilePosition tilePos = locationManager.getBaseEntranceChokePoint();
	MyBotModule.Broodwar.drawCircleMap(tilePos.getX() * 32, tilePos.getY() * 32, 30, Color.Red);
	MyBotModule.Broodwar.drawTextMap(tilePos.getX() * 32 - 40, tilePos.getY() * 32 - 7, "base ent choke point");

	//확장 초크포인트 위치
	tilePos = locationManager.getFirstExtensionChokePoint();
	MyBotModule.Broodwar.drawCircleMap(tilePos.getX() * 32, tilePos.getY() * 32, 30, Color.Red);
	MyBotModule.Broodwar.drawTextMap(tilePos.getX() * 32 - 40, tilePos.getY() * 32 - 7, "first ex choke point");

	//확장 초크포인트 위치
	tilePos = locationManager.getSecondExtensionChokePoint();
	MyBotModule.Broodwar.drawCircleMap(tilePos.getX() * 32, tilePos.getY() * 32, 200, Color.Red);
	MyBotModule.Broodwar.drawTextMap(tilePos.getX() * 32 - 40, tilePos.getY() * 32 - 7, "Second ex choke point");
	
//	//다음 확장 위치
//	tilePos = strategyManager.getNextExpansionPoint();
//	MyBotModule.Broodwar.drawCircleMap(tilePos.getX() * 32, tilePos.getY() * 32, 100, Color.Blue);
//	MyBotModule.Broodwar.drawTextMap(tilePos.getX() * 32 - 40, tilePos.getY() * 32 - 7, "Second ex choke point");

	//확장 터렛 건설 위치
	tilePositionList = locationManager.getFirstExpansionTurret();
	sequence = 0;
	for (TilePosition tilePosition : tilePositionList) {
	    sequence++;
	    int x = tilePosition.getX();
	    int y = tilePosition.getY();
	    int x1 = x * 32 + 8;
	    int y1 = y * 32 + 8;
	    int x2 = (x + UnitType.Terran_Missile_Turret.tileSize().getX()) * 32 - 8;
	    int y2 = (y + UnitType.Terran_Missile_Turret.tileSize().getY()) * 32 - 8;
	    MyBotModule.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Green, false);
	    MyBotModule.Broodwar.drawTextMap(x1 + 5, y1 + 2, "ExTurret " + sequence);
	}

	//엔지니어링 베이 위치
	tilePositionList = locationManager.getFirstExpansionLocation();
	for (TilePosition tilePosition : tilePositionList) {
	    int x = tilePosition.getX();
	    int y = tilePosition.getY();
	    int x1 = x * 32 + 8;
	    int y1 = y * 32 + 8;
	    int x2 = (x + UnitType.Terran_Command_Center.tileSize().getX()) * 32 - 8;
	    int y2 = (y + UnitType.Terran_Command_Center.tileSize().getY()) * 32 - 8;
	    MyBotModule.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Blue, false);
	    MyBotModule.Broodwar.drawTextMap(x1 + 5, y1 + 2, "First Expansion Location");
	}

	//배럭 위치
	tilePositionList = locationManager.getEntranceBuilding();
	for (TilePosition tilePosition : tilePositionList) {
	    int x = tilePosition.getX();
	    int y = tilePosition.getY();
	    int x1 = x * 32 + 8;
	    int y1 = y * 32 + 8;
	    int x2 = (x + UnitType.Terran_Barracks.tileSize().getX()) * 32 - 8;
	    int y2 = (y + UnitType.Terran_Barracks.tileSize().getY()) * 32 - 8;
	    MyBotModule.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Red, false);
	    MyBotModule.Broodwar.drawTextMap(x1 + 5, y1 + 2, "Entrance Building");
	}

	//배럭 위치
	tilePositionList = locationManager.getSecondEntranceBuilding();
	for (TilePosition tilePosition : tilePositionList) {
	    int x = tilePosition.getX();
	    int y = tilePosition.getY();
	    int x1 = x * 32 + 8;
	    int y1 = y * 32 + 8;
	    int x2 = (x + UnitType.Terran_Barracks.tileSize().getX()) * 32 - 8;
	    int y2 = (y + UnitType.Terran_Barracks.tileSize().getY()) * 32 - 8;
	    MyBotModule.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Red, false);
	    MyBotModule.Broodwar.drawTextMap(x1 + 5, y1 + 2, "Second Entrance Building");
	}

	//확장 터렛 건설 위치
	tilePositionList = locationManager.getBaseTankPoint();
	sequence = 0;
	for (TilePosition tilePosition : tilePositionList) {
	    sequence++;
	    int x = tilePosition.getX();
	    int y = tilePosition.getY();
	    int x1 = x * 32 + 8;
	    int y1 = y * 32 + 8;
	    int x2 = (x + UnitType.Terran_Missile_Turret.tileSize().getX()) * 32 - 8;
	    int y2 = (y + UnitType.Terran_Missile_Turret.tileSize().getY()) * 32 - 8;
	    MyBotModule.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Red, false);
	    MyBotModule.Broodwar.drawTextMap(x1 + 5, y1 + 2, "Tank " + sequence);
	}

	for (BaseLocation targetBaseLocation : BWTA.getStartLocations()) {
	    TilePosition tt = targetBaseLocation.getTilePosition();
	    int x = tt.getX();
	    int y = tt.getY();
	    int x1 = x * 32 + 8;
	    int y1 = y * 32 + 8;
	    int x2 = (x + UnitType.Terran_Command_Center.tileSize().getX()) * 32 - 8;
	    int y2 = (y + UnitType.Terran_Command_Center.tileSize().getY()) * 32 - 8;
	    MyBotModule.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Purple, false);
	    MyBotModule.Broodwar.drawTextMap(x1 + 5, y1 + 2, "Expansion " + sequence);
	}

	//	System.out.println("적 본진으로부터의 거리 " + locationManager.getEnemyStartLocation().getDistance(locationManager.getAllianceBaseLocation()));
	//서킷브레이커 가로전 110.0
	//서킷브레이커 대각전 154.8
	//서킷브레이커 세로전 109.0

	//		double tempDistance;
	//		double sourceDistance;
	//		double closestDistance = 1000000000;
	//		TilePosition sourceBaseLocation = locationManager.getFirstExpansionLocation().get(0);
	//		TilePosition enemyBaseLocation = locationManager.getEnemyStartLocation();
	//		BaseLocation base = null;
	//	
	//		for (BaseLocation targetBaseLocation : BWTA.getBaseLocations()) {
	//		    TilePosition tt = targetBaseLocation.getTilePosition();
	//	
	//		    if (tt.equals(locationManager.allianceBaseLocation))
	//			continue;
	//		    if (tt.equals(locationManager.getFirstExpansionLocation().get(0)))
	//			continue;
	//		    if (tt.equals(locationManager.enemyStartLocation))
	//			continue;
	//		    if (tt.equals(locationManager.getEnemyFirstExpansionLocation().get(0)))
	//			continue;
	//	
	//		    //건물이 이미 지어져 있는곳 패스
	//		    if (hasBuildingAroundBaseLocation(tt, 350) == true) {
	//			continue;
	//		    }
	//	
	//		    int x = tt.getX();
	//		    int y = tt.getY();
	//		    int x1 = x * 32 + 8;
	//		    int y1 = y * 32 + 8;
	//		    int x2 = (x + UnitType.Terran_Command_Center.tileSize().getX()) * 32 - 8;
	//		    int y2 = (y + UnitType.Terran_Command_Center.tileSize().getY()) * 32 - 8;
	//		    MyBotModule.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Cyan, false);
	//		    MyBotModule.Broodwar.drawTextMap(x1 + 5, y1 + 2, "Expansionsss " + sequence);
	//	
	//		    System.out.println("확장 위치 정보 " + targetBaseLocation.getTilePosition().getX() + " : " + targetBaseLocation.getTilePosition().getY());
	//		    sourceDistance = sourceBaseLocation.getDistance(targetBaseLocation.getTilePosition());
	//		    System.out.println("내 확장으로부터의 거리" + sourceDistance);
	//		    tempDistance = sourceDistance - enemyBaseLocation.getDistance(targetBaseLocation.getTilePosition());
	//		    System.out.println("적 본진으로부터의 거리 " + enemyBaseLocation.getDistance(targetBaseLocation.getTilePosition()));
	//		    System.out.println("총점수 " + tempDistance);
	//	
	//		    if (tempDistance < closestDistance && sourceDistance > 0) {
	//			closestDistance = tempDistance;
	//			base = targetBaseLocation;
	//		    }
	//		}
	//	
	//		TilePosition wow = base.getTilePosition();
	//		int x = wow.getX();
	//		int y = wow.getY();
	//		int x1 = x * 32 + 8;
	//		int y1 = y * 32 + 8;
	//		int x2 = (x + UnitType.Terran_Command_Center.tileSize().getX()) * 32 - 8;
	//		int y2 = (y + UnitType.Terran_Command_Center.tileSize().getY()) * 32 - 8;
	//		MyBotModule.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Red, false);
	//		MyBotModule.Broodwar.drawTextMap(x1 + 5, y1 + 2, "Expansionsss " + sequence);

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