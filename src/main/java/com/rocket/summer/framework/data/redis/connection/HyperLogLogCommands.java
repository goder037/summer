package com.rocket.summer.framework.data.redis.connection;

/**
 * {@literal HyperLogLog} specific commands supported by Redis.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 1.5
 */
public interface HyperLogLogCommands {

    /**
     * Adds given {@literal values} to the HyperLogLog stored at given {@literal key}.
     *
     * @param key must not be {@literal null}.
     * @param values must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/pfadd">Redis Documentation: PFADD</a>
     */
    Long pfAdd(byte[] key, byte[]... values);

    /**
     * Return the approximated cardinality of the structures observed by the HyperLogLog at {@literal key(s)}.
     *
     * @param keys must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/pfcount">Redis Documentation: PFCOUNT</a>
     */
    Long pfCount(byte[]... keys);

    /**
     * Merge N different HyperLogLogs at {@literal sourceKeys} into a single {@literal destinationKey}.
     *
     * @param destinationKey must not be {@literal null}.
     * @param sourceKeys must not be {@literal null}.
     * @see <a href="http://redis.io/commands/pfmerge">Redis Documentation: PFMERGE</a>
     */
    void pfMerge(byte[] destinationKey, byte[]... sourceKeys);

}

