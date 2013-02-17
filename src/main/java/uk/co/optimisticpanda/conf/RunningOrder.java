package uk.co.optimisticpanda.conf;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

/**
 * This is parsed directly from a json configuration file.
 */
public class RunningOrder {

	private static final Logger logger = Logger.getLogger(RunningOrder.class);

	@Autowired
	private transient AutowireCapableBeanFactory factory;

	private ConnectionCollection connections = new ConnectionCollection();
	private PhaseCollection phases = new PhaseCollection();
	private Map<String, List<String>> profiles = Maps.newHashMap();

	public void setPhases(PhaseCollection phases) {
		this.phases = phases;
	}

	public PhaseCollection getMatchingPhases(String... phaseNames) {
		return phases.getMatchingPhases(phaseNames);
	}

	public ConnectionCollection getConnections() {
		return connections;
	}

	public void setConnections(ConnectionCollection connections) {
		this.connections = connections;
	}

	public Map<String, List<String>> getProfiles() {
		return profiles;
	}

	public void setProfiles(Map<String, List<String>> profiles) {
		this.profiles = profiles;
	}

	public LinkedHashMap<String, Phase> getPhases() {
		return phases.getElements();
	}
	
	public void executeProfile(String profileName) {
		logger.info("Executing profile: " + profileName);
		if (!profiles.containsKey(profileName)) {
			logger.error("No profile named: " + profileName + ", possible options: " + profiles.keySet());
			return;
		}
		execute(profiles.get(profileName).toArray(new String[0]));
	}

	public void execute(String... phaseNames) {
		for (Phase phase : getMatchingPhases(phaseNames)) {
			factory.autowireBean(phase);
			logger.info("Executing phase: " + phase);
			phase.execute();
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(connections, phases, super.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final RunningOrder other = (RunningOrder) obj;
		return Objects.equal(this.connections, other.connections) //
				&& Objects.equal(this.phases, other.phases);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass())//
				.add("connections", connections) //
				.add("phases", phases)//
				.toString();
	}

	public static class ConnectionCollection extends AbstractNamedCollection<ConnectionDefinition, ConnectionCollection> {
	}

	public static class PhaseCollection extends AbstractNamedCollection<Phase, PhaseCollection> {

		public PhaseCollection getMatchingPhases(String... phaseNames) {
			PhaseCollection result = new PhaseCollection();
			for (String phase : phaseNames) {
				if (!getElements().containsKey(phase)) {
					logger.debug("Ignoring phase: " + phase + " as not specified in json file.");
				} else {
					result.put(phase, getElements().get(phase));
				}
			}
			logger.info("Found the following matching phases: " + result.getElements().keySet());
			return result;
		}

	}

}
