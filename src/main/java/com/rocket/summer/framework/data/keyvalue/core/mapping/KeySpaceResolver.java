package com.rocket.summer.framework.data.keyvalue.core.mapping;

/**
 * {@link KeySpaceResolver} determines the {@literal keyspace} a given type is assigned to. A keyspace in this context
 * is a specific region/collection/grouping of elements sharing a common keyrange. <br />
 *
 * @author Christoph Strobl
 */
public interface KeySpaceResolver {

    /**
     * Determine the {@literal keySpace} to use for a given type.
     *
     * @param type must not be {@literal null}.
     * @return
     */
    String resolveKeySpace(Class<?> type);
}

