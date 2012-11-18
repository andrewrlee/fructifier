package uk.co.optimisticpanda.db.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import uk.co.optimisticpanda.db.apply.ScriptApplier;

public class SingleScriptDatabasePhase extends DatabasePhase {

	private Resource script;

	@Autowired
	private transient ScriptApplier applier;

	public Resource getScript() {
		return script;
	}

	public void setScript(Resource script) {
		this.script = script;
	}

	@Override
	public void execute() {
		applier.applyScript(this, script);
	}

}
