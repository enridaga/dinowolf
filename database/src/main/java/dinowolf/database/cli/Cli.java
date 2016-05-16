package dinowolf.database.cli;

import java.io.File;
import java.io.PrintStream;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Cli {

	private String data = "./dinowolf-repository";
	private String[] args;

	private static PrintStream O = System.out;
	private static PrintStream E = System.err;

	private Options options = new Options();

	public Cli(String[] args) {
		this.args = args;
		options.addOption("h", "help", false, "Show this help.");
		//options.addOption("p", "port", true, "Set the port the server will listen to (defaults to 8080).");
		//options.addOption("l", "load", true, "Load .wfbundle files from this directory.");
		options.addOption("r", "repository", true, "Repository directory (default is ./dinowolf-repository).");
		//options.addOption("b", "build", false, "(re)build the features database.");
	}

	private void help() {
		String syntax = "java [java-opts] -jar [jarfile] ";
		new HelpFormatter().printHelp(syntax, options);
		System.exit(0);
	}

	/**
	 * Parses command line arguments and acts upon them.
	 */
	public void parse() {
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			
			if (cmd.hasOption('h'))
				help();
			
			if (cmd.hasOption('r')) {
				String dataDir = cmd.getOptionValue('r');
				File dataFile = new File(dataDir);
				if (dataFile.exists() && !dataFile.canRead()) {
					throw new RuntimeException(
							"Cannot access/create repository in this directory! Please provide another directory.");
				}
				data = dataFile.getAbsolutePath();
			}
			
		} catch (ParseException e) {
			E.println("Failed to parse comand line properties");
			e.printStackTrace();
			help();
		}
	}

	public String getParamData() {
		return data;
	}

}
