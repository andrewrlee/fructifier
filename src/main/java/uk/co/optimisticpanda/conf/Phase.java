package uk.co.optimisticpanda.conf;

import java.util.Map;

import uk.co.optimisticpanda.util.Named;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

/**
 * A phase is a step of an upgrade process.
 * 
 * It consists of a name and connection details. It also contains a map of
 * arbitrary data that contains context information for this phase.
 */
public abstract class Phase implements Named {

	private Map<String, Object> data = Maps.newHashMap();
	private String phaseType;
	private String name;

	public Phase() {
	}

	public String getPhaseType() {
		return phaseType;
	}

	public void setPhaseType(String phaseType) {
		this.phaseType = phaseType;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public abstract void execute();
	
	@Override
	public int hashCode() {
		return Objects.hashCode(data, phaseType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Phase other = (Phase) obj;
		return Objects.equal(this.data, other.data)
				&& Objects.equal(this.phaseType, other.phaseType);
	}

}
