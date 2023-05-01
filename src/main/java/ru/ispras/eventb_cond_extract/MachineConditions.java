package ru.ispras.eventb_cond_extract;

import java.util.*;

/**
 * Represents conditions of Event-B machine.
 *
 * {@link eventsConditions} are conditions of events.
 * It is a mapping from event labels to key-supplied sequences
 * of conditions (a key is a condition id).
 *
 * {@link invariantsConditions} are conditions of invariants.
 * It is a mapping from invariant labels to key-supplied
 * sequences of conditions (a key is a condition id).
 *
 * The class is immutable.
 */ 
public final class MachineConditions
{
	/**
	 * Mapping from event labels to their conditions.
	 *
	 * Immutable.
	 */
	public final Map<String, KeySuppliedSequence<String, Condition>> eventsConditions;

	/**
	 * Mapping from invariant labels to their conditions.
	 *
	 * Immutable.
	 */
	public final Map<String, KeySuppliedSequence<String, Condition>> invariantsConditions;

	/**
	 * @param eventsConditions	mapping from event labels to their conditions
	 * @param invariantsConditions	mapping from invariant labels to their conditions
	 */
	public MachineConditions(
		final Map<String, KeySuppliedSequence<String, Condition>> eventsConditions,
		final Map<String, KeySuppliedSequence<String, Condition>> invariantsConditions)
	{
		this.eventsConditions = Collections.unmodifiableMap(eventsConditions);
		this.invariantsConditions = Collections.unmodifiableMap(invariantsConditions);
	}
}
