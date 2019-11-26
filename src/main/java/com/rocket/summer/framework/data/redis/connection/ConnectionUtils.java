package com.rocket.summer.framework.data.redis.connection;

import com.rocket.summer.framework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * Utilities for examining a {@link RedisConnection}
 *
 * @author Jennifer Hickey
 * @author Thomas Darimont
 */
public abstract class ConnectionUtils {

    public static boolean isJedis(RedisConnectionFactory connectionFactory) {
        return connectionFactory instanceof JedisConnectionFactory;
    }
}

