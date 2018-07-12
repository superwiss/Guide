// Naming Rule이 이상하지만, BWAPI의 규칙을 따라서 Camel표기법 + _(언더바)를 조합해서 사용한다.
// Ex) 테란 마린의 경우, terranMarine(Camel 표기법)도 아니고 terran_marine도 아니고 Terran_Marine로 사용한다.
public enum UnitKind {
    Worker, // SCV, Probe, Drone

    Worker_Gather_Gas, // 가스를 캐는 일꾼

    Combat_Unit, // 공격 유닛 (일꾼, 옵저버, 오버로드, 라바, 에그 등을 제외한 공격,마법 유닛)

    Bionic_Unit, // 바이오닉 유닛

    Clocked, // 클로킹 유닛

    Scouting_Unit, // 정찰 유닛

    Building, // 건물

    MAIN_BUILDING, // 커맨드 센터, 넥서스, 해쳐리 류의 빌딩들...

    // /////////////////////////////////////////////////////////////////////////
    // UnitType을 래핑했다.
    // /////////////////////////////////////////////////////////////////////////

    // Terran
    Terran_Firebat, Terran_Ghost, Terran_Goliath, Terran_Marine, Terran_Medic, Terran_SCV, Terran_Siege_Tank_Siege_Mode, Terran_Siege_Tank_Tank_Mode, Terran_Vulture, Terran_Vulture_Spider_Mine,

    Terran_Battlecruiser, Terran_Dropship, Terran_Nuclear_Missile, Terran_Science_Vessel, Terran_Valkyrie, Terran_Wraith,

    Terran_Academy, Terran_Armory, Terran_Barracks, Terran_Bunker, Terran_Command_Center, Terran_Engineering_Bay, Terran_Factory, Terran_Missile_Turret, Terran_Refinery, Terran_Science_Facility, Terran_Starport, Terran_Supply_Depot,

    Terran_Comsat_Station, Terran_Control_Tower, Terran_Covert_Ops, Terran_Machine_Shop, Terran_Nuclear_Silo, Terran_Physics_Lab,

    // Protoss
    Protoss_Archon, Protoss_Dark_Archon, Protoss_Dark_Templar, Protoss_Dragoon, Protoss_High_Templar, Protoss_Probe, Protoss_Reaver, Protoss_Scarab, Protoss_Zealot,

    Protoss_Arbiter, Protoss_Carrier, Protoss_Corsair, Protoss_Interceptor, Protoss_Observer, Protoss_Scout, Protoss_Shuttle,

    Protoss_Arbiter_Tribunal, Protoss_Assimilator, Protoss_Citadel_of_Adun, Protoss_Cybernetics_Core, Protoss_Fleet_Beacon, Protoss_Forge, Protoss_Gateway, Protoss_Nexus, Protoss_Observatory, Protoss_Photon_Cannon, Protoss_Pylon, Protoss_Robotics_Facility, Protoss_Robotics_Support_Bay, Protoss_Shield_Battery, Protoss_Stargate, Protoss_Templar_Archives,

    // Zerg
    Zerg_Broodling, Zerg_Defiler, Zerg_Drone, Zerg_Egg, Zerg_Hydralisk, Zerg_Infested_Terran, Zerg_Larva, Zerg_Lurker, Zerg_Lurker_Egg, Zerg_Ultralisk, Zerg_Zergling,

    Zerg_Cocoon, Zerg_Devourer, Zerg_Guardian, Zerg_Mutalisk, Zerg_Overlord, Zerg_Queen, Zerg_Scourge,

    Zerg_Creep_Colony, Zerg_Defiler_Mound, Zerg_Evolution_Chamber, Zerg_Extractor, Zerg_Greater_Spire, Zerg_Hatchery, Zerg_Hive, Zerg_Hydralisk_Den, Zerg_Infested_Command_Center, Zerg_Lair, Zerg_Nydus_Canal, Zerg_Queens_Nest, Zerg_Spawning_Pool, Zerg_Spire, Zerg_Spore_Colony, Zerg_Sunken_Colony, Zerg_Ultralisk_Cavern,

    // Resources
    Resource_Mineral_Field, Resource_Vespene_Geyser;

}
