package ru.ispras.eventb_cond_extract;

import org.eventb.core.ast.*;

/**
 * Computes normalized representation of predicates.
 */
class PredicatesNormalizer
{
	String normalize(final Predicate predicate)
	{
		return getNormalizedPredicate(predicate).toString();
	}

	private Predicate getNormalizedPredicate(final Predicate predicate)
	{
		if (predicate instanceof RelationalPredicate) {

			final RelationalPredicate p = (RelationalPredicate) predicate;
			final Expression left = p.getLeft();
			final Expression right = p.getRight();
			switch (p.getTag()) {
				case Formula.EQUAL:
				case Formula.NOTEQUAL:
				case Formula.LT:
				case Formula.LE:
				case Formula.GT:
				case Formula.GE:
					if (left.toString().compareTo(right.toString()) > 0) {
						return getNormalizedPredicate(p.getFactory().makeRelationalPredicate(
									getInvertedTag(p.getTag()),
									right,
									left,
									p.getSourceLocation()));
					} else if (p.getTag() == Formula.NOTEQUAL || p.getTag() == Formula.GT || p.getTag() == Formula.GE) {
						return getNormalizedPredicate(p.getFactory().makeRelationalPredicate(
									getNegatedTag(p.getTag()),
									left,
									right,
									p.getSourceLocation()));
					} else {
						break;
					}
				case Formula.IN:
				case Formula.SUBSETEQ:
				case Formula.SUBSET:
					break;
				case Formula.NOTIN:
				case Formula.NOTSUBSETEQ:
				case Formula.NOTSUBSET:
					return getNormalizedPredicate(p.getFactory().makeRelationalPredicate(
								getNegatedTag(p.getTag()),
								left,
								right,
								p.getSourceLocation()));
				default:
					break;
			}
		}
	
		return predicate;
	}

	private int getInvertedTag(int tag)
	{
		switch (tag) {
			case Formula.LT:
				return Formula.GT;
			case Formula.LE:
				return Formula.GE;
			case Formula.GT:
				return Formula.LT;
			case Formula.GE:
				return Formula.LE;
			default:
				return tag;
		}
	}

	private int getNegatedTag(int tag)
	{
		switch (tag) {
			case Formula.EQUAL:
				return Formula.NOTEQUAL;
			case Formula.NOTEQUAL:
				return Formula.EQUAL;
			case Formula.LT:
				return Formula.GE;
			case Formula.LE:
				return Formula.GT;
			case Formula.GT:
				return Formula.LE;
			case Formula.GE:
				return Formula.LT;
			case Formula.IN:
				return Formula.NOTIN;
			case Formula.SUBSETEQ:
				return Formula.NOTSUBSETEQ;
			case Formula.SUBSET:
				return Formula.NOTSUBSET;
			case Formula.NOTIN:
				return Formula.IN;
			case Formula.NOTSUBSETEQ:
				return Formula.SUBSETEQ;
			case Formula.NOTSUBSET:
				return Formula.SUBSET;
			default:
				return tag;
		}
	}
}
