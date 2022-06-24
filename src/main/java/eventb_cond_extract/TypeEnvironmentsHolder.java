package eventb_cond_extract;

import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

import org.eventb.core.ast.*;

class TypeEnvironmentsHolder
{
	public final ITypeEnvironment variablesTypeEnvironment;
	public final /*unmodifiable*/ Map<String, ITypeEnvironment> eventsTypeEnvironments;
	public final FormulaFactory ff;

	public TypeEnvironmentsHolder(final StaticallyCheckedMachine scMachine, final PrintStream out) {

		this.ff = FormulaFactory.getDefault();
		final ITypeEnvironmentBuilder builder = ff.makeTypeEnvironment();

		scMachine.variables.forEach(variable -> {
			final IParseResult r = ff.parseType(variable.type);
			if (r.hasProblem()) {
				out.println("Some problems in parsing type of the variable " + variable.name + ": " + variable.type);
				for (final ASTProblem problem: r.getProblems()) {
					out.println(problem);
				}
			} else {
				builder.addName(variable.name, r.getParsedType());
			}
		});

		this.variablesTypeEnvironment = builder.makeSnapshot();

		this.eventsTypeEnvironments = Collections.unmodifiableMap(
				scMachine.events.stream().collect(Collectors.toMap(scEvent -> scEvent.label, scEvent -> {

			final ITypeEnvironmentBuilder eventBuilder = ff.makeTypeEnvironment();
			eventBuilder.addAll(this.variablesTypeEnvironment);
			scEvent.parameters.forEach(parameter -> {
				final IParseResult r = ff.parseType(parameter.type);
				if (r.hasProblem()) {
					out.println("Some problems in parsing type of the parameter " + parameter.name + ": " + parameter.type);
					for (final ASTProblem problem: r.getProblems()) {
						out.println(problem);
					}
				} else {
					eventBuilder.addName(parameter.name, r.getParsedType());
				}
			});
			return eventBuilder.makeSnapshot();
		})));
	}
}
