/// 게임 초반에 일꾼 유닛 중에서 정찰 유닛을 하나 지정하고, 정찰 유닛을 이동시켜 정찰을 수행하는 class<br>
/// 적군의 BaseLocation 위치를 알아내는 것까지만 개발되어있습니다
public class MagiScoutManager {

    private static MagiScoutManager instance = new MagiScoutManager();

    /// static singleton 객체를 리턴합니다
    public static MagiScoutManager Instance() {
	return instance;
    }

    public void onFrame(GameData gameData) {
	// TODO Auto-generated method stub

    }
}