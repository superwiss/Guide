import bwapi.UnitType;

public class UnitSpecMarine implements UnitSpec {

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
    public int getSameDirectionCloseDistance() {
	return UnitType.Terran_Marine.groundWeapon().maxRange() - 20;
    }

    @Override
    public int getSameDirectionFarDistance() {
	return UnitType.Terran_Marine.groundWeapon().maxRange() + 80;
    }

    @Override
    public int getDifferenceDirectionCloseDistance() {
	return 20;
    }

    @Override
    public int getDifferenceDirectionFarDistance() {
	return 50;
    }

    @Override
    public int getNearMoveDistance() {
	return UnitType.Terran_Marine.groundWeapon().maxRange() + 80;
    }
}
