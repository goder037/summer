package com.rocket.summer.framework.data.redis.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.dao.DataAccessException;
import com.rocket.summer.framework.data.redis.connection.RedisConnection;

/**
 * Default implementation of {@link SetOperations}.
 *
 * @author Costin Leau
 * @author Christoph Strobl
 */
class DefaultSetOperations<K, V> extends AbstractOperations<K, V> implements SetOperations<K, V> {

    public DefaultSetOperations(RedisTemplate<K, V> template) {
        super(template);
    }

    public Long add(K key, V... values) {
        final byte[] rawKey = rawKey(key);
        final byte[][] rawValues = rawValues(values);
        return execute(new RedisCallback<Long>() {

            public Long doInRedis(RedisConnection connection) {
                return connection.sAdd(rawKey, rawValues);
            }
        }, true);
    }

    public Set<V> difference(K key, K otherKey) {
        return difference(key, Collections.singleton(otherKey));
    }

    public Set<V> difference(final K key, final Collection<K> otherKeys) {
        final byte[][] rawKeys = rawKeys(key, otherKeys);
        Set<byte[]> rawValues = execute(new RedisCallback<Set<byte[]>>() {

            public Set<byte[]> doInRedis(RedisConnection connection) {
                return connection.sDiff(rawKeys);
            }
        }, true);

        return deserializeValues(rawValues);
    }

    public Long differenceAndStore(K key, K otherKey, K destKey) {
        return differenceAndStore(key, Collections.singleton(otherKey), destKey);
    }

    public Long differenceAndStore(final K key, final Collection<K> otherKeys, K destKey) {
        final byte[][] rawKeys = rawKeys(key, otherKeys);
        final byte[] rawDestKey = rawKey(destKey);
        return execute(new RedisCallback<Long>() {

            public Long doInRedis(RedisConnection connection) {
                return connection.sDiffStore(rawDestKey, rawKeys);
            }
        }, true);
    }

    public Set<V> intersect(K key, K otherKey) {
        return intersect(key, Collections.singleton(otherKey));
    }

    public Set<V> intersect(K key, Collection<K> otherKeys) {
        final byte[][] rawKeys = rawKeys(key, otherKeys);
        Set<byte[]> rawValues = execute(new RedisCallback<Set<byte[]>>() {

            public Set<byte[]> doInRedis(RedisConnection connection) {
                return connection.sInter(rawKeys);
            }
        }, true);

        return deserializeValues(rawValues);
    }

    public Long intersectAndStore(K key, K otherKey, K destKey) {
        return intersectAndStore(key, Collections.singleton(otherKey), destKey);
    }

    public Long intersectAndStore(K key, Collection<K> otherKeys, K destKey) {
        final byte[][] rawKeys = rawKeys(key, otherKeys);
        final byte[] rawDestKey = rawKey(destKey);
        return execute(new RedisCallback<Long>() {

            public Long doInRedis(RedisConnection connection) {
                return connection.sInterStore(rawDestKey, rawKeys);
            }
        }, true);
    }

    public Boolean isMember(K key, Object o) {
        final byte[] rawKey = rawKey(key);
        final byte[] rawValue = rawValue(o);
        return execute(new RedisCallback<Boolean>() {

            public Boolean doInRedis(RedisConnection connection) {
                return connection.sIsMember(rawKey, rawValue);
            }
        }, true);
    }

    public Set<V> members(K key) {
        final byte[] rawKey = rawKey(key);
        Set<byte[]> rawValues = execute(new RedisCallback<Set<byte[]>>() {

            public Set<byte[]> doInRedis(RedisConnection connection) {
                return connection.sMembers(rawKey);
            }
        }, true);

        return deserializeValues(rawValues);
    }

    public Boolean move(K key, V value, K destKey) {
        final byte[] rawKey = rawKey(key);
        final byte[] rawDestKey = rawKey(destKey);
        final byte[] rawValue = rawValue(value);

        return execute(new RedisCallback<Boolean>() {

            public Boolean doInRedis(RedisConnection connection) {
                return connection.sMove(rawKey, rawDestKey, rawValue);
            }
        }, true);
    }

