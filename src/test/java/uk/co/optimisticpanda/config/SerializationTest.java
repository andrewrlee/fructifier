package uk.co.optimisticpanda.config;

import static junit.framework.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
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

import uk.co.optimisticpanda.conf.ConnectionCollection;
import uk.co.optimisticpanda.conf.Phase;
import uk.co.optimisticpanda.conf.PhaseCollection;
import uk.co.optimisticpanda.conf.RunningOrder;
import uk.co.optimisticpanda.config.db.DatabaseConfiguration;
import uk.co.optimisticpanda.config.db.DatabaseConnection;
import uk.co.optimisticpanda.config.db.DatabasePhase;
import uk.co.optimisticpanda.config.db.IncrementalDatabasePhase;
import uk.co.optimisticpanda.config.db.SingleScriptDatabasePhase;
import uk.co.optimisticpanda.config.db.apply.QueryExtractor.SeparatorLocation;
import uk.co.optimisticpanda.config.serializing.Serializer;
import uk.co.optimisticpanda.runner.BaseConfiguration;
import uk.co.optimisticpanda.runner.JsonProvider;
import uk.co.optimisticpanda.runner.RegisteredExtensions;

import com.google.common.collect.Maps;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BaseConfiguration.class, DatabaseConfiguration.class, TestContext.class })
public class SerializationTest {

	@Autowired
	private RegisteredExtensions registeredExtensions;

	private Serializer serializer;

	@Before
	public void setUp() {
		serializer = new Serializer(registeredExtensions);
	}

	@Test
	public void testSerializingBuildConfig() {
		RunningOrder config = createConfig(createPhase());
		try {
			String serialized = serializer.toString(config);
			RunningOrder readConfig = serializer.parseRunningOrder(serialized);
			assertEquals(config, readConfig);

		} catch (RuntimeException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testSerializingPhases() {
		PhaseCollection phases = new PhaseCollection().put("phase 1", createPhase());

		DatabasePhase phase = createPhase();

		String serialized = serializer.toString(phases);

		PhaseCollection readPhases = serializer.parsePhases(serialized);

		assertEquals("contains one phase", phases.size(), 1);
		Phase deserializedPhase = phases.next();
		assertEquals("database.incremental.phase", deserializedPhase.getPhaseType());
		assertEquals(phase.getData(), deserializedPhase.getData());
		assertEquals(phases, readPhases);
	}

	@Test
	public void testDeSerializeMultiplePhases() {
		DatabasePhase phase = createPhase();
		SingleScriptDatabasePhase phase2 = new SingleScriptDatabasePhase();
		phase2.setConnectionName("db1");
		phase2.setName("02");
		phase2.setScript("classpath:/pla");
		PhaseCollection phases = new PhaseCollection().put("01", phase).put("02", phase2);
		RunningOrder config = createConfig();
		config.setPhases(phases);

		String serialized = serializer.toString(config);
		config = serializer.parseRunningOrder(serialized);
		phases = config.getPhases("01", "02");

		assertEquals("contains two phase", phases.size(), 2);
		Phase deserializedPhase = phases.next();
		assertEquals("database.incremental.phase", deserializedPhase.getPhaseType());
		assertEquals(phase.getData(), deserializedPhase.getData());

		deserializedPhase = phases.next();
		assertEquals("database.single.script.phase", deserializedPhase.getPhaseType());
		assertEquals(Maps.newHashMap(), deserializedPhase.getData());
	}

	private RunningOrder createConfig(Phase... phases) {
		RunningOrder config = new RunningOrder();

		DatabaseConnection details = new DatabaseConnection();
		details.setConnectionUrl("url");
		details.setPassword("password");
		details.setUser("user");
		details.setConnectionType("database");
		details.setChangeLogTableName("changeLog");
		details.setDelimiter("delimiter");
		details.setEncoding("enc");
		details.setLineEnding("lendin");
		details.setSeparatorLocation(SeparatorLocation.END_OF_LINE);
		details.setSeperator("seperator");

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
		phase.setDeltaDir(new File(new File("bobbins").getAbsolutePath()));
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("fooArrayList", Arrays.asList("a", "b", "c"));
		map.put("fooMap", Collections.singletonMap("foo", "blum"));
		map.put("fooPrim", "bar");
		phase.setData(map);
		phase.setConnectionName("name");
		return phase;
	}

}

@org.springframework.context.annotation.Configuration
class TestContext {
	
	@Autowired
	private ResourceLoader loader;
	
	@Bean
	public JsonProvider provider() {
		Resource resource = loader.getResource("file:src/test/resources/runner-test1.json");
		return new JsonProvider(resource, new Properties());
	}
}
