import java.util.List;

import bwapi.TilePosition;
import bwapi.Unit;

public interface ILocation {

    void init(Unit commandCenter);

    List<TilePosition> getBarracks();

    List<TilePosition> getBunker();

    List<TilePosition> getSupplyDepot();

    List<TilePosition> getRefinery();

    List<TilePosition> getTurret();

    TilePosition getChokePoint1();

    TilePosition getChokePoint2();

    TilePosition getAllianceStartTilePosition();

    List<TilePosition> getSearchList();

    TilePosition getEnemyStartTilePosition();

    void setEnemyStartLocation(TilePosition enemyStartTilePosition);

}
