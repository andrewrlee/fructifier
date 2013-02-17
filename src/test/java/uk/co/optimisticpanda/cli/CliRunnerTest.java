package uk.co.optimisticpanda.cli;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.beust.jcommander.ParameterException;

public class CliRunnerTest {

	private CliRunner cliRunner;

	@Before public void createRunner(){
		String buildLocation = "classpath:incremental.database.scripts/runner-runTest.json";
		cliRunner = new CliRunner(buildLocation, true);
	}
	
	@Test
	public void checkShowProfiles(){
		cliRunner.run(new String[]{"show", "profiles" });
	}
	@Test
	public void checkShowPhases(){
		cliRunner.run(new String[]{"show", "phases" });
	}
	
	@Test
	public void checkShowBuild(){
		cliRunner.run(new String[]{"show", "build" });
	}
	@Test
	public void checkShowHelp(){
		cliRunner.run(new String[]{"show", "help" });
	}
	
	
	@Test
	public void checkListCommandReceivesWrongNumberOfModes(){
		try{
			cliRunner.run(new String[]{"list", "profiles", "build"});
			Assert.fail("Should fail due to the wrong number of parameters");
		}catch(ParameterException e){
			Assert.assertEquals("mode must be one of:[PROFILES, PHASES, BUILD]", e.getMessage());
		}
	}
	
}
