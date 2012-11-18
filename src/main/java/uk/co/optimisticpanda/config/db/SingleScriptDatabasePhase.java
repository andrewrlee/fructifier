package uk.co.optimisticpanda.config.db;

import org.springframework.beans.factory.annotation.Autowired;

import uk.co.optimisticpanda.config.db.apply.ScriptApplier;

public class SingleScriptDatabasePhase extends DatabasePhase {

	private String script;

	@Autowired
	private transient ScriptApplier applier;

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	@Override
	public void execute() {
		applier.applyScript(this, script);
	}

}
