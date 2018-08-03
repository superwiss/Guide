public enum StrategyItem {
    // 벙커가 있으면 마린을 벙커에 집어 넣는다.
    AUTO_TRAIN_SCV, AUTO_TRAIN_BIONIC_UNIT, // 자동으로 바이오닉 유닛을  훈련한다.
    AUTO_TRAIN_MECHANIC_UNIT, // 자동으로 메카닉 유닛을  훈련한다.
    AUTO_UPGRADE_BIONIC_UNIT, // 자동으로 바이오닉 유닛의 공방업 업그레이드를 한다.
    AUTO_BALANCE_SCV, // 자동으로 미네랄 일꾼의 밸런스를 맞춘다.
    AUTO_ASSIGN_GAS_SCV, BLOCK_ENTRANCE, AUTO_BUILD_TWO_ARMORY, AUTO_UPGRADE_MECHANIC_UNIT,

    AUTO_RESEARCH_U_238_Shells, // 자동으로 마린 사거리 리서치를 한다.
    AUTO_RESEARCH_STIMPACK, // 자동으로 스팀팩 리서치를 한다.
    AUTO_RESEARCH_ION_THRUSTERS, AUTO_RESEARCH_CHARON_BOOSTERS, AUTO_RESEARCH_SPIDER_MINES, AUTO_RESEARCH_SIEGE_MODE,

    AUTO_ADDON_COMSAT_STATION, // 자동으로 컴샛 스테이션 애드온을 단다.
    AUTO_ADDON_MACHINE_SHOP, // 자동으로 머신샵 애드온을 단다.

    AUTO_USING_SCAN, // 자동으로 스캔을 사용한다.

    AUTO_LIFT_COMMAND_CENTER, // 자동으로 커맨드 센터를 띄운다.

    AUTO_LOAD_MARINE_TO_BUNKER, // 자동으로 마린이 벙커로 들어간다.
    AUTO_REPAIR_BUNKER, // 자동으로 벙커를 수리한다.
    AUTO_DEFENCE_ALLIANCE_BASE, // 본진을 공격한 적 유닛을 자동으로 공격한다.
    AUTO_BUILD_SUPPLY, // 자동으로 서플라이 디팟을 건설한다.
    AUTO_TRAIN_VULTURE, // 자동으로 벌쳐를 생성한다.
    AUTO_TRAIN_TANK, // 자동으로 탱크를 생성한다.
    AUTO_BUILD_FACTORY, // 자동으로 팩토리를 늘린다.
    AUTO_EXTENSION, // 자동으로 확장한다.
    AUTO_REBALANCE_WORKER, // 자동으로 일꾼을 재분배 한다.

    ASSEMBLE_FIRST_CHOCKPOINT, // 아군 앞마당에 병력을 집결한다.
    ASSEMBLE_FOR_BLOCKING_ENEMY, // 적 입구를 조이면서 적이 공격해 오기를 대기한다.

    AGGRESSIVE_MOVE_ATTACK, // 공격 후 쿨 타임 차는 동안 Move로 적 방향으로 이동한다.
    SET_BARRACKS_RALLY, // 자동으로 배럭의 랠리 포인트가 아군의 FirstChokePoint로 찍한다.
    SET_FACTORY_RALLY,

    ALLOW_PHASE,
    USE_SCIENCE_VESSEL,

    AUTO_BUILD_EXPANSION // 자동으로 확장을 건설한다.
}
