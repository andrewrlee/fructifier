package uk.co.optimisticpanda.cli;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.collect.Lists;

@Parameters(commandDescription = "Run Build")
public class RunCommand implements Command{

	@Parameter(names = "-phases", description = "Specify a list of phases to be run", variableArity = true)
	private List<String> phases = Lists.newArrayList();

	@Parameter(names = "-profile", description = "Specify a profile to be run")
	private String profile;

	public void run() {
		System.out.println("phase:" + phases);
		System.out.println("profile:" + profile);
	}

	public String getName() {
		return "run";
	}

}