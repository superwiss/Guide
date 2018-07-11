import java.util.HashSet;
import java.util.Set;

import bwapi.UnitType;

// 매딕을 컨트롤 한다.
public class MagiMicroControlMarine extends Manager {
    private static MagiMicroControlMarine instance = new MagiMicroControlMarine();

    public static MagiMicroControlMarine Instance() {
	return instance;
    }

    private LocationManager locationManager = null;;
    private final Set<UnitType> medicUnitTypeSet = new HashSet<>();

    public MagiMicroControlMarine() {
	medicUnitTypeSet.add(UnitType.Terran_Medic);
    }

    @Override
    protected void onStart(GameStatus gameStatus) {
	locationManager = gameStatus.getLocationManager();
	super.onStart(gameStatus);
    }

    @Override
    protected void onFrame() {
	super.onFrame();

	// 메딕은 42프레임(1초)에 1번만 컨트롤 한다.
	if (0 == gameStatus.getFrameCount() || gameStatus.getFrameCount() % 42 != 0) {
	    return;
	}
    }
}