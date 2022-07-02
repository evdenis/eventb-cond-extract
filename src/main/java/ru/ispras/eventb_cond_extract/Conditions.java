package ru.ispras.eventb_cond_extract;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents conditions structure of events.
 *
 * All conditions of each event are indexed
 * sequentially. {@link conditions_order} is used
 * for the conditions identifiers sequence
 * for given event.
 * {@link conditions} is all conditions objects
 * for given event indexed by conditions identifiers.
 *
 * The class is immutable.
 */ 
public class Conditions
{
	/**
	 * List of conditions (theirs identifiers) for each event.
	 *
	 * Immutable.
	 */
	public final Map<String, List<String>> conditions_order; // event-label |-> list-of-conditions-ids

	/**
	 * All conditions objects for each event indexed by conditions identifiers.
	 *
	 * Immutable.
	 */
	public final Map<String, Map<String, Condition>> conditions; // event-label |-> { condition-id |-> condition }

	/**
	 * @param conditions_order	list of conditions for each event
	 * @param conditions		all conditions objects for each event
	 */
	public Conditions(final Map<String, List<String>> conditions_order,
				final Map<String, Map<String, Condition>> conditions)
	{
		//1. dom conditions_order == dom conditions
		if (! conditions_order.keySet().equals(conditions.keySet())
		) {
			throw new IllegalArgumentException("Different keys in conditions substructures");
		}
		//2. for each key in conditions: dom conditions[key] == set from conditions_order[key]
		if (! conditions.keySet().stream().allMatch(key -> 
			conditions.get(key).keySet().equals(new HashSet<>(conditions_order.get(key)))
		)) {
			throw new IllegalArgumentException("Different values in conditions substructures");
		}
		//3. for each key in conditions: for each k in conditions[key]: conditions[key][k].id == k
		if (! conditions.values().stream().allMatch(map ->
			map.keySet().stream().allMatch(key ->
				map.get(key).id == key
		))) {
			throw new IllegalArgumentException("Conditions are not indexed by theirs identifiers");
		}

		this.conditions_order = Collections.unmodifiableMap(conditions_order);
		this.conditions = Collections.unmodifiableMap(conditions);
	}

	/**
	 * Mutable builder for conditions.
	 *
	 * Instantiate this class, change {@link conditions_order} and
	 * {@link conditions} and call {@link build}.
	 */
	public static class Builder
	{
		/**
		 * Order of conditions for each event.
		 */
		public final Map<String, List<String>> conditions_order; // event-label |-> list-of-conditions-ids

		/**
		 * Conditions for each event indexed by conditions identifiers.
		 */
		public final Map<String, Map<String, Condition>> conditions; // event-label |-> { condition-id |-> condition }

		/**
		 * Makes empty conditions.
		 */
		public Builder()
		{
			this.conditions_order = new HashMap<>();
			this.conditions = new HashMap<>();
		}

		/**
		 * Make the immutable conditions.
		 *
		 * @return	immutable conditions
		 */
		public Conditions build()
		{
			return new Conditions(conditions_order, conditions);
		}
	}
}
