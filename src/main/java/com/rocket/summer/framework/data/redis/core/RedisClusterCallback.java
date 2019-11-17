package com.rocket.summer.framework.data.redis.core;

import com.rocket.summer.framework.dao.DataAccessException;
import com.rocket.summer.framework.data.redis.connection.RedisClusterConnection;

/**
 * Callback interface for low level operations executed against a clustered Redis environment.
 *
 * @author Christoph Strobl
 * @since 1.7
 * @param <T>
 */
public interface RedisClusterCallback<T> {

    /**
     * Gets called by {@link RedisClusterTemplate} with an active Redis connection. Does not need to care about activating
     * or closing the connection or handling exceptions.
     *
     * @param connection never {@literal null}.
     * @return
     * @throws DataAccessException
     */
    T doInRedis(RedisClusterConnection connection) throws DataAccessException;
}

