package ru.ispras.eventb_cond_extract;

import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eventb.core.ast.*;

/**
 * Holder for type environments.
 *
 * Type environments store types of variables
 * and types of parameters of events.
 *
 * Instantiate the class and use the fields.
 * {@link variablesTypeEnvironments} is a type
 * environment with all variables.
 * {@link eventsTypeEnvironments} is a types
 * environments with all events.
 *
 * Immutable.
 */
class TypeEnvironmentsHolder
{
	/**
	 * Type environment with all variables.
	 */
	public final ITypeEnvironment variablesTypeEnvironment;

	/**
	 * Type environments with all events indexed by events.
	 *
	 * Each event's type environment has
	 * types of its parameters and types
	 * of machine's variables.
	 *
	 * Immutable.
	 */
	public final Map<String, ITypeEnvironment> eventsTypeEnvironments;

	/**
	 * Formula factory used to generate type environments.
	 *
	 * It may be used for parsing and type-checking
	 * other formulas.
	 */
	public final FormulaFactory ff;

	/**
	 * @param scMachine	statically checked machine to extract type environments
	 * @throws IllegalArgumentException	if the machine is incorrect
	 */
	public TypeEnvironmentsHolder(final StaticallyCheckedMachine scMachine) {

		this.ff = FormulaFactory.getDefault();
		final ITypeEnvironmentBuilder builder = ff.makeTypeEnvironment();

		scMachine.variables.forEach(variable -> {
			final IParseResult r = ff.parseType(variable.type);
			if (r.hasProblem()) {
				throw new IllegalArgumentException(
						Stream.concat(
							Stream.of("Can't parse type of the variable " + variable.name + ": " + variable.type),
							r.getProblems().stream().map(Object::toString))
						.collect(Collectors.joining(System.lineSeparator())));
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
					throw new IllegalArgumentException(
						Stream.concat(
							Stream.of("Can't parse type of the parmeter " + parameter.name + ": " + parameter.type),
							r.getProblems().stream().map(Object::toString))
						.collect(Collectors.joining(System.lineSeparator())));
				} else {
					eventBuilder.addName(parameter.name, r.getParsedType());
				}
			});
			return eventBuilder.makeSnapshot();
		})));
	}
}
