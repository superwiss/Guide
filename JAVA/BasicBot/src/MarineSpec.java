import bwapi.UnitType;

public class MarineSpec implements UnitSpec {

    private int closeDistance = 70;
    private int farDistance = 20;

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

    @Override
    public int getCloseDistance() {
	return closeDistance;
    }

    @Override
    public int getFarDistance() {
	return getWeaponMaxRange() + farDistance;
    }
}
