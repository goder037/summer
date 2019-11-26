package com.rocket.summer.framework.data.keyvalue.core;

import com.rocket.summer.framework.data.domain.Sort;
import com.rocket.summer.framework.data.keyvalue.core.query.KeyValueQuery;

/**
 * Resolves the {@link Sort} object from given {@link KeyValueQuery} and potentially converts it into a store specific
 * representation that can be used by the {@link QueryEngine} implementation.
 *
 * @author Christoph Strobl
 * @param <T>
 */
public interface SortAccessor<T> {

    /**
     * Reads {@link KeyValueQuery#getSort()} of given {@link KeyValueQuery} and applies required transformation to match
     * the desired type.
     *
     * @param query can be {@literal null}.
     * @return {@literal null} in case {@link Sort} has not been defined on {@link KeyValueQuery}.
     */
    T resolve(KeyValueQuery<?> query);
}

