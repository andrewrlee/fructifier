package uk.co.optimisticpanda.cli;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uk.co.optimisticpanda.conf.Phase;
import uk.co.optimisticpanda.runner.Runner;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.google.common.collect.Lists;

@Parameters(commandDescription = "Lists information about the embedded script")
public class ShowCommand implements Command{

	public static final String DESCRIPTION = "The type of information to display where options= [profiles|phases|build|help]";
	private final PrintStream printStream;
	private Runner runner;
	private final JCommander commander;

	public enum Mode {
		PROFILES, PHASES, BUILD, HELP
	}

	public ShowCommand(PrintStream printStream, String buildScriptLocation, JCommander commander) {
		this.printStream = printStream;
		this.commander = commander;
		runner = new Runner(buildScriptLocation);
	}

	@Parameter(description = DESCRIPTION, required = true)
	private List<Mode> mode = Lists.newArrayList();
	

	public void run(){
		if(mode.isEmpty() || mode.size() > 1){
			throw new ParameterException("mode must be one of:" + Arrays.toString(Mode.values()));
		}
		printStream.println("Executing Show Command in '" + mode.get(0).name().toLowerCase()+ "' mode.\n");
		
		switch (mode.iterator().next()) {
		case BUILD:
			printStream.println("Build Script:");
			printStream.println(runner.getScript());
			break;
		case PHASES:
			Map<String, Phase> phases = runner.getPhases();
			printStream.println("Phases:");
			for (Entry<String, Phase> entry : phases.entrySet()) {
				printStream.println("\t" + entry.getKey() + ":\n\t\t" + entry.getValue());
			}
			break;
		case PROFILES:
			Map<String, List<String>> profiles = runner.getProfiles();
			printStream.println("Profiles:");
			for (Entry<String, List<String>> entry : profiles.entrySet()) {
				printStream.println("\t" + entry.getKey() + ":\n\t\t" + entry.getValue());
			}
			break;
		case HELP:
			StringBuilder builder = new StringBuilder();
			commander.usage(builder);
			printStream.println(builder.toString());
			break;

		default:
			throw new IllegalStateException("Don't recognise mode: " + mode);
		}
	}

	public String getName() {
		return "show";
	}
}