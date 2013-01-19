package uk.co.optimisticpanda.db.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.co.optimisticpanda.conf.RunningOrder;
import uk.co.optimisticpanda.db.apply.ScriptApplier;
import uk.co.optimisticpanda.db.apply.DatabaseApplier;
import uk.co.optimisticpanda.db.phase.IncrementalDatabasePhase;
import uk.co.optimisticpanda.db.phase.SingleScriptDatabasePhase;
import uk.co.optimisticpanda.runner.RegisteredExtensions.ConnectionRegistration;
import uk.co.optimisticpanda.runner.RegisteredExtensions.PhaseRegistration;
import uk.co.optimisticpanda.runner.RegisteredExtensions.RegisterExtension;
import uk.co.optimisticpanda.util.TemplateApplier;

/**
 * The spring configuration for database functionality
 */
@Configuration
public class DatabaseConfiguration {

	@Autowired
	private RunningOrder runningOrder;

	/**
	 * Register this extension
	 * @formatter:off
	 */
	@Bean
	public RegisterExtension registerDatabaseExtension() {
		return new RegisterExtension()
				.connectionTypes(
						new ConnectionRegistration("database", DatabaseConnectionDefinition.class)
						)
				.typeAdaptors(
						new DelimiterLocationTypeAdaptor()
						)
				.phaseTypes(
						new PhaseRegistration("database.incremental.phase", IncrementalDatabasePhase.class),
						new PhaseRegistration("database.single.script.phase", SingleScriptDatabasePhase.class)
						);
	}
	/* @formatter:off*/

	@Bean
	public JdbcConnectionProvider jdbcProviders() {
		return new JdbcConnectionProvider(runningOrder.getConnections());
	}

	@Bean
	public ScriptApplier scriptApplier() {
		return new ScriptApplier();
	}
	
	@Bean
	public DatabaseApplier upgradeApplier() {
		return new DatabaseApplier();
	}

	@Bean
	public TemplateApplier templateApplier(){
		return new TemplateApplier();
	} 
	
}
