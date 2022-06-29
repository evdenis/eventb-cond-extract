package ru.ispras.eventb_cond_extract;

import java.io.PrintWriter;

import org.apache.commons.cli.*;

/**
 * Cli for conditions extractor.
 *
 * Reads command-line arguments, reads Rodin Event-B model,
 * extracts the conditions, prints the conditions to standard output.
 *
 * Command-line arguments available:
 * -m, --model	file path to *.bum file (Rodin Event-B model)
 *
 * Errors are printed to standard errors stream.
 */
public class Runner
{
	public static void main(String[] args) {
		Options options = new Options();

		options.addOption("m", "model", true, "model.bum file path");
		options.addOption("g", "guards", false, "print guards coverage instead of events coverage");
		//options.addOption("o", "output", false, "file to output the conditions");

		final CommandLineParser parser = new DefaultParser();
		final HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);

			String defaultModelFile = System.getenv("EVENTB_MODEL");
			if (defaultModelFile == null) {
				defaultModelFile = "/opt/model/MX1.bum";
			}
			final String modelFile = cmd.getOptionValue("model", defaultModelFile);
			if (!modelFile.endsWith(".bum")) {
				throw new IllegalArgumentException("Wrong extension of model file; .bum expected; gotten " + modelFile);
			}
			final String scModelFile = modelFile.substring(0, modelFile.length() - ".bum".length()) + ".bcm";
			final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachineReader().read(scModelFile);

			final ConditionsExtractor extractor = new ConditionsExtractor(scMachine);
			if (cmd.hasOption("guards")) {
				extractor.printGuardsConditions(System.out);
			} else {
				extractor.printConditions(System.out);
			}

			System.exit(0);
		} catch (final Exception e) {
			System.err.println(e.getMessage());
			formatter.printHelp(new PrintWriter(System.err), 1024, "eventb_cond_extract", "", options, 0, 0, "");

			System.exit(1);
		}	
	}
}
