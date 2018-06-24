public interface UnitSpec {
    // 시야
    public int getSight();

    // 이 거리보다 적이 가까이 있으면 전투에 참가한다.
    public int getCombatDistance();

    // 공격 가능 범위
    public int getWeaponMaxRange();

    // 지상 무기 공격력
    public int getGroundWeaponDamage();

    int getSameDirectionCloseDistance();

    int getSameDirectionFarDistance();

    int getDifferenceDirectionCloseDistance();

    int getDifferenceDirectionFarDistance();

    int getNearMoveDistance();
}
