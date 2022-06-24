package eventb_cond_extract;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.parsers.ParserConfigurationException;

import org.eventb.core.ast.*;

import org.xml.sax.SAXException;

public class ConditionsExtractor {

	public final StaticallyCheckedMachine scMachine;
	public final TypeEnvironmentsHolder typeEnvironments;
	public final Conditions conditions;

	public void printConditions(final PrintStream out) {
		scMachine.events.stream().forEach(scEvent -> {
			out.println(scEvent.label);
			conditions.conditions_order.get(scEvent.label).stream().forEach(condition_id -> {
				final String predicate = conditions.conditions.get(scEvent.label).get(condition_id).predicate.toString();
				out.println(" - [" + condition_id + "] " + predicate);
			});
			out.println();
		});
	}

	public ConditionsExtractor(final String modelPath) 
		throws ParserConfigurationException, SAXException, IOException
	{
		if (!modelPath.endsWith(".bum")) {
			throw new IllegalArgumentException("Wrong extension of model file; .bum expected; gotten " + modelPath);
		}
		final String scMachinePath = modelPath.substring(0, modelPath.length() - ".bum".length()) + ".bcm";
		this.scMachine = new StaticallyCheckedMachineReader().read(scMachinePath);

		this.typeEnvironments = new TypeEnvironmentsHolder(scMachine, System.out);

		this.conditions = computeConditions(System.out).build();
	}

	private Conditions.Builder computeConditions(final PrintStream out) {
		final Conditions.Builder conditionsBuilder = new Conditions.Builder();

		scMachine.events.forEach(event -> {
			final Map<String, Condition> event_conditions = new HashMap<>();
			final List<String> event_conditions_order = new ArrayList<>();
			final ITypeEnvironment typeEnvironment = typeEnvironments.eventsTypeEnvironments.get(event.label);
			event.guards.forEach(guard -> {
				computeGuardConditions(guard, out, typeEnvironment, event_conditions, event_conditions_order);
			});
			conditionsBuilder.conditions.put(event.label, event_conditions);
			conditionsBuilder.conditions_order.put(event.label, event_conditions_order);
		});

		return conditionsBuilder;
	}

	private void computeGuardConditions(
			final StaticallyCheckedGuard guard,
			final PrintStream out,
			final ITypeEnvironment typeEnvironment,
			final Map<String, Condition> result,
			final List<String> result_order)
	{
			final IParseResult parsedFormula = typeEnvironments.ff.parsePredicate(guard.predicate, null);
			final Predicate predicate = parsedFormula.getParsedPredicate();
			if (predicate != null) {
				computePredicateConditions(predicate, guard, out, typeEnvironment, result, result_order);
			} else {
				out.println("Guard predicate " + guard.label + " is null after parsing");
			}
	}

