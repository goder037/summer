package com.rocket.summer.framework.data.redis.core.index;

import java.io.Serializable;
import java.util.Set;

/**
 * {@link IndexDefinitionProvider} give access to {@link IndexDefinition}s for creating secondary index structures.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public interface IndexDefinitionProvider {

    /**
     * Gets all of the {@link RedisIndexSetting} for a given keyspace.
     *
     * @param keyspace the keyspace to get
     * @return never {@literal null}
     */
    boolean hasIndexFor(Serializable keyspace);

    /**
     * Checks if an index is defined for a given keyspace and property path.
     *
     * @param keyspace
     * @param path
     * @return true if index is defined.
     */
    boolean hasIndexFor(Serializable keyspace, String path);

    /**
     * Get the list of {@link IndexDefinition} for a given keyspace.
     *
     * @param keyspace
     * @return never {@literal null}.
     */
    Set<IndexDefinition> getIndexDefinitionsFor(Serializable keyspace);

    /**
     * Get the list of {@link IndexDefinition} for a given keyspace and property path.
     *
     * @param keyspace
     * @param path
     * @return never {@literal null}.
     */
    Set<IndexDefinition> getIndexDefinitionsFor(Serializable keyspace, String path);
}

