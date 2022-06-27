package ru.ispras.eventb_cond_extract;

/**
 * Event-B guard.
 *
 * {@link label} is the name of the guard.
 * {@link predicate} is the formula of the guard.
 *
 * Immutable.
 */
public final class StaticallyCheckedGuard
{
	/**
	 * Name of the guard.
	 */
	public final String label;

	/**
	 * Formula of the guard.
	 */
	public final String predicate;

	/**
	 * @param label		name of the guard
	 * @param predicate	predicate of the guard
	 */
	public StaticallyCheckedGuard(final String label, final String predicate)
	{
		this.label = label;
		this.predicate = predicate;
	}
}
