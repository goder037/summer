package com.rocket.summer.framework.data.redis.core;

import com.rocket.summer.framework.data.redis.connection.DefaultStringRedisConnection;
import com.rocket.summer.framework.data.redis.connection.RedisConnection;
import com.rocket.summer.framework.data.redis.connection.RedisConnectionFactory;
import com.rocket.summer.framework.data.redis.connection.StringRedisConnection;
import com.rocket.summer.framework.data.redis.serializer.RedisSerializer;
import com.rocket.summer.framework.data.redis.serializer.StringRedisSerializer;

/**
 * String-focused extension of RedisTemplate. Since most operations against Redis are String based, this class provides
 * a dedicated class that minimizes configuration of its more generic {@link RedisTemplate template} especially in terms
 * of serializers.
 * <p/>
 * Note that this template exposes the {@link RedisConnection} used by the {@link RedisCallback} as a
 * {@link StringRedisConnection}.
 *
 * @author Costin Leau
 */
public class StringRedisTemplate extends RedisTemplate<String, String> {

    /**
     * Constructs a new <code>StringRedisTemplate</code> instance. {@link #setConnectionFactory(RedisConnectionFactory)}
     * and {@link #afterPropertiesSet()} still need to be called.
     */
    public StringRedisTemplate() {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        setKeySerializer(stringSerializer);
        setValueSerializer(stringSerializer);
        setHashKeySerializer(stringSerializer);
        setHashValueSerializer(stringSerializer);
    }

    /**
     * Constructs a new <code>StringRedisTemplate</code> instance ready to be used.
     *
     * @param connectionFactory connection factory for creating new connections
     */
    public StringRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }

    protected RedisConnection preProcessConnection(RedisConnection connection, boolean existingConnection) {
        return new DefaultStringRedisConnection(connection);
    }
}

