package ru.ispras.eventb_cond_extract;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;

/**
 * Checks the predicate is const, i.e.
 * doens't depend on a machine's variable or an event's parameter. 
 */
final class PredicateConstChecker {
    /**
     * Checks the predicate is const.
     * @param predicate the predicate to be checked
     * @param machine   machine object from which the predicate came
     * @param event     event object if the predicate came from its guards or null otherwise
     * @return          is the predicate const
     */
    static boolean isConst(
        final Predicate predicate,
        final StaticallyCheckedMachine machine,
        final /*Nullable*/ StaticallyCheckedEvent event) {
        for (final FreeIdentifier ident: predicate.getFreeIdentifiers()) {
            if (machine.variables.entries.containsKey(ident.getName())) {
                return false;
            } else if (event != null && event.parameters.entries.containsKey(ident.getName())) {
                return false;
            }
        }
        return true;
    }
}
