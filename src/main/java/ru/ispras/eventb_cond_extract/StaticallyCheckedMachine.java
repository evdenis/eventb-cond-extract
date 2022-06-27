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
 * {@link variables} is machine's variables.
 * {@link events} is machine's events.
 * Order of both these lists are gotten from the machine.
 *
 * Immutable.
 */
public final class StaticallyCheckedMachine
{
	/**
	 * List of variables.
	 *
	 * Immutable.
	 */
	public final List<StaticallyCheckedVariable> variables;

	/**
	 * List of events.
	 *
	 * Immutable.
	 */
	public final List<StaticallyCheckedEvent> events;

	/**
	 * Variables indexed by theirs names.
	 *
	 * Immutable.
	 */
	public final Map<String, StaticallyCheckedVariable> variablesMap;

	/**
	 * Events indexed by theirs names.
	 *
	 * Immutable.
	 */
	public final Map<String, StaticallyCheckedEvent> eventsMap;

	/**
	 * @param variables	variables of the machine
	 * @param events	events of the machine
	 */
	public StaticallyCheckedMachine(
			final List<StaticallyCheckedVariable> variables,
			final List<StaticallyCheckedEvent> events)
	{
		this.variables = Collections.unmodifiableList(variables);
		this.events = Collections.unmodifiableList(events);
		this.variablesMap = Collections.unmodifiableMap(variables.stream().collect(Collectors.toMap(v -> v.name, v -> v)));
		this.eventsMap = Collections.unmodifiableMap(events.stream().collect(Collectors.toMap(e -> e.label, e -> e)));
	}
}
