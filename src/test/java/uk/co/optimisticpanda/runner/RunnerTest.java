package uk.co.optimisticpanda.runner;

import static junit.framework.Assert.fail;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.io.Resources;

public class RunnerTest {

	@Test
	public void testRunner() throws IOException {
		try {

			new Runner( //
					"classpath:runner-test1.json", //
					Optional.of("classpath:runner-test1.properties")//
			).run("createDatabase");

		} catch (RuntimeException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testRunner2() throws IOException {
		URL resource = Resources.getResource("runner-test2.json");
		String json = Resources.toString(resource, Charsets.UTF_8);
		Runner runner = new Runner(json);

		runner.run("update", "update2");
	}

}