    public V randomMember(K key) {

        return execute(new ValueDeserializingRedisCallback(key) {

            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                return connection.sRandMember(rawKey);
            }
        }, true);
    }

    public Set<V> distinctRandomMembers(K key, final long count) {
        if (count < 0) {
            throw new IllegalArgumentException("Negative count not supported. "
                    + "Use randomMembers to allow duplicate elements.");
        }
        final byte[] rawKey = rawKey(key);
        Set<byte[]> rawValues = execute(new RedisCallback<Set<byte[]>>() {
            public Set<byte[]> doInRedis(RedisConnection connection) {
                return new HashSet<byte[]>(connection.sRandMember(rawKey, count));
            }
        }, true);

        return deserializeValues(rawValues);
    }

    public List<V> randomMembers(K key, final long count) {
        if (count < 0) {
            throw new IllegalArgumentException("Use a positive number for count. "
                    + "This method is already allowing duplicate elements.");
        }
        final byte[] rawKey = rawKey(key);
        List<byte[]> rawValues = execute(new RedisCallback<List<byte[]>>() {
            public List<byte[]> doInRedis(RedisConnection connection) {
                return connection.sRandMember(rawKey, -count);
            }
        }, true);

        return deserializeValues(rawValues);
    }

    public Long remove(K key, Object... values) {
        final byte[] rawKey = rawKey(key);
        final byte[][] rawValues = rawValues(values);
        return execute(new RedisCallback<Long>() {

            public Long doInRedis(RedisConnection connection) {
                return connection.sRem(rawKey, rawValues);
            }
        }, true);
    }

    public V pop(K key) {
        return execute(new ValueDeserializingRedisCallback(key) {

            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                return connection.sPop(rawKey);
            }
        }, true);
    }

    public Long size(K key) {
        final byte[] rawKey = rawKey(key);
        return execute(new RedisCallback<Long>() {

            public Long doInRedis(RedisConnection connection) {
                return connection.sCard(rawKey);
            }
        }, true);
    }

    public Set<V> union(K key, K otherKey) {
        return union(key, Collections.singleton(otherKey));
    }

    public Set<V> union(K key, Collection<K> otherKeys) {
        final byte[][] rawKeys = rawKeys(key, otherKeys);
        Set<byte[]> rawValues = execute(new RedisCallback<Set<byte[]>>() {

            public Set<byte[]> doInRedis(RedisConnection connection) {
                return connection.sUnion(rawKeys);
            }
        }, true);

        return deserializeValues(rawValues);
    }

    public Long unionAndStore(K key, K otherKey, K destKey) {
        return unionAndStore(key, Collections.singleton(otherKey), destKey);
    }

    public Long unionAndStore(K key, Collection<K> otherKeys, K destKey) {
        final byte[][] rawKeys = rawKeys(key, otherKeys);
        final byte[] rawDestKey = rawKey(destKey);
        return execute(new RedisCallback<Long>() {

            public Long doInRedis(RedisConnection connection) {
                return connection.sUnionStore(rawDestKey, rawKeys);
            }
        }, true);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.SetOperations#sScan(java.lang.Object, com.rocket.summer.framework.data.redis.core.ScanOptions)
     */
    @Override
    public Cursor<V> scan(K key, final ScanOptions options) {

        final byte[] rawKey = rawKey(key);
        return template.executeWithStickyConnection(new RedisCallback<Cursor<V>>() {

            @Override
            public Cursor<V> doInRedis(RedisConnection connection) throws DataAccessException {
                return new ConvertingCursor<byte[], V>(connection.sScan(rawKey, options), new Converter<byte[], V>() {

                    @Override
                    public V convert(byte[] source) {
                        return deserializeValue(source);
                    }
                });
            }
        });

    }
}

