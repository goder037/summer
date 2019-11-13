package com.rocket.summer.framework.data.redis.core;

import com.rocket.summer.framework.dao.DataAccessException;

/**
 * Callback executing all operations against a surrogate 'session' (basically against the same underlying Redis
 * connection). Allows 'transactions' to take place through the use of multi/discard/exec/watch/unwatch commands.
 *
 * @author Costin Leau
 */
public interface SessionCallback<T> {

    /**
     * Executes all the given operations inside the same session.
     *
     * @param operations Redis operations
     * @return return value
     */
    <K, V> T execute(RedisOperations<K, V> operations) throws DataAccessException;
}

