package com.rocket.summer.framework.data.keyvalue.core;

import java.util.NoSuchElementException;

import com.rocket.summer.framework.dao.DataAccessException;
import com.rocket.summer.framework.dao.DataRetrievalFailureException;
import com.rocket.summer.framework.dao.support.PersistenceExceptionTranslator;

/**
 * Simple {@link PersistenceExceptionTranslator} implementation for key/value stores that converts the given runtime
 * exception to an appropriate exception from the {@code com.rocket.summer.framework.dao} hierarchy.
 *
 * @author Christoph Strobl
 */
public class KeyValuePersistenceExceptionTranslator implements PersistenceExceptionTranslator {

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.dao.support.PersistenceExceptionTranslator#translateExceptionIfPossible(java.lang.RuntimeException)
     */
    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException e) {

        if (e == null || e instanceof DataAccessException) {
            return (DataAccessException) e;
        }

        if (e instanceof NoSuchElementException || e instanceof IndexOutOfBoundsException
                || e instanceof IllegalStateException) {
            return new DataRetrievalFailureException(e.getMessage(), e);
        }

        if (e.getClass().getName().startsWith("java")) {
            return new UncategorizedKeyValueException(e.getMessage(), e);
        }
        return null;
    }
}

