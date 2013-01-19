package uk.co.optimisticpanda.versioning;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

public enum VersionUtils {
	;

	/**
	 * @param versions
	 * @return the difference between the versions stored in 2 version providers
	 */
	public static <A extends Version, B extends Version> Difference<A, B> getChanges(Supplier<List<A>> source, Supplier<List<B>> target) {
		Difference<A, B> changes = new Difference<A, B>(source.get());

		List<B> versionsAppliedToTarget = Lists.newArrayList(target.get());

		for (A version : source.get()) {
			if (!versionsAppliedToTarget.remove(version)) {
				changes.addUnrecognisedAppliedVersion(version);
			}
		}
		for (B version : versionsAppliedToTarget) {
			changes.addVersionToBeApplied(version);
		}
		return changes;
	}

	public static class Difference<A extends Version, B extends Version> {
		private List<A> unrecognisedAppliedVersion = Lists.newArrayList();
		private List<B> toBeApplied = Lists.newArrayList();
		private final List<A> appliedVersions;

		public Difference(List<A> appliedVersions) {
			this.appliedVersions = Lists.newArrayList(appliedVersions);
		}

		public void addUnrecognisedAppliedVersion(A extra) {
			unrecognisedAppliedVersion.add(extra);
		}

		public void addVersionToBeApplied(B missing) {
			toBeApplied.add(missing);
		}

		public List<A> getUnrecognisedAppliedVersions() {
			Collections.sort(unrecognisedAppliedVersion);
			return unrecognisedAppliedVersion;
		}
		
		public boolean sourceHasUnrecognisedAppliedVersions() {
			return !getUnrecognisedAppliedVersions().isEmpty();
		}
		public boolean noDifference() {
			return getVersionsToBeApplied().isEmpty();
		}

		public List<B> getVersionsToBeApplied() {
			Collections.sort(toBeApplied);
			return toBeApplied;
		}
		
		public List<A> getAppliedVersions() {
			return appliedVersions;
		}
	}

}