	private void computePredicateConditions(
			final Predicate predicate,
			final StaticallyCheckedGuard guard,
			final PrintStream out,
			final ITypeEnvironment typeEnvironment,
			final Map<String, Condition> result,
			final List<String> result_order)
	{
		final Deque<Predicate> queue = new LinkedList<>();
		queue.addFirst(predicate);

		int count = 0;

		while (!queue.isEmpty()) {
			final Predicate p = queue.removeFirst();
			if (p instanceof BinaryPredicate) {
				queue.addFirst(((BinaryPredicate) p).getRight());
				queue.addFirst(((BinaryPredicate) p).getLeft());
				continue;
			} else if (p instanceof AssociativePredicate) {
				final AssociativePredicate ap = (AssociativePredicate) p;
				for (int i = ap.getChildCount() - 1; i >= 0; --i) {
					queue.addFirst(ap.getChild(i));
				}
				continue;
			} else if (p instanceof UnaryPredicate) {
				queue.addFirst(((UnaryPredicate) p).getChild());
				continue;
			} else if (p instanceof RelationalPredicate) {
				final RelationalPredicate r = (RelationalPredicate) p;
				final Expression left = r.getLeft();
				final Expression right = r.getRight();
				switch (p.getTag()) {
					case Formula.EQUAL:
						// sort children
						if (left.toString().compareTo(right.toString()) > 0) {
							queue.addFirst(p.getFactory().makeRelationalPredicate(
										Formula.EQUAL,
										right,
										left,
										p.getSourceLocation()));
							continue;
						} else {
							break;
						}
					case Formula.NOTEQUAL:
						queue.addFirst(p.getFactory().makeRelationalPredicate(
									Formula.EQUAL,
									left,
									right,
									p.getSourceLocation()));
						continue;
					case Formula.LT:
						break;
					case Formula.LE:
						queue.addFirst(p.getFactory().makeRelationalPredicate(
									Formula.LT,
									left,
									right,
									p.getSourceLocation()));
						queue.addFirst(p.getFactory().makeRelationalPredicate(
									Formula.EQUAL,
									left,
									right,
									p.getSourceLocation()));
						continue;
					case Formula.GT:
						queue.addFirst(p.getFactory().makeRelationalPredicate(
									Formula.LT,
									right,
									left,
									p.getSourceLocation()));
						continue;
					case Formula.GE:
						queue.addFirst(p.getFactory().makeRelationalPredicate(
									Formula.LE,
									right,
									left,
									p.getSourceLocation()));
						continue;
					case Formula.IN:
						if (right instanceof SetExtension) {
							for (final Expression member: ((SetExtension) right).getMembers()) {
								queue.addFirst(p.getFactory().makeRelationalPredicate(
											Formula.EQUAL,
											left,
											member,
											p.getSourceLocation()));
							}
							continue;
						} else if (right instanceof AssociativeExpression) {
							switch (right.getTag()) {
								case Formula.BUNION: case Formula.BINTER:
									for (final Expression child: ((AssociativeExpression) right).getChildren()) {
										queue.addFirst(p.getFactory().makeRelationalPredicate(
													Formula.IN,
													left,
													child,
													p.getSourceLocation()));
									}
									continue;
								default:
									break;
							}
						} else {
							break;
						}
					case Formula.NOTIN:
						queue.addFirst(p.getFactory().makeRelationalPredicate(
									Formula.IN,
									left,
									right,
									p.getSourceLocation()));
						continue;
					case Formula.SUBSETEQ:
						if (left instanceof SetExtension) {
							for (final Expression member: ((SetExtension) left).getMembers()) {
								queue.addFirst(p.getFactory().makeRelationalPredicate(
											Formula.IN,
											member,
											right,
											p.getSourceLocation()));
							}
							continue;
						} else {
							break;
						}
					case Formula.NOTSUBSETEQ:
						queue.addFirst(p.getFactory().makeRelationalPredicate(
									Formula.SUBSETEQ,
									left,
									right,
									p.getSourceLocation()));
						continue;
					case Formula.SUBSET:
						queue.addFirst(p.getFactory().makeRelationalPredicate(
									Formula.EQUAL,
									left,
									right,
									p.getSourceLocation()));
						queue.addFirst(p.getFactory().makeRelationalPredicate(
									Formula.SUBSETEQ,
									left,
									right,
									p.getSourceLocation()));
						continue;
					case Formula.NOTSUBSET:
						queue.addFirst(p.getFactory().makeRelationalPredicate(
									Formula.SUBSET,
									left,
									right,
									p.getSourceLocation()));
						continue;
					default:
						break;
				}
			}

			if (containsPredicate(p, result.values())) {
				continue;
			}

			final String condition_id = (p == predicate ? guard.label : String.format("%s/%d", guard.label, count++));
			final String wdPredicate = computeWDPredicate(p, condition_id, out, typeEnvironment);
			result.put(condition_id, new Condition(condition_id, p, wdPredicate));
			result_order.add(condition_id);
		}
	}

	private boolean containsPredicate(final Predicate predicate, final Collection<Condition> conditions)
	{
		return conditions.stream().anyMatch(c -> c.predicate.toString().equals(predicate.toString()));
	}

	private String computeWDPredicate(
			final Predicate predicate,
			final String condition_id,
			final PrintStream out,
			final ITypeEnvironment typeEnvironment)
	{
		if (!predicate.isTypeChecked()) {
			final ITypeCheckResult tc = predicate.typeCheck(typeEnvironment);
			if (tc.hasProblem()) {
				out.println("Cannot type-check condition " + condition_id + ": " + predicate.toString());
				for (final ASTProblem problem: tc.getProblems()) {
					out.println(problem.toString());
				}
				return "true";
			}
		}
		return predicate.getWDPredicate().toString();
	}
}
