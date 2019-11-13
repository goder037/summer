package com.rocket.summer.framework.data.redis.core.query;

import java.util.List;

import com.rocket.summer.framework.data.redis.connection.RedisConnection;
import com.rocket.summer.framework.data.redis.connection.SortParameters;
import com.rocket.summer.framework.data.redis.connection.SortParameters.Order;
import com.rocket.summer.framework.data.redis.connection.SortParameters.Range;
import com.rocket.summer.framework.data.redis.core.RedisTemplate;

/**
 * High-level abstraction over a Redis SORT (generified equivalent of {@link SortParameters}). To be used with
 * {@link RedisTemplate} (just as {@link SortParameters} is used by {@link RedisConnection}).
 *
 * @author Costin Leau
 */
public interface SortQuery<K> {

    /**
     * Returns the sorting order. Can be null if nothing is specified.
     *
     * @return sorting order
     */
    Order getOrder();

    /**
     * Indicates if the sorting is numeric (default) or alphabetical (lexicographical). Can be null if nothing is
     * specified.
     *
     * @return the type of sorting
     */
    Boolean isAlphabetic();

    /**
     * Returns the sorting limit (range or pagination). Can be null if nothing is specified.
     *
     * @return sorting limit/range
     */
    Range getLimit();

    /**
     * Return the target key for sorting.
     *
     * @return the target key
     */
    K getKey();

    /**
     * Returns the pattern of the external key used for sorting.
     *
     * @return the external key pattern
     */
    String getBy();

    /**
     * Returns the external key(s) whose values are returned by the sort.
     *
     * @return the (list of) keys used for GET
     */
    List<String> getGetPattern();
}

