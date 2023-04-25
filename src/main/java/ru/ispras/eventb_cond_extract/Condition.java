package ru.ispras.eventb_cond_extract;

import org.eventb.core.ast.Predicate;

/**
 * Represents the condition.
 * The class is immutable.
 */
public final class Condition
{
	/**
	 * identifier of the condition
	 */
	public final String id;

	/**
	 * formula of the condition
	 */
	public final Predicate predicate;

	/**
	 * well-formed predicate for {@link predicate}
	 */
	public final String wdPredicate;

	/**
	 * normalized text of the predicate
	 */
	public final String normalizedPredicate;

	/**
	 * @param id		identifier of the condition
	 * @param predicate	formula of the condition
	 * @param wdPredicate	formula of the well-formed predicate for {@link predicate}
	 */
	public Condition(final String id, final Predicate predicate, final String wdPredicate)
	{
		this.id = id;
		this.predicate = predicate;
		this.wdPredicate = wdPredicate;
		this.normalizedPredicate = new PredicatesNormalizer().normalize(predicate);
	}
}
