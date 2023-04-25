package ru.ispras.eventb_cond_extract;

/**
 * Represents an Event-B invariant from the statically checked model.
 *
 * {@link label} is for name of the invariant.
 * {@link predicate} is for predicate of the invariant.
 *
 * Immutable.
 */
public final class StaticallyCheckedInvariant {
	/**
	 * Name of the invariant.
	 */
	public final String label;

	/**
	 * Predicate of the invariant.
	 *
	 * Immutable.
	 */
	public final String predicate;

	/**
	 * @param label		name of the invariant
	 * @param predicate	predicate of the invariant
	 */
	public StaticallyCheckedInvariant(final String label, final String predicate) {
		this.label = label;
		this.predicate = predicate;
	}
}
