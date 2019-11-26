package com.rocket.summer.framework.data.redis.core.convert;

import java.util.Set;

import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.data.util.TypeInformation;

/**
 * {@link IndexResolver} extracts secondary index structures to be applied on a given path, {@link PersistentProperty}
 * and value.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public interface IndexResolver {

    /**
     * Resolves all indexes for given type information / value combination.
     *
     * @param typeInformation must not be {@literal null}.
     * @param value the actual value. Can be {@literal null}.
     * @return never {@literal null}.
     */
    Set<IndexedData> resolveIndexesFor(TypeInformation<?> typeInformation, Object value);

    /**
     * Resolves all indexes for given type information / value combination.
     *
     * @param keyspace must not be {@literal null}.
     * @param path must not be {@literal null}.
     * @param typeInformation must not be {@literal null}.
     * @param value the actual value. Can be {@literal null}.
     * @return never {@literal null}.
     */
    Set<IndexedData> resolveIndexesFor(String keyspace, String path, TypeInformation<?> typeInformation, Object value);

}

