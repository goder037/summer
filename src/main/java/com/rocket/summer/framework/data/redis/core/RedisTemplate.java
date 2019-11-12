package com.rocket.summer.framework.data.redis.core;

import java.io.Closeable;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import com.rocket.summer.framework.beans.factory.BeanClassLoaderAware;
import com.rocket.summer.framework.dao.DataAccessException;
import com.rocket.summer.framework.dao.InvalidDataAccessApiUsageException;
import com.rocket.summer.framework.data.redis.connection.DataType;
import com.rocket.summer.framework.data.redis.connection.RedisConnection;
import com.rocket.summer.framework.data.redis.connection.RedisConnectionFactory;
import com.rocket.summer.framework.data.redis.connection.SortParameters;
import com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Tuple;
import com.rocket.summer.framework.data.redis.core.ZSetOperations.TypedTuple;
import com.rocket.summer.framework.data.redis.core.query.QueryUtils;
import com.rocket.summer.framework.data.redis.core.query.SortQuery;
import com.rocket.summer.framework.data.redis.core.script.DefaultScriptExecutor;
import com.rocket.summer.framework.data.redis.core.script.RedisScript;
import com.rocket.summer.framework.data.redis.core.script.ScriptExecutor;
import com.rocket.summer.framework.data.redis.core.types.RedisClientInfo;
import com.rocket.summer.framework.data.redis.serializer.JdkSerializationRedisSerializer;
import com.rocket.summer.framework.data.redis.serializer.RedisSerializer;
import com.rocket.summer.framework.data.redis.serializer.SerializationUtils;
import com.rocket.summer.framework.data.redis.serializer.StringRedisSerializer;
import com.rocket.summer.framework.transaction.support.TransactionSynchronizationManager;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.CollectionUtils;

public class RedisTemplate<K, V> extends RedisAccessor implements RedisOperations<K, V>, BeanClassLoaderAware {
    private boolean enableTransactionSupport = false;
    private boolean exposeConnection = false;
    private boolean initialized = false;
    private boolean enableDefaultSerializer = true;
    private RedisSerializer<?> defaultSerializer;
    private ClassLoader classLoader;
    private RedisSerializer keySerializer = null;
    private RedisSerializer valueSerializer = null;
    private RedisSerializer hashKeySerializer = null;
    private RedisSerializer hashValueSerializer = null;
    private RedisSerializer<String> stringSerializer = new StringRedisSerializer();
    private ScriptExecutor<K> scriptExecutor;
    private ValueOperations<K, V> valueOps;
    private ListOperations<K, V> listOps;
    private SetOperations<K, V> setOps;
    private ZSetOperations<K, V> zSetOps;
    private GeoOperations<K, V> geoOps;
    private HyperLogLogOperations<K, V> hllOps;

    public RedisTemplate() {
    }

    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        boolean defaultUsed = false;
        if (this.defaultSerializer == null) {
            this.defaultSerializer = new JdkSerializationRedisSerializer(this.classLoader != null ? this.classLoader : this.getClass().getClassLoader());
        }

        if (this.enableDefaultSerializer) {
            if (this.keySerializer == null) {
                this.keySerializer = this.defaultSerializer;
                defaultUsed = true;
            }

            if (this.valueSerializer == null) {
                this.valueSerializer = this.defaultSerializer;
                defaultUsed = true;
            }

            if (this.hashKeySerializer == null) {
                this.hashKeySerializer = this.defaultSerializer;
                defaultUsed = true;
            }

            if (this.hashValueSerializer == null) {
                this.hashValueSerializer = this.defaultSerializer;
                defaultUsed = true;
            }
        }

        if (this.enableDefaultSerializer && defaultUsed) {
            Assert.notNull(this.defaultSerializer, "default serializer null and not all serializers initialized");
        }

        if (this.scriptExecutor == null) {
            this.scriptExecutor = new DefaultScriptExecutor(this);
        }

