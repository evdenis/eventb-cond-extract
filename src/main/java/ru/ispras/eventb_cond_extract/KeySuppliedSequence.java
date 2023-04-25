package ru.ispras.eventb_cond_extract;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents keys-supplied sequence of objects.
 *
 * The class is immutable.
 */ 
public final class KeySuppliedSequence<Key, Value> 
{
	/**
	 * List of keys.
	 *
	 * Immutable.
	 */
	public final List<Key> keys;

	/**
	 * List of values.
	 *
	 * Immutable.
	 */
	public final List<Value> values;

	/**
	 * Mapping to fast search of value by key.
	 *
	 * Immutable.
	 */
	public final Map<Key, Value> entries;

	/**
	 * @param values	list of values
	 * @param keyGetter	function to get a key from a value
	 */
	public KeySuppliedSequence(final List<Value> values, final Function<Value, Key> keyGetter)
	{
		this.keys = values.stream().map(keyGetter).collect(Collectors.toUnmodifiableList());
		this.values = Collections.unmodifiableList(values);
		this.entries = values.stream().collect(Collectors.toUnmodifiableMap(keyGetter, value -> value));
	}
}
