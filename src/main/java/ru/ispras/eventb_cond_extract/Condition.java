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
	 * is the condition const (doesn't depend on a machine's variable or an event's parameter).
	 */
	public final boolean isConst;

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
	 * @param isConst	is the condition const (doesn't depend on a variable or a parameter)
	 * @param wdPredicate	formula of the well-formed predicate for {@link predicate}
	 */
	public Condition(final String id, final Predicate predicate, final boolean isConst, final String wdPredicate)
	{
		this.id = id;
		this.predicate = predicate;
		this.isConst = isConst;
		this.wdPredicate = wdPredicate;
		this.normalizedPredicate = new PredicatesNormalizer().normalize(predicate);
	}
}
