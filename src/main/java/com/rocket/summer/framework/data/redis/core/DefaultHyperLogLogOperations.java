package com.rocket.summer.framework.data.redis.core;

import java.util.Arrays;

import com.rocket.summer.framework.dao.DataAccessException;
import com.rocket.summer.framework.data.redis.connection.RedisConnection;

/**
 * @author Christoph Strobl
 * @since 1.5
 * @param <K>
 * @param <V>
 */
public class DefaultHyperLogLogOperations<K, V> extends AbstractOperations<K, V> implements HyperLogLogOperations<K, V> {

    public DefaultHyperLogLogOperations(RedisTemplate<K, V> template) {
        super(template);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.HyperLogLogOperations#add(java.lang.Object, java.lang.Object[])
     */
    @Override
    public Long add(K key, V... values) {

        final byte[] rawKey = rawKey(key);
        final byte[][] rawValues = rawValues(values);

        return execute(new RedisCallback<Long>() {

            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.pfAdd(rawKey, rawValues);
            }
        }, true);

    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.HyperLogLogOperations#size(java.lang.Object[])
     */
    @Override
    public Long size(K... keys) {

        final byte[][] rawKeys = rawKeys(Arrays.asList(keys));

        return execute(new RedisCallback<Long>() {

            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.pfCount(rawKeys);
            }
        }, true);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.HyperLogLogOperations#union(java.lang.Object, java.lang.Object[])
     */
    @Override
    public Long union(K destination, K... sourceKeys) {

        final byte[] rawDestinationKey = rawKey(destination);
        final byte[][] rawSourceKeys = rawKeys(Arrays.asList(sourceKeys));

        return execute(new RedisCallback<Long>() {

            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {

                connection.pfMerge(rawDestinationKey, rawSourceKeys);
                return connection.pfCount(rawDestinationKey);
            }
        }, true);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.HyperLogLogOperations#delete(java.lang.Object)
     */
    @Override
    public void delete(K key) {
        template.delete(key);
    }
}

