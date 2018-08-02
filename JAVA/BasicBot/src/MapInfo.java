import java.util.List;
import java.util.Set;

import bwapi.TilePosition;

public interface MapInfo {

    // Map 이름에 대한 Setter
    abstract void setMapName(String mapName);

    // Map 이름에 대한 Getter
    abstract String getMapName();

    // 스타팅 위치를 리턴한다. 12시 위치부터 시계 방향으로 본진 위치를 리턴한다.
    abstract List<TilePosition> initBaseLocations();

    // index 번째 스타팅 위치를 리턴한다.
    abstract TilePosition getBaseLocations(int index);

    // CommandCenter를 기준으로 아군 본진이 위치를 계산한다.
    abstract void init(Unit2 commandCenter);

    // 아군 본진 위치를 리턴
    abstract TilePosition getAllianceBaseLocation();

    // 아군 첫번째 확장 위치를 리턴
    abstract List<TilePosition> getFirstExpansionLocation();
    
    // 적군 첫번째 확장 위치를 리턴
    abstract List<TilePosition> getEnemyFirstExpansionLocation();

    // 적군 본진 위치에 대한 Getter
    abstract TilePosition getEnemyStartLocation();

    // 적군 본진 위치에 대한 Setter
    abstract void setEnemyStartLocation(TilePosition enemyBaseLocation);

    // 정찰할 위치(순서)를 리턴한다.
    abstract List<TilePosition> getSearchSequence();
    
    // 적기지 가장자리의 정찰할 위치를 리턴한다.
    abstract List<TilePosition> getEnemyBaseSearchSequence();

    // 정찰할 위치(순서)를 설정한다.
    abstract List<TilePosition> initSearchSequence();
    
    // 적기지 가장자리의 정찰 위치를 설정한다.
    abstract List<TilePosition> initEnemyBaseSearchSequence();

    // 배럭, 팩토리, 스타포트와 같은 병력 훈련용 타일의 위치를 지정한다. Add on 건물 위치까지 고려해야 한다.
    abstract List<TilePosition> initTrainingBuildings();

    // 배럭, 팩토리, 스타포트와 같은 병력 훈련용 타일의 위치를 리턴한다.
    abstract List<TilePosition> getTrainingBuildings();

    abstract List<TilePosition> initEntranceBuildings();
    
    abstract List<TilePosition> getEntranceBuilding();
    
    abstract List<TilePosition> initSecondEntranceBuildings();
    
    abstract List<TilePosition> getSecondEntranceBuilding();
    
    // 본진 입구 벙커를 지을 위치를 설정한다.
    abstract List<TilePosition> initBaseEntranceBunker();

    // 본진 입구 벙커를 지을 위치를 리턴한다.
    abstract List<TilePosition> getBaseEntranceBunker();
    
    // 본진 탱크를 배치할 위치를 리턴한다.
    abstract List<TilePosition> getBaseTankPoint();
    
    // 3*2 사이즈 건물을 짓기 위한 위치들을 설정한다. (서플라이 디팟, 아마데미 등)
    abstract List<TilePosition> init3by2SizeBuildings();

    // 3*2 사이즈 건물을 짓기 위한 위치들을 리턴한다. (서플라이 디팟, 아마데미 등)
    abstract List<TilePosition> get3by2SizeBuildings();

    // 본진 가스를 짓기 위한 위치를 설정한다.
    abstract List<TilePosition> initBaseRefinery();
    
    // 본진 탱크 배치를 위한 위치를 설정한다.
    abstract List<TilePosition> initBaseTankPoint();

    // 본진 가스를 짓기 위한 위치를 리턴한다.
    abstract List<TilePosition> getBaseRefinery();

    // 본진 입구에 위치한 터렛의 위치를 설정한다.
    abstract List<TilePosition> initBaseTurret();

    // 첫번째 확장에 위치한 터렛의 위치를 설정한다.
    abstract List<TilePosition> initFirstExpansionTurret();

    // 본진 입구에 위치한 터렛의 위치를 리턴한다.
    abstract List<TilePosition> getBaseTurret();

    // 첫번째 확장에 위치한 터렛의 위치를 리턴한다.
    abstract List<TilePosition> getFirstExpansionTurret();
    


    // 본진 입구 방어를 위한 위치를 설정한다.
    abstract TilePosition initBaseEntranceChokePoint();

    // 본진 입구 방어를 위한 위치를 리턴한다.
    abstract TilePosition getBaseEntranceChokePoint();

    // 앞마당 입구 방어를 위한 위치를 설정한다.
    abstract TilePosition initFirstExtensionChokePoint();
    
    // 두번째 확장  방어를 위한 위치를 설정한다.
    abstract TilePosition initSecondExtensionChokePoint();
    
    abstract TilePosition initTwoPhaseChokePoint();
    
    abstract TilePosition initThreePhaseChokePointForSiege();
    
    abstract TilePosition initThreePhaseChokePointForMech();
    
    // 앞마당 입구 방어를 위한 위치를 리턴한다.
    abstract TilePosition getFirstExtensionChokePoint();
    
    // 앞마당 입구 방어를 위한 위치를 리턴한다.
    abstract TilePosition getSecondExtensionChokePoint();
    
    abstract TilePosition getTwoPhaseChokePoint();
    
    abstract TilePosition getThreePhaseChokePointForSiege();
    
    abstract TilePosition getThreePhaseChokePointForMech();
    
    // 언덕의 타일을 리턴한다.
    abstract Set<TilePosition> getHillTilePosition();
}
