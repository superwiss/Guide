import bwapi.UnitType;

public class MarineSpec implements UnitSpec {

    @Override
    public int getSight() {
	return 268;
    }

    @Override
    public int getCombatDistance() {
	return 500;
    }

    @Override
    public int getWeaponMaxRange() {
	return UnitType.Terran_Marine.groundWeapon().maxRange();
    }

    @Override
    public int getGroundWeaponDamage() {
	return UnitType.Terran_Marine.groundWeapon().damageAmount();
    }

}
