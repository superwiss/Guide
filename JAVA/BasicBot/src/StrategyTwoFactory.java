import java.util.Set;

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
    }

    @Override
    public void initialStrategyItem(Set<StrategyItem> strategyItems) {
	strategyItems.add(StrategyItem.AUTO_DEFENCE_ALLIANCE_BASE);
    }

}