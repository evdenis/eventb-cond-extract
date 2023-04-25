package ru.ispras.eventb_cond_extract;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents Event-B event from the statically checked model.
 *
 * {@link label} is for name of the event.
 * {@link parameters} is a list of event's parameters.
 * The order of the parameters is gotten from the model.
 * {@link guards} is a list of event's guards.
 * The order of the guards is gotten from the model.
 *
 * Immutable.
 */
public final class StaticallyCheckedEvent
{
	/**
	 * Name of the event.
	 */
	public final String label;

	/**
	 * List of parameters.
	 *
	 * Immutable.
	 */
	public final KeySuppliedSequence<String, StaticallyCheckedParameter> parameters;

	/**
	 * List of guards.
	 *
	 * Immutable.
	 */
	public final KeySuppliedSequence<String, StaticallyCheckedGuard> guards;

	/**
	 * @param label		name of the event
	 * @param parameters	parameters of the event
	 * @param guards	guards of the event
	 */
	public StaticallyCheckedEvent(final String label,
			final List<StaticallyCheckedParameter> parameters,
			final List<StaticallyCheckedGuard> guards)
	{
		this.label = label;
		this.parameters = new KeySuppliedSequence<>(parameters, p -> p.name);
		this.guards = new KeySuppliedSequence<>(guards, g -> g.label);
	}
}
