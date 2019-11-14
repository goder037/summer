package com.rocket.summer.framework.data.redis.connection;

/**
 * Specifies that the connection decorates another {@link RedisConnection}.
 *
 * @author Mark Paluch
 * @since 1.7
 */
public interface DecoratedRedisConnection {

    /**
     * Gets the underlying {@link RedisConnection}.
     *
     * @return never {@literal null}.
     */
    RedisConnection getDelegate();

}
