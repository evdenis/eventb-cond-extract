package ru.ispras.eventb_cond_extract;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Statically checked machine.
 *
 * Machine is statically checked if it collects all
 * items from its refinements.
 *
 * {@link variables} represents machine's variables.
 * {@link events} represents machine's events.
 * {@link invariants} represents machine's invariants.
 * Order of these collections are gotten from the machine.
 *
 * Immutable.
 */
public final class StaticallyCheckedMachine {
	/**
	 * Sequence of variables.
	 *
	 * Immutable.
	 */
	public final KeySuppliedSequence<String, StaticallyCheckedVariable> variables;

	/**
	 * Sequence of events.
	 *
	 * Immutable.
	 */
	public final KeySuppliedSequence<String, StaticallyCheckedEvent> events;

	/**
	 * Sequence of invariants.
	 *
	 * Immutable.
	 */
	public final KeySuppliedSequence<String, StaticallyCheckedInvariant> invariants;

	/**
	 * @param variables	variables of the machine
	 * @param invariants	invariants of the machine
	 * @param events	events of the machine
	 */
	public StaticallyCheckedMachine(
			final List<StaticallyCheckedVariable> variables,
			final List<StaticallyCheckedInvariant> invariants,
			final List<StaticallyCheckedEvent> events)
	{
		this.variables = new KeySuppliedSequence<>(variables, v -> v.name);
		this.invariants = new KeySuppliedSequence<>(invariants, i -> i.label);
		this.events = new KeySuppliedSequence<>(events, e -> e.label);
	}
}
