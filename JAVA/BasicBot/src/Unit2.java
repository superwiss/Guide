import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bwapi.Order;
import bwapi.Player;
import bwapi.Position;
import bwapi.PositionOrUnit;
import bwapi.Region;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.UnitCommand;
import bwapi.UnitCommandType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import bwapi.WeaponType;

public class Unit2 {
    private static Map<Integer, Unit2> instances = new HashMap<>();

    private bwapi.Unit unit;

    public static Unit2 get(bwapi.Unit unit) {
	if (null == unit) {
	    return null;
	}
	Unit2 instance = instances.get(unit.getID());
	if (instance == null) {
	    instance = new Unit2(unit);
	    instances.put(unit.getID(), instance);
	}
	return instance;
    }

    private Unit2(bwapi.Unit unit) {
	this.unit = unit;
    }

    public static List<Unit2> get(List<bwapi.Unit> unitList) {
	List<Unit2> result = new ArrayList<>(unitList.size());
	for (bwapi.Unit unit : unitList) {
	    result.add(Unit2.get(unit));
	}
	return result;
    }

    private bwapi.Unit getRaw() {
	return unit;
    }

    @Override
    public boolean equals(Object that) {
	if (!(that instanceof Unit2)) {
	    return false;
	}
	return getID() == ((Unit2) that).getID();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	return prime * unit.getID();
    }

    @Override
    public String toString() {
	return "Unit[id=" + unit.getID() + "]";
    }

    // /////////////////////////////////////////////////////////////////////////
    // Unit은 상속이 안되서.. 노가다로 ㅠㅠ
    // /////////////////////////////////////////////////////////////////////////
    public int getID() {
	return unit.getID();
    }

    public boolean exists() {
	return unit.exists();
    }

    public int getReplayID() {
	return unit.getReplayID();
    }

    public Player getPlayer() {
	return unit.getPlayer();
    }

    public UnitType getType() {
	return unit.getType();
    }

    public Position getPosition() {
	return unit.getPosition();
    }

    public TilePosition getTilePosition() {
	return unit.getTilePosition();
    }

    public double getAngle() {
	return unit.getAngle();
    }

    public double getVelocityX() {
	return unit.getVelocityX();
    }

    public double getVelocityY() {
	return unit.getVelocityY();
    }

    public Region getRegion() {
	return unit.getRegion();
    }

    public int getLeft() {
	return unit.getLeft();
    }

    public int getTop() {
	return unit.getTop();
    }

    public int getRight() {
	return unit.getRight();
    }

    public int getBottom() {
	return unit.getBottom();
    }

    public int getHitPoints() {
	return unit.getHitPoints();
    }

    public int getShields() {
	return unit.getShields();
    }

    public int getEnergy() {
	return unit.getEnergy();
    }

    public int getResources() {
	return unit.getResources();
    }

    public int getResourceGroup() {
	return unit.getResourceGroup();
    }

    public int getDistance(Position target) {
	return unit.getDistance(target);
    }

    public int getDistance(Unit2 target) {
	return unit.getDistance(target.getRaw());
    }

    public int getDistance(PositionOrUnit target) {
	return unit.getDistance(target);
    }

    public boolean hasPath(Position target) {
	return unit.hasPath(target);
    }

    public boolean hasPath(Unit2 target) {
	return unit.hasPath(target.getRaw());
    }

    public boolean hasPath(PositionOrUnit target) {
	return unit.hasPath(target);
    }

    public int getLastCommandFrame() {
	return unit.getLastCommandFrame();
    }

    public UnitCommand getLastCommand() {
	return unit.getLastCommand();
    }

    public Player getLastAttackingPlayer() {
	return unit.getLastAttackingPlayer();
    }

    public UnitType getInitialType() {
	return unit.getInitialType();
    }

    public Position getInitialPosition() {
	return unit.getInitialPosition();
    }

    public TilePosition getInitialTilePosition() {
	return unit.getInitialTilePosition();
    }

    public int getInitialHitPoints() {
	return unit.getInitialHitPoints();
    }

    public int getInitialResources() {
	return unit.getInitialResources();
    }

    public int getKillCount() {
	return unit.getKillCount();
    }

    public int getAcidSporeCount() {
	return unit.getAcidSporeCount();
    }

    public int getInterceptorCount() {
	return unit.getInterceptorCount();
    }

    public int getScarabCount() {
	return unit.getScarabCount();
    }

    public int getSpiderMineCount() {
	return unit.getSpiderMineCount();
    }

    public int getGroundWeaponCooldown() {
	return unit.getGroundWeaponCooldown();
    }

    public int getAirWeaponCooldown() {
	return unit.getAirWeaponCooldown();
    }

    public int getSpellCooldown() {
	return unit.getSpellCooldown();
    }

    public int getDefenseMatrixPoints() {
	return unit.getDefenseMatrixPoints();
    }

    public int getDefenseMatrixTimer() {
	return unit.getDefenseMatrixTimer();
    }

    public int getEnsnareTimer() {
	return unit.getEnsnareTimer();
    }

    public int getIrradiateTimer() {
	return unit.getIrradiateTimer();
    }

    public int getLockdownTimer() {
	return unit.getLockdownTimer();
    }

    public int getMaelstromTimer() {
	return unit.getMaelstromTimer();
    }

    public int getOrderTimer() {
	return unit.getOrderTimer();
    }

    public int getPlagueTimer() {
	return unit.getPlagueTimer();
    }

    public int getRemoveTimer() {
	return unit.getRemoveTimer();
    }

    public int getStasisTimer() {
	return unit.getStasisTimer();
    }

    public int getStimTimer() {
	return unit.getStimTimer();
    }

    public UnitType getBuildType() {
	return unit.getBuildType();
    }

    public List<UnitType> getTrainingQueue() {
	return unit.getTrainingQueue();
    }

    public TechType getTech() {
	return unit.getTech();
    }

    public UpgradeType getUpgrade() {
	return unit.getUpgrade();
    }

    public int getRemainingBuildTime() {
	return unit.getRemainingBuildTime();
    }

    public int getRemainingTrainTime() {
	return unit.getRemainingTrainTime();
    }

    public int getRemainingResearchTime() {
	return unit.getRemainingResearchTime();
    }

    public int getRemainingUpgradeTime() {
	return unit.getRemainingUpgradeTime();
    }

    public Unit2 getBuildUnit() {
	return Unit2.get(unit.getBuildUnit());
    }

    public Unit2 getTarget() {
	return Unit2.get(unit.getTarget());
    }

    public Position getTargetPosition() {
	return unit.getTargetPosition();
    }

    public Order getOrder() {
	return unit.getOrder();
    }

    public Order getSecondaryOrder() {
	return unit.getSecondaryOrder();
    }

    public Unit2 getOrderTarget() {
	return Unit2.get(unit.getOrderTarget());
    }

    public Position getOrderTargetPosition() {
	return unit.getOrderTargetPosition();
    }

    public Position getRallyPosition() {
	return unit.getRallyPosition();
    }

    public Unit2 getRallyUnit() {
	return Unit2.get(unit.getRallyUnit());
    }

    public Unit2 getAddon() {
	return Unit2.get(unit.getAddon());
    }

    public Unit2 getNydusExit() {
	return Unit2.get(unit.getNydusExit());
    }

    public Unit2 getPowerUp() {
	return Unit2.get(unit.getPowerUp());
    }

    public Unit2 getTransport() {
	return Unit2.get(unit.getTransport());
    }

    public List<Unit2> getLoadedUnits() {
	return Unit2.get(unit.getLoadedUnits());
    }

    public int getSpaceRemaining() {
	return unit.getSpaceRemaining();
    }

    public Unit2 getCarrier() {
	return Unit2.get(unit.getCarrier());
    }

    public List<Unit2> getInterceptors() {
	return Unit2.get(unit.getInterceptors());
    }

    public Unit2 getHatchery() {
	return Unit2.get(unit.getHatchery());
    }

    public List<Unit2> getLarva() {
	return Unit2.get(unit.getLarva());
    }

    public List<Unit2> getUnitsInRadius(int radius) {
	return Unit2.get(unit.getUnitsInRadius(radius));
    }

    public List<Unit2> getUnitsInWeaponRange(WeaponType weapon) {
	return Unit2.get(unit.getUnitsInWeaponRange(weapon));
    }

    public boolean hasNuke() {
	return unit.hasNuke();
    }

    public boolean isAccelerating() {
	return unit.isAccelerating();
    }

    public boolean isAttacking() {
	return unit.isAttacking();
    }

    public boolean isAttackFrame() {
	return unit.isAttackFrame();
    }

    public boolean isBeingConstructed() {
	return unit.isBeingConstructed();
    }

    public boolean isBeingGathered() {
	return unit.isBeingGathered();
    }

    public boolean isBeingHealed() {
	return unit.isBeingHealed();
    }

    public boolean isBlind() {
	return unit.isBlind();
    }

    public boolean isBraking() {
	return unit.isBraking();
    }

    public boolean isBurrowed() {
	return unit.isBurrowed();
    }

    public boolean isCarryingGas() {
	return unit.isCarryingGas();
    }

    public boolean isCarryingMinerals() {
	return unit.isCarryingMinerals();
    }

    public boolean isCloaked() {
	return unit.isCloaked();
    }

    public boolean isCompleted() {
	return unit.isCompleted();
    }

    public boolean isConstructing() {
	return unit.isConstructing();
    }

    public boolean isDefenseMatrixed() {
	return unit.isDefenseMatrixed();
    }

    public boolean isDetected() {
	return unit.isDetected();
    }

    public boolean isEnsnared() {
	return unit.isEnsnared();
    }

    public boolean isFlying() {
	return unit.isFlying();
    }

    public boolean isFollowing() {
	return unit.isFollowing();
    }

    public boolean isGatheringGas() {
	return unit.isGatheringGas();
    }

    public boolean isGatheringMinerals() {
	return unit.isGatheringMinerals();
    }

    public boolean isHallucination() {
	return unit.isHallucination();
    }

    public boolean isHoldingPosition() {
	return unit.isHoldingPosition();
    }

    public boolean isIdle() {
	return unit.isIdle();
    }

    public boolean isInterruptible() {
	return unit.isInterruptible();
    }

    public boolean isInvincible() {
	return unit.isInvincible();
    }

    public boolean isInWeaponRange(Unit2 target) {
	return unit.isInWeaponRange(target.getRaw());
    }

    public boolean isIrradiated() {
	return unit.isIrradiated();
    }

    public boolean isLifted() {
	return unit.isLifted();
    }

    public boolean isLoaded() {
	return unit.isLoaded();
    }

    public boolean isLockedDown() {
	return unit.isLockedDown();
    }

    public boolean isMaelstrommed() {
	return unit.isMaelstrommed();
    }

    public boolean isMorphing() {
	return unit.isMorphing();
    }

    public boolean isMoving() {
	return unit.isMoving();
    }

    public boolean isParasited() {
	return unit.isParasited();
    }

    public boolean isPatrolling() {
	return unit.isPatrolling();
    }

    public boolean isPlagued() {
	return unit.isPlagued();
    }

    public boolean isRepairing() {
	return unit.isRepairing();
    }

    public boolean isResearching() {
	return unit.isResearching();
    }

    public boolean isSelected() {
	return unit.isSelected();
    }

    public boolean isSieged() {
	return unit.isSieged();
    }

    public boolean isStartingAttack() {
	return unit.isStartingAttack();
    }

    public boolean isStasised() {
	return unit.isStasised();
    }

    public boolean isStimmed() {
	return unit.isStimmed();
    }

    public boolean isStuck() {
	return unit.isStuck();
    }

    public boolean isTraining() {
	return unit.isTraining();
    }

    public boolean isUnderAttack() {
	return unit.isUnderAttack();
    }

    public boolean isUnderDarkSwarm() {
	return unit.isUnderDarkSwarm();
    }

    public boolean isUnderDisruptionWeb() {
	return unit.isUnderDisruptionWeb();
    }

    public boolean isUnderStorm() {
	return unit.isUnderStorm();
    }

    public boolean isPowered() {
	return unit.isPowered();
    }

    public boolean isUpgrading() {
	return unit.isUpgrading();
    }

    public boolean isVisible() {
	return unit.isVisible();
    }

    public boolean isVisible(Player player) {
	return unit.isVisible(player);
    }

    public boolean isTargetable() {
	return unit.isTargetable();
    }

    public boolean issueCommand(UnitCommand command) {
	return unit.issueCommand(command);
    }

    public boolean attack(Position target) {
	return unit.attack(target);
    }

    public boolean attack(Unit2 target) {
	return unit.attack(target.getRaw());
    }

    public boolean attack(PositionOrUnit target) {
	return unit.attack(target);
    }

    public boolean attack(Position target, boolean shiftQueueCommand) {
	return unit.attack(target, shiftQueueCommand);
    }

    public boolean attack(Unit2 target, boolean shiftQueueCommand) {
	return unit.attack(target.getRaw(), shiftQueueCommand);
    }

    public boolean attack(PositionOrUnit target, boolean shiftQueueCommand) {
	return unit.attack(target, shiftQueueCommand);
    }

    public boolean build(UnitType type) {
	return unit.build(type);
    }

    public boolean build(UnitType type, TilePosition target) {
	return unit.build(type, target);
    }

    public boolean buildAddon(UnitType type) {
	return unit.buildAddon(type);
    }

    public boolean train() {
	return unit.train();
    }

    public boolean train(UnitType type) {
	return unit.train(type);
    }

    public boolean morph(UnitType type) {
	return unit.morph(type);
    }

    public boolean research(TechType tech) {
	return unit.research(tech);
    }

    public boolean upgrade(UpgradeType upgrade) {
	return unit.upgrade(upgrade);
    }

    public boolean setRallyPoint(Position target) {
	return unit.setRallyPoint(target);
    }

    public boolean setRallyPoint(Unit2 target) {
	return unit.setRallyPoint(target.getRaw());
    }

    public boolean setRallyPoint(PositionOrUnit target) {
	return unit.setRallyPoint(target);
    }

    public boolean move(Position target) {
	return unit.move(target);
    }

    public boolean move(Position target, boolean shiftQueueCommand) {
	return unit.move(target, shiftQueueCommand);
    }

    public boolean patrol(Position target) {
	return unit.patrol(target);
    }

    public boolean patrol(Position target, boolean shiftQueueCommand) {
	return unit.patrol(target, shiftQueueCommand);
    }

    public boolean holdPosition() {
	return unit.holdPosition();
    }

    public boolean holdPosition(boolean shiftQueueCommand) {
	return unit.holdPosition(shiftQueueCommand);
    }

    public boolean stop() {
	return unit.stop();
    }

    public boolean stop(boolean shiftQueueCommand) {
	return unit.stop(shiftQueueCommand);
    }

    public boolean follow(Unit2 target) {
	return unit.follow(target.getRaw());
    }

    public boolean follow(Unit2 target, boolean shiftQueueCommand) {
	return unit.follow(target.getRaw(), shiftQueueCommand);
    }

    public boolean gather(Unit2 target) {
	return unit.gather(target.getRaw());
    }

    public boolean gather(Unit2 target, boolean shiftQueueCommand) {
	return unit.gather(target.getRaw(), shiftQueueCommand);
    }

    public boolean returnCargo() {
	return unit.returnCargo();
    }

    public boolean returnCargo(boolean shiftQueueCommand) {
	return unit.returnCargo(shiftQueueCommand);
    }

    public boolean repair(Unit2 target) {
	return unit.repair(target.getRaw());
    }

    public boolean repair(Unit2 target, boolean shiftQueueCommand) {
	return unit.repair(target.getRaw(), shiftQueueCommand);
    }

    public boolean burrow() {
	return unit.burrow();
    }

    public boolean unburrow() {
	return unit.unburrow();
    }

    public boolean cloak() {
	return unit.cloak();
    }

    public boolean decloak() {
	return unit.decloak();
    }

    public boolean siege() {
	return unit.siege();
    }

    public boolean unsiege() {
	return unit.unsiege();
    }

    public boolean lift() {
	return unit.lift();
    }

    public boolean land(TilePosition target) {
	return unit.land(target);
    }

    public boolean load(Unit2 target) {
	return unit.load(target.getRaw());
    }

    public boolean load(Unit2 target, boolean shiftQueueCommand) {
	return unit.load(target.getRaw(), shiftQueueCommand);
    }

    public boolean unload(Unit2 target) {
	return unit.unload(target.getRaw());
    }

    public boolean unloadAll() {
	return unit.unloadAll();
    }

    public boolean unloadAll(boolean shiftQueueCommand) {
	return unit.unloadAll(shiftQueueCommand);
    }

    public boolean unloadAll(Position target) {
	return unit.unloadAll(target);
    }

    public boolean unloadAll(Position target, boolean shiftQueueCommand) {
	return unit.unloadAll(target, shiftQueueCommand);
    }

    public boolean rightClick(Position target) {
	return unit.rightClick(target);
    }

    public boolean rightClick(Unit2 target) {
	return unit.rightClick(target.getRaw());
    }

    public boolean rightClick(PositionOrUnit target) {
	return unit.rightClick(target);
    }

    public boolean rightClick(Position target, boolean shiftQueueCommand) {
	return unit.rightClick(target, shiftQueueCommand);
    }

    public boolean rightClick(Unit2 target, boolean shiftQueueCommand) {
	return unit.rightClick(target.getRaw(), shiftQueueCommand);
    }

    public boolean rightClick(PositionOrUnit target, boolean shiftQueueCommand) {
	return unit.rightClick(target, shiftQueueCommand);
    }

    public boolean haltConstruction() {
	return unit.haltConstruction();
    }

    public boolean cancelConstruction() {
	return unit.cancelConstruction();
    }

    public boolean cancelAddon() {
	return unit.cancelAddon();
    }

    public boolean cancelTrain() {
	return unit.cancelTrain();
    }

    public boolean cancelTrain(int slot) {
	return unit.cancelTrain(slot);
    }

    public boolean cancelMorph() {
	return unit.cancelMorph();
    }

    public boolean cancelResearch() {
	return unit.cancelResearch();
    }

    public boolean cancelUpgrade() {
	return unit.cancelUpgrade();
    }

    public boolean useTech(TechType tech) {
	return unit.useTech(tech);
    }

    public boolean useTech(TechType tech, Position target) {
	return unit.useTech(tech, target);
    }

    public boolean useTech(TechType tech, Unit2 target) {
	return unit.useTech(tech, target.getRaw());
    }

    public boolean useTech(TechType tech, PositionOrUnit target) {
	return unit.useTech(tech, target);
    }

    public boolean placeCOP(TilePosition target) {
	return unit.placeCOP(target);
    }

    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanBuildUnitType,
	    boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canIssueCommand(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanBuildUnitType, checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanBuildUnitType,
	    boolean checkCanTargetUnit) {
	return unit.canIssueCommand(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanBuildUnitType, checkCanTargetUnit);
    }

    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanBuildUnitType) {
	return unit.canIssueCommand(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanBuildUnitType);
    }

    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits) {
	return unit.canIssueCommand(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits);
    }

    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions) {
	return unit.canIssueCommand(command, checkCanUseTechPositionOnPositions);
    }

    public boolean canIssueCommand(UnitCommand command) {
	return unit.canIssueCommand(command);
    }

    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanBuildUnitType,
	    boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canIssueCommand(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanBuildUnitType, checkCanTargetUnit, checkCanIssueCommandType,
		checkCommandibility);
    }

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanTargetUnit,
	    boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped) {
	return unit.canIssueCommandGrouped(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanTargetUnit, checkCanIssueCommandType,
		checkCommandibilityGrouped);
    }

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanTargetUnit,
	    boolean checkCanIssueCommandType) {
	return unit.canIssueCommandGrouped(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanTargetUnit) {
	return unit.canIssueCommandGrouped(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanTargetUnit);
    }

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits) {
	return unit.canIssueCommandGrouped(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits);
    }

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions) {
	return unit.canIssueCommandGrouped(command, checkCanUseTechPositionOnPositions);
    }

    public boolean canIssueCommandGrouped(UnitCommand command) {
	return unit.canIssueCommandGrouped(command);
    }

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanTargetUnit,
	    boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped, boolean checkCommandibility) {
	return unit.canIssueCommandGrouped(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanTargetUnit, checkCanIssueCommandType,
		checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canCommand() {
	return unit.canCommand();
    }

    public boolean canCommandGrouped() {
	return unit.canCommandGrouped();
    }

    public boolean canCommandGrouped(boolean checkCommandibility) {
	return unit.canCommandGrouped(checkCommandibility);
    }

    public boolean canIssueCommandType(UnitCommandType ct) {
	return unit.canIssueCommandType(ct);
    }

    public boolean canIssueCommandType(UnitCommandType ct, boolean checkCommandibility) {
	return unit.canIssueCommandType(ct, checkCommandibility);
    }

    public boolean canIssueCommandTypeGrouped(UnitCommandType ct, boolean checkCommandibilityGrouped) {
	return unit.canIssueCommandTypeGrouped(ct, checkCommandibilityGrouped);
    }

    public boolean canIssueCommandTypeGrouped(UnitCommandType ct) {
	return unit.canIssueCommandTypeGrouped(ct);
    }

    public boolean canIssueCommandTypeGrouped(UnitCommandType ct, boolean checkCommandibilityGrouped, boolean checkCommandibility) {
	return unit.canIssueCommandTypeGrouped(ct, checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canTargetUnit(Unit2 targetUnit) {
	return unit.canTargetUnit(targetUnit.getRaw());
    }

    public boolean canTargetUnit(Unit2 targetUnit, boolean checkCommandibility) {
	return unit.canTargetUnit(targetUnit.getRaw(), checkCommandibility);
    }

    public boolean canAttack() {
	return unit.canAttack();
    }

    public boolean canAttack(boolean checkCommandibility) {
	return unit.canAttack(checkCommandibility);
    }

    public boolean canAttack(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canAttack(target, checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canAttack(Unit2 target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canAttack(target.getRaw(), checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canAttack(PositionOrUnit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canAttack(target, checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canAttack(Position target, boolean checkCanTargetUnit) {
	return unit.canAttack(target, checkCanTargetUnit);
    }

    public boolean canAttack(Unit2 target, boolean checkCanTargetUnit) {
	return unit.canAttack(target.getRaw(), checkCanTargetUnit);
    }

    public boolean canAttack(PositionOrUnit target, boolean checkCanTargetUnit) {
	return unit.canAttack(target, checkCanTargetUnit);
    }

    public boolean canAttack(Position target) {
	return unit.canAttack(target);
    }

    public boolean canAttack(Unit2 target) {
	return unit.canAttack(target.getRaw());
    }

    public boolean canAttack(PositionOrUnit target) {
	return unit.canAttack(target);
    }

    public boolean canAttack(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canAttack(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canAttack(Unit2 target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canAttack(target.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canAttack(PositionOrUnit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canAttack(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canAttackGrouped(boolean checkCommandibilityGrouped) {
	return unit.canAttackGrouped(checkCommandibilityGrouped);
    }

    public boolean canAttackGrouped() {
	return unit.canAttackGrouped();
    }

    public boolean canAttackGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility) {
	return unit.canAttackGrouped(checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canAttackGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped) {
	return unit.canAttackGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped);
    }

    public boolean canAttackGrouped(Unit2 target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped) {
	return unit.canAttackGrouped(target.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped);
    }

    public boolean canAttackGrouped(PositionOrUnit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped) {
	return unit.canAttackGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped);
    }

    public boolean canAttackGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canAttackGrouped(target, checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canAttackGrouped(Unit2 target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canAttackGrouped(target.getRaw(), checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canAttackGrouped(PositionOrUnit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canAttackGrouped(target, checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canAttackGrouped(Position target, boolean checkCanTargetUnit) {
	return unit.canAttackGrouped(target, checkCanTargetUnit);
    }

    public boolean canAttackGrouped(Unit2 target, boolean checkCanTargetUnit) {
	return unit.canAttackGrouped(target.getRaw(), checkCanTargetUnit);
    }

    public boolean canAttackGrouped(PositionOrUnit target, boolean checkCanTargetUnit) {
	return unit.canAttackGrouped(target, checkCanTargetUnit);
    }

    public boolean canAttackGrouped(Position target) {
	return unit.canAttackGrouped(target);
    }

    public boolean canAttackGrouped(Unit2 target) {
	return unit.canAttackGrouped(target.getRaw());
    }

    public boolean canAttackGrouped(PositionOrUnit target) {
	return unit.canAttackGrouped(target);
    }

    public boolean canAttackGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped,
	    boolean checkCommandibility) {
	return unit.canAttackGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canAttackGrouped(Unit2 target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped, boolean checkCommandibility) {
	return unit.canAttackGrouped(target.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canAttackGrouped(PositionOrUnit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped,
	    boolean checkCommandibility) {
	return unit.canAttackGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canAttackMove() {
	return unit.canAttackMove();
    }

    public boolean canAttackMove(boolean checkCommandibility) {
	return unit.canAttackMove(checkCommandibility);
    }

    public boolean canAttackMoveGrouped(boolean checkCommandibilityGrouped) {
	return unit.canAttackMoveGrouped(checkCommandibilityGrouped);
    }

    public boolean canAttackMoveGrouped() {
	return unit.canAttackMoveGrouped();
    }

    public boolean canAttackMoveGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility) {
	return unit.canAttackMoveGrouped(checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canAttackUnit() {
	return unit.canAttackUnit();
    }

    public boolean canAttackUnit(boolean checkCommandibility) {
	return unit.canAttackUnit(checkCommandibility);
    }

    public boolean canAttackUnit(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canAttackUnit(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canAttackUnit(Unit2 targetUnit, boolean checkCanTargetUnit) {
	return unit.canAttackUnit(targetUnit.getRaw(), checkCanTargetUnit);
    }

    public boolean canAttackUnit(Unit2 targetUnit) {
	return unit.canAttackUnit(targetUnit.getRaw());
    }

    public boolean canAttackUnit(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canAttackUnit(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canAttackUnitGrouped(boolean checkCommandibilityGrouped) {
	return unit.canAttackUnitGrouped(checkCommandibilityGrouped);
    }

    public boolean canAttackUnitGrouped() {
	return unit.canAttackUnitGrouped();
    }

    public boolean canAttackUnitGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility) {
	return unit.canAttackUnitGrouped(checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canAttackUnitGrouped(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped) {
	return unit.canAttackUnitGrouped(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped);
    }

    public boolean canAttackUnitGrouped(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canAttackUnitGrouped(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canAttackUnitGrouped(Unit2 targetUnit, boolean checkCanTargetUnit) {
	return unit.canAttackUnitGrouped(targetUnit.getRaw(), checkCanTargetUnit);
    }

    public boolean canAttackUnitGrouped(Unit2 targetUnit) {
	return unit.canAttackUnitGrouped(targetUnit.getRaw());
    }

    public boolean canAttackUnitGrouped(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped,
	    boolean checkCommandibility) {
	return unit.canAttackUnitGrouped(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canBuild() {
	return unit.canBuild();
    }

    public boolean canBuild(boolean checkCommandibility) {
	return unit.canBuild(checkCommandibility);
    }

    public boolean canBuild(UnitType uType, boolean checkCanIssueCommandType) {
	return unit.canBuild(uType, checkCanIssueCommandType);
    }

    public boolean canBuild(UnitType uType) {
	return unit.canBuild(uType);
    }

    public boolean canBuild(UnitType uType, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canBuild(uType, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canBuild(UnitType uType, TilePosition tilePos, boolean checkTargetUnitType, boolean checkCanIssueCommandType) {
	return unit.canBuild(uType, tilePos, checkTargetUnitType, checkCanIssueCommandType);
    }

    public boolean canBuild(UnitType uType, TilePosition tilePos, boolean checkTargetUnitType) {
	return unit.canBuild(uType, tilePos, checkTargetUnitType);
    }

    public boolean canBuild(UnitType uType, TilePosition tilePos) {
	return unit.canBuild(uType, tilePos);
    }

    public boolean canBuild(UnitType uType, TilePosition tilePos, boolean checkTargetUnitType, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canBuild(uType, tilePos, checkTargetUnitType, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canBuildAddon() {
	return unit.canBuildAddon();
    }

    public boolean canBuildAddon(boolean checkCommandibility) {
	return unit.canBuildAddon(checkCommandibility);
    }

    public boolean canBuildAddon(UnitType uType, boolean checkCanIssueCommandType) {
	return unit.canBuildAddon(uType, checkCanIssueCommandType);
    }

    public boolean canBuildAddon(UnitType uType) {
	return unit.canBuildAddon(uType);
    }

    public boolean canBuildAddon(UnitType uType, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canBuildAddon(uType, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canTrain() {
	return unit.canTrain();
    }

    public boolean canTrain(boolean checkCommandibility) {
	return unit.canTrain(checkCommandibility);
    }

    public boolean canTrain(UnitType uType, boolean checkCanIssueCommandType) {
	return unit.canTrain(uType, checkCanIssueCommandType);
    }

    public boolean canTrain(UnitType uType) {
	return unit.canTrain(uType);
    }

    public boolean canTrain(UnitType uType, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canTrain(uType, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canMorph() {
	return unit.canMorph();
    }

    public boolean canMorph(boolean checkCommandibility) {
	return unit.canMorph(checkCommandibility);
    }

    public boolean canMorph(UnitType uType, boolean checkCanIssueCommandType) {
	return unit.canMorph(uType, checkCanIssueCommandType);
    }

    public boolean canMorph(UnitType uType) {
	return unit.canMorph(uType);
    }

    public boolean canMorph(UnitType uType, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canMorph(uType, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canResearch() {
	return unit.canResearch();
    }

    public boolean canResearch(boolean checkCommandibility) {
	return unit.canResearch(checkCommandibility);
    }

    public boolean canResearch(TechType type) {
	return unit.canResearch(type);
    }

    public boolean canResearch(TechType type, boolean checkCanIssueCommandType) {
	return unit.canResearch(type, checkCanIssueCommandType);
    }

    public boolean canUpgrade() {
	return unit.canUpgrade();
    }

    public boolean canUpgrade(boolean checkCommandibility) {
	return unit.canUpgrade(checkCommandibility);
    }

    public boolean canUpgrade(UpgradeType type) {
	return unit.canUpgrade(type);
    }

    public boolean canUpgrade(UpgradeType type, boolean checkCanIssueCommandType) {
	return unit.canUpgrade(type, checkCanIssueCommandType);
    }

    public boolean canSetRallyPoint() {
	return unit.canSetRallyPoint();
    }

    public boolean canSetRallyPoint(boolean checkCommandibility) {
	return unit.canSetRallyPoint(checkCommandibility);
    }

    public boolean canSetRallyPoint(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canSetRallyPoint(target, checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canSetRallyPoint(Unit2 target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canSetRallyPoint(target.getRaw(), checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canSetRallyPoint(PositionOrUnit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canSetRallyPoint(target, checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canSetRallyPoint(Position target, boolean checkCanTargetUnit) {
	return unit.canSetRallyPoint(target, checkCanTargetUnit);
    }

    public boolean canSetRallyPoint(Unit2 target, boolean checkCanTargetUnit) {
	return unit.canSetRallyPoint(target.getRaw(), checkCanTargetUnit);
    }

    public boolean canSetRallyPoint(PositionOrUnit target, boolean checkCanTargetUnit) {
	return unit.canSetRallyPoint(target, checkCanTargetUnit);
    }

    public boolean canSetRallyPoint(Position target) {
	return unit.canSetRallyPoint(target);
    }

    public boolean canSetRallyPoint(Unit2 target) {
	return unit.canSetRallyPoint(target.getRaw());
    }

    public boolean canSetRallyPoint(PositionOrUnit target) {
	return unit.canSetRallyPoint(target);
    }

    public boolean canSetRallyPoint(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canSetRallyPoint(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canSetRallyPoint(Unit2 target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canSetRallyPoint(target.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canSetRallyPoint(PositionOrUnit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canSetRallyPoint(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canSetRallyPosition() {
	return unit.canSetRallyPosition();
    }

    public boolean canSetRallyPosition(boolean checkCommandibility) {
	return unit.canSetRallyPosition(checkCommandibility);
    }

    public boolean canSetRallyUnit() {
	return unit.canSetRallyUnit();
    }

    public boolean canSetRallyUnit(boolean checkCommandibility) {
	return unit.canSetRallyUnit(checkCommandibility);
    }

    public boolean canSetRallyUnit(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canSetRallyUnit(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canSetRallyUnit(Unit2 targetUnit, boolean checkCanTargetUnit) {
	return unit.canSetRallyUnit(targetUnit.getRaw(), checkCanTargetUnit);
    }

    public boolean canSetRallyUnit(Unit2 targetUnit) {
	return unit.canSetRallyUnit(targetUnit.getRaw());
    }

    public boolean canSetRallyUnit(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canSetRallyUnit(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canMove() {
	return unit.canMove();
    }

    public boolean canMove(boolean checkCommandibility) {
	return unit.canMove(checkCommandibility);
    }

    public boolean canMoveGrouped(boolean checkCommandibilityGrouped) {
	return unit.canMoveGrouped(checkCommandibilityGrouped);
    }

    public boolean canMoveGrouped() {
	return unit.canMoveGrouped();
    }

    public boolean canMoveGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility) {
	return unit.canMoveGrouped(checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canPatrol() {
	return unit.canPatrol();
    }

    public boolean canPatrol(boolean checkCommandibility) {
	return unit.canPatrol(checkCommandibility);
    }

    public boolean canPatrolGrouped(boolean checkCommandibilityGrouped) {
	return unit.canPatrolGrouped(checkCommandibilityGrouped);
    }

    public boolean canPatrolGrouped() {
	return unit.canPatrolGrouped();
    }

    public boolean canPatrolGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility) {
	return unit.canPatrolGrouped(checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canFollow() {
	return unit.canFollow();
    }

    public boolean canFollow(boolean checkCommandibility) {
	return unit.canFollow(checkCommandibility);
    }

    public boolean canFollow(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canFollow(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canFollow(Unit2 targetUnit, boolean checkCanTargetUnit) {
	return unit.canFollow(targetUnit.getRaw(), checkCanTargetUnit);
    }

    public boolean canFollow(Unit2 targetUnit) {
	return unit.canFollow(targetUnit.getRaw());
    }

    public boolean canFollow(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canFollow(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canGather() {
	return unit.canGather();
    }

    public boolean canGather(boolean checkCommandibility) {
	return unit.canGather(checkCommandibility);
    }

    public boolean canGather(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canGather(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canGather(Unit2 targetUnit, boolean checkCanTargetUnit) {
	return unit.canGather(targetUnit.getRaw(), checkCanTargetUnit);
    }

    public boolean canGather(Unit2 targetUnit) {
	return unit.canGather(targetUnit.getRaw());
    }

    public boolean canGather(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canGather(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canReturnCargo() {
	return unit.canReturnCargo();
    }

    public boolean canReturnCargo(boolean checkCommandibility) {
	return unit.canReturnCargo(checkCommandibility);
    }

    public boolean canHoldPosition() {
	return unit.canHoldPosition();
    }

    public boolean canHoldPosition(boolean checkCommandibility) {
	return unit.canHoldPosition(checkCommandibility);
    }

    public boolean canStop() {
	return unit.canStop();
    }

    public boolean canStop(boolean checkCommandibility) {
	return unit.canStop(checkCommandibility);
    }

    public boolean canRepair() {
	return unit.canRepair();
    }

    public boolean canRepair(boolean checkCommandibility) {
	return unit.canRepair(checkCommandibility);
    }

    public boolean canRepair(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canRepair(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canRepair(Unit2 targetUnit, boolean checkCanTargetUnit) {
	return unit.canRepair(targetUnit.getRaw(), checkCanTargetUnit);
    }

    public boolean canRepair(Unit2 targetUnit) {
	return unit.canRepair(targetUnit.getRaw());
    }

    public boolean canRepair(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canRepair(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canBurrow() {
	return unit.canBurrow();
    }

    public boolean canBurrow(boolean checkCommandibility) {
	return unit.canBurrow(checkCommandibility);
    }

    public boolean canUnburrow() {
	return unit.canUnburrow();
    }

    public boolean canUnburrow(boolean checkCommandibility) {
	return unit.canUnburrow(checkCommandibility);
    }

    public boolean canCloak() {
	return unit.canCloak();
    }

    public boolean canCloak(boolean checkCommandibility) {
	return unit.canCloak(checkCommandibility);
    }

    public boolean canDecloak() {
	return unit.canDecloak();
    }

    public boolean canDecloak(boolean checkCommandibility) {
	return unit.canDecloak(checkCommandibility);
    }

    public boolean canSiege() {
	return unit.canSiege();
    }

    public boolean canSiege(boolean checkCommandibility) {
	return unit.canSiege(checkCommandibility);
    }

    public boolean canUnsiege() {
	return unit.canUnsiege();
    }

    public boolean canUnsiege(boolean checkCommandibility) {
	return unit.canUnsiege(checkCommandibility);
    }

    public boolean canLift() {
	return unit.canLift();
    }

    public boolean canLift(boolean checkCommandibility) {
	return unit.canLift(checkCommandibility);
    }

    public boolean canLand() {
	return unit.canLand();
    }

    public boolean canLand(boolean checkCommandibility) {
	return unit.canLand(checkCommandibility);
    }

    public boolean canLand(TilePosition target, boolean checkCanIssueCommandType) {
	return unit.canLand(target, checkCanIssueCommandType);
    }

    public boolean canLand(TilePosition target) {
	return unit.canLand(target);
    }

    public boolean canLand(TilePosition target, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canLand(target, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canLoad() {
	return unit.canLoad();
    }

    public boolean canLoad(boolean checkCommandibility) {
	return unit.canLoad(checkCommandibility);
    }

    public boolean canLoad(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canLoad(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canLoad(Unit2 targetUnit, boolean checkCanTargetUnit) {
	return unit.canLoad(targetUnit.getRaw(), checkCanTargetUnit);
    }

    public boolean canLoad(Unit2 targetUnit) {
	return unit.canLoad(targetUnit.getRaw());
    }

    public boolean canLoad(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canLoad(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canUnloadWithOrWithoutTarget() {
	return unit.canUnloadWithOrWithoutTarget();
    }

    public boolean canUnloadWithOrWithoutTarget(boolean checkCommandibility) {
	return unit.canUnloadWithOrWithoutTarget(checkCommandibility);
    }

    public boolean canUnloadAtPosition(Position targDropPos, boolean checkCanIssueCommandType) {
	return unit.canUnloadAtPosition(targDropPos, checkCanIssueCommandType);
    }

    public boolean canUnloadAtPosition(Position targDropPos) {
	return unit.canUnloadAtPosition(targDropPos);
    }

    public boolean canUnloadAtPosition(Position targDropPos, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canUnloadAtPosition(targDropPos, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canUnload() {
	return unit.canUnload();
    }

    public boolean canUnload(boolean checkCommandibility) {
	return unit.canUnload(checkCommandibility);
    }

    public boolean canUnload(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkPosition, boolean checkCanIssueCommandType) {
	return unit.canUnload(targetUnit.getRaw(), checkCanTargetUnit, checkPosition, checkCanIssueCommandType);
    }

    public boolean canUnload(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkPosition) {
	return unit.canUnload(targetUnit.getRaw(), checkCanTargetUnit, checkPosition);
    }

    public boolean canUnload(Unit2 targetUnit, boolean checkCanTargetUnit) {
	return unit.canUnload(targetUnit.getRaw(), checkCanTargetUnit);
    }

    public boolean canUnload(Unit2 targetUnit) {
	return unit.canUnload(targetUnit.getRaw());
    }

    public boolean canUnload(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkPosition, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canUnload(targetUnit.getRaw(), checkCanTargetUnit, checkPosition, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canUnloadAll() {
	return unit.canUnloadAll();
    }

    public boolean canUnloadAll(boolean checkCommandibility) {
	return unit.canUnloadAll(checkCommandibility);
    }

    public boolean canUnloadAllPosition() {
	return unit.canUnloadAllPosition();
    }

    public boolean canUnloadAllPosition(boolean checkCommandibility) {
	return unit.canUnloadAllPosition(checkCommandibility);
    }

    public boolean canUnloadAllPosition(Position targDropPos, boolean checkCanIssueCommandType) {
	return unit.canUnloadAllPosition(targDropPos, checkCanIssueCommandType);
    }

    public boolean canUnloadAllPosition(Position targDropPos) {
	return unit.canUnloadAllPosition(targDropPos);
    }

    public boolean canUnloadAllPosition(Position targDropPos, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canUnloadAllPosition(targDropPos, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canRightClick() {
	return unit.canRightClick();
    }

    public boolean canRightClick(boolean checkCommandibility) {
	return unit.canRightClick(checkCommandibility);
    }

    public boolean canRightClick(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canRightClick(target, checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canRightClick(Unit2 target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canRightClick(target.getRaw(), checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canRightClick(PositionOrUnit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canRightClick(target, checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canRightClick(Position target, boolean checkCanTargetUnit) {
	return unit.canRightClick(target, checkCanTargetUnit);
    }

    public boolean canRightClick(Unit2 target, boolean checkCanTargetUnit) {
	return unit.canRightClick(target.getRaw(), checkCanTargetUnit);
    }

    public boolean canRightClick(PositionOrUnit target, boolean checkCanTargetUnit) {
	return unit.canRightClick(target, checkCanTargetUnit);
    }

    public boolean canRightClick(Position target) {
	return unit.canRightClick(target);
    }

    public boolean canRightClick(Unit2 target) {
	return unit.canRightClick(target.getRaw());
    }

    public boolean canRightClick(PositionOrUnit target) {
	return unit.canRightClick(target);
    }

    public boolean canRightClick(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canRightClick(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canRightClick(Unit2 target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canRightClick(target.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canRightClick(PositionOrUnit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canRightClick(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canRightClickGrouped(boolean checkCommandibilityGrouped) {
	return unit.canRightClickGrouped(checkCommandibilityGrouped);
    }

    public boolean canRightClickGrouped() {
	return unit.canRightClickGrouped();
    }

    public boolean canRightClickGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility) {
	return unit.canRightClickGrouped(checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canRightClickGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped) {
	return unit.canRightClickGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped);
    }

    public boolean canRightClickGrouped(Unit2 target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped) {
	return unit.canRightClickGrouped(target.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped);
    }

    public boolean canRightClickGrouped(PositionOrUnit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped) {
	return unit.canRightClickGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped);
    }

    public boolean canRightClickGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canRightClickGrouped(target, checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canRightClickGrouped(Unit2 target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canRightClickGrouped(target.getRaw(), checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canRightClickGrouped(PositionOrUnit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canRightClickGrouped(target, checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canRightClickGrouped(Position target, boolean checkCanTargetUnit) {
	return unit.canRightClickGrouped(target, checkCanTargetUnit);
    }

    public boolean canRightClickGrouped(Unit2 target, boolean checkCanTargetUnit) {
	return unit.canRightClickGrouped(target.getRaw(), checkCanTargetUnit);
    }

    public boolean canRightClickGrouped(PositionOrUnit target, boolean checkCanTargetUnit) {
	return unit.canRightClickGrouped(target, checkCanTargetUnit);
    }

    public boolean canRightClickGrouped(Position target) {
	return unit.canRightClickGrouped(target);
    }

    public boolean canRightClickGrouped(Unit2 target) {
	return unit.canRightClickGrouped(target.getRaw());
    }

    public boolean canRightClickGrouped(PositionOrUnit target) {
	return unit.canRightClickGrouped(target);
    }

    public boolean canRightClickGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped,
	    boolean checkCommandibility) {
	return unit.canRightClickGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canRightClickGrouped(Unit2 target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped,
	    boolean checkCommandibility) {
	return unit.canRightClickGrouped(target.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canRightClickGrouped(PositionOrUnit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped,
	    boolean checkCommandibility) {
	return unit.canRightClickGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canRightClickPosition() {
	return unit.canRightClickPosition();
    }

    public boolean canRightClickPosition(boolean checkCommandibility) {
	return unit.canRightClickPosition(checkCommandibility);
    }

    public boolean canRightClickPositionGrouped(boolean checkCommandibilityGrouped) {
	return unit.canRightClickPositionGrouped(checkCommandibilityGrouped);
    }

    public boolean canRightClickPositionGrouped() {
	return unit.canRightClickPositionGrouped();
    }

    public boolean canRightClickPositionGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility) {
	return unit.canRightClickPositionGrouped(checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canRightClickUnit() {
	return unit.canRightClickUnit();
    }

    public boolean canRightClickUnit(boolean checkCommandibility) {
	return unit.canRightClickUnit(checkCommandibility);
    }

    public boolean canRightClickUnit(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canRightClickUnit(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canRightClickUnit(Unit2 targetUnit, boolean checkCanTargetUnit) {
	return unit.canRightClickUnit(targetUnit.getRaw(), checkCanTargetUnit);
    }

    public boolean canRightClickUnit(Unit2 targetUnit) {
	return unit.canRightClickUnit(targetUnit.getRaw());
    }

    public boolean canRightClickUnit(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canRightClickUnit(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canRightClickUnitGrouped(boolean checkCommandibilityGrouped) {
	return unit.canRightClickUnitGrouped(checkCommandibilityGrouped);
    }

    public boolean canRightClickUnitGrouped() {
	return unit.canRightClickUnitGrouped();
    }

    public boolean canRightClickUnitGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility) {
	return unit.canRightClickUnitGrouped(checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canRightClickUnitGrouped(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped) {
	return unit.canRightClickUnitGrouped(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped);
    }

    public boolean canRightClickUnitGrouped(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
	return unit.canRightClickUnitGrouped(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType);
    }

    public boolean canRightClickUnitGrouped(Unit2 targetUnit, boolean checkCanTargetUnit) {
	return unit.canRightClickUnitGrouped(targetUnit.getRaw(), checkCanTargetUnit);
    }

    public boolean canRightClickUnitGrouped(Unit2 targetUnit) {
	return unit.canRightClickUnitGrouped(targetUnit.getRaw());
    }

    public boolean canRightClickUnitGrouped(Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped,
	    boolean checkCommandibility) {
	return unit.canRightClickUnitGrouped(targetUnit.getRaw(), checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, checkCommandibility);
    }

    public boolean canHaltConstruction() {
	return unit.canHaltConstruction();
    }

    public boolean canHaltConstruction(boolean checkCommandibility) {
	return unit.canHaltConstruction(checkCommandibility);
    }

    public boolean canCancelConstruction() {
	return unit.canCancelConstruction();
    }

    public boolean canCancelConstruction(boolean checkCommandibility) {
	return unit.canCancelConstruction(checkCommandibility);
    }

    public boolean canCancelAddon() {
	return unit.canCancelAddon();
    }

    public boolean canCancelAddon(boolean checkCommandibility) {
	return unit.canCancelAddon(checkCommandibility);
    }

    public boolean canCancelTrain() {
	return unit.canCancelTrain();
    }

    public boolean canCancelTrain(boolean checkCommandibility) {
	return unit.canCancelTrain(checkCommandibility);
    }

    public boolean canCancelTrainSlot() {
	return unit.canCancelTrainSlot();
    }

    public boolean canCancelTrainSlot(boolean checkCommandibility) {
	return unit.canCancelTrainSlot(checkCommandibility);
    }

    public boolean canCancelTrainSlot(int slot, boolean checkCanIssueCommandType) {
	return unit.canCancelTrainSlot(slot, checkCanIssueCommandType);
    }

    public boolean canCancelTrainSlot(int slot) {
	return unit.canCancelTrainSlot(slot);
    }

    public boolean canCancelTrainSlot(int slot, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canCancelTrainSlot(slot, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canCancelMorph() {
	return unit.canCancelMorph();
    }

    public boolean canCancelMorph(boolean checkCommandibility) {
	return unit.canCancelMorph(checkCommandibility);
    }

    public boolean canCancelResearch() {
	return unit.canCancelResearch();
    }

    public boolean canCancelResearch(boolean checkCommandibility) {
	return unit.canCancelResearch(checkCommandibility);
    }

    public boolean canCancelUpgrade() {
	return unit.canCancelUpgrade();
    }

    public boolean canCancelUpgrade(boolean checkCommandibility) {
	return unit.canCancelUpgrade(checkCommandibility);
    }

    public boolean canUseTechWithOrWithoutTarget() {
	return unit.canUseTechWithOrWithoutTarget();
    }

    public boolean canUseTechWithOrWithoutTarget(boolean checkCommandibility) {
	return unit.canUseTechWithOrWithoutTarget(checkCommandibility);
    }

    public boolean canUseTechWithOrWithoutTarget(TechType tech, boolean checkCanIssueCommandType) {
	return unit.canUseTechWithOrWithoutTarget(tech, checkCanIssueCommandType);
    }

    public boolean canUseTechWithOrWithoutTarget(TechType tech) {
	return unit.canUseTechWithOrWithoutTarget(tech);
    }

    public boolean canUseTechWithOrWithoutTarget(TechType tech, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canUseTechWithOrWithoutTarget(tech, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canUseTech(TechType tech, Position target, boolean checkCanTargetUnit, boolean checkTargetsType, boolean checkCanIssueCommandType) {
	return unit.canUseTech(tech, target, checkCanTargetUnit, checkTargetsType, checkCanIssueCommandType);
    }

    public boolean canUseTech(TechType tech, Unit2 target, boolean checkCanTargetUnit, boolean checkTargetsType, boolean checkCanIssueCommandType) {
	return unit.canUseTech(tech, target.getRaw(), checkCanTargetUnit, checkTargetsType, checkCanIssueCommandType);
    }

    public boolean canUseTech(TechType tech, PositionOrUnit target, boolean checkCanTargetUnit, boolean checkTargetsType, boolean checkCanIssueCommandType) {
	return unit.canUseTech(tech, target, checkCanTargetUnit, checkTargetsType, checkCanIssueCommandType);
    }

    public boolean canUseTech(TechType tech, Position target, boolean checkCanTargetUnit, boolean checkTargetsType) {
	return unit.canUseTech(tech, target, checkCanTargetUnit, checkTargetsType);
    }

    public boolean canUseTech(TechType tech, Unit2 target, boolean checkCanTargetUnit, boolean checkTargetsType) {
	return unit.canUseTech(tech, target.getRaw(), checkCanTargetUnit, checkTargetsType);
    }

    public boolean canUseTech(TechType tech, PositionOrUnit target, boolean checkCanTargetUnit, boolean checkTargetsType) {
	return unit.canUseTech(tech, target, checkCanTargetUnit, checkTargetsType);
    }

    public boolean canUseTech(TechType tech, Position target, boolean checkCanTargetUnit) {
	return unit.canUseTech(tech, target, checkCanTargetUnit);
    }

    public boolean canUseTech(TechType tech, Unit2 target, boolean checkCanTargetUnit) {
	return unit.canUseTech(tech, target.getRaw(), checkCanTargetUnit);
    }

    public boolean canUseTech(TechType tech, PositionOrUnit target, boolean checkCanTargetUnit) {
	return unit.canUseTech(tech, target, checkCanTargetUnit);
    }

    public boolean canUseTech(TechType tech, Position target) {
	return unit.canUseTech(tech, target);
    }

    public boolean canUseTech(TechType tech, Unit2 target) {
	return unit.canUseTech(tech, target.getRaw());
    }

    public boolean canUseTech(TechType tech, PositionOrUnit target) {
	return unit.canUseTech(tech, target);
    }

    public boolean canUseTech(TechType tech) {
	return unit.canUseTech(tech);
    }

    public boolean canUseTech(TechType tech, Position target, boolean checkCanTargetUnit, boolean checkTargetsType, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canUseTech(tech, target, checkCanTargetUnit, checkTargetsType, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canUseTech(TechType tech, Unit2 target, boolean checkCanTargetUnit, boolean checkTargetsType, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canUseTech(tech, target.getRaw(), checkCanTargetUnit, checkTargetsType, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canUseTech(TechType tech, PositionOrUnit target, boolean checkCanTargetUnit, boolean checkTargetsType, boolean checkCanIssueCommandType,
	    boolean checkCommandibility) {
	return unit.canUseTech(tech, target, checkCanTargetUnit, checkTargetsType, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canUseTechWithoutTarget(TechType tech, boolean checkCanIssueCommandType) {
	return unit.canUseTechWithoutTarget(tech, checkCanIssueCommandType);
    }

    public boolean canUseTechWithoutTarget(TechType tech) {
	return unit.canUseTechWithoutTarget(tech);
    }

    public boolean canUseTechWithoutTarget(TechType tech, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canUseTechWithoutTarget(tech, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canUseTechUnit(TechType tech, boolean checkCanIssueCommandType) {
	return unit.canUseTechUnit(tech, checkCanIssueCommandType);
    }

    public boolean canUseTechUnit(TechType tech) {
	return unit.canUseTechUnit(tech);
    }

    public boolean canUseTechUnit(TechType tech, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canUseTechUnit(tech, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canUseTechUnit(TechType tech, Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkTargetsUnits, boolean checkCanIssueCommandType) {
	return unit.canUseTechUnit(tech, targetUnit.getRaw(), checkCanTargetUnit, checkTargetsUnits, checkCanIssueCommandType);
    }

    public boolean canUseTechUnit(TechType tech, Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkTargetsUnits) {
	return unit.canUseTechUnit(tech, targetUnit.getRaw(), checkCanTargetUnit, checkTargetsUnits);
    }

    public boolean canUseTechUnit(TechType tech, Unit2 targetUnit, boolean checkCanTargetUnit) {
	return unit.canUseTechUnit(tech, targetUnit.getRaw(), checkCanTargetUnit);
    }

    public boolean canUseTechUnit(TechType tech, Unit2 targetUnit) {
	return unit.canUseTechUnit(tech, targetUnit.getRaw());
    }

    public boolean canUseTechUnit(TechType tech, Unit2 targetUnit, boolean checkCanTargetUnit, boolean checkTargetsUnits, boolean checkCanIssueCommandType,
	    boolean checkCommandibility) {
	return unit.canUseTechUnit(tech, targetUnit.getRaw(), checkCanTargetUnit, checkTargetsUnits, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canUseTechPosition(TechType tech, boolean checkCanIssueCommandType) {
	return unit.canUseTechPosition(tech, checkCanIssueCommandType);
    }

    public boolean canUseTechPosition(TechType tech) {
	return unit.canUseTechPosition(tech);
    }

    public boolean canUseTechPosition(TechType tech, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canUseTechPosition(tech, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canUseTechPosition(TechType tech, Position target, boolean checkTargetsPositions, boolean checkCanIssueCommandType) {
	return unit.canUseTechPosition(tech, target, checkTargetsPositions, checkCanIssueCommandType);
    }

    public boolean canUseTechPosition(TechType tech, Position target, boolean checkTargetsPositions) {
	return unit.canUseTechPosition(tech, target, checkTargetsPositions);
    }

    public boolean canUseTechPosition(TechType tech, Position target) {
	return unit.canUseTechPosition(tech, target);
    }

    public boolean canUseTechPosition(TechType tech, Position target, boolean checkTargetsPositions, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canUseTechPosition(tech, target, checkTargetsPositions, checkCanIssueCommandType, checkCommandibility);
    }

    public boolean canPlaceCOP() {
	return unit.canPlaceCOP();
    }

    public boolean canPlaceCOP(boolean checkCommandibility) {
	return unit.canPlaceCOP(checkCommandibility);
    }

    public boolean canPlaceCOP(TilePosition target, boolean checkCanIssueCommandType) {
	return unit.canPlaceCOP(target, checkCanIssueCommandType);
    }

    public boolean canPlaceCOP(TilePosition target) {
	return unit.canPlaceCOP(target);
    }

    public boolean canPlaceCOP(TilePosition target, boolean checkCanIssueCommandType, boolean checkCommandibility) {
	return unit.canPlaceCOP(target, checkCanIssueCommandType, checkCommandibility);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Unit의 Parent
    // /////////////////////////////////////////////////////////////////////////
    public Position getPoint() {
	return unit.getPoint();
    }
}
