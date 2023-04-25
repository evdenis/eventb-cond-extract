package ru.ispras.eventb_cond_extract;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.cli.*;

/**
 * Cli for conditions extractor.
 *
 * Reads command-line arguments, reads Rodin Event-B model,
 * extracts the conditions, prints the conditions to standard output.
 *
 * Command-line arguments available:
 * -m, --model		file path to *.bum file (Rodin Event-B model)
 * -e, --events		need print conditions of events (default, not print)
 * -i, --invariants	need print conditions of invariants (default, not print)
 * -n, --normalized	need print normalized predicates
 *
 * Errors are printed to standard errors stream.
 */
public class Runner {
	public static void main(String[] args) {
		Options options = new Options();

		options.addOption("m", "model", true, "model.bum file path");
		options.addOption("e", "events", false, "print conditions from events");
		options.addOption("i", "invariants", false, "print conditions from invariants");
		options.addOption("n", "normalized", false, "print normalized predicates of conditions");

		final CommandLineParser parser = new DefaultParser();
		final HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);

			String defaultModelFile = System.getenv("REPLAY_MODEL");
			if (defaultModelFile == null) {
				defaultModelFile = "/opt/model/MX1.bum";
			}
			final boolean needNormalized = cmd.hasOption("normalized");
			final boolean needEvents = cmd.hasOption("events");
			final boolean needInvariants = cmd.hasOption("invariants");
			final String modelFile = cmd.getOptionValue("model", defaultModelFile);
			if (!modelFile.endsWith(".bum")) {
				throw new IllegalArgumentException("Wrong extension of model file; .bum expected; gotten " + modelFile);
			}
			final String scModelFile = modelFile.substring(0, modelFile.length() - ".bum".length()) + ".bcm";
			final StaticallyCheckedMachine scMachine = new StaticallyCheckedMachineReader().read(scModelFile);
			final MachineConditions conditions = new ConditionsExtractor(scMachine).machineConditions;

			if (needEvents) {
				printConditions(conditions.eventsConditions, needNormalized, System.out);
			}
			if (needInvariants) {
				printConditions(conditions.invariantsConditions, needNormalized, System.out);
			}

			System.exit(0);
		} catch (final Exception e) {
			System.err.println(e.getMessage());
			formatter.printHelp(new PrintWriter(System.err), 1024, "eventb_cond_extract", "", options, 0, 0, "");

			System.exit(1);
		}
	}

	/**
	 * Prints the conditions by events or invariants.
	 * It prints the list of conditions identifiers and
	 * conditions predicates for each event/invariant.
	 * The normalized versions of conditions' predicates
	 * are printed if needed.
	 *
	 * For example, for event <em>evt</em> with two
	 * guards:
	 * <pre>
	 * grd1: p /= 0 &amp; q = 1
	 * grd2: p = 0 or q = 2
	 * </pre>
	 * the function prints the following (normalized is needed):
	 * <pre>
	 * [evt/grd1/1][p/=0][0=p]
	 * [evt/grd1/2][q=1][1=q]
	 * [evt/grd2/1][p=0][0=p]
	 * [evt/grd2/2][q=2][2=q]
	 * </pre>
	 *
	 * @param conditions		conditions
	 * @param needNormalized	does need to print normalized conditions
	 * @param out			stream to output the conditions
	 */
	private static void printConditions(
			final Map<String, KeySuppliedSequence<String, Condition>> conditions,
			final boolean needNormalized,
			final PrintStream out) {
		streamOf(conditions).forEach(condition -> {
			final String predicate = condition.predicate.toString();
			out.print("[" + condition.id + "][" + predicate + "]");
			if (needNormalized) {
				out.print("[" + condition.normalizedPredicate + "]");
			}
			out.println();
		});
	}

	private static <I, Key, Value> Stream<Value> streamOf(final Map<I, KeySuppliedSequence<Key, Value>> mapping) {
		return mapping.values().stream().flatMap(seq -> seq.values.stream());
	}
}