        this.initialized = true;
    }

    public <T> T execute(RedisCallback<T> action) {
        return this.execute(action, this.isExposeConnection());
    }

    public <T> T execute(RedisCallback<T> action, boolean exposeConnection) {
        return this.execute(action, exposeConnection, false);
    }

    public <T> T execute(RedisCallback<T> action, boolean exposeConnection, boolean pipeline) {
        Assert.isTrue(this.initialized, "template not initialized; call afterPropertiesSet() before using it");
        Assert.notNull(action, "Callback object must not be null");
        RedisConnectionFactory factory = this.getConnectionFactory();
        RedisConnection conn = null;

        Object var11;
        try {
            if (this.enableTransactionSupport) {
                conn = RedisConnectionUtils.bindConnection(factory, this.enableTransactionSupport);
            } else {
                conn = RedisConnectionUtils.getConnection(factory);
            }

            boolean existingConnection = TransactionSynchronizationManager.hasResource(factory);
            RedisConnection connToUse = this.preProcessConnection(conn, existingConnection);
            boolean pipelineStatus = connToUse.isPipelined();
            if (pipeline && !pipelineStatus) {
                connToUse.openPipeline();
            }

            RedisConnection connToExpose = exposeConnection ? connToUse : this.createRedisConnectionProxy(connToUse);
            T result = action.doInRedis(connToExpose);
            if (pipeline && !pipelineStatus) {
                connToUse.closePipeline();
            }

            var11 = this.postProcessResult(result, connToUse, existingConnection);
        } finally {
            RedisConnectionUtils.releaseConnection(conn, factory);
        }

        return var11;
    }

    public <T> T execute(SessionCallback<T> session) {
        Assert.isTrue(this.initialized, "template not initialized; call afterPropertiesSet() before using it");
        Assert.notNull(session, "Callback object must not be null");
        RedisConnectionFactory factory = this.getConnectionFactory();
        RedisConnectionUtils.bindConnection(factory, this.enableTransactionSupport);

        Object var3;
        try {
            var3 = session.execute(this);
        } finally {
            RedisConnectionUtils.unbindConnection(factory);
        }

        return var3;
    }

    public List<Object> executePipelined(SessionCallback<?> session) {
        return this.executePipelined(session, this.valueSerializer);
    }

    public List<Object> executePipelined(final SessionCallback<?> session, final RedisSerializer<?> resultSerializer) {
        Assert.isTrue(this.initialized, "template not initialized; call afterPropertiesSet() before using it");
        Assert.notNull(session, "Callback object must not be null");
        RedisConnectionFactory factory = this.getConnectionFactory();
        RedisConnectionUtils.bindConnection(factory, this.enableTransactionSupport);

        List var4;
        try {
            var4 = (List)this.execute(new RedisCallback<List<Object>>() {
                public List<Object> doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.openPipeline();
                    boolean pipelinedClosed = false;

                    List var5;
                    try {
                        Object result = RedisTemplate.this.executeSession(session);
                        if (result != null) {
                            throw new InvalidDataAccessApiUsageException("Callback cannot return a non-null value as it gets overwritten by the pipeline");
                        }

                        List<Object> closePipeline = connection.closePipeline();
                        pipelinedClosed = true;
                        var5 = RedisTemplate.this.deserializeMixedResults(closePipeline, resultSerializer, RedisTemplate.this.hashKeySerializer, RedisTemplate.this.hashValueSerializer);
                    } finally {
                        if (!pipelinedClosed) {
                            connection.closePipeline();
                        }

                    }

                    return var5;
                }
            });
        } finally {
            RedisConnectionUtils.unbindConnection(factory);
        }

        return var4;
    }

    public List<Object> executePipelined(RedisCallback<?> action) {
        return this.executePipelined(action, this.valueSerializer);
    }

    public List<Object> executePipelined(final RedisCallback<?> action, final RedisSerializer<?> resultSerializer) {
        return (List)this.execute(new RedisCallback<List<Object>>() {
            public List<Object> doInRedis(RedisConnection connection) throws DataAccessException {
                connection.openPipeline();
                boolean pipelinedClosed = false;

                List var5;
                try {
                    Object result = action.doInRedis(connection);
                    if (result != null) {
                        throw new InvalidDataAccessApiUsageException("Callback cannot return a non-null value as it gets overwritten by the pipeline");
                    }

                    List<Object> closePipeline = connection.closePipeline();
                    pipelinedClosed = true;
                    var5 = RedisTemplate.this.deserializeMixedResults(closePipeline, resultSerializer, RedisTemplate.this.hashKeySerializer, RedisTemplate.this.hashValueSerializer);
                } finally {
                    if (!pipelinedClosed) {
                        connection.closePipeline();
                    }

                }

                return var5;
            }
        });
    }

    public <T> T execute(RedisScript<T> script, List<K> keys, Object... args) {
        return this.scriptExecutor.execute(script, keys, args);
    }

    public <T> T execute(RedisScript<T> script, RedisSerializer<?> argsSerializer, RedisSerializer<T> resultSerializer, List<K> keys, Object... args) {
        return this.scriptExecutor.execute(script, argsSerializer, resultSerializer, keys, args);
    }

    public <T extends Closeable> T executeWithStickyConnection(RedisCallback<T> callback) {
        Assert.isTrue(this.initialized, "template not initialized; call afterPropertiesSet() before using it");
        Assert.notNull(callback, "Callback object must not be null");
        RedisConnectionFactory factory = this.getConnectionFactory();
        RedisConnection connection = this.preProcessConnection(RedisConnectionUtils.doGetConnection(factory, true, false, false), false);
        return (Closeable)callback.doInRedis(connection);
    }

    private Object executeSession(SessionCallback<?> session) {
        return session.execute(this);
    }

    protected RedisConnection createRedisConnectionProxy(RedisConnection pm) {
        Class<?>[] ifcs = ClassUtils.getAllInterfacesForClass(pm.getClass(), this.getClass().getClassLoader());
        return (RedisConnection)Proxy.newProxyInstance(pm.getClass().getClassLoader(), ifcs, new CloseSuppressingInvocationHandler(pm));
    }

    protected RedisConnection preProcessConnection(RedisConnection connection, boolean existingConnection) {
        return connection;
    }

    protected <T> T postProcessResult(T result, RedisConnection conn, boolean existingConnection) {
        return result;
    }

    public boolean isExposeConnection() {
        return this.exposeConnection;
    }

    public void setExposeConnection(boolean exposeConnection) {
        this.exposeConnection = exposeConnection;
    }

    public boolean isEnableDefaultSerializer() {
        return this.enableDefaultSerializer;
    }

    public void setEnableDefaultSerializer(boolean enableDefaultSerializer) {
        this.enableDefaultSerializer = enableDefaultSerializer;
    }

    public RedisSerializer<?> getDefaultSerializer() {
        return this.defaultSerializer;
    }

    public void setDefaultSerializer(RedisSerializer<?> serializer) {
        this.defaultSerializer = serializer;
    }

    public void setKeySerializer(RedisSerializer<?> serializer) {
        this.keySerializer = serializer;
    }

    public RedisSerializer<?> getKeySerializer() {
        return this.keySerializer;
    }

    public void setValueSerializer(RedisSerializer<?> serializer) {
        this.valueSerializer = serializer;
    }

    public RedisSerializer<?> getValueSerializer() {
        return this.valueSerializer;
    }

    public RedisSerializer<?> getHashKeySerializer() {
        return this.hashKeySerializer;
    }

    public void setHashKeySerializer(RedisSerializer<?> hashKeySerializer) {
        this.hashKeySerializer = hashKeySerializer;
    }

    public RedisSerializer<?> getHashValueSerializer() {
        return this.hashValueSerializer;
    }

    public void setHashValueSerializer(RedisSerializer<?> hashValueSerializer) {
        this.hashValueSerializer = hashValueSerializer;
    }

    public RedisSerializer<String> getStringSerializer() {
        return this.stringSerializer;
    }

    public void setStringSerializer(RedisSerializer<String> stringSerializer) {
        this.stringSerializer = stringSerializer;
    }

    public void setScriptExecutor(ScriptExecutor<K> scriptExecutor) {
        this.scriptExecutor = scriptExecutor;
    }

    private byte[] rawKey(Object key) {
        Assert.notNull(key, "non null key required");
        return this.keySerializer == null && key instanceof byte[] ? (byte[])((byte[])key) : this.keySerializer.serialize(key);
    }

    private byte[] rawString(String key) {
        return this.stringSerializer.serialize(key);
    }

    private byte[] rawValue(Object value) {
        return this.valueSerializer == null && value instanceof byte[] ? (byte[])((byte[])value) : this.valueSerializer.serialize(value);
    }

    private byte[][] rawKeys(Collection<K> keys) {
        byte[][] rawKeys = new byte[keys.size()][];
        int i = 0;

        Object key;
        for(Iterator var4 = keys.iterator(); var4.hasNext(); rawKeys[i++] = this.rawKey(key)) {
            key = var4.next();
        }

        return rawKeys;
    }

    private K deserializeKey(byte[] value) {
        return this.keySerializer != null ? this.keySerializer.deserialize(value) : value;
    }

    private List<Object> deserializeMixedResults(List<Object> rawValues, RedisSerializer valueSerializer, RedisSerializer hashKeySerializer, RedisSerializer hashValueSerializer) {
        if (rawValues == null) {
            return null;
        } else {
            List<Object> values = new ArrayList();
            Iterator var6 = rawValues.iterator();

            while(true) {
                while(var6.hasNext()) {
                    Object rawValue = var6.next();
                    if (rawValue instanceof byte[] && valueSerializer != null) {
                        values.add(valueSerializer.deserialize((byte[])((byte[])rawValue)));
                    } else if (rawValue instanceof List) {
                        values.add(this.deserializeMixedResults((List)rawValue, valueSerializer, hashKeySerializer, hashValueSerializer));
                    } else if (rawValue instanceof Set && !((Set)rawValue).isEmpty()) {
                        values.add(this.deserializeSet((Set)rawValue, valueSerializer));
                    } else if (rawValue instanceof Map && !((Map)rawValue).isEmpty() && ((Map)rawValue).values().iterator().next() instanceof byte[]) {
                        values.add(SerializationUtils.deserialize((Map)rawValue, hashKeySerializer, hashValueSerializer));
                    } else {
                        values.add(rawValue);
                    }
                }

                return values;
            }
        }
    }

    private Set<?> deserializeSet(Set rawSet, RedisSerializer valueSerializer) {
        if (rawSet.isEmpty()) {
            return rawSet;
        } else {
            Object setValue = rawSet.iterator().next();
            if (setValue instanceof byte[] && valueSerializer != null) {
                return SerializationUtils.deserialize(rawSet, valueSerializer);
            } else {
                return setValue instanceof Tuple ? this.convertTupleValues(rawSet, valueSerializer) : rawSet;
            }
        }
    }

    private Set<TypedTuple<V>> convertTupleValues(Set<Tuple> rawValues, RedisSerializer valueSerializer) {
        Set<TypedTuple<V>> set = new LinkedHashSet(rawValues.size());

        Tuple rawValue;
        Object value;
        for(Iterator var4 = rawValues.iterator(); var4.hasNext(); set.add(new DefaultTypedTuple(value, rawValue.getScore()))) {
            rawValue = (Tuple)var4.next();
            value = rawValue.getValue();
            if (valueSerializer != null) {
                value = valueSerializer.deserialize(rawValue.getValue());
            }
        }

        return set;
    }

    public List<Object> exec() {
        List<Object> results = this.execRaw();
        return this.getConnectionFactory().getConvertPipelineAndTxResults() ? this.deserializeMixedResults(results, this.valueSerializer, this.hashKeySerializer, this.hashValueSerializer) : results;
    }

    public List<Object> exec(RedisSerializer<?> valueSerializer) {
        return this.deserializeMixedResults(this.execRaw(), valueSerializer, valueSerializer, valueSerializer);
    }

    protected List<Object> execRaw() {
        return (List)this.execute(new RedisCallback<List<Object>>() {
            public List<Object> doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.exec();
            }
        });
    }

    public void delete(K key) {
        final byte[] rawKey = this.rawKey(key);
        this.execute(new RedisCallback<Object>() {
            public Object doInRedis(RedisConnection connection) {
                connection.del(new byte[][]{rawKey});
                return null;
            }
        }, true);
    }

    public void delete(Collection<K> keys) {
        if (!CollectionUtils.isEmpty(keys)) {
            final byte[][] rawKeys = this.rawKeys(keys);
            this.execute(new RedisCallback<Object>() {
                public Object doInRedis(RedisConnection connection) {
                    connection.del(rawKeys);
                    return null;
                }
            }, true);
        }
    }

    public Boolean hasKey(K key) {
        final byte[] rawKey = this.rawKey(key);
        return (Boolean)this.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) {
                return connection.exists(rawKey);
            }
        }, true);
    }

    public Boolean expire(K key, final long timeout, final TimeUnit unit) {
        final byte[] rawKey = this.rawKey(key);
        final long rawTimeout = TimeoutUtils.toMillis(timeout, unit);
        return (Boolean)this.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) {
                try {
                    return connection.pExpire(rawKey, rawTimeout);
                } catch (Exception var3) {
                    return connection.expire(rawKey, TimeoutUtils.toSeconds(timeout, unit));
                }
            }
        }, true);
    }

    public Boolean expireAt(K key, final Date date) {
        final byte[] rawKey = this.rawKey(key);
        return (Boolean)this.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) {
                try {
                    return connection.pExpireAt(rawKey, date.getTime());
                } catch (Exception var3) {
                    return connection.expireAt(rawKey, date.getTime() / 1000L);
                }
            }
        }, true);
    }

    public void convertAndSend(String channel, Object message) {
        Assert.hasText(channel, "a non-empty channel is required");
        final byte[] rawChannel = this.rawString(channel);
        final byte[] rawMessage = this.rawValue(message);
        this.execute(new RedisCallback<Object>() {
            public Object doInRedis(RedisConnection connection) {
                connection.publish(rawChannel, rawMessage);
                return null;
            }
        }, true);
    }

    public Long getExpire(K key) {
        final byte[] rawKey = this.rawKey(key);
        return (Long)this.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection) {
                return connection.ttl(rawKey);
            }
        }, true);
    }

    public Long getExpire(K key, final TimeUnit timeUnit) {
        final byte[] rawKey = this.rawKey(key);
        return (Long)this.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection) {
                try {
                    return connection.pTtl(rawKey, timeUnit);
                } catch (Exception var3) {
                    return connection.ttl(rawKey, timeUnit);
                }
            }
        }, true);
    }

    public Set<K> keys(K pattern) {
        final byte[] rawKey = this.rawKey(pattern);
        Set<byte[]> rawKeys = (Set)this.execute(new RedisCallback<Set<byte[]>>() {
            public Set<byte[]> doInRedis(RedisConnection connection) {
                return connection.keys(rawKey);
            }
        }, true);
        return this.keySerializer != null ? SerializationUtils.deserialize(rawKeys, this.keySerializer) : rawKeys;
    }

    public Boolean persist(K key) {
        final byte[] rawKey = this.rawKey(key);
        return (Boolean)this.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) {
                return connection.persist(rawKey);
            }
        }, true);
    }

    public Boolean move(K key, final int dbIndex) {
        final byte[] rawKey = this.rawKey(key);
        return (Boolean)this.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) {
                return connection.move(rawKey, dbIndex);
            }
        }, true);
    }

    public K randomKey() {
        byte[] rawKey = (byte[])this.execute(new RedisCallback<byte[]>() {
            public byte[] doInRedis(RedisConnection connection) {
                return connection.randomKey();
            }
        }, true);
        return this.deserializeKey(rawKey);
    }

    public void rename(K oldKey, K newKey) {
        final byte[] rawOldKey = this.rawKey(oldKey);
        final byte[] rawNewKey = this.rawKey(newKey);
        this.execute(new RedisCallback<Object>() {
            public Object doInRedis(RedisConnection connection) {
                connection.rename(rawOldKey, rawNewKey);
                return null;
            }
        }, true);
    }

    public Boolean renameIfAbsent(K oldKey, K newKey) {
        final byte[] rawOldKey = this.rawKey(oldKey);
        final byte[] rawNewKey = this.rawKey(newKey);
        return (Boolean)this.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) {
                return connection.renameNX(rawOldKey, rawNewKey);
            }
        }, true);
    }

    public DataType type(K key) {
        final byte[] rawKey = this.rawKey(key);
        return (DataType)this.execute(new RedisCallback<DataType>() {
            public DataType doInRedis(RedisConnection connection) {
                return connection.type(rawKey);
            }
        }, true);
    }

    public byte[] dump(K key) {
        final byte[] rawKey = this.rawKey(key);
        return (byte[])this.execute(new RedisCallback<byte[]>() {
            public byte[] doInRedis(RedisConnection connection) {
                return connection.dump(rawKey);
            }
        }, true);
    }

    public void restore(K key, final byte[] value, long timeToLive, TimeUnit unit) {
        final byte[] rawKey = this.rawKey(key);
        final long rawTimeout = TimeoutUtils.toMillis(timeToLive, unit);
        this.execute(new RedisCallback<Object>() {
            public Boolean doInRedis(RedisConnection connection) {
                connection.restore(rawKey, rawTimeout, value);
                return null;
            }
        }, true);
    }

    public void multi() {
        this.execute(new RedisCallback<Object>() {
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.multi();
                return null;
            }
        }, true);
    }

    public void discard() {
        this.execute(new RedisCallback<Object>() {
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.discard();
                return null;
            }
        }, true);
    }

    public void watch(K key) {
        final byte[] rawKey = this.rawKey(key);
        this.execute(new RedisCallback<Object>() {
            public Object doInRedis(RedisConnection connection) {
                connection.watch(new byte[][]{rawKey});
                return null;
            }
        }, true);
    }

    public void watch(Collection<K> keys) {
        final byte[][] rawKeys = this.rawKeys(keys);
        this.execute(new RedisCallback<Object>() {
            public Object doInRedis(RedisConnection connection) {
                connection.watch(rawKeys);
                return null;
            }
        }, true);
    }

    public void unwatch() {
        this.execute(new RedisCallback<Object>() {
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.unwatch();
                return null;
            }
        }, true);
    }

    public List<V> sort(SortQuery<K> query) {
        return this.sort(query, this.valueSerializer);
    }

    public <T> List<T> sort(SortQuery<K> query, RedisSerializer<T> resultSerializer) {
        final byte[] rawKey = this.rawKey(query.getKey());
        final SortParameters params = QueryUtils.convertQuery(query, this.stringSerializer);
        List<byte[]> vals = (List)this.execute(new RedisCallback<List<byte[]>>() {
            public List<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.sort(rawKey, params);
            }
        }, true);
        return SerializationUtils.deserialize(vals, resultSerializer);
    }

    public <T> List<T> sort(SortQuery<K> query, BulkMapper<T, V> bulkMapper) {
        return this.sort(query, bulkMapper, this.valueSerializer);
    }

    public <T, S> List<T> sort(SortQuery<K> query, BulkMapper<T, S> bulkMapper, RedisSerializer<S> resultSerializer) {
        List<S> values = this.sort(query, resultSerializer);
        if (values != null && !values.isEmpty()) {
            int bulkSize = query.getGetPattern().size();
            List<T> result = new ArrayList(values.size() / bulkSize + 1);
            List<S> bulk = new ArrayList(bulkSize);
            Iterator var8 = values.iterator();

            while(var8.hasNext()) {
                S s = var8.next();
                bulk.add(s);
                if (bulk.size() == bulkSize) {
                    result.add(bulkMapper.mapBulk(Collections.unmodifiableList(bulk)));
                    bulk = new ArrayList(bulkSize);
                }
            }

            return result;
        } else {
            return Collections.emptyList();
        }
    }

    public Long sort(SortQuery<K> query, K storeKey) {
        final byte[] rawStoreKey = this.rawKey(storeKey);
        final byte[] rawKey = this.rawKey(query.getKey());
        final SortParameters params = QueryUtils.convertQuery(query, this.stringSerializer);
        return (Long)this.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.sort(rawKey, params, rawStoreKey);
            }
        }, true);
    }

    public BoundValueOperations<K, V> boundValueOps(K key) {
        return new DefaultBoundValueOperations(key, this);
    }

    public ValueOperations<K, V> opsForValue() {
        if (this.valueOps == null) {
            this.valueOps = new DefaultValueOperations(this);
        }

        return this.valueOps;
    }

    public ListOperations<K, V> opsForList() {
        if (this.listOps == null) {
            this.listOps = new DefaultListOperations(this);
        }

        return this.listOps;
    }

    public BoundListOperations<K, V> boundListOps(K key) {
        return new DefaultBoundListOperations(key, this);
    }

    public BoundSetOperations<K, V> boundSetOps(K key) {
        return new DefaultBoundSetOperations(key, this);
    }

    public SetOperations<K, V> opsForSet() {
        if (this.setOps == null) {
            this.setOps = new DefaultSetOperations(this);
        }

        return this.setOps;
    }

    public BoundZSetOperations<K, V> boundZSetOps(K key) {
        return new DefaultBoundZSetOperations(key, this);
    }

    public ZSetOperations<K, V> opsForZSet() {
        if (this.zSetOps == null) {
            this.zSetOps = new DefaultZSetOperations(this);
        }

        return this.zSetOps;
    }

    public GeoOperations<K, V> opsForGeo() {
        if (this.geoOps == null) {
            this.geoOps = new DefaultGeoOperations(this);
        }

        return this.geoOps;
    }

    public BoundGeoOperations<K, V> boundGeoOps(K key) {
        return new DefaultBoundGeoOperations(key, this);
    }

    public HyperLogLogOperations<K, V> opsForHyperLogLog() {
        if (this.hllOps == null) {
            this.hllOps = new DefaultHyperLogLogOperations(this);
        }

        return this.hllOps;
    }

    public <HK, HV> BoundHashOperations<K, HK, HV> boundHashOps(K key) {
        return new DefaultBoundHashOperations(key, this);
    }

    public <HK, HV> HashOperations<K, HK, HV> opsForHash() {
        return new DefaultHashOperations(this);
    }

    public ClusterOperations<K, V> opsForCluster() {
        return new DefaultClusterOperations(this);
    }

    public void killClient(final String host, final int port) {
        this.execute(new RedisCallback<Void>() {
            public Void doInRedis(RedisConnection connection) throws DataAccessException {
                connection.killClient(host, port);
                return null;
            }
        });
    }

    public List<RedisClientInfo> getClientList() {
        return (List)this.execute(new RedisCallback<List<RedisClientInfo>>() {
            public List<RedisClientInfo> doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.getClientList();
            }
        });
    }

    public void slaveOf(final String host, final int port) {
        this.execute(new RedisCallback<Void>() {
            public Void doInRedis(RedisConnection connection) throws DataAccessException {
                connection.slaveOf(host, port);
                return null;
            }
        });
    }

    public void slaveOfNoOne() {
        this.execute(new RedisCallback<Void>() {
            public Void doInRedis(RedisConnection connection) throws DataAccessException {
                connection.slaveOfNoOne();
                return null;
            }
        });
    }

    public void setEnableTransactionSupport(boolean enableTransactionSupport) {
        this.enableTransactionSupport = enableTransactionSupport;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}

