import java.util.HashSet;
import java.util.Set;

import bwapi.Unit;
import bwapi.UnitType;

/// 상황을 판단하여, 정찰, 빌드, 공격, 방어 등을 수행하도록 총괄 지휘를 하는 class <br>
/// InformationManager 에 있는 정보들로부터 상황을 판단하고, <br>
/// BuildManager 의 buildQueue에 빌드 (건물 건설 / 유닛 훈련 / 테크 리서치 / 업그레이드) 명령을 입력합니다.<br>
/// 정찰, 빌드, 공격, 방어 등을 수행하는 코드가 들어가는 class
public class MagiStrategyManager {

    private static MagiStrategyManager instance = new MagiStrategyManager();

    /// static singleton 객체를 리턴합니다
    public static MagiStrategyManager Instance() {
	return instance;
    }

    private Set<StrategyItem> strategyItems = new HashSet<>();

    private MagiBuildManager buildManager = MagiBuildManager.Instance();

    public MagiStrategyManager() {
	strategyItems.add(StrategyItem.MARINE_INTO_BUNKER);
	strategyItems.add(StrategyItem.REPAIR_BUNKER);
    }

    /// 경기가 시작될 때 일회적으로 전략 초기 세팅 관련 로직을 실행합니다
    public void onStart() {
	defense6droneBuildOrder();
    }

    public void onFrame(GameData gameData) {
	UnitManager allianceUnitManager = gameData.getAllianceUnitManager();
	Set<Integer> bunkerSet = allianceUnitManager.getUnitsByUnitKind(UnitKind.Bunker);
	for (Integer bunkerId : bunkerSet) {
	    Unit bunker = allianceUnitManager.getUnit(bunkerId);
	    if (strategyItems.contains(StrategyItem.MARINE_INTO_BUNKER)) {
		if (0 < bunker.getSpaceRemaining()) {
		    marineToBunker(allianceUnitManager, bunker);
		}
	    }
	    if (strategyItems.contains(StrategyItem.REPAIR_BUNKER)) {
		Set<Integer> workerSet = allianceUnitManager.getUnitsByUnitKind(UnitKind.WORKER);
		int minDistance = Integer.MAX_VALUE;
		Unit targetWorker = null;
		for (Integer workerId : workerSet) {
		    Unit worker = allianceUnitManager.getUnit(workerId);
		    if (!allianceUnitManager.isinterruptableWorker(worker)) {
			// 동원 가능한 일꾼이 아니면 Skip한다.
			continue;
		    }
		    int distance = bunker.getDistance(worker);
		    if (distance < minDistance) {
			minDistance = distance;
			targetWorker = worker;
		    }
		}
		if (null != targetWorker) {
		    targetWorker.repair(bunker);
		}
	    }
	}
    }

    private void marineToBunker(UnitManager allianceUnitManager, Unit bunker) {
	Set<Integer> marineSet = allianceUnitManager.getUnitsByUnitKind(UnitKind.MARINE);
	int minDistance = Integer.MAX_VALUE;
	Unit targetMarine = null;
	for (Integer marineId : marineSet) {
	    Unit marine = allianceUnitManager.getUnit(marineId);
	    if (marine.isLoaded()) {
		// 벙커나 수송선에 타고 있으면 skip한다.
		continue;
	    }
	    int distance = bunker.getDistance(marine);
	    if (distance < minDistance) {
		minDistance = distance;
		targetMarine = marine;
	    }
	}
	if (null != targetMarine) {
	    targetMarine.load(bunker);
	}
    }

    ///  경기가 종료될 때 일회적으로 전략 결과 정리 관련 로직을 실행합니다
    public void onEnd(boolean isWinner) {
    }

    /// 경기 진행 중 매 프레임마다 경기 전략 관련 로직을 실행합니다
    public void update() {
    }

    public void defense6droneBuildOrder() {
	// 초기 빌드 오더
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.BUILD, UnitType.Terran_Barracks));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.BUILD, UnitType.Terran_Bunker));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_MARINE));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_MARINE));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.BUILD, UnitType.Terran_Supply_Depot));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_MARINE));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_MARINE));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
	buildManager.add(new MagiBuildOrderItem(MagiBuildOrderItem.Order.TRAINING_WORKER));
    }
}