
public enum EnemyUnitStatus {
    // 적 유닛의 이동 방향에 아군 유닛이 위치하고 있는데, 거리가 너무 가깝다.
    SAME_DIR_CLOSE,

    // 적 유닛의 이동 방향에 아군 유닛이 위치하고 있는데, 거리가 멀지도 가깝지도 않다.
    SAME_DIR_MIDDLE,

    // 적 유닛의 이동 방향에 아군 유닛이 위치하고 있는데, 거리가 멀다.
    SAME_DIR_FAR,

    // 적 유닛의 이동 방향에 아군 유닛이 없는데, 거리가 너무 가깝다.
    DIFFERENCE_DIR_CLOSE,

    // 적 유닛의 이동 방향에 아군 유닛이 없는데, 거리가 멀지도 가깝지도 않다.
    DIFFERENCE_DIR_MIDDLE,

    // 적 유닛의 이동 방향에 아군 유닛이 없는데, 거리가 멀다.
    DIFFERENCE_DIR_FAR,

    // 적이 아군 유닛 근처로 이동한다.
    NEAR_MOVE,

    // 상태를 알 수 없는 기타 상황.
    UNKNOWN;

}
