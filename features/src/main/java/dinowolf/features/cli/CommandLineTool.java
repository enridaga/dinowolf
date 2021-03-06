package dinowolf.features.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;

import javax.inject.Inject;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;

import dinowolf.annotation.FromTo;
import dinowolf.features.Feature;
import dinowolf.features.FeaturesMap;
import dinowolf.features.FeaturesMapExtractor;
import io.airlift.airline.Cli;
import io.airlift.airline.Cli.CliBuilder;
import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.Option;

public class CommandLineTool {

	private final static PrintStream P = System.out;

	public static void main(String[] args) throws Exception {
		new CommandLineTool().parse(args);
		System.out.println();
	}

	private static Cli<DWTool> parser() {
		CliBuilder<DWTool> build = Cli.<DWTool> builder("dinowolf").withDescription("Dinowolf Tool")
				.withDefaultCommand(HelpCommand.class).withCommand(FeaturesCommand.class)
				.withCommand(PortmapsCommand.class); // Statistics

		return build.build();
	}

	public CommandLineTool() {
	};

	public void parse(String... args) {
		DWTool command = parser().parse(args);
		try {
			command.perform();
		} catch (Exception e) {
			e.printStackTrace();
		}
		P.println();
	}

	public static abstract class DWTool {
		public DWTool() {
		}

		public abstract void perform() throws Exception;
	}

	@Command(name = "help", description = "Display help information about Dinowolf")
	public static final class HelpCommand extends DWTool {

		@Inject
		public Help help;

		@Override
		public void perform() {
			help.call();
		}
	}

	@Command(name = "portmaps", description = "Lists port maps from a workflow bundle")
	public static final class PortmapsCommand extends DWTool {
		@Option(name = { "-i", "--input" }, description = "File to read")
		public static String input = null;

		private WorkflowBundleIO io = new WorkflowBundleIO();

		@Override
		public void perform() throws ReaderException, IOException {
			if (input == null)
				throw new RuntimeException("Missing parameter: input");

			File f = new File(input);
			if (!f.exists())
				throw new FileNotFoundException(input);

			WorkflowBundle wb = io.readBundle(f, null);
			FeaturesMap ex = FeaturesMapExtractor.generate(f.getName().replaceAll("\\..*$", ""), wb);
			Set<FromTo> inout = ex.getPortPairs();
			for (FromTo x : inout) {
				P.println(x);
			}

		}
	}

	// @Command(name = "summary", description = "Summary a workflow bundle")
	// public static final class SummaryCommand extends DWTool {
	// @Option(name = { "-i", "--input" }, description = "File to read")
	// public static String input = null;
	//
	// private WorkflowBundleIO io = new WorkflowBundleIO();
	//
	// @Override
	// public void perform() throws ReaderException, IOException {
	// if (input == null)
	// throw new RuntimeException("Missing parameter: input");
	//
	// File f = new File(input);
	// if (!f.exists())
	// throw new FileNotFoundException(input);
	//
	// WorkflowBundle wb = io.readBundle(f, null);
	// StandardFeaturesExtractor ex = new StandardFeaturesExtractor(wb);
	// Set<FromTo> inout = ex.getSet();
	// P.println("Processors");
	// for (FromTo x : inout) {
	// P.print('-');
	// P.println(x);
	// }
	//
	// }
	// }

	@Command(name = "features", description = "Extract features from a workflow bundle")
	public static final class FeaturesCommand extends DWTool {

		@Option(name = { "-i", "--input" }, description = "File to read")
		public static String input = null;

		private WorkflowBundleIO io = new WorkflowBundleIO();

		@Override
		public void perform() throws ReaderException, IOException {
			if (input == null)
				throw new RuntimeException("Missing parameter: input");

			File f = new File(input);
			if (!f.exists())
				throw new FileNotFoundException(input);

			WorkflowBundle wb = io.readBundle(f, null);
			FeaturesMap ex = FeaturesMapExtractor.generate(f.getName().replaceAll("\\..*$", ""), wb);
			Set<FromTo> inout = ex.getPortPairs();
			for (FromTo x : inout) {
				P.println(x);
				for (Feature y : ex.getFeatures(x)) {
					P.print(" - ");
					P.println(y);
				}
			}
		}
	}

}
