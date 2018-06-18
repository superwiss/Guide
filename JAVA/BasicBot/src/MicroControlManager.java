import bwapi.Position;
import bwapi.Unit;

// MagiBot을 빠르게 연습시키기 위해서, 유즈맵으로 미션을 만들어 MagiBot이 미션을 해결하는 방식으로 훈련한다.
public class MicroControlManager {
    private static MicroControlManager instance = new MicroControlManager();

    public static MicroControlManager Instance() {
	return instance;
    }

    public void onFrame(GameData gameData) {
	UnitManager myUnitManager = gameData.getMyUnitManager();
	UnitManager enemyUnitManager = gameData.getEnemyUnitManager();
	for (Unit myUnit : myUnitManager.getUnitList()) {

	    printUnitInfo(myUnit);

	    Unit enemyUnit = UnitUtil.selectEnemyTargetUnit(myUnit, enemyUnitManager);
	    int distance = -1;
	    if (null != enemyUnit) {
		distance = myUnit.getDistance(enemyUnit);
		Log.debug("Distance: %d, Cooldown: %d, isInWeaponRange: %b, MyHp: %d, EnemyHp: %d, CurrentStatus: %s, BeforeStatus: %s", distance, myUnit.getGroundWeaponCooldown(),
			myUnit.isInWeaponRange(enemyUnit), myUnit.getHitPoints(), enemyUnit.getHitPoints(), myUnitManager.getUnitCurrentStatus(myUnit),
			myUnitManager.getUnitBeforeStatus(myUnit));
	    } else {
		return;
	    }
	    // printUnitInfo(enemyUnit);

	    boolean isEnemyUnitLookingMyUnit = UnitUtil.isEnemyUnitLookingMyUnit(myUnit, enemyUnit, Math.PI);

	    switch (myUnitManager.getUnitCurrentStatus(myUnit)) {
	    case IDLE:
		// 공격 대상을 찾는다.
		if (null != enemyUnit) {
		    Log.debug("\tFound Enemy. %s will atack %s", UnitUtil.getUnitAsString(myUnit), UnitUtil.getUnitAsString(enemyUnit));
		    myUnitManager.setEnemy(myUnit, enemyUnit);

		    actionMoveToEnemyUnit(myUnitManager, myUnit, enemyUnit);
		    onFrame(gameData);
		}
		break;
	    // TODO wait 기능 만들기
	    case WAIT:
		if (isEnemyUnitLookingMyUnit) {
		    // 적이 나를 바라보고 있다.
		    if (enemyUnit.isMoving()) {
			// 적이 이동 중이다.
			if (0 == myUnit.getGroundWeaponCooldown()) {
			    // 무기가 준비 되었다.
			    if (myUnit.isInWeaponRange(enemyUnit)) {
				Log.debug("적이 나를 향해 달려오고 있고, 무기는 준비 되었고, 사거리 이내라면 공격한다.");
				actionAttackEnemyUnit(myUnitManager, myUnit, enemyUnit);
			    } else {
				Log.debug("적이 나를 향해 달려오고 있고, 무기는 준비 되었지만, 사거리 밖이라면 계속 대기한다.");
			    }
			    break;
			} else {
			    // 무기가 준비되지 않았다.
			    if (distance < 100) {
				Log.debug("적이 나를 향해 달려오고 있고, 무기는 준비되지 않았고, 적이 너무 가까이 있으면 도망간다.");
				actionRunAway(myUnitManager, myUnit, enemyUnit);
			    } else {
				Log.debug("적이 나를 향해 달려오고 있고, 무기는 준비되지 않았지만, 적이 멀리 떨어져 있으면 계속 대기한다.");
			    }
			    break;
			}
		    } else {
			// 적이 대기 중이다.
			if (0 == myUnit.getGroundWeaponCooldown()) {
			    // 무기가 준비 되었다.
			    if (myUnit.isInWeaponRange(enemyUnit)) {
				Log.debug("적이 대기 중이고, 무기는 준비 되었고, 사거리 이내라면 공격한다.");
				actionAttackEnemyUnit(myUnitManager, myUnit, enemyUnit);
			    } else {
				Log.debug("적이 대기 중이고, 무기는 준비 되었지만, 사거리 밖이라면 적에게 다가간다.");
				actionMoveToEnemyUnit(myUnitManager, myUnit, enemyUnit);
			    }
			    break;
			} else {
			    // 무기가 준비되지 않았다.
			    if (distance < 100) {
				Log.debug("적이 대기 중이고, 무기는 준비되지 않았고, 적이 너무 가까이 있으면 대기한다.");
			    } else {
				Log.debug("적이 대기 중이고, 무기는 준비되지 않았지만, 적이 멀리 떨어져 있으면 적에게 다가간다.");
				actionMoveToEnemyUnit(myUnitManager, myUnit, enemyUnit);
			    }
			    break;
			}
		    }
		} else {
		    // 적이 나를 바라보고 있지 않다.
		    if (enemyUnit.isMoving()) {
			// 적이 이동 중이다.
			if (0 == myUnit.getGroundWeaponCooldown()) {
			    // 무기가 준비 되었다.
			    if (myUnit.isInWeaponRange(enemyUnit)) {
				Log.debug("적이 도망중이고, 무기는 준비 되었고, 사거리 이내라면 공격한다.");
				actionAttackEnemyUnit(myUnitManager, myUnit, enemyUnit);
			    } else {
				Log.debug("적이 도망중이고, 무기는 준비 되었지만, 사거리 밖이라면 적에게 다가간다.");
				actionMoveToEnemyUnit(myUnitManager, myUnit, enemyUnit);
			    }
			    break;
			} else {
			    // 무기가 준비되지 않았다.
			    if (distance < 100) {
				Log.debug("적이 도망중이고, 무기는 준비되지 않았고, 적이 너무 가까이 있으면 대기한다.");
			    } else {
				Log.debug("적이 도망중이고, 무기는 준비되지 않았지만, 적이 멀리 떨어져 있으면 적에게 다가간다.");
				actionMoveToEnemyUnit(myUnitManager, myUnit, enemyUnit);
			    }
			    break;
			}
		    } else {
			// 적이 대기 중이다.
			if (0 == myUnit.getGroundWeaponCooldown()) {
			    // 무기가 준비 되었다.
			    if (myUnit.isInWeaponRange(enemyUnit)) {
				Log.debug("적이 대기 중이고, 무기는 준비 되었고, 사거리 이내라면 공격한다.");
				actionAttackEnemyUnit(myUnitManager, myUnit, enemyUnit);
			    } else {
				Log.debug("적이 대기 중이고, 무기는 준비 되었지만, 사거리 밖이라면 적에게 다가간다.");
				actionMoveToEnemyUnit(myUnitManager, myUnit, enemyUnit);
			    }
			    break;
			} else {
			    // 무기가 준비되지 않았다.
			    if (distance < 100) {
				Log.debug("적이 대기 중이고, 무기는 준비되지 않았고, 적이 너무 가까이 있으면 대기한다.");
			    } else {
				Log.debug("적이 대기 중이고, 무기는 준비되지 않았지만, 적이 멀리 떨어져 있으면 적에게 다가간다.");
				actionMoveToEnemyUnit(myUnitManager, myUnit, enemyUnit);
			    }
			    break;
			}
		    }
		}
	    case ATTACK_ENEMY_UNIT:
		if (0 != myUnit.getGroundWeaponCooldown()) {
		    if (distance < 100) {
			Log.debug("공격이 완료되었다. 거리가 너무 가까우므로 도망가자.");
			actionRunAway(myUnitManager, myUnit, enemyUnit);
		    } else {
			Log.debug("공격이 완료되었다. 거리가 적당히 떨어져 있으니, 적의 반응을 기다리며 대기한다.");
			actionWait(myUnitManager, myUnit);
		    }
		    break;
		}
		if (myUnit.isAttackFrame()) {
		    Log.debug("Skip: 아직 공격 모션 중이다.");
		    break;
		}

		Log.debug("Skip: 공격 준비 중이다.");
		break;
	    case MOVE_TO_EMEMY_UNIT:
		if (distance > 150) {
		    Log.debug("적이 너무 멀리 떨어져 있다. 적에게 다가가자.");
		    actionMoveToEnemyUnit(myUnitManager, myUnit, enemyUnit);
		} else {
		    Log.debug("적이 충분히 가까이 왔으므로 대기하자.");
		    actionWait(myUnitManager, myUnit);
		    onFrame(gameData);
		}
		break;
	    case RUN_AWAY:
		if (isEnemyUnitLookingMyUnit) {
		    if (distance < 100) {
			Log.debug("적이 나를 보고 있고, 너무 가까우면 계속 도망간다.");
			actionRunAway(myUnitManager, myUnit, enemyUnit);
		    } else {
			Log.debug("적이 나를 보고 있지만 거리가 떨어져 있으면 기다린다.");
			actionWait(myUnitManager, myUnit);
		    }
		} else {
		    Log.debug("적이 나를 보고 있지 않으므로 기다린다.");
		    actionWait(myUnitManager, myUnit);
		}
		break;
	    }
	}
    }

    private void actionWait(UnitManager myUnitManager, Unit myUnit) {
	if (myUnitManager.getUnitCurrentStatus(myUnit).equals(UnitStatus.WAIT)) {
	    Log.debug("Skip : 대기 중이다.");
	    return;
	} else {
	    myUnitManager.setUnitStatus(myUnit, UnitStatus.WAIT);
	    Log.debug(">>>> 대기한다.");
	    return;
	}
    }

    private void actionAttackEnemyUnit(UnitManager myUnitManager, Unit myUnit, Unit enemyUnit) {
	// 적이 있으면, 적을 공격한다.
	if (myUnitManager.getUnitCurrentStatus(myUnit).equals(UnitStatus.ATTACK_ENEMY_UNIT)) {
	    Log.debug("Skip : 공격하는 중이다.");
	    return;
	} else {
	    myUnitManager.setUnitStatus(myUnit, UnitStatus.ATTACK_ENEMY_UNIT);
	    myUnit.attack(enemyUnit);
	    Log.debug(">>>> 적을 공격한다!");
	    return;
	}
    }

    private void actionMoveToEnemyUnit(UnitManager myUnitManager, Unit myUnit, Unit enemyUnit) {
	if (myUnitManager.getUnitCurrentStatus(myUnit).equals(UnitStatus.MOVE_TO_EMEMY_UNIT)) {
	    Log.debug("Skip : 적에게 다가가는 중이다.");
	    return;
	} else {
	    myUnitManager.setUnitStatus(myUnit, UnitStatus.MOVE_TO_EMEMY_UNIT);
	    myUnit.patrol(enemyUnit.getPosition());
	    //myUnit.move(enemyUnit.getPosition());
	    Log.debug(">>>> 적이 멀리 있다. 적에게 다가가자. (패트롤)");
	    return;
	}
    }

    private void actionRunAway(UnitManager myUnitManager, Unit myUnit, Unit enemyUnit) {
	if (myUnitManager.getUnitCurrentStatus(myUnit).equals(UnitStatus.RUN_AWAY)) {
	    Log.debug("Skip : 도망가는 중이다.");
	    return;
	} else {
	    myUnitManager.setUnitStatus(myUnit, UnitStatus.RUN_AWAY);
	    myUnit.move(new Position(0, 0));
	    Log.debug(">>>> 도망가자!");
	    return;
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
	    Log.debug(currentAction);
	}
    }
}