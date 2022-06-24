package eventb_cond_extract;

import java.util.*;
import java.util.stream.Collectors;

public /*unmodifiable*/ class Conditions
{
	public final /*unmodifiable*/ Map<String, List<String>> conditions_order; // event-label |-> list-of-conditions-ids
	public final /*unmodifiable*/ Map<String, Map<String, Condition>> conditions; // event-label |-> { condition-id |-> condition }

	public Conditions(final Map<String, List<String>> conditions_order,
				final Map<String, Map<String, Condition>> conditions)
	{
		this.conditions_order = Collections.unmodifiableMap(conditions_order);
		this.conditions = Collections.unmodifiableMap(conditions);
	}

	public static class Builder
	{
		public final Map<String, List<String>> conditions_order; // event-label |-> list-of-conditions-ids
		public final Map<String, Map<String, Condition>> conditions; // event-label |-> { condition-id |-> condition }

		public Builder()
		{
			this.conditions_order = new HashMap<>();
			this.conditions = new HashMap<>();
		}

		public Conditions build()
		{
			return new Conditions(conditions_order, conditions);
		}
	}
}
