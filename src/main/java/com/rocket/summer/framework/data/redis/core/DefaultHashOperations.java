package com.rocket.summer.framework.data.redis.core;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.dao.DataAccessException;
import com.rocket.summer.framework.data.redis.connection.RedisConnection;

/**
 * Default implementation of {@link HashOperations}.
 *
 * @author Costin Leau
 * @author Christoph Strobl
 * @author Ninad Divadkar
 */
class DefaultHashOperations<K, HK, HV> extends AbstractOperations<K, Object> implements HashOperations<K, HK, HV> {

    @SuppressWarnings("unchecked")
    DefaultHashOperations(RedisTemplate<K, ?> template) {
        super((RedisTemplate<K, Object>) template);
    }

    @SuppressWarnings("unchecked")
    public HV get(K key, Object hashKey) {
        final byte[] rawKey = rawKey(key);
        final byte[] rawHashKey = rawHashKey(hashKey);

        byte[] rawHashValue = execute(new RedisCallback<byte[]>() {

            public byte[] doInRedis(RedisConnection connection) {
                return connection.hGet(rawKey, rawHashKey);
            }
        }, true);

        return (HV) deserializeHashValue(rawHashValue);
    }

    public Boolean hasKey(K key, Object hashKey) {
        final byte[] rawKey = rawKey(key);
        final byte[] rawHashKey = rawHashKey(hashKey);

        return execute(new RedisCallback<Boolean>() {

            public Boolean doInRedis(RedisConnection connection) {
                return connection.hExists(rawKey, rawHashKey);
            }
        }, true);
    }

    public Long increment(K key, HK hashKey, final long delta) {
        final byte[] rawKey = rawKey(key);
        final byte[] rawHashKey = rawHashKey(hashKey);

        return execute(new RedisCallback<Long>() {

            public Long doInRedis(RedisConnection connection) {
                return connection.hIncrBy(rawKey, rawHashKey, delta);
            }
        }, true);

    }

    public Double increment(K key, HK hashKey, final double delta) {
        final byte[] rawKey = rawKey(key);
        final byte[] rawHashKey = rawHashKey(hashKey);

        return execute(new RedisCallback<Double>() {
            public Double doInRedis(RedisConnection connection) {
                return connection.hIncrBy(rawKey, rawHashKey, delta);
            }
        }, true);
    }

    public Set<HK> keys(K key) {
        final byte[] rawKey = rawKey(key);

        Set<byte[]> rawValues = execute(new RedisCallback<Set<byte[]>>() {

            public Set<byte[]> doInRedis(RedisConnection connection) {
                return connection.hKeys(rawKey);
            }
        }, true);

        return deserializeHashKeys(rawValues);
    }

    public Long size(K key) {
        final byte[] rawKey = rawKey(key);

        return execute(new RedisCallback<Long>() {

            public Long doInRedis(RedisConnection connection) {
                return connection.hLen(rawKey);
            }
        }, true);
    }

    public void putAll(K key, Map<? extends HK, ? extends HV> m) {
        if (m.isEmpty()) {
            return;
        }

        final byte[] rawKey = rawKey(key);

        final Map<byte[], byte[]> hashes = new LinkedHashMap<byte[], byte[]>(m.size());

        for (Map.Entry<? extends HK, ? extends HV> entry : m.entrySet()) {
            hashes.put(rawHashKey(entry.getKey()), rawHashValue(entry.getValue()));
        }

        execute(new RedisCallback<Object>() {

            public Object doInRedis(RedisConnection connection) {
                connection.hMSet(rawKey, hashes);
                return null;
            }
        }, true);
    }

    public List<HV> multiGet(K key, Collection<HK> fields) {
        if (fields.isEmpty()) {
            return Collections.emptyList();
        }

        final byte[] rawKey = rawKey(key);

        final byte[][] rawHashKeys = new byte[fields.size()][];

        int counter = 0;
        for (HK hashKey : fields) {
            rawHashKeys[counter++] = rawHashKey(hashKey);
        }

        List<byte[]> rawValues = execute(new RedisCallback<List<byte[]>>() {

            public List<byte[]> doInRedis(RedisConnection connection) {
                return connection.hMGet(rawKey, rawHashKeys);
            }
        }, true);

        return deserializeHashValues(rawValues);
    }

    public void put(K key, HK hashKey, HV value) {
        final byte[] rawKey = rawKey(key);
        final byte[] rawHashKey = rawHashKey(hashKey);
        final byte[] rawHashValue = rawHashValue(value);

        execute(new RedisCallback<Object>() {

            public Object doInRedis(RedisConnection connection) {
                connection.hSet(rawKey, rawHashKey, rawHashValue);
                return null;
            }
        }, true);
    }

    public Boolean putIfAbsent(K key, HK hashKey, HV value) {
        final byte[] rawKey = rawKey(key);
        final byte[] rawHashKey = rawHashKey(hashKey);
        final byte[] rawHashValue = rawHashValue(value);

        return execute(new RedisCallback<Boolean>() {

            public Boolean doInRedis(RedisConnection connection) {
                return connection.hSetNX(rawKey, rawHashKey, rawHashValue);
            }
        }, true);
    }

    public List<HV> values(K key) {
        final byte[] rawKey = rawKey(key);

        List<byte[]> rawValues = execute(new RedisCallback<List<byte[]>>() {

            public List<byte[]> doInRedis(RedisConnection connection) {
                return connection.hVals(rawKey);
            }
        }, true);

        return deserializeHashValues(rawValues);
    }

    public Long delete(K key, Object... hashKeys) {
        final byte[] rawKey = rawKey(key);
        final byte[][] rawHashKeys = rawHashKeys(hashKeys);

        return execute(new RedisCallback<Long>() {

            public Long doInRedis(RedisConnection connection) {
                return connection.hDel(rawKey, rawHashKeys);
            }
        }, true);
    }

    public Map<HK, HV> entries(K key) {
        final byte[] rawKey = rawKey(key);

        Map<byte[], byte[]> entries = execute(new RedisCallback<Map<byte[], byte[]>>() {

            public Map<byte[], byte[]> doInRedis(RedisConnection connection) {
                return connection.hGetAll(rawKey);
            }
        }, true);

        return deserializeHashMap(entries);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.HashOperations#hscan(java.lang.Object, com.rocket.summer.framework.data.redis.core.ScanOptions)
     */
    @Override
    public Cursor<Entry<HK, HV>> scan(K key, final ScanOptions options) {

        final byte[] rawKey = rawKey(key);
        return template.executeWithStickyConnection(new RedisCallback<Cursor<Map.Entry<HK, HV>>>() {

            @Override
            public Cursor<Entry<HK, HV>> doInRedis(RedisConnection connection) throws DataAccessException {

                return new ConvertingCursor<Map.Entry<byte[], byte[]>, Map.Entry<HK, HV>>(connection.hScan(rawKey, options),
                        new Converter<Map.Entry<byte[], byte[]>, Map.Entry<HK, HV>>() {

                            @Override
                            public Entry<HK, HV> convert(final Entry<byte[], byte[]> source) {

                                return new Map.Entry<HK, HV>() {

                                    @Override
                                    public HK getKey() {
                                        return deserializeHashKey(source.getKey());
                                    }

                                    @Override
                                    public HV getValue() {
                                        return deserializeHashValue(source.getValue());
                                    }

                                    @Override
                                    public HV setValue(HV value) {
                                        throw new UnsupportedOperationException("Values cannot be set when scanning through entries.");
                                    }
                                };

                            }
                        });
            }

        });

    }
}
