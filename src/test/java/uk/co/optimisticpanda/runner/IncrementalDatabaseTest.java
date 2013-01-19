package uk.co.optimisticpanda.runner;

import static junit.framework.Assert.fail;

import org.junit.Test;

import com.google.common.base.Optional;

public class IncrementalDatabaseTest {

	@Test
	public void check() {
		try {
			Runner runner = new Runner( //
					"classpath:incremental.database.scripts/runner-runTest.json", //
					Optional.of("classpath:incremental.database.scripts/runner-runTest.properties")//
			);
			
			runner.runProfile("rebuild");
			System.out.println("*******************************************************");
			System.out.println("*******************************************************");
			runner.runProfile("update");

		} catch (RuntimeException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
