public enum StrategyStatus {
    ATTACK, // 공격 위치로 공격간다.
    FULLY_ATTACK, // 총 공격 상태이다.
    BACK_TO_BASE, // 적이 아군 본진으로 역습온 상태. 공격 나간 유닛을 아군 본진쪽으로 회군해야 한다.
    SEARCH_FOR_ELIMINATE // 적 건물을 찾을 수 없으므로, 맵 전체를 대상으로 아군 유닛들이 흩어져서 적 건물을 찾는다.
}
