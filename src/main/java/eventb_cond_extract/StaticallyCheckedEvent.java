package eventb_cond_extract;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public /*unmodifiable*/ class StaticallyCheckedEvent
{
	public final String label;
	public final /*unmodifiable*/ List<StaticallyCheckedParameter> parameters;
	public final /*unmodifiable*/ List<StaticallyCheckedGuard> guards;

	public final /*unmodifiable*/ Map<String, StaticallyCheckedParameter> parametersMap;
	public final /*unmodifiable*/ Map<String, StaticallyCheckedGuard> guardsMap;

	public StaticallyCheckedEvent(final String label,
			final List<StaticallyCheckedParameter> parameters,
			final List<StaticallyCheckedGuard> guards)
	{
		this.label = label;
		this.parameters = Collections.unmodifiableList(parameters);
		this.guards = Collections.unmodifiableList(guards);
		this.parametersMap = Collections.unmodifiableMap(parameters.stream().collect(Collectors.toMap(p -> p.name, p -> p)));
		this.guardsMap = Collections.unmodifiableMap(guards.stream().collect(Collectors.toMap(g -> g.label, g -> g)));
	}
}
