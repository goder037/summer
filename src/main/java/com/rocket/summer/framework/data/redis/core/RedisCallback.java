package com.rocket.summer.framework.data.redis.core;

import com.rocket.summer.framework.dao.DataAccessException;
import com.rocket.summer.framework.data.redis.connection.RedisConnection;

/**
 * Callback interface for Redis 'low level' code. To be used with {@link RedisTemplate} execution methods, often as
 * anonymous classes within a method implementation. Usually, used for chaining several operations together (
 * {@code get/set/trim etc...}.
 *
 * @author Costin Leau
 */
public interface RedisCallback<T> {

    /**
     * Gets called by {@link RedisTemplate} with an active Redis connection. Does not need to care about activating or
     * closing the connection or handling exceptions.
     *
     * @param connection active Redis connection
     * @return a result object or {@code null} if none
     * @throws DataAccessException
     */
    T doInRedis(RedisConnection connection) throws DataAccessException;
}

