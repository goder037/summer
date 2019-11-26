package com.rocket.summer.framework.data.keyvalue.core;

import com.rocket.summer.framework.data.keyvalue.core.query.KeyValueQuery;

/**
 * Resolves the criteria object from given {@link KeyValueQuery}.
 *
 * @author Christoph Strobl
 * @param <T>
 */
public interface CriteriaAccessor<T> {

    /**
     * Checks and reads {@link KeyValueQuery#getCriteria()} of given {@link KeyValueQuery}. Might also apply additional
     * transformation to match the desired type.
     *
     * @param query can be {@literal null}.
     * @return the criteria extracted from the query.
     * @throws IllegalArgumentException in case the criteria is not valid for usage with specific {@link CriteriaAccessor}
     *           .
     */
    T resolve(KeyValueQuery<?> query);
}

