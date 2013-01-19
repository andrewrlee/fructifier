package uk.co.optimisticpanda.db.apply;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import uk.co.optimisticpanda.db.conf.JdbcConnectionProvider.JdbcConnection;
import uk.co.optimisticpanda.db.phase.DatabasePhase;
import uk.co.optimisticpanda.versioning.ChangeSetAndDeltaVersion;
import uk.co.optimisticpanda.versioning.ResourceChangeSetAndDeltaVersion;
import uk.co.optimisticpanda.versioning.ResourceVersionProvider;
import uk.co.optimisticpanda.versioning.VersionUtils.Difference;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class DatabaseApplier {

	private static final Logger logger = Logger.getLogger(DatabaseApplier.class);

	@Autowired
	private ScriptApplier applier;

	//TODO : handle lastRevision, outputFile, combined template file
	//TODO : Add default properties for a specific dbms type
	public void applyUpgrade(DatabasePhase phase, Resource deltaDir, Optional<File> outputFile, Optional<Resource> lastChangeToApply, Resource combineTemplate) {
		JdbcConnection connection = phase.getConnection();
		ResourceVersionProvider resourceVersionProvider = new ResourceVersionProvider(deltaDir);
		
		Difference<ChangeSetAndDeltaVersion, ResourceChangeSetAndDeltaVersion> difference = connection.getDifferences(resourceVersionProvider);
		logger.info("Already applied deltas: " + difference.getAppliedVersions());

		if (difference.sourceHasUnrecognisedAppliedVersions()) {
			logger.error("The following extra delta's have been applied that we don't know about: " + difference.getUnrecognisedAppliedVersions());
			throw new IllegalStateException("System is in a unrecognised state... exiting..");
		}

		if (difference.noDifference()) {
			logger.info("No Changes to apply!");
			return;
		}

		List<ResourceChangeSetAndDeltaVersion> missing = difference.getVersionsToBeApplied();
		logger.info("deltas to apply: " + missing);
		
		Map<String, Object> model = Maps.newHashMap();
		model.put("phase", phase);
		model.put("connectionDefinition", phase.getConnectionDefinition());
		model.put("scripts",missing);
		applier.applyScript(connection, model, combineTemplate);
	}

	public void applySingleScript(DatabasePhase phase, Resource script) {
		Map<String, Object> model = Maps.newHashMap();
		model.put("phase", phase);
		model.put("connectionDefinition", phase.getConnectionDefinition());
		applier.applyScript(phase.getConnection(), model, script);
	}
}
