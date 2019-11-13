package com.rocket.summer.framework.data.redis.core;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis list specific operations.
 *
 * @author Costin Leau
 * @author David Liu
 * @author Thomas Darimont
 * @author Christoph Strobl
 * @author Mark Paluch
 */
public interface ListOperations<K, V> {

    /**
     * Get elements between {@code begin} and {@code end} from list at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param start
     * @param end
     * @return
     * @see <a href="http://redis.io/commands/lrange">Redis Documentation: LRANGE</a>
     */
    List<V> range(K key, long start, long end);

    /**
     * Trim list at {@code key} to elements between {@code start} and {@code end}.
     *
     * @param key must not be {@literal null}.
     * @param start
     * @param end
     * @see <a href="http://redis.io/commands/ltrim">Redis Documentation: LTRIM</a>
     */
    void trim(K key, long start, long end);

    /**
     * Get the size of list stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/llen">Redis Documentation: LLEN</a>
     */
    Long size(K key);

    /**
     * Prepend {@code value} to {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
     */
    Long leftPush(K key, V value);

    /**
     * Prepend {@code values} to {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param values
     * @return
     * @see <a href="http://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
     */
    Long leftPushAll(K key, V... values);

    /**
     * Prepend {@code values} to {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param values must not be {@literal null}.
     * @return
     * @since 1.5
     * @see <a href="http://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
     */
    Long leftPushAll(K key, Collection<V> values);

    /**
     * Prepend {@code values} to {@code key} only if the list exists.
     *
     * @param key must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/lpushx">Redis Documentation: LPUSHX</a>
     */
    Long leftPushIfPresent(K key, V value);

    /**
     * Prepend {@code values} to {@code key} before {@code value}.
     *
     * @param key must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
     */
    Long leftPush(K key, V pivot, V value);

    /**
     * Append {@code value} to {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
     */
    Long rightPush(K key, V value);

    /**
     * Append {@code values} to {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param values
     * @return
     * @see <a href="http://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
     */
    Long rightPushAll(K key, V... values);

    /**
     * Append {@code values} to {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param values
     * @return
     * @since 1.5
     * @see <a href="http://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
     */
    Long rightPushAll(K key, Collection<V> values);

    /**
     * Append {@code values} to {@code key} only if the list exists.
     *
     * @param key must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/rpushx">Redis Documentation: RPUSHX</a>
     */
    Long rightPushIfPresent(K key, V value);

    /**
     * Append {@code values} to {@code key} before {@code value}.
     *
     * @param key must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/lpush">Redis Documentation: RPUSH</a>
     */
    Long rightPush(K key, V pivot, V value);

    /**
     * Set the {@code value} list element at {@code index}.
     *
     * @param key must not be {@literal null}.
     * @param index
     * @param value
     * @see <a href="http://redis.io/commands/lset">Redis Documentation: LSET</a>
     */
    void set(K key, long index, V value);

    /**
     * Removes the first {@code count} occurrences of {@code value} from the list stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param count
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/lrem">Redis Documentation: LREM</a>
     */
    Long remove(K key, long count, Object value);

    /**
     * Get element at {@code index} form list at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param index
     * @return
     * @see <a href="http://redis.io/commands/lindex">Redis Documentation: LINDEX</a>
     */
    V index(K key, long index);

    /**
     * Removes and returns first element in list stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/lpop">Redis Documentation: LPOP</a>
     */
    V leftPop(K key);

    /**
     * Removes and returns first element from lists stored at {@code key} . <br>
     * <b>Blocks connection</b> until element available or {@code timeout} reached.
     *
     * @param key must not be {@literal null}.
     * @param timeout
     * @param unit must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/blpop">Redis Documentation: BLPOP</a>
     */
    V leftPop(K key, long timeout, TimeUnit unit);

    /**
     * Removes and returns last element in list stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/rpop">Redis Documentation: RPOP</a>
     */
    V rightPop(K key);

    /**
     * Removes and returns last element from lists stored at {@code key}. <br>
     * <b>Blocks connection</b> until element available or {@code timeout} reached.
     *
     * @param key must not be {@literal null}.
     * @param timeout
     * @param unit must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/brpop">Redis Documentation: BRPOP</a>
     */
    V rightPop(K key, long timeout, TimeUnit unit);

    /**
     * Remove the last element from list at {@code sourceKey}, append it to {@code destinationKey} and return its value.
     *
     * @param sourceKey must not be {@literal null}.
     * @param destinationKey must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/rpoplpush">Redis Documentation: RPOPLPUSH</a>
     */
    V rightPopAndLeftPush(K sourceKey, K destinationKey);

    /**
     * Remove the last element from list at {@code srcKey}, append it to {@code dstKey} and return its value.<br>
     * <b>Blocks connection</b> until element available or {@code timeout} reached.
     *
     * @param sourceKey must not be {@literal null}.
     * @param destinationKey must not be {@literal null}.
     * @param timeout
     * @param unit must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/brpoplpush">Redis Documentation: BRPOPLPUSH</a>
     */
    V rightPopAndLeftPush(K sourceKey, K destinationKey, long timeout, TimeUnit unit);

    RedisOperations<K, V> getOperations();
}

