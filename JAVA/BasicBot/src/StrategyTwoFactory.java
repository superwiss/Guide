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

	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 5
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 6
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 7
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 8
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 9
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 10
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 11
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Refinery));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 12
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 13
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.SCOUTING));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 14
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.GATHER_GAS));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.GATHER_GAS));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 15
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine)); // 16
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 17
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Marine)); // 18
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 19
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Factory));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 20
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.ADD_ON, UnitType.Terran_Machine_Shop));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 21
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Siege_Tank_Tank_Mode)); // 23
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 24
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.ADD_ON, UnitType.Terran_Machine_Shop));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_SCV)); // 25
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Siege_Tank_Tank_Mode)); // 27
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Siege_Tank_Tank_Mode)); // 29
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.UPGRADE, UpgradeType.Ion_Thrusters));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.UPGRADE, TechType.Spider_Mines));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture)); // 31
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture)); // 33
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.TRAINING, UnitType.Terran_Vulture));
	buildManager.addLast(new BuildOrderItem(BuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
    }

    @Override
    public void initialStrategyItem(Set<StrategyItem> strategyItems) {
	// Do nothing
    }

}