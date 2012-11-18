package uk.co.optimisticpanda.conf;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import com.google.common.base.Objects;


public class PhaseCollection implements Iterator<Phase>, Iterable<Phase> {
	private transient static Logger log = Logger.getLogger(PhaseCollection.class);
	private LinkedHashMap<String, Phase> phases = new LinkedHashMap<String, Phase>();
	private transient Iterator<Phase> iterator;
	
	public LinkedHashMap<String, Phase> getPhases() {
		return phases;
	}

	public int size() {
		return phases.size();
	}

	public Iterator<Phase> iterator() {
		if(iterator == null) {
			iterator = phases.values().iterator();
		}
		return iterator;
	}
	
	public boolean hasNext() {
		return iterator().hasNext();
	}

	public Phase next() {
		return iterator().next();
	}

	public void remove() {
		throw new UnsupportedOperationException("Does not support remove");
	}

	public PhaseCollection put(String name, Phase phase) {
		phase.setName(name);
		phases.put(name, phase);
		return this;
	}

	public PhaseCollection getMatchingPhases(String[] phasesToRun) {
		PhaseCollection result = new PhaseCollection();
		for (String phase : phasesToRun) {
			if(!phases.containsKey(phase)) {
				log.debug("Ignoring phase: " + phase + " as not specified in json file.");
			}else {
				result.put(phase, phases.get(phase));
			}
		}
		log.debug("Found the following matching phases: " + result.phases.keySet());
		return result;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(phases);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final PhaseCollection other = (PhaseCollection) obj;
		return Objects.equal(this.phases, other.phases); //
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass()).add("phases", phases)
				.toString();
	}

}
