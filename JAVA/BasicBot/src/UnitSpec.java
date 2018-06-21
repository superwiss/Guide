public interface UnitSpec {
    // 시야
    public int getSight();

    // 이 거리보다 적이 가까이 있으면 전투에 참가한다.
    public int getCombatDistance();

    // 공격 가능 범위
    public int getWeaponMaxRange();

    // 지상 무기 공격력
    public int getGroundWeaponDamage();

    // 사거리 - colseDistance (적과 거리가 너무 가깝다는 의미)
    public int getCloseDistance();

    // 사거리 + farDistance (적과 거리가 너무 멀다는 의미)
    public int getFarDistance();
}
