package ru.ispras.eventb_cond_extract;

/**
 * Represents Event-B event parameter.
 * 
 * {@link name} is the parameter's name.
 * {@link type} is the parameters' type expression.
 *
 * Immutable.
 */
public final class StaticallyCheckedParameter
{
	/**
	 * Name of the parameter.
	 */
	public final String name;

	/**
	 * Expression for type of the parameter.
	 */
	public final String type;

	/**
	 *
	 * @param name	name of the parameter
	 * @param type	type of the parameter
	 */
	public StaticallyCheckedParameter(final String name, final String type)
	{
		this.name = name;
		this.type = type;
	}
}
