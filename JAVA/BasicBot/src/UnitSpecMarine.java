import java.util.Random;

import bwapi.UnitType;

public class UnitSpecMarine implements UnitSpec {

    private static UnitSpecMarine instance = new UnitSpecMarine();

    public static UnitSpecMarine Instance() {
	return instance;
    }

    private boolean isRandomSpec = false;
    private int randomSameDirectionCloseDistance = 0;
    private int randomSameDirectionFarDistance = 0;
    private int randomDifferenceDirectionCloseDistance = 0;
    private int randomDifferenceDirectionFarDistance = 0;
    private int randomNearMoveDistance = 0;

    public UnitSpecMarine() {
	if (true == isRandomSpec) {
	    Random random = new Random();
	    randomSameDirectionCloseDistance = random.nextInt(41) - 20;
	    randomSameDirectionFarDistance = random.nextInt(41) - 20;
	    randomDifferenceDirectionCloseDistance = random.nextInt(41) - 20;
	    randomDifferenceDirectionFarDistance = random.nextInt(41) - 20;
	    randomNearMoveDistance = random.nextInt(41) - 20;
	}
    }

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
	return UnitType.Terran_Marine.groundWeapon().maxRange() - 20 + randomSameDirectionCloseDistance;
    }

    @Override
    public int getSameDirectionFarDistance() {
	return UnitType.Terran_Marine.groundWeapon().maxRange() + 80 + randomSameDirectionFarDistance;
    }

    @Override
    public int getDifferenceDirectionCloseDistance() {
	return 20 + randomDifferenceDirectionCloseDistance;
    }

    @Override
    public int getDifferenceDirectionFarDistance() {
	return 50 + randomDifferenceDirectionFarDistance;
    }

    @Override
    public int getNearMoveDistance() {
	return UnitType.Terran_Marine.groundWeapon().maxRange() + 80 + randomNearMoveDistance;
    }
}
