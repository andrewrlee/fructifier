package uk.co.optimisticpanda.conf;

import uk.co.optimisticpanda.runner.PhaseExecutor;

import com.google.common.base.Objects;

/**
 * This is parsed directly from a json configuration file. After instantiation
 * the {@link PhaseExecutor} gets injected into it by the spring configuration. 
 */
public class RunningOrder {

	private ConnectionCollection connections = new ConnectionCollection();
	private PhaseCollection phases = new PhaseCollection();
	private transient PhaseExecutor phaseExecutor;

	public void setPhaseExecutor(PhaseExecutor phaseExecutor) {
		this.phaseExecutor = phaseExecutor;
	}

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
			phaseExecutor.execute(phase);
		}
	}

}
