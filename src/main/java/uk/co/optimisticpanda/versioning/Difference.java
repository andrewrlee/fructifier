package uk.co.optimisticpanda.versioning;

import com.google.common.base.Objects;

public class Difference<D extends Version> implements Comparable<Difference<D>>{

	public enum Type {
		MISSING, EXTRA;
	}

	private D version;
	private Type type;

	public Difference(D version, Type type) {
		this.version = version;
		this.type = type;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(type, version);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Difference<?> other = (Difference<?>) obj;
		return Objects.equal(this.type, other.type)
				&& Objects.equal(this.version, other.version);
	}

	public int compareTo(Difference<D> o) {
		return version.compareTo(o.version);
	}

}
