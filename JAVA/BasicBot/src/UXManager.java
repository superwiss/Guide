import java.util.List;

import bwapi.Color;
import bwapi.TilePosition;
import bwapi.UnitType;

/// 봇 프로그램 개발의 편의성 향상을 위해 게임 화면에 추가 정보들을 표시하는 class<br>
/// 여러 Manager 들로부터 정보를 조회하여 Screen 혹은 Map 에 정보를 표시합니다
public class UXManager {

    private static UXManager instance = new UXManager();

    private LocationManager locationManager = null;

    /// static singleton 객체를 리턴합니다
    public static UXManager Instance() {
	return instance;
    }

    /// 경기가 시작될 때 일회적으로 추가 정보를 출력합니다
    public void onStart(GameStatus gameStatus) {
	locationManager = gameStatus.getLocationManager();
    }

    /// 경기 진행 중 매 프레임마다 추가 정보를 출력하고 사용자 입력을 처리합니다
    public void update() {

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
	tilePositionList = locationManager.getEngineeringBay();
	sequence = 0;
	for (TilePosition tilePosition : tilePositionList) {
	    sequence++;
	    int x = tilePosition.getX();
	    int y = tilePosition.getY();
	    int x1 = x * 32 + 8;
	    int y1 = y * 32 + 8;
	    int x2 = (x + UnitType.Terran_Engineering_Bay.tileSize().getX()) * 32 - 8;
	    int y2 = (y + UnitType.Terran_Engineering_Bay.tileSize().getY()) * 32 - 8;
	    MyBotModule.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Green, false);
	    MyBotModule.Broodwar.drawTextMap(x1 + 5, y1 + 2, "Engineering  " + sequence);
	}
    }
}