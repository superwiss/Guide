import bwapi.Position;
import bwapi.Unit;

// MagiBot을 빠르게 연습시키기 위해서, 유즈맵으로 미션을 만들어 MagiBot이 미션을 해결하는 방식으로 훈련한다.
public class MicroControlManager {
    private static MicroControlManager instance = new MicroControlManager();

    public static MicroControlManager Instance() {
	return instance;
    }

    public void onFrame(GameData gameData) {
	UnitManager allianceUnitManager = gameData.getAllianceUnitManager();
	UnitManager enemyUnitManager = gameData.getEnemyUnitManager();

	// 적을 공격할 수 있는 아군 유닛을 대상으로 컨트롤을 한다.
	for (Integer allianceUnitId : allianceUnitManager.getAttackableUnitList()) {

	    Unit allianceUnit = allianceUnitManager.getUnit(allianceUnitId);

	    // 공격할 적 유닛을 선택한다.
	    Unit enemyUnit = UnitUtil.selectEnemyTargetUnit(allianceUnit, enemyUnitManager);
	    if (null == enemyUnit) {
		return;
	    }

	    printUnitInfo(allianceUnit);
	    printUnitInfo(enemyUnit);

	    // 도망칠 때 집결 장소. ex) first chockpoint
	    Position backPosition = new Position(0, 0);

	    int distanceFromEnemy = -1;
	    distanceFromEnemy = allianceUnit.getDistance(enemyUnit);
	    Log.debug("Distance: %d, Cooldown: %d, isInWeaponRange: %b, MyHp: %d, EnemyHp: %d, LastStatus: %s", distanceFromEnemy, allianceUnit.getGroundWeaponCooldown(),
		    allianceUnit.isInWeaponRange(enemyUnit), allianceUnit.getHitPoints(), enemyUnit.getHitPoints(), allianceUnitManager.getLastStatus(allianceUnit));

	    // 적 유닛이 현재 선택된 아군 유닛을 바라보고 있는지 확인한다. 
	    boolean isEnemyUnitLookingCurrentAllianceUnit = UnitUtil.isEnemyUnitLookingMyUnit(allianceUnit, enemyUnit, Math.PI);

	    switch (allianceUnitManager.getLastStatus(allianceUnit)) {
	    case IDLE:
		// 공격 대상을 찾는다.
		Log.debug("\tFound Enemy. %s will atack %s", UnitUtil.toString(allianceUnit), UnitUtil.toString(enemyUnit));
		ActionUtil.patrolToEnemyUnit(allianceUnitManager, allianceUnit, enemyUnit);
		allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.MOVE_TO_ENEMY);
		onFrame(gameData);
		break;
	    case WAIT_ENEMY:
		if (isEnemyUnitLookingCurrentAllianceUnit) {
		    // 적이 아군을 바라보고 있다.
		    if (enemyUnit.isMoving()) {
			// 적이 이동 중이다.
			if (0 == allianceUnit.getGroundWeaponCooldown()) {
			    // 무기가 준비 되었다.
			    if (allianceUnit.isInWeaponRange(enemyUnit)) {
				Log.debug("적이 아군을 향해 달려오고 있고, 무기는 준비 되었고, 사거리 이내라면 공격한다.");
				ActionUtil.attackEnemyUnit(allianceUnitManager, allianceUnit, enemyUnit);
				allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.ATTACK_ENEMY);
			    } else {
				Log.debug("적이 아군을 향해 달려오고 있고, 무기는 준비 되었지만, 사거리 밖이라면 계속 대기한다.");
			    }
			} else {
			    // 무기가 준비되지 않았다.
			    if (distanceFromEnemy < 100) {
				Log.debug("적이 아군을 향해 달려오고 있고, 무기는 준비되지 않았고, 적이 너무 가까이 있으면 도망간다.");
				ActionUtil.moveToPosition(allianceUnitManager, allianceUnit, backPosition);
				allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.RUNAWAY_FROM_ENEMY);
			    } else {
				Log.debug("적이 아군을 향해 달려오고 있고, 무기는 준비되지 않았지만, 적이 멀리 떨어져 있으면 계속 대기한다.");
			    }
			}
		    } else {
			// 적이 대기 중이다.
			if (0 == allianceUnit.getGroundWeaponCooldown()) {
			    // 무기가 준비 되었다.
			    if (allianceUnit.isInWeaponRange(enemyUnit)) {
				Log.debug("적이 아군에게 등을 돌리고 있고, 무기는 준비 되었고, 사거리 이내라면 공격한다.");
				ActionUtil.attackEnemyUnit(allianceUnitManager, allianceUnit, enemyUnit);
				allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.ATTACK_ENEMY);
			    } else {
				Log.debug("적이 아군에게 등을 돌리고 있고, 무기는 준비 되었지만, 사거리 밖이라면 적에게 다가간다.");
				ActionUtil.patrolToEnemyUnit(allianceUnitManager, allianceUnit, enemyUnit);
				allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.MOVE_TO_ENEMY);
			    }
			    break;
			} else {
			    // 무기가 준비되지 않았다.
			    if (distanceFromEnemy < 100) {
				Log.debug("적이 아군에게 등을 돌리고 있고, 무기는 준비되지 않았고, 적이 너무 가까이 있으면 대기한다.");
			    } else {
				Log.debug("적이 아군에게 등을 돌리고 있고, 무기는 준비되지 않았지만, 적이 멀리 떨어져 있으면 적에게 다가간다.");
				ActionUtil.patrolToEnemyUnit(allianceUnitManager, allianceUnit, enemyUnit);
				allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.MOVE_TO_ENEMY);
			    }
			    break;
			}
		    }
		} else {
		    // 적이 아군을 바라보고 있지 않다.
		    if (enemyUnit.isMoving()) {
			// 적이 이동 중이다.
			if (0 == allianceUnit.getGroundWeaponCooldown()) {
			    // 무기가 준비 되었다.
			    if (allianceUnit.isInWeaponRange(enemyUnit)) {
				Log.debug("적이 도망중이고, 무기는 준비 되었고, 사거리 이내라면 공격한다.");
				ActionUtil.attackEnemyUnit(allianceUnitManager, allianceUnit, enemyUnit);
				allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.ATTACK_ENEMY);
			    } else {
				Log.debug("적이 도망중이고, 무기는 준비 되었지만, 사거리 밖이라면 적에게 다가간다.");
				ActionUtil.patrolToEnemyUnit(allianceUnitManager, allianceUnit, enemyUnit);
				allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.MOVE_TO_ENEMY);
			    }
			    break;
			} else {
			    // 무기가 준비되지 않았다.
			    if (distanceFromEnemy < 100) {
				Log.debug("적이 도망중이고, 무기는 준비되지 않았고, 적이 너무 가까이 있으면 대기한다.");
			    } else {
				Log.debug("적이 도망중이고, 무기는 준비되지 않았지만, 적이 멀리 떨어져 있으면 적에게 다가간다.");
				ActionUtil.patrolToEnemyUnit(allianceUnitManager, allianceUnit, enemyUnit);
				allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.MOVE_TO_ENEMY);
			    }
			    break;
			}
		    } else {
			// 적이 대기 중이다.
			if (0 == allianceUnit.getGroundWeaponCooldown()) {
			    // 무기가 준비 되었다.
			    if (allianceUnit.isInWeaponRange(enemyUnit)) {
				Log.debug("적이 대기 중이고, 무기는 준비 되었고, 사거리 이내라면 공격한다.");
				ActionUtil.attackEnemyUnit(allianceUnitManager, allianceUnit, enemyUnit);
				allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.ATTACK_ENEMY);
			    } else {
				Log.debug("적이 대기 중이고, 무기는 준비 되었지만, 사거리 밖이라면 적에게 다가간다.");
				ActionUtil.patrolToEnemyUnit(allianceUnitManager, allianceUnit, enemyUnit);
				allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.MOVE_TO_ENEMY);
			    }
			    break;
			} else {
			    // 무기가 준비되지 않았다.
			    if (distanceFromEnemy < 100) {
				Log.debug("적이 대기 중이고, 무기는 준비되지 않았고, 적이 너무 가까이 있으면 대기한다.");
			    } else {
				Log.debug("적이 대기 중이고, 무기는 준비되지 않았지만, 적이 멀리 떨어져 있으면 적에게 다가간다.");
				ActionUtil.patrolToEnemyUnit(allianceUnitManager, allianceUnit, enemyUnit);
				allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.MOVE_TO_ENEMY);
			    }
			    break;
			}
		    }
		}
		break;
	    case ATTACK_ENEMY:
		if (0 != allianceUnit.getGroundWeaponCooldown()) {
		    if (distanceFromEnemy < 100) {
			Log.debug("공격이 완료되었다. 거리가 너무 가까우므로 도망가자.");
			ActionUtil.moveToPosition(allianceUnitManager, allianceUnit, backPosition);
			allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.RUNAWAY_FROM_ENEMY);
		    } else {
			Log.debug("공격이 완료되었다. 거리가 적당히 떨어져 있으니, 적의 반응을 기다리며 대기한다.");
			ActionUtil.stop(allianceUnitManager, allianceUnit);
			allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.WAIT_ENEMY);
		    }
		    break;
		}
		if (allianceUnit.isAttackFrame()) {
		    Log.debug("Skip: 아직 공격 모션 중이다.");
		    break;
		}

		Log.debug("Skip: 공격 준비 중이다.");
		break;
	    case MOVE_TO_ENEMY:
		if (distanceFromEnemy > 150) {
		    Log.debug("적이 너무 멀리 떨어져 있다. 적에게 다가가자.");
		    ActionUtil.patrolToEnemyUnit(allianceUnitManager, allianceUnit, enemyUnit);
		    allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.MOVE_TO_ENEMY);
		} else {
		    Log.debug("적이 충분히 가까이 왔으므로 대기하자.");
		    ActionUtil.stop(allianceUnitManager, allianceUnit);
		    allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.WAIT_ENEMY);
		    onFrame(gameData);
		}
		break;
	    case RUNAWAY_FROM_ENEMY:
		if (isEnemyUnitLookingCurrentAllianceUnit) {
		    if (distanceFromEnemy < 100) {
			Log.debug("적이 아군을 보고 있고, 너무 가까우면 계속 도망간다.");
			ActionUtil.moveToPosition(allianceUnitManager, allianceUnit, backPosition);
			allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.RUNAWAY_FROM_ENEMY);
		    } else {
			Log.debug("적이 아군을 보고 있지만 거리가 떨어져 있으면 기다린다.");
			ActionUtil.stop(allianceUnitManager, allianceUnit);
			allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.WAIT_ENEMY);
		    }
		} else {
		    Log.debug("적이 아군을 보고 있지 않으므로 기다린다.");
		    ActionUtil.stop(allianceUnitManager, allianceUnit);
		    allianceUnitManager.updateLastStatus(allianceUnit, UnitStatus.WAIT_ENEMY);
		}
		break;
	    }
	}
    }

    private void printUnitInfo(Unit unit) {
	if (null != unit) {
	    String unitId = "[" + unit.getID() + "] ";

	    // 현재 수행 가능한 액션을 로깅
	    String posibility = unitId + "Possible action: ";
	    if (unit.canStop()) {
		posibility += "[Stop] ";
	    }
	    if (unit.canMove()) {
		posibility += "[Move] ";
	    }
	    if (unit.canAttackMove()) {
		posibility += "[Attack Move] ";
	    }
	    if (unit.canHoldPosition()) {
		posibility += "[Hold] ";
	    }
	    if (unit.canPatrol()) {
		posibility += "[Patrol] ";
	    }
	    // 큰 의미가 없어서 Trace Level
	    Log.trace(posibility);

	    // 현재 동작 중인 액션을 로깅
	    String currentAction = unitId + "Current action: ";
	    if (unit.isIdle()) {
		currentAction += "[Idle] ";
	    }
	    if (unit.isAccelerating()) {
		currentAction += "[Accelerating] ";
	    }
	    if (unit.isMoving()) {
		currentAction += "[Moving] ";
	    }
	    if (unit.isBraking()) {
		currentAction += "[Braking] ";
	    }
	    if (unit.isAttacking()) {
		currentAction += "[Attacking] ";
	    }
	    if (unit.isAttackFrame()) {
		currentAction += "[Attack Frame] ";
	    }
	    if (unit.isHoldingPosition()) {
		currentAction += "[Holding] ";
	    }
	    if (unit.isFollowing()) {
		currentAction += "[Following] ";
	    }
	    if (unit.isSelected()) {
		currentAction += "[Selected] ";
	    }
	    if (unit.isStuck()) {
		currentAction += "[Stuck] ";
	    }
	    /*
	    if (unit.isInterruptible()) {
	    currentAction += "[Interruptible] ";
	    }
	    if (unit.isCompleted()) {
	    currentAction += "[Completed] ";
	    }
	    */
	    Log.trace(currentAction);

	    // 기타 정보: Target과 Order 정보를 로깅
	    String etcInfo = unitId + "Etc Info: ";
	    if (null != unit.getTarget()) {
		etcInfo += "[Target:" + unit.getTarget().getID() + "] ";
	    }
	    if (null != unit.getTargetPosition()) {
		etcInfo += "[TargetPosition:" + unit.getTargetPosition() + "] ";
	    }
	    if (null != unit.getOrder()) {
		etcInfo += "[Order:" + unit.getOrder() + "] ";
	    }
	    if (null != unit.getOrderTarget()) {
		etcInfo += "[OrderTarget:" + unit.getOrderTarget().getID() + "] ";
	    }
	    if (null != unit.getOrderTargetPosition()) {
		etcInfo += "[OrderTargetPosition:" + unit.getOrderTargetPosition() + "] ";
	    }
	    etcInfo += "[OrderTimer:" + unit.getOrderTimer() + "] ";
	    if (null != unit.getSecondaryOrder()) {
		etcInfo += "[SecondaryOrder:" + unit.getSecondaryOrder() + "] ";
	    }

	    Log.trace(etcInfo);
	}
    }
}