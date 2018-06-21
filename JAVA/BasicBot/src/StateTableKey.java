
public class StateTableKey {
    private UnitUtil.DistanceType distanceType;
    private boolean allianceDirection;
    private boolean enemyDirection;

    public StateTableKey(UnitUtil.DistanceType distanceType, boolean allianceDirection, boolean enemyDirection) {
	this.distanceType = distanceType;
	this.allianceDirection = allianceDirection;
	this.enemyDirection = enemyDirection;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}

	if (this.getClass() != obj.getClass()) {
	    return false;
	}

	if (this == obj) {
	    return true;
	}

	StateTableKey that = (StateTableKey) obj;

	if (this.distanceType.equals(that.distanceType) && this.allianceDirection == that.allianceDirection && this.enemyDirection == that.enemyDirection) {
	    return true;
	}

	return false;
    }

    @Override
    public int hashCode() {
	int result = distanceType.hashCode();
	result += allianceDirection ? 1 : 0;
	result += enemyDirection ? 1 : 0;
	return result;
    }

    @Override
    public String toString() {
	return "StateTableKey(distanceType=" + distanceType + ", allianceDirection=" + allianceDirection + ", enemyDirection=" + enemyDirection + ")";
    }
}
