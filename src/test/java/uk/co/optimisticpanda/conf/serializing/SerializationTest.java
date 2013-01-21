package uk.co.optimisticpanda.conf.serializing;


import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.entry;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.co.optimisticpanda.conf.Phase;
import uk.co.optimisticpanda.conf.RunningOrder;
import uk.co.optimisticpanda.conf.RunningOrder.ConnectionCollection;
import uk.co.optimisticpanda.conf.RunningOrder.PhaseCollection;
import uk.co.optimisticpanda.db.apply.QueryExtractor.DelimiterLocation;
import uk.co.optimisticpanda.db.conf.DatabaseConfiguration;
import uk.co.optimisticpanda.db.conf.DatabaseConnectionDefinition;
import uk.co.optimisticpanda.db.phase.DatabasePhase;
import uk.co.optimisticpanda.db.phase.IncrementalDatabasePhase;
import uk.co.optimisticpanda.db.phase.SingleScriptDatabasePhase;
import uk.co.optimisticpanda.runner.BaseConfiguration;
import uk.co.optimisticpanda.runner.RegisteredExtensionsGatherer;
import uk.co.optimisticpanda.util.JsonProvider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BaseConfiguration.class, DatabaseConfiguration.class, TestContext.class })
public class SerializationTest {

	@Autowired
	private RegisteredExtensionsGatherer registeredExtensions;

	@Autowired
	private ResourceLoader loader;
	
	private Serializer serializer;

	@Before public void setUp() {
		serializer = new Serializer(loader, registeredExtensions);
	}

	@Test
	public void testSerializingBuildConfig() {
		RunningOrder config = createConfig(createPhase());
		try {
			String serialized = serializer.toString(config);
			RunningOrder readConfig = serializer.parse(serialized, RunningOrder.class);
			assertThat(readConfig).isEqualTo(config);

		} catch (RuntimeException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testSerializingPhases() {
		PhaseCollection phases = new PhaseCollection().put("phase 1", createPhase());

		DatabasePhase phase = createPhase();

		String serialized = serializer.toString(phases);

		PhaseCollection readPhases = serializer.parse(serialized, PhaseCollection.class);

		assertThat(phases.getElements()).hasSize(1);
		Phase deserializedPhase = phases.next();
		assertThat(deserializedPhase.getPhaseType()).isEqualTo("database.incremental.phase");
		assertThat(deserializedPhase.getData()).isEqualTo(phase.getData());
		assertThat(readPhases).isEqualTo(phases);
	}

	@Test
	public void testDeSerializeMultiplePhases() {
		DatabasePhase phase = createPhase();
		SingleScriptDatabasePhase phase2 = new SingleScriptDatabasePhase();
		phase2.setConnection("db1");
		phase2.setName("02");
		phase2.script = loader.getResource("file:src/test/resources/test1/scripts/single/create_db.sql");
		PhaseCollection phases = new PhaseCollection().put("01", phase).put("02", phase2);
		RunningOrder config = createConfig();
		config.setPhases(phases);

		String serialized = serializer.toString(config);
		config = serializer.parse(serialized, RunningOrder.class);
		phases = config.getMatchingPhases("01", "02");

		assertThat(phases.getElements()).hasSize(2);
		Phase deserializedPhase = phases.next();
		assertThat(deserializedPhase.getPhaseType()).isEqualTo("database.incremental.phase");
		assertThat(deserializedPhase.getData()).isEqualTo(phase.getData());

		deserializedPhase = phases.next();
		assertThat(deserializedPhase.getPhaseType()).isEqualTo("database.single.script.phase");
		assertThat(deserializedPhase.getData()).isEqualTo(Maps.<String,Object>newHashMap());
	}

	@Test
	public void testProfiles() {
		DatabasePhase phase = createPhase();
		SingleScriptDatabasePhase phase2 = new SingleScriptDatabasePhase();
		phase2.setConnection("db1");
		phase2.setName("02");
		phase2.script = loader.getResource("file:src/test/resources/test1/scripts/single/create_db.sql");
		PhaseCollection phases = new PhaseCollection().put("01", phase).put("02", phase2);
		RunningOrder config = createConfig();
		config.setPhases(phases);

		Map<String,List<String>> profiles = Maps.newLinkedHashMap();
		profiles.put("dev", Lists.newArrayList("teardown","createChangeLog","apply", "testData"));
		profiles.put("prodSetUp", Lists.newArrayList("teardown","createChangeLog","apply"));
		profiles.put("prodUpgrade", Lists.newArrayList("apply"));
		config.setProfiles(profiles);
		
		String serialized = serializer.toString(config);
		
		config = serializer.parse(serialized, RunningOrder.class);
		assertThat(config.getProfiles()) //
				.contains(entry("dev",Lists.newArrayList("teardown","createChangeLog","apply", "testData")))
				.contains(entry("prodSetUp",Lists.newArrayList("teardown","createChangeLog","apply")))
				.contains(entry("prodUpgrade",Lists.newArrayList("apply")));
	}
	
	private RunningOrder createConfig(Phase... phases) {
		RunningOrder config = new RunningOrder();

		DatabaseConnectionDefinition details = new DatabaseConnectionDefinition();
		details.setConnectionUrl("url");
		details.setPassword("password");
		details.setUser("user");
		details.setConnectionType("database");
		details.setChangeLogTableName("changeLog");
		details.setDelimiter("delimiter");
		details.setEncoding("enc");
		details.setLineEnding("lendin");
		details.setSeparatorLocation(DelimiterLocation.END_OF_LINE);
		details.setSeparator("separator");

		ConnectionCollection c = new ConnectionCollection();
		c.put("db1", details);
		config.setConnections(c);

		PhaseCollection p = new PhaseCollection();
		if (phases.length != 0) {
			int i = 0;
			for (Phase phase : phases) {
				p.put("phase" + i++, phase);
			}
			config.setPhases(p);
		}
		return config;
	}

	private DatabasePhase createPhase() {
		IncrementalDatabasePhase phase = new IncrementalDatabasePhase();
//		phase.setDeltaDir(new File(new File("bobbins").getAbsolutePath()));
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("fooArrayList", Arrays.asList("a", "b", "c"));
		map.put("fooMap", Collections.singletonMap("foo", "blum"));
		map.put("fooPrim", "bar");
		phase.setData(map);
		phase.setConnection("name");
		return phase;
	}

}

@org.springframework.context.annotation.Configuration
class TestContext {
	
	@Autowired
	private ResourceLoader loader;
	
	@Bean
	public JsonProvider provider() {
		Resource resource = loader.getResource("file:src/test/resources/runner-runTest.json");
		return new JsonProvider(resource, new Properties());
	}
}
