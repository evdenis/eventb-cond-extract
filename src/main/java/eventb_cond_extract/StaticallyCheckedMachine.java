package eventb_cond_extract;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public /*unmodifiable*/ class StaticallyCheckedMachine
{
	public final /*unmodifiable*/ List<StaticallyCheckedVariable> variables;
	public final /*unmodifiable*/ List<StaticallyCheckedEvent> events;

	public final /*unmodifiable*/ Map<String, StaticallyCheckedVariable> variablesMap;
	public final /*unmodifiable*/ Map<String, StaticallyCheckedEvent> eventsMap;

	public StaticallyCheckedMachine(
			final List<StaticallyCheckedVariable> variables,
			final List<StaticallyCheckedEvent> events)
	{
		this.variables = Collections.unmodifiableList(variables);
		this.events = Collections.unmodifiableList(events);
		this.variablesMap = Collections.unmodifiableMap(variables.stream().collect(Collectors.toMap(v -> v.name, v -> v)));
		this.eventsMap = Collections.unmodifiableMap(events.stream().collect(Collectors.toMap(e -> e.label, e -> e)));
	}
}
