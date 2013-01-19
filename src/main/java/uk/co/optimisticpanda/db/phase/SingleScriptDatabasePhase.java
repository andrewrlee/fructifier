package uk.co.optimisticpanda.db.phase;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import uk.co.optimisticpanda.db.apply.DatabaseApplier;

import com.google.common.base.Objects;

//Directly applies a single sql script with variable replacement (using freemarker)
public class SingleScriptDatabasePhase extends DatabasePhase {

	public Resource script;

	@Autowired
	private transient DatabaseApplier applier;

	@Override
	public void execute() {
		applier.applySingleScript(this, script);
	}

	@Override
	public String toString() {
		return toStringHelper(this.getClass())//
				.add("name", getName()).add("script", script) //
				.add("phaseType", getPhaseType()) .add("data", getData())//
				.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(script, super.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SingleScriptDatabasePhase other = (SingleScriptDatabasePhase) obj;
		return super.equals(obj) && equal(this.script, other.script);
	}
}
