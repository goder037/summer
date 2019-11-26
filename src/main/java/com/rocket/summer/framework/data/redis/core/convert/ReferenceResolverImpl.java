package com.rocket.summer.framework.data.redis.core.convert;

import java.io.Serializable;
import java.util.Map;

import com.rocket.summer.framework.dao.DataAccessException;
import com.rocket.summer.framework.data.redis.connection.RedisConnection;
import com.rocket.summer.framework.data.redis.core.RedisCallback;
import com.rocket.summer.framework.data.redis.core.RedisKeyValueAdapter;
import com.rocket.summer.framework.data.redis.core.RedisOperations;
import com.rocket.summer.framework.data.redis.core.convert.BinaryConverters.StringToBytesConverter;
import com.rocket.summer.framework.util.Assert;

/**
 * {@link ReferenceResolver} using {@link RedisKeyValueAdapter} to read raw data.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public class ReferenceResolverImpl implements ReferenceResolver {

    private final RedisOperations<?, ?> redisOps;
    private final StringToBytesConverter converter;

    /**
     * @param redisOperations must not be {@literal null}.
     */
    public ReferenceResolverImpl(RedisOperations<?, ?> redisOperations) {

        Assert.notNull(redisOperations, "RedisOperations must not be null!");

        this.redisOps = redisOperations;
        this.converter = new StringToBytesConverter();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.convert.ReferenceResolver#resolveReference(java.io.Serializable, java.io.Serializable, java.lang.Class)
     */
    @Override
    public Map<byte[], byte[]> resolveReference(Serializable id, String keyspace) {

        final byte[] key = converter.convert(keyspace + ":" + id);

        return redisOps.execute(new RedisCallback<Map<byte[], byte[]>>() {

            @Override
            public Map<byte[], byte[]> doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.hGetAll(key);
            }
        });
    }
}

