package uk.co.optimisticpanda.conf;

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

	public void executeProfile(String profileName) {
		printBanner();
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

	private void printBanner() {
		StringBuilder builder = new StringBuilder();
		builder.append("\n\n");
		builder.append("\t8888888888                        888    d8b  .d888 d8b\n");
		builder.append("\t888                               888    Y8P d88P\"  Y8P                  \n");
		builder.append("\t888                               888        888                         \n");
		builder.append("\t8888888 888d888 888  888  .d8888b 888888 888 888888 888  .d88b.  888d888 \n");
		builder.append("\t888     888P\"   888  888 d88P\"    888    888 888    888 d8P  Y8b 888P\"   \n");
		builder.append("\t888     888     888  888 888      888    888 888    888 88888888 888     \n");
		builder.append("\t888     888     Y88b 888 Y88b.    Y88b.  888 888    888 Y8b.     888     \n");
		builder.append("\t888     888      \"Y88888  \"Y8888P  \"Y888 888 888    888  \"Y8888  888     \n\n");
//		builder.append("\t  ____|                   |   _)   _| _)                  \n");            
//		builder.append("\t  |     __|  |   |   __|  __|  |  |    |   _ \\   __|     \n");
//		builder.append("\t  __|  |     |   |  (     |    |  __|  |   __/  |         \n");
//		builder.append("\t _|   _|    \\__,_| \\___| \\__| _| _|   _| \\___| _|         \n\n");
		System.out.println(builder.toString());
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
