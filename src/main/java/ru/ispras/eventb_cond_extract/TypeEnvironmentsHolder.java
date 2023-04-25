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
 * {@link variablesTypeEnvironment} is a type
 * environment with all variables.
 * {@link eventsTypeEnvironments} is a types
 * environments with all events.
 *
 * Immutable.
 */
public final class TypeEnvironmentsHolder {
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

	private final StaticallyCheckedMachine scMachine;

	/**
	 * @param scMachine	statically checked machine to extract type environments
	 * @throws IllegalArgumentException	if the machine is incorrect
	 */
	public TypeEnvironmentsHolder(final StaticallyCheckedMachine scMachine) {
		this.ff = FormulaFactory.getDefault();
		this.scMachine = scMachine;
		this.variablesTypeEnvironment = makeVariablesTypeEnvironment();
		this.eventsTypeEnvironments = makeEventsTypeEnvironments();
	}

	private ITypeEnvironment makeVariablesTypeEnvironment() {
		final ITypeEnvironmentBuilder builder = ff.makeTypeEnvironment();
		this.scMachine.variables.values.forEach(v -> builder.addName(v.name, getVariableType(v)));
		return builder.makeSnapshot();
	}

	private Type getVariableType(final StaticallyCheckedVariable variable) {
		final IParseResult r = this.ff.parseType(variable.type);
		if (r.hasProblem()) {
			throw new IllegalArgumentException(
					Stream.concat(
						Stream.of("Can't parse type of the variable " + variable.name + ": " + variable.type),
						r.getProblems().stream().map(Object::toString))
					.collect(Collectors.joining(System.lineSeparator())));
		}

		return r.getParsedType();
	}

	private Map<String, ITypeEnvironment> makeEventsTypeEnvironments() {
		return this.scMachine.events.values.stream().collect(
				Collectors.toUnmodifiableMap(scEvent -> scEvent.label, this::makeEventTypeEnvironment));
	}

	private ITypeEnvironment makeEventTypeEnvironment(final StaticallyCheckedEvent event) {
		final ITypeEnvironmentBuilder builder = this.ff.makeTypeEnvironment();
		builder.addAll(this.variablesTypeEnvironment);
		event.parameters.values.forEach(p -> builder.addName(p.name, getParameterType(p)));
		return builder.makeSnapshot();
	}

	private Type getParameterType(final StaticallyCheckedParameter parameter) {
		final IParseResult r = this.ff.parseType(parameter.type);
		if (r.hasProblem()) {
			throw new IllegalArgumentException(
				Stream.concat(
					Stream.of("Can't parse type of the parmeter " + parameter.name + ": " + parameter.type),
					r.getProblems().stream().map(Object::toString))
				.collect(Collectors.joining(System.lineSeparator())));
		}

		return r.getParsedType();
	}
}
