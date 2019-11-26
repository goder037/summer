package com.rocket.summer.framework.data.redis.core.convert;

/**
 * {@link IndexedData} represents a secondary index for a property path in a given keyspace.
 *
 * @author Christoph Strobl
 * @author Rob Winch
 * @since 1.7
 */
public interface IndexedData {

    /**
     * Get the {@link String} representation of the index name.
     *
     * @return never {@literal null}.
     */
    String getIndexName();

    /**
     * Get the associated keyspace the index resides in.
     *
     * @return
     */
    String getKeyspace();

}

