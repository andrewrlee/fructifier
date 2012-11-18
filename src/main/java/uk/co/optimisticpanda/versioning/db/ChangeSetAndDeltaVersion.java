package uk.co.optimisticpanda.versioning.db;

import uk.co.optimisticpanda.versioning.Version;

import com.google.common.base.Objects;

/**
 * Versioning that is split over a changeset and a delta number. 
 */
public class ChangeSetAndDeltaVersion implements Version {

	private final String changeSet;
	private final Long delta;

	public ChangeSetAndDeltaVersion(String changeSet, Long delta) {
		this.changeSet = changeSet;
		this.delta = delta;
	}

	public int compareTo(Version o) {
		if(! (o instanceof ChangeSetAndDeltaVersion) ) {
			throw new IllegalArgumentException("Can only compare ChangeSetAndDelta versions");
		}
		ChangeSetAndDeltaVersion other = ChangeSetAndDeltaVersion.class.cast(o);
		if (this.changeSet.equals(other.changeSet)) {
			return this.delta.compareTo(other.delta);
		}
		return this.changeSet.compareTo(other.changeSet);
	}
	
	public boolean isContinuationOfRange(Version o) {
		if(! (o instanceof ChangeSetAndDeltaVersion) ) {
			throw new IllegalArgumentException("Can only compare ChangeSetAndDelta versions");
		}
		ChangeSetAndDeltaVersion other = ChangeSetAndDeltaVersion.class.cast(o);
		return other.changeSet.equals(this.changeSet) && other.delta == this.delta + 1;
	}

	@Override
	public String toString() {
		return changeSet + "/" + delta;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(changeSet, delta);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChangeSetAndDeltaVersion other = (ChangeSetAndDeltaVersion) obj;
		return Objects.equal(changeSet, other.changeSet) && Objects.equal(delta, other.delta);
	}



}
