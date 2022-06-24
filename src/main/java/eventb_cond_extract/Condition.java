package eventb_cond_extract;

import org.eventb.core.ast.Predicate;

public /*unmodifiable*/ class Condition
{
	public final String id;
	public final Predicate predicate;
	public final String wdPredicate;

	public Condition(final String id, final Predicate predicate, final String wdPredicate)
	{
		this.id = id;
		this.predicate = predicate;
		this.wdPredicate = wdPredicate;
	}
}
