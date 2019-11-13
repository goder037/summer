package com.rocket.summer.framework.data.redis.core;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * List operations bound to a certain key.
 *
 * @author Costin Leau
 * @author Mark Paluch
 */
public interface BoundListOperations<K, V> extends BoundKeyOperations<K> {

    /**
     * Get elements between {@code begin} and {@code end} from list at the bound key.
     *
     * @param start
     * @param end
     * @return
     * @see <a href="http://redis.io/commands/lrange">Redis Documentation: LRANGE</a>
     */
    List<V> range(long start, long end);

    /**
     * Trim list at the bound key to elements between {@code start} and {@code end}.
     *
     * @param start
     * @param end
     * @see <a href="http://redis.io/commands/ltrim">Redis Documentation: LTRIM</a>
     */
    void trim(long start, long end);

    /**
     * Get the size of list stored at the bound key.
     *
     * @return
     * @see <a href="http://redis.io/commands/llen">Redis Documentation: LLEN</a>
     */
    Long size();

    /**
     * Prepend {@code value} to the bound key.
     *
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
     */
    Long leftPush(V value);

    /**
     * Prepend {@code values} to the bound key.
     *
     * @param values
     * @return
     * @see <a href="http://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
     */
    Long leftPushAll(V... values);

    /**
     * Prepend {@code values} to the bound key only if the list exists.
     *
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/lpushx">Redis Documentation: LPUSHX</a>
     */
    Long leftPushIfPresent(V value);

    /**
     * Prepend {@code values} to the bound key before {@code value}.
     *
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
     */
    Long leftPush(V pivot, V value);

    /**
     * Append {@code value} to the bound key.
     *
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
     */
    Long rightPush(V value);

    /**
     * Append {@code values} to the bound key.
     *
     * @param values
     * @return
     * @see <a href="http://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
     */
    Long rightPushAll(V... values);

    /**
     * Append {@code values} to the bound key only if the list exists.
     *
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/rpushx">Redis Documentation: RPUSHX</a>
     */
    Long rightPushIfPresent(V value);

    /**
     * Append {@code values} to the bound key before {@code value}.
     *
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/lpush">Redis Documentation: RPUSH</a>
     */
    Long rightPush(V pivot, V value);

    /**
     * Set the {@code value} list element at {@code index}.
     *
     * @param index
     * @param value
     * @see <a href="http://redis.io/commands/lset">Redis Documentation: LSET</a>
     */
    void set(long index, V value);

    /**
     * Removes the first {@code count} occurrences of {@code value} from the list stored at the bound key.
     *
     * @param count
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/lrem">Redis Documentation: LREM</a>
     */
    Long remove(long count, Object value);

    /**
     * Get element at {@code index} form list at the bound key.
     *
     * @param index
     * @return
     * @see <a href="http://redis.io/commands/lindex">Redis Documentation: LINDEX</a>
     */
    V index(long index);

    /**
     * Removes and returns first element in list stored at the bound key.
     *
     * @return
     * @see <a href="http://redis.io/commands/lpop">Redis Documentation: LPOP</a>
     */
    V leftPop();

    /**
     * Removes and returns first element from lists stored at the bound key . <br>
     * <b>Blocks connection</b> until element available or {@code timeout} reached.
     *
     * @param timeout
     * @param unit must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/blpop">Redis Documentation: BLPOP</a>
     */
    V leftPop(long timeout, TimeUnit unit);

    /**
     * Removes and returns last element in list stored at the bound key.
     *
     * @return
     * @see <a href="http://redis.io/commands/rpop">Redis Documentation: RPOP</a>
     */
    V rightPop();

    /**
     * Removes and returns last element from lists stored at the bound key. <br>
     * <b>Blocks connection</b> until element available or {@code timeout} reached.
     *
     * @param timeout
     * @param unit must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/brpop">Redis Documentation: BRPOP</a>
     */
    V rightPop(long timeout, TimeUnit unit);

    RedisOperations<K, V> getOperations();
}

