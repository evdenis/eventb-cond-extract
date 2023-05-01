package ru.ispras.eventb_cond_extract;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ast.*;

/**
 * Splits the predicate to the list of elementary expressions.
 * 
 * Here is the list of operators which are splitted and when:
 * <ul>
 * <li>conjunction (always)</li>
 * <li>disjunction (always)</li>
 * <li>implication (always)</li>
 * <li>equivalence (always)</li>
 * <li>negation (always)</li>
 * <li>relations: not equal, less, less than, greater, greater than,
 * 	contains, not contains, is subset, is subset or equal,
 * 	is not subset, is not subset or equal (are splitted
 * 	only if there are one of the part in the same event)</li>
 * </ul>
 */
final class PredicateSplitter {
    /**
     * Splits the predicate
     * 
     * @param predicate predicate to be splitted
     * @return          list of elementary expressions
     */
	static List<Predicate> splitToConditions(final Predicate predicate) {
		final List<Predicate> conditions = new ArrayList<>();

		final Deque<Predicate> queue = new LinkedList<>();
		queue.addFirst(predicate);

		while (!queue.isEmpty()) {
			final Predicate p = queue.removeFirst();
			if (p instanceof BinaryPredicate) {
				// left => right
				// left <=> right
				queue.addFirst(((BinaryPredicate) p).getRight());
				queue.addFirst(((BinaryPredicate) p).getLeft());
				continue;
			} else if (p instanceof AssociativePredicate) {
				// child & child & child ... child
				// child or child or child ... child
				final AssociativePredicate ap = (AssociativePredicate) p;
				for (int i = ap.getChildCount() - 1; i >= 0; --i) {
					queue.addFirst(ap.getChild(i));
				}
				continue;
			} else if (p instanceof UnaryPredicate) {
				// not child
				queue.addFirst(((UnaryPredicate) p).getChild());
				continue;
			} else if (p instanceof RelationalPredicate) {
				// left = right
				// left /= right
				// left < right
				// left <= right
				// left > right
				// left >= right
				// left : right
				// left /: right
				// left <: right
				// left <<: right
				// left /<: right
				// left /<<: right
				final RelationalPredicate r = (RelationalPredicate) p;
				final Expression left = r.getLeft();
				final Expression right = r.getRight();
				switch (p.getTag()) {
					case Formula.EQUAL:
					case Formula.NOTEQUAL:
					case Formula.LT:
					case Formula.LE:
					case Formula.GT:
					case Formula.GE:
						break;
					case Formula.IN:
					case Formula.NOTIN:
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
					case Formula.SUBSETEQ:
					case Formula.NOTSUBSETEQ:
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
					case Formula.SUBSET:
					case Formula.NOTSUBSET:
					default:
						break;
				}
			}

			conditions.add(p);
		}
		return conditions;
	}
}