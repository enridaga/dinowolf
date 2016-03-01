package dinowolf.io;

import io.airlift.airline.Cli;
import io.airlift.airline.Cli.CliBuilder;
import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.Option;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;

import dinowolf.annotation.InOut;
import dinowolf.features.Feature;
import dinowolf.features.InOutExtractor;

public class CommandLineTool {

	public static void main(String[] args) throws Exception {
		new CommandLineTool().parse(args);
		System.out.println();
	}

	private static Cli<DWTool> parser() {
		CliBuilder<DWTool> build = Cli.<DWTool> builder("dinowolf")
				.withDescription("Dinowolf Tool")
				.withDefaultCommand(HelpCommand.class)
				.withCommand(FeaturesCommand.class); // Statistics

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
		System.out.println();
	}

	public static abstract class DWTool {
		public DWTool() {}

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
			InOutExtractor ex = new InOutExtractor(wb);
			Set<InOut> inout = ex.getSet();
			for (InOut x : inout) {
				System.out.println(x);
				for (Feature y : x.features()) {
					System.out.print(" - ");
					System.out.println(y);
				}
			}

		}
	}

}
