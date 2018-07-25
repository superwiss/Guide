import java.util.Set;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class StrategyTwoFactory extends StrategyBase {

    private StrategyManager strategyManager = null;
    private LocationManager locationManager = null;

    public StrategyTwoFactory() {
	strategyName = "TowFactory";
    }

    @Override
    public void onStart(GameStatus gameStatus) {
	super.onStart(gameStatus);

	strategyManager = gameStatus.getStrategyManager();
	locationManager = gameStatus.getLocationManager();
    }

    @Override
    public void onFrame() {
	super.onFrame();
    }

    @Override
    public void initialBuildOrder() {
	// 초기 빌드 오더
	BuildManager buildManager = gameStatus.getBuildManager();

	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 5
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 6
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 7
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 8
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 9
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 10
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 11
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Refinery));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 12
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 13
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.SCOUTING));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 14
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.GATHER_GAS));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.GATHER_GAS));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 15
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine)); // 16
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 17
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine)); // 18
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 19
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 20
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.ADD_ON, UnitType.Terran_Machine_Shop));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 21
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Siege_Tank_Tank_Mode)); // 23
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 24
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.ADD_ON, UnitType.Terran_Machine_Shop));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 25
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Siege_Tank_Tank_Mode)); // 27
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Siege_Tank_Tank_Mode)); // 29
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.UPGRADE, UpgradeType.Ion_Thrusters));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.UPGRADE, TechType.Spider_Mines));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture)); // 31
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture)); // 33
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.add(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
    }

    @Override
    public void initialStrategyItem(Set<StrategyItem> strategyItems) {
	strategyItems.add(StrategyItem.AUTO_DEFENCE_ALLIANCE_BASE);
    }

}