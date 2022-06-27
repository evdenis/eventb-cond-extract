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
	public final List<StaticallyCheckedParameter> parameters;

	/**
	 * List of guards.
	 *
	 * Immutable.
	 */
	public final List<StaticallyCheckedGuard> guards;

	/**
	 * Parameters indexed by its identifiers.
	 *
	 * Immutable.
	 */
	public final Map<String, StaticallyCheckedParameter> parametersMap;

	/**
	 * Guards indexed by its identifiers.
	 *
	 * Immutable.
	 */
	public final Map<String, StaticallyCheckedGuard> guardsMap;

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
		this.parameters = Collections.unmodifiableList(parameters);
		this.guards = Collections.unmodifiableList(guards);
		this.parametersMap = Collections.unmodifiableMap(parameters.stream().collect(Collectors.toMap(p -> p.name, p -> p)));
		this.guardsMap = Collections.unmodifiableMap(guards.stream().collect(Collectors.toMap(g -> g.label, g -> g)));
	}
}
