package com.rocket.summer.framework.data.redis.connection;

import java.util.List;
import java.util.Set;

import com.rocket.summer.framework.data.redis.core.Cursor;
import com.rocket.summer.framework.data.redis.core.ScanOptions;

/**
 * Set-specific commands supported by Redis.
 *
 * @author Costin Leau
 * @author Christoph Strobl
 * @author Mark Paluch
 */
public interface RedisSetCommands {

    /**
     * Add given {@code values} to set at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param values
     * @return
     * @see <a href="http://redis.io/commands/sadd">Redis Documentation: SADD</a>
     */
    Long sAdd(byte[] key, byte[]... values);

    /**
     * Remove given {@code values} from set at {@code key} and return the number of removed elements.
     *
     * @param key must not be {@literal null}.
     * @param values
     * @return
     * @see <a href="http://redis.io/commands/srem">Redis Documentation: SREM</a>
     */
    Long sRem(byte[] key, byte[]... values);

    /**
     * Remove and return a random member from set at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/spop">Redis Documentation: SPOP</a>
     */
    byte[] sPop(byte[] key);

    /**
     * Move {@code value} from {@code srcKey} to {@code destKey}
     *
     * @param srcKey must not be {@literal null}.
     * @param destKey  must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/smove">Redis Documentation: SMOVE</a>
     */
    Boolean sMove(byte[] srcKey, byte[] destKey, byte[] value);

    /**
     * Get size of set at {@code key}.
     *
     * @param key  must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/scard">Redis Documentation: SCARD</a>
     */
    Long sCard(byte[] key);

    /**
     * Check if set at {@code key} contains {@code value}.
     *
     * @param key must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/sismember">Redis Documentation: SISMEMBER</a>
     */
    Boolean sIsMember(byte[] key, byte[] value);

    /**
     * Returns the members intersecting all given sets at {@code keys}.
     *
     * @param keys  must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/sinter">Redis Documentation: SINTER</a>
     */
    Set<byte[]> sInter(byte[]... keys);

    /**
     * Intersect all given sets at {@code keys} and store result in {@code destKey}.
     *
     * @param destKey must not be {@literal null}.
     * @param keys must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/sinterstore">Redis Documentation: SINTERSTORE</a>
     */
    Long sInterStore(byte[] destKey, byte[]... keys);

    /**
     * Union all sets at given {@code keys}.
     *
     * @param keys must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/sunion">Redis Documentation: SUNION</a>
     */
    Set<byte[]> sUnion(byte[]... keys);

    /**
     * Union all sets at given {@code keys} and store result in {@code destKey}.
     *
     * @param destKey must not be {@literal null}.
     * @param keys  must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/sunionstore">Redis Documentation: SUNIONSTORE</a>
     */
    Long sUnionStore(byte[] destKey, byte[]... keys);

    /**
     * Diff all sets for given {@code keys}.
     *
     * @param keys must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/sdiff">Redis Documentation: SDIFF</a>
     */
    Set<byte[]> sDiff(byte[]... keys);

    /**
     * Diff all sets for given {@code keys} and store result in {@code destKey}.
     *
     * @param destKey must not be {@literal null}.
     * @param keys must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/sdiffstore">Redis Documentation: SDIFFSTORE</a>
     */
    Long sDiffStore(byte[] destKey, byte[]... keys);

    /**
     * Get all elements of set at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/smembers">Redis Documentation: SMEMBERS</a>
     */
    Set<byte[]> sMembers(byte[] key);

    /**
     * Get random element from set at {@code key}.
     *
     * @param key  must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/srandmember">Redis Documentation: SRANDMEMBER</a>
     */
    byte[] sRandMember(byte[] key);

    /**
     * Get {@code count} random elements from set at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param count
     * @return
     * @see <a href="http://redis.io/commands/srandmember">Redis Documentation: SRANDMEMBER</a>
     */
    List<byte[]> sRandMember(byte[] key, long count);

    /**
     * Use a {@link Cursor} to iterate over elements in set at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param options must not be {@literal null}.
     * @return
     * @since 1.4
     * @see <a href="http://redis.io/commands/scan">Redis Documentation: SCAN</a>
     */
    Cursor<byte[]> sScan(byte[] key, ScanOptions options);
}

