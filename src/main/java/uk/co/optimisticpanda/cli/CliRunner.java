package uk.co.optimisticpanda.cli;

import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.internal.Lists;

public class CliRunner {
	
	private JCommander commander;
	private MainCommand main;
	private List<Command> commands;

	public CliRunner(String buildScriptLocation, boolean printBorder){
		main = new MainCommand();
		if(printBorder){
			printBanner();
		}
		commander = new JCommander(main);
		commander.setProgramName("fructifier");
		
		commands = Lists.newArrayList(new ShowCommand(System.out, buildScriptLocation, commander), new RunCommand());
		for (Command command : commands) {
			commander.addCommand(command.getName(), command);
			
		}
	}
	
	public void run(String[] args){
		commander.parse(args);
		if(main.isHelpRequest()){
			commander.usage();
			return;
		}
		for (Command command: commands) {
			if(command.getName().equals(commander.getParsedCommand())){
				command.run();
				break;
			}
		}
	}
	
	private void printBanner() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("\n\n");            
		builder.append("   ____|                   |   _)   _| _)                  \n");            
		builder.append("   |     __|  |   |   __|  __|  |  |    |   _ \\   __|     \n");
		builder.append("   __|  |     |   |  (     |    |  __|  |   __/  |         \n");
		builder.append("  _|   _|    \\__,_| \\___| \\__| _| _|   _| \\___| _|         \n\n");
//		builder.append("\t8888888888                        888    d8b  .d888 d8b\n");
//		builder.append("\t888                               888    Y8P d88P\"  Y8P                  \n");
//		builder.append("\t888                               888        888                         \n");
//		builder.append("\t8888888 888d888 888  888  .d8888b 888888 888 888888 888  .d88b.  888d888 \n");
//		builder.append("\t888     888P\"   888  888 d88P\"    888    888 888    888 d8P  Y8b 888P\"   \n");
//		builder.append("\t888     888     888  888 888      888    888 888    888 88888888 888     \n");
//		builder.append("\t888     888     Y88b 888 Y88b.    Y88b.  888 888    888 Y8b.     888     \n");
//		builder.append("\t888     888      \"Y88888  \"Y8888P  \"Y888 888 888    888  \"Y8888  888     \n\n");
		System.out.println(builder.toString());
	}
	
}
