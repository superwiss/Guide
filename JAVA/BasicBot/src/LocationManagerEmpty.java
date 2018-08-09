import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bwapi.TilePosition;

// 투혼 맵
public class LocationManagerEmpty extends LocationManager {

    @Override
    public List<TilePosition> initBaseLocations() {
	return new ArrayList<>();
    }

    // 정찰할 위치(순서)를 설정한다.
    @Override
    public List<TilePosition> initSearchSequence() {
	return new ArrayList<>();
    }

    // 배럭, 팩토리, 스타포트와 같은 병력 훈련용 타일의 위치를 지정한다. Add on 건물 위치까지 고려해야 한다.
    @Override
    public List<TilePosition> initTrainingBuildings() {
	return new ArrayList<>();
    }

    // 본진 입구 벙커를 지을 위치를 설정한다.
    @Override
    public List<TilePosition> initBaseEntranceBunker() {
	return new ArrayList<>();
    }

    // 3*2 사이즈 건물을 짓기 위한 위치들을 설정한다. (서플라이 디팟, 아마데미 등)
    public List<TilePosition> init3by2SizeBuildings() {
	return new ArrayList<>();
    }

    // 본진 가스를 짓기 위한 위치를 설정한다.
    @Override
    public List<TilePosition> initBaseRefinery() {
	return new ArrayList<>();
    }

    // 본진에 위치한 터렛의 위치를 설정한다.
    @Override
    public List<TilePosition> initBaseTurret() {
	return new ArrayList<>();
    }

    // 첫번째 확장에 위치한 터렛의 위치를 설정한다.
    @Override
    public List<TilePosition> initFirstExpansionTurret() {
	return new ArrayList<>();
    }

    // 본진 입구 방어를 위한 위치를 설정한다.
    @Override
    public TilePosition initBaseEntranceChokePoint() {
	return new TilePosition(0, 0);
    }

    // 엔지니어링 베이의 위치를 설정한다.
    @Override
    public List<TilePosition> initEngineeringBay() {
	return new ArrayList<>();
    }

    // 앞마당 입구 방어를 위한 위치를 설정한다.
    @Override
    public TilePosition initFirstExtensionChokePoint() {
	return new TilePosition(0, 0);
    }

    @Override
    public Set<TilePosition> getHillTilePosition() {
	return new HashSet<>();
    }

    @Override
    // 조이기 상태에서 적을 기다리고 있을 위치를 리턴한다.
    public TilePosition getBlockingChokePoint() {
	return null;
    }

    @Override
    public List<TilePosition> getExtentionPosition() {
	List<TilePosition> result = new ArrayList<>();

	return result;
    }

    @Override
    public List<TilePosition> getBlockingEntranceBuilding() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<TilePosition> getSecondEntranceBuilding() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<TilePosition> getEnemyBaseSearchSequence() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<TilePosition> getMineralExpansion() {
	// TODO Auto-generated method stub
	return null;
    }
}