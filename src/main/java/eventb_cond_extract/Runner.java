package eventb_cond_extract;

import org.apache.commons.cli.*;

public class Runner
{
	public static void main(String[] args) {
		Options options = new Options();

		options.addOption("m", "model", true, "model.bum file path");
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

			final ConditionsExtractor extractor = new ConditionsExtractor(modelFile);
			extractor.printConditions(System.out);

			System.exit(0);
		} catch (final Exception e) {
			System.out.println(e.getMessage());
			formatter.printHelp("eventb_cond_extract", options);

			System.exit(1);
		}	
	}
}
