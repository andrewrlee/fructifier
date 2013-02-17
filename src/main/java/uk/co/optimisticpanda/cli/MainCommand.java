package uk.co.optimisticpanda.cli;

import com.beust.jcommander.Parameter;

public class MainCommand {

		@Parameter(names = {"--help", "-h"}, description="Displays this menu.", help = true)
		private boolean help;

		public boolean isHelpRequest(){
			return help;
		}
		
	}
