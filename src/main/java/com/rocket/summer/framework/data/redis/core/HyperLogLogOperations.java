package com.rocket.summer.framework.data.redis.core;

/**
 * @author Christoph Strobl
 * @since 1.5
 */
public interface HyperLogLogOperations<K, V> {

    /**
     * Adds the given {@literal values} to the {@literal key}.
     *
     * @param key must not be {@literal null}.
     * @param values must not be {@literal null}.
     * @return 1 of at least one of the values was added to the key; 0 otherwise.
     */
    Long add(K key, V... values);

    /**
     * Gets the current number of elements within the {@literal key}.
     *
     * @param keys must not be {@literal null} or {@literal empty}.
     * @return
     */
    Long size(K... keys);

    /**
     * Merges all values of given {@literal sourceKeys} into {@literal destination} key.
     *
     * @param destination key of HyperLogLog to move source keys into.
     * @param sourceKeys must not be {@literal null} or {@literal empty}.
     */
    Long union(K destination, K... sourceKeys);

    /**
     * Removes the given {@literal key}.
     *
     * @param key must not be {@literal null}.
     */
    void delete(K key);

}

