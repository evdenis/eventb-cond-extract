package ru.ispras.eventb_cond_extract;

/**
 * Represents Event-B variable.
 *
 * {@link name} is variable's name.
 * {@link type} is variable's type expression.
 *
 * Immutable.
 */
public final class StaticallyCheckedVariable
{
	/**
	 * Name of the variable.
	 */
	public final String name;

	/**
	 * Expression for type of the variable.
	 */
	public final String type;

	/**
	 * @param name	name of the variable
	 * @param type	type expression of the variable
	 */
	public StaticallyCheckedVariable(final String name, final String type)
	{
		this.name = name;
		this.type = type;
	}
}
