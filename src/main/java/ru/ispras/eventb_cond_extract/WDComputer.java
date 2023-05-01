package ru.ispras.eventb_cond_extract;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;

/**
 * Computes well-definedeness predicate.
 */
final class WDComputer {
    /**
     * Computes WD Predicate.
     * Performs type checking of {@link predicate} which changes
     * its hash code. If {@link predicate} was a key in a mapping, it becomes
     * non-existent.
     * 
     * @param predicate         the predicate to compute a WD predicate
     * @param typeEnvironment   type environment of the predicate
     * @return                  string representation of the WD predicate
     * @throws IllegalArgumentException if type checking fails
     */
    static String computeWDPredicate(
        final Predicate predicate,
        final ITypeEnvironment typeEnvironment) {
        if (!predicate.isTypeChecked()) {
            // NB! typeCheck() call changes "predicate" object!
            // if "predicate" was a key in any mapping, it becomes
            // non-existent after typeCheck()!
            final ITypeCheckResult tc = predicate.typeCheck(typeEnvironment);
            if (tc.hasProblem()) {
                final String errorMessage = Stream.concat(
                    Stream.of("Cannot type-check predicate: " + predicate.toString()),
                    tc.getProblems().stream().map(Object::toString))
                .collect(Collectors.joining(System.lineSeparator()));
                throw new IllegalArgumentException(errorMessage);
            }
        }
        return predicate.getWDPredicate().toString();
    }
}