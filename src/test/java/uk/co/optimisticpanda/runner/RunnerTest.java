package uk.co.optimisticpanda.runner;

import static junit.framework.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.google.common.base.Optional;

public class RunnerTest {

	@Test
	public void runWithoutProperties() throws IOException {
		try {
			
			new Runner( //
					"classpath:runner-runTestWithoutProperties.json" //
					).run("createDatabase");
			
		} catch (RuntimeException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void run() throws IOException {
		try {

			new Runner( //
					"classpath:runner-runTest.json", //
					Optional.of("classpath:runner-runTest.properties")//
			).run("createDatabase");

		} catch (RuntimeException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void runProfile() throws IOException {
		Runner runner = new Runner("classpath:runner-runProfileTest.json", Optional.of("classpath:runner-runProfileTest.properties"));
		runner.runProfile("test-profile");
	}

}
