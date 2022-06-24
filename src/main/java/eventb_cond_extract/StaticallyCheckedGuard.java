package eventb_cond_extract;

public /*unmodifiable*/ class StaticallyCheckedGuard
{
	public final String label;
	public final String predicate;

	public StaticallyCheckedGuard(final String label, final String predicate)
	{
		this.label = label;
		this.predicate = predicate;
	}
}
