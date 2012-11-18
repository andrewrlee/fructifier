package uk.co.optimisticpanda.conf;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.google.common.base.Objects;

/**
 * This is parsed directly from a json configuration file.
 */
public class RunningOrder {

	@Autowired
	private transient AutowireCapableBeanFactory factory;

	private ConnectionCollection connections = new ConnectionCollection();
	private PhaseCollection phases = new PhaseCollection();

	public PhaseCollection getPhases(String... phasesToRun) {
		return phases.getMatchingPhases(phasesToRun);
	}

	public void setPhases(PhaseCollection phases) {
		this.phases = phases;
	}

	public ConnectionCollection getConnections() {
		return connections;
	}

	public void setConnections(ConnectionCollection connections) {
		this.connections = connections;
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

	public void execute(String... phasesToRun) {
		for (Phase phase : getPhases(phasesToRun)) {
			factory.autowireBean(phase);
			phase.execute();
		}
	}

	public static class ConnectionCollection extends AbstractCollection<Connection, ConnectionCollection> {
	}

	public static class PhaseCollection extends AbstractCollection<Phase, PhaseCollection> {
		private transient static Logger log = Logger.getLogger(PhaseCollection.class);

		public PhaseCollection getMatchingPhases(String[] phasesToRun) {
			PhaseCollection result = new PhaseCollection();
			for (String phase : phasesToRun) {
				if (!getElements().containsKey(phase)) {
					log.debug("Ignoring phase: " + phase + " as not specified in json file.");
				} else {
					result.put(phase, getElements().get(phase));
				}
			}
			log.debug("Found the following matching phases: " + result.getElements().keySet());
			return result;
		}

	}

}
