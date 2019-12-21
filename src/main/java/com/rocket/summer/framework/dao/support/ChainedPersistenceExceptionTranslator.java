package com.rocket.summer.framework.dao.support;

import java.util.ArrayList;
import java.util.List;

import com.rocket.summer.framework.dao.DataAccessException;
import com.rocket.summer.framework.util.Assert;

/**
 * Implementation of {@link PersistenceExceptionTranslator} that supports chaining,
 * allowing the addition of PersistenceExceptionTranslator instances in order.
 * Returns {@code non-null} on the first (if any) match.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 */
public class ChainedPersistenceExceptionTranslator implements PersistenceExceptionTranslator {

    /** List of PersistenceExceptionTranslators */
    private final List<PersistenceExceptionTranslator> delegates = new ArrayList<PersistenceExceptionTranslator>(4);


    /**
     * Add a PersistenceExceptionTranslator to the chained delegate list.
     */
    public final void addDelegate(PersistenceExceptionTranslator pet) {
        Assert.notNull(pet, "PersistenceExceptionTranslator must not be null");
        this.delegates.add(pet);
    }

    /**
     * Return all registered PersistenceExceptionTranslator delegates (as array).
     */
    public final PersistenceExceptionTranslator[] getDelegates() {
        return this.delegates.toArray(new PersistenceExceptionTranslator[this.delegates.size()]);
    }


    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        for (PersistenceExceptionTranslator pet : this.delegates) {
            DataAccessException translatedDex = pet.translateExceptionIfPossible(ex);
            if (translatedDex != null) {
                return translatedDex;
            }
        }
        return null;
    }

}

