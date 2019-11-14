package com.rocket.summer.framework.data.redis.cache;

import com.rocket.summer.framework.data.redis.serializer.RedisSerializer;
import com.rocket.summer.framework.data.redis.serializer.StringRedisSerializer;

/**
 * Default implementation for {@link RedisCachePrefix} which uses the given cache name and a delimiter for creating the
 * prefix.
 *
 * @author Costin Leau
 */
public class DefaultRedisCachePrefix implements RedisCachePrefix {

    private final RedisSerializer serializer = new StringRedisSerializer();
    private final String delimiter;

    public DefaultRedisCachePrefix() {
        this(":");
    }

    public DefaultRedisCachePrefix(String delimiter) {
        this.delimiter = delimiter;
    }

    public byte[] prefix(String cacheName) {
        return serializer.serialize((delimiter != null ? cacheName.concat(delimiter) : cacheName.concat(":")));
    }
}

