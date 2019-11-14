package com.rocket.summer.framework.data.redis.cache;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Callable;

import com.rocket.summer.framework.cache.support.AbstractValueAdaptingCache;
import com.rocket.summer.framework.cache.support.NullValue;
import com.rocket.summer.framework.cache.support.SimpleValueWrapper;
import com.rocket.summer.framework.dao.DataAccessException;
import com.rocket.summer.framework.data.redis.RedisSystemException;
import com.rocket.summer.framework.data.redis.connection.DecoratedRedisConnection;
import com.rocket.summer.framework.data.redis.connection.RedisClusterConnection;
import com.rocket.summer.framework.data.redis.connection.RedisConnection;
import com.rocket.summer.framework.data.redis.connection.ReturnType;
import com.rocket.summer.framework.data.redis.core.RedisCallback;
import com.rocket.summer.framework.data.redis.core.RedisOperations;
import com.rocket.summer.framework.data.redis.serializer.GenericToStringSerializer;
import com.rocket.summer.framework.data.redis.serializer.Jackson2JsonRedisSerializer;
import com.rocket.summer.framework.data.redis.serializer.JdkSerializationRedisSerializer;
import com.rocket.summer.framework.data.redis.serializer.RedisSerializer;
import com.rocket.summer.framework.data.redis.serializer.StringRedisSerializer;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;

/**
 * Cache implementation on top of Redis.
 *
 * @author Costin Leau
 * @author Christoph Strobl
 * @author Thomas Darimont
 * @author Mark Paluch
 */
@SuppressWarnings("unchecked")
public class RedisCache extends AbstractValueAdaptingCache {

    @SuppressWarnings("rawtypes") //
    private final RedisOperations redisOperations;
    private final RedisCacheMetadata cacheMetadata;
    private final CacheValueAccessor cacheValueAccessor;

    /**
     * Constructs a new {@link RedisCache} instance.
     *
     * @param name cache name
     * @param prefix
     * @param redisOperations
     * @param expiration
     */
    public RedisCache(String name, byte[] prefix, RedisOperations<? extends Object, ? extends Object> redisOperations,
                      long expiration) {
        this(name, prefix, redisOperations, expiration, false);
    }

    /**
     * Constructs a new {@link RedisCache} instance.
     *
     * @param name cache name
     * @param prefix must not be {@literal null} or empty.
     * @param redisOperations
     * @param expiration
     * @param allowNullValues
     * @since 1.8
     */
    public RedisCache(String name, byte[] prefix, RedisOperations<? extends Object, ? extends Object> redisOperations,
                      long expiration, boolean allowNullValues) {

        super(allowNullValues);

        Assert.hasText(name, "CacheName must not be null or empty!");

        RedisSerializer<?> serializer = redisOperations.getValueSerializer() != null ? redisOperations.getValueSerializer()
                : (RedisSerializer<?>) new JdkSerializationRedisSerializer();

        this.cacheMetadata = new RedisCacheMetadata(name, prefix);
        this.cacheMetadata.setDefaultExpiration(expiration);
        this.redisOperations = redisOperations;
        this.cacheValueAccessor = new CacheValueAccessor(serializer);

        if (allowNullValues) {

            if (redisOperations.getValueSerializer() instanceof StringRedisSerializer
                    || redisOperations.getValueSerializer() instanceof GenericToStringSerializer
                    || redisOperations.getValueSerializer() instanceof Jackson2JsonRedisSerializer) {
                throw new IllegalArgumentException(String.format(
                        "Redis does not allow keys with null value ¯\\_(ツ)_/¯. "
                                + "The chosen %s does not support generic type handling and therefore cannot be used with allowNullValues enabled. "
                                + "Please use a different RedisSerializer or disable null value support.",
                        ClassUtils.getShortName(redisOperations.getValueSerializer().getClass())));
            }
        }
    }

    /**
     * Return the value to which this cache maps the specified key, generically specifying a type that return value will
     * be cast to.
     *
     * @param key
     * @param type
     * @return
     */
    public <T> T get(Object key, Class<T> type) {

        ValueWrapper wrapper = get(key);
        return wrapper == null ? null : (T) wrapper.get();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.cache.Cache#get(java.lang.Object)
     */
    @Override
    public ValueWrapper get(Object key) {
        return get(getRedisCacheKey(key));
    }

    /*
     * @see  com.rocket.summer.framework.cache.Cache#get(java.lang.Object, java.util.concurrent.Callable)
     * introduced in springframework 4.3.0.RC1
     */
    public <T> T get(final Object key, final Callable<T> valueLoader) {

        RedisCacheElement cacheElement = new RedisCacheElement(getRedisCacheKey(key),
                new StoreTranslatingCallable(valueLoader)).expireAfter(cacheMetadata.getDefaultExpiration());
        BinaryRedisCacheElement rce = new BinaryRedisCacheElement(cacheElement, cacheValueAccessor);

        ValueWrapper val = get(key);
        if (val != null) {
            return (T) val.get();
        }

        RedisWriteThroughCallback callback = new RedisWriteThroughCallback(rce, cacheMetadata);

        try {
            byte[] result = (byte[]) redisOperations.execute(callback);
            return (T) (result == null ? null : fromStoreValue(cacheValueAccessor.deserializeIfNecessary(result)));
        } catch (RuntimeException e) {
            throw CacheValueRetrievalExceptionFactory.INSTANCE.create(key, valueLoader, e);
        }
    }

    /**
     * Return the value to which this cache maps the specified key.
     *
     * @param cacheKey the key whose associated value is to be returned via its binary representation.
     * @return the {@link RedisCacheElement} stored at given key or {@literal null} if no value found for key.
     * @since 1.5
     */
    public RedisCacheElement get(final RedisCacheKey cacheKey) {

        Assert.notNull(cacheKey, "CacheKey must not be null!");

        Boolean exists = (Boolean) redisOperations.execute(new RedisCallback<Boolean>() {

            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.exists(cacheKey.getKeyBytes());
            }
        });

        if (!exists) {
            return null;
        }

        byte[] bytes = doLookup(cacheKey);

        // safeguard if key gets deleted between EXISTS and GET calls.
        if (bytes == null) {
            return null;
        }

        return new RedisCacheElement(cacheKey, fromStoreValue(deserialize(bytes)));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.cache.Cache#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public void put(final Object key, final Object value) {

        put(new RedisCacheElement(getRedisCacheKey(key), toStoreValue(value))
                .expireAfter(cacheMetadata.getDefaultExpiration()));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.cache.support.AbstractValueAdaptingCache#fromStoreValue(java.lang.Object)
     */
    @Override
    protected Object fromStoreValue(Object storeValue) {

        // we need this override for the GenericJackson2JsonRedisSerializer support.
        if (isAllowNullValues() && storeValue instanceof NullValue) {
            return null;
        }

        return super.fromStoreValue(storeValue);
    }

    /**
     * Add the element by adding {@link RedisCacheElement#get()} at {@link RedisCacheElement#getKeyBytes()}. If the cache
     * previously contained a mapping for this {@link RedisCacheElement#getKeyBytes()}, the old value is replaced by
     * {@link RedisCacheElement#get()}.
     *
     * @param element must not be {@literal null}.
     * @since 1.5
     */
    public void put(RedisCacheElement element) {

        Assert.notNull(element, "Element must not be null!");

        redisOperations
                .execute(new RedisCachePutCallback(new BinaryRedisCacheElement(element, cacheValueAccessor), cacheMetadata));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.cache.Cache#putIfAbsent(java.lang.Object, java.lang.Object)
     */
    public ValueWrapper putIfAbsent(Object key, final Object value) {

        return putIfAbsent(new RedisCacheElement(getRedisCacheKey(key), toStoreValue(value))
                .expireAfter(cacheMetadata.getDefaultExpiration()));
    }

    /**
     * Add the element as long as no element exists at {@link RedisCacheElement#getKeyBytes()}. If a value is present for
     * {@link RedisCacheElement#getKeyBytes()} this one is returned.
     *
     * @param element must not be {@literal null}.
     * @return
     * @since 1.5
     */
    public ValueWrapper putIfAbsent(RedisCacheElement element) {

        Assert.notNull(element, "Element must not be null!");

        new RedisCachePutIfAbsentCallback(new BinaryRedisCacheElement(element, cacheValueAccessor), cacheMetadata);

        return toWrapper(cacheValueAccessor.deserializeIfNecessary((byte[]) redisOperations.execute(
                new RedisCachePutIfAbsentCallback(new BinaryRedisCacheElement(element, cacheValueAccessor), cacheMetadata))));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.cache.Cache#evict(java.lang.Object)
     */
    public void evict(Object key) {
        evict(new RedisCacheElement(getRedisCacheKey(key), null));
    }

    /**
     * @param element {@link RedisCacheElement#getKeyBytes()}
     * @since 1.5
     */
    public void evict(final RedisCacheElement element) {

        Assert.notNull(element, "Element must not be null!");
        redisOperations
                .execute(new RedisCacheEvictCallback(new BinaryRedisCacheElement(element, cacheValueAccessor), cacheMetadata));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.cache.Cache#clear()
     */
    public void clear() {
        redisOperations.execute(cacheMetadata.usesKeyPrefix() ? new RedisCacheCleanByPrefixCallback(cacheMetadata)
                : new RedisCacheCleanByKeysCallback(cacheMetadata));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.cache.Cache#getName()
     */
    public String getName() {
        return cacheMetadata.getCacheName();
    }

    /**
     * {@inheritDoc} This implementation simply returns the RedisTemplate used for configuring the cache, giving access to
     * the underlying Redis store.
     */
    public Object getNativeCache() {
        return redisOperations;
    }

    private ValueWrapper toWrapper(Object value) {
        return (value != null ? new SimpleValueWrapper(value) : null);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.cache.support.AbstractValueAdaptingCache#lookup(java.lang.Object)
     */
    @Override
    protected Object lookup(Object key) {
        return deserialize(doLookup(key));
    }

    private byte[] doLookup(Object key) {

        RedisCacheKey cacheKey = key instanceof RedisCacheKey ? (RedisCacheKey) key : getRedisCacheKey(key);

        return (byte[]) redisOperations.execute(new AbstractRedisCacheCallback<byte[]>(
                new BinaryRedisCacheElement(new RedisCacheElement(cacheKey, null), cacheValueAccessor), cacheMetadata) {

            @Override
            public byte[] doInRedis(BinaryRedisCacheElement element, RedisConnection connection) throws DataAccessException {
                return connection.get(element.getKeyBytes());
            }
        });
    }

    private Object deserialize(byte[] bytes) {
        return bytes == null ? null : cacheValueAccessor.deserializeIfNecessary(bytes);
    }

    private RedisCacheKey getRedisCacheKey(Object key) {
        return new RedisCacheKey(key).usePrefix(this.cacheMetadata.getKeyPrefix())
                .withKeySerializer(redisOperations.getKeySerializer());
    }

    /**
     * {@link Callable} to transform a value obtained from another {@link Callable} to its store value.
     *
     * @author Mark Paluch
     * @since 1.8
     * @see #toStoreValue(Object)
     */
    private class StoreTranslatingCallable implements Callable<Object> {

        private Callable<?> valueLoader;

        public StoreTranslatingCallable(Callable<?> valueLoader) {
            this.valueLoader = valueLoader;
        }

        @Override
        public Object call() throws Exception {
            return toStoreValue(valueLoader.call());
        }
    }

    /**
     * Metadata required to maintain {@link RedisCache}. Keeps track of additional data structures required for processing
     * cache operations.
     *
     * @author Christoph Strobl
     * @since 1.5
     */
    static class RedisCacheMetadata {

        private final String cacheName;
        private final byte[] keyPrefix;
        private final byte[] setOfKnownKeys;
        private final byte[] cacheLockName;
        private long defaultExpiration = 0;

        /**
         * @param cacheName must not be {@literal null} or empty.
         * @param keyPrefix can be {@literal null}.
         */
        public RedisCacheMetadata(String cacheName, byte[] keyPrefix) {

            Assert.hasText(cacheName, "CacheName must not be null or empty!");
            this.cacheName = cacheName;
            this.keyPrefix = keyPrefix;

            StringRedisSerializer stringSerializer = new StringRedisSerializer();

            // name of the set holding the keys
            this.setOfKnownKeys = usesKeyPrefix() ? new byte[] {} : stringSerializer.serialize(cacheName + "~keys");
            this.cacheLockName = stringSerializer.serialize(cacheName + "~lock");
        }

        /**
         * @return true if the {@link RedisCache} uses a prefix for key ranges.
         */
        public boolean usesKeyPrefix() {
            return (keyPrefix != null && keyPrefix.length > 0);
        }

        /**
         * Get the binary representation of the key prefix.
         *
         * @return never {@literal null}.
         */
        public byte[] getKeyPrefix() {
            return this.keyPrefix;
        }

        /**
         * Get the binary representation of the key identifying the data structure used to maintain known keys.
         *
         * @return never {@literal null}.
         */
        public byte[] getSetOfKnownKeysKey() {
            return setOfKnownKeys;
        }

        /**
         * Get the binary representation of the key identifying the data structure used to lock the cache.
         *
         * @return never {@literal null}.
         */
        public byte[] getCacheLockKey() {
            return cacheLockName;
        }

        /**
         * Get the name of the cache.
         *
         * @return
         */
        public String getCacheName() {
            return cacheName;
        }

        /**
         * Set the default expiration time in seconds
         *
         * @param seconds
         */
        public void setDefaultExpiration(long seconds) {
            this.defaultExpiration = seconds;
        }

        /**
         * Get the default expiration time in seconds.
         *
         * @return
         */
        public long getDefaultExpiration() {
            return defaultExpiration;
        }

    }

    /**
     * @author Christoph Strobl
     * @since 1.5
     */
    static class CacheValueAccessor {

        @SuppressWarnings("rawtypes") //
        private final RedisSerializer valueSerializer;

        @SuppressWarnings("rawtypes")
        CacheValueAccessor(RedisSerializer valueRedisSerializer) {
            valueSerializer = valueRedisSerializer;
        }

        byte[] convertToBytesIfNecessary(Object value) {

            if (value == null) {
                return new byte[0];
            }

            if (valueSerializer == null && value instanceof byte[]) {
                return (byte[]) value;
            }

            return valueSerializer.serialize(value);
        }

        Object deserializeIfNecessary(byte[] value) {

            if (valueSerializer != null) {
                return valueSerializer.deserialize(value);
            }

            return value;
        }
    }

    /**
     * @author Christoph Strobl
     * @since 1.6
     */
    static class BinaryRedisCacheElement extends RedisCacheElement {

        private byte[] keyBytes;
        private byte[] valueBytes;
        private RedisCacheElement element;
        private boolean lazyLoad;
        private CacheValueAccessor accessor;

        public BinaryRedisCacheElement(RedisCacheElement element, CacheValueAccessor accessor) {

            super(element.getKey(), element.get());
            this.element = element;
            this.keyBytes = element.getKeyBytes();
            this.accessor = accessor;

            lazyLoad = element.get() instanceof Callable;
            this.valueBytes = lazyLoad ? null : accessor.convertToBytesIfNecessary(element.get());
        }

        @Override
        public byte[] getKeyBytes() {
            return keyBytes;
        }

        public long getTimeToLive() {
            return element.getTimeToLive();
        }

        public boolean hasKeyPrefix() {
            return element.hasKeyPrefix();
        }

        public boolean isEternal() {
            return element.isEternal();
        }

        public RedisCacheElement expireAfter(long seconds) {
            return element.expireAfter(seconds);
        }

        @Override
        public byte[] get() {

            if (lazyLoad && valueBytes == null) {
                try {
                    valueBytes = accessor.convertToBytesIfNecessary(((Callable<?>) element.get()).call());
                } catch (Exception e) {
                    throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e.getMessage(), e);
                }
            }
            return valueBytes;
        }
    }

    /**
     * @author Christoph Strobl
     * @since 1.5
     * @param <T>
     */
    static abstract class AbstractRedisCacheCallback<T> implements RedisCallback<T> {

        private long WAIT_FOR_LOCK_TIMEOUT = 300;
        private final BinaryRedisCacheElement element;
        private final RedisCacheMetadata cacheMetadata;

        public AbstractRedisCacheCallback(BinaryRedisCacheElement element, RedisCacheMetadata metadata) {
            this.element = element;
            this.cacheMetadata = metadata;
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.redis.core.RedisCallback#doInRedis(com.rocket.summer.framework.data.redis.connection.RedisConnection)
         */
        @Override
        public T doInRedis(RedisConnection connection) throws DataAccessException {
            waitForLock(connection);
            return doInRedis(element, connection);
        }

        public abstract T doInRedis(BinaryRedisCacheElement element, RedisConnection connection) throws DataAccessException;

        protected void processKeyExpiration(RedisCacheElement element, RedisConnection connection) {
            if (!element.isEternal()) {
                connection.expire(element.getKeyBytes(), element.getTimeToLive());
            }
        }

        protected void maintainKnownKeys(RedisCacheElement element, RedisConnection connection) {

            if (!element.hasKeyPrefix()) {

                connection.zAdd(cacheMetadata.getSetOfKnownKeysKey(), 0, element.getKeyBytes());

                if (!element.isEternal()) {
                    connection.expire(cacheMetadata.getSetOfKnownKeysKey(), element.getTimeToLive());
                }
            }
        }

        protected void cleanKnownKeys(RedisCacheElement element, RedisConnection connection) {

            if (!element.hasKeyPrefix()) {
                connection.zRem(cacheMetadata.getSetOfKnownKeysKey(), element.getKeyBytes());
            }
        }

        protected boolean waitForLock(RedisConnection connection) {

            boolean retry;
            boolean foundLock = false;
            do {
                retry = false;
                if (connection.exists(cacheMetadata.getCacheLockKey())) {
                    foundLock = true;
                    try {
                        Thread.sleep(WAIT_FOR_LOCK_TIMEOUT);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    retry = true;
                }
            } while (retry);

            return foundLock;
        }

        protected void lock(RedisConnection connection) {
            waitForLock(connection);
            connection.set(cacheMetadata.getCacheLockKey(), "locked".getBytes());
        }

        protected void unlock(RedisConnection connection) {
            connection.del(cacheMetadata.getCacheLockKey());
        }
    }

    /**
     * @author Christoph Strobl
     * @param <T>
     * @since 1.5
     */
    static abstract class LockingRedisCacheCallback<T> implements RedisCallback<T> {

        private final RedisCacheMetadata metadata;

        public LockingRedisCacheCallback(RedisCacheMetadata metadata) {
            this.metadata = metadata;
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.redis.core.RedisCallback#doInRedis(com.rocket.summer.framework.data.redis.connection.RedisConnection)
         */
        @Override
        public T doInRedis(RedisConnection connection) throws DataAccessException {

            if (connection.exists(metadata.getCacheLockKey())) {
                return null;
            }
            try {
                connection.set(metadata.getCacheLockKey(), metadata.getCacheLockKey());
                return doInLock(connection);
            } finally {
                connection.del(metadata.getCacheLockKey());
            }
        }

        public abstract T doInLock(RedisConnection connection);
    }

    /**
     * @author Christoph Strobl
     * @since 1.5
     */
    static class RedisCacheCleanByKeysCallback extends LockingRedisCacheCallback<Void> {

        private static final int PAGE_SIZE = 128;
        private final RedisCacheMetadata metadata;

        RedisCacheCleanByKeysCallback(RedisCacheMetadata metadata) {
            super(metadata);
            this.metadata = metadata;
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.redis.cache.RedisCache.LockingRedisCacheCallback#doInLock(com.rocket.summer.framework.data.redis.connection.RedisConnection)
         */
        @Override
        public Void doInLock(RedisConnection connection) {

            int offset = 0;
            boolean finished = false;

            do {
                // need to paginate the keys
                Set<byte[]> keys = connection.zRange(metadata.getSetOfKnownKeysKey(), (offset) * PAGE_SIZE,
                        (offset + 1) * PAGE_SIZE - 1);
                finished = keys.size() < PAGE_SIZE;
                offset++;
                if (!keys.isEmpty()) {
                    connection.del(keys.toArray(new byte[keys.size()][]));
                }
            } while (!finished);

            connection.del(metadata.getSetOfKnownKeysKey());
            return null;
        }
    }

    /**
     * @author Christoph Strobl
     * @since 1.5
     */
    static class RedisCacheCleanByPrefixCallback extends LockingRedisCacheCallback<Void> {

        private static final byte[] REMOVE_KEYS_BY_PATTERN_LUA = new StringRedisSerializer().serialize(
                "local keys = redis.call('KEYS', ARGV[1]); local keysCount = table.getn(keys); if(keysCount > 0) then for _, key in ipairs(keys) do redis.call('del', key); end; end; return keysCount;");
        private static final byte[] WILD_CARD = new StringRedisSerializer().serialize("*");
        private final RedisCacheMetadata metadata;

        public RedisCacheCleanByPrefixCallback(RedisCacheMetadata metadata) {
            super(metadata);
            this.metadata = metadata;
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.redis.cache.RedisCache.LockingRedisCacheCallback#doInLock(com.rocket.summer.framework.data.redis.connection.RedisConnection)
         */
        @Override
        public Void doInLock(RedisConnection connection) throws DataAccessException {

            byte[] prefixToUse = Arrays.copyOf(metadata.getKeyPrefix(), metadata.getKeyPrefix().length + WILD_CARD.length);
            System.arraycopy(WILD_CARD, 0, prefixToUse, metadata.getKeyPrefix().length, WILD_CARD.length);

            if (isClusterConnection(connection)) {

                // load keys to the client because currently Redis Cluster connections do not allow eval of lua scripts.
                Set<byte[]> keys = connection.keys(prefixToUse);
                if (!keys.isEmpty()) {
                    connection.del(keys.toArray(new byte[keys.size()][]));
                }
            } else {
                connection.eval(REMOVE_KEYS_BY_PATTERN_LUA, ReturnType.INTEGER, 0, prefixToUse);
            }

            return null;
        }
    }

    /**
     * @author Christoph Strobl
     * @since 1.5
     */
    static class RedisCacheEvictCallback extends AbstractRedisCacheCallback<Void> {

        public RedisCacheEvictCallback(BinaryRedisCacheElement element, RedisCacheMetadata metadata) {
            super(element, metadata);
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.redis.cache.RedisCache.AbstractRedisCacheCallback#doInRedis(com.rocket.summer.framework.data.redis.cache.RedisCacheElement, com.rocket.summer.framework.data.redis.connection.RedisConnection)
         */
        @Override
        public Void doInRedis(BinaryRedisCacheElement element, RedisConnection connection) throws DataAccessException {

            connection.del(element.getKeyBytes());
            cleanKnownKeys(element, connection);
            return null;
        }
    }

    /**
     * @author Christoph Strobl
     * @since 1.5
     */
    static class RedisCachePutCallback extends AbstractRedisCacheCallback<Void> {

        public RedisCachePutCallback(BinaryRedisCacheElement element, RedisCacheMetadata metadata) {

            super(element, metadata);
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.redis.cache.RedisCache.AbstractRedisPutCallback#doInRedis(com.rocket.summer.framework.data.redis.cache.RedisCache.RedisCacheElement, com.rocket.summer.framework.data.redis.connection.RedisConnection)
         */
        @Override
        public Void doInRedis(BinaryRedisCacheElement element, RedisConnection connection) throws DataAccessException {

            if (!isClusterConnection(connection)) {
                connection.multi();
            }

            if (element.get().length == 0) {
                connection.del(element.getKeyBytes());
            } else {
                connection.set(element.getKeyBytes(), element.get());

                processKeyExpiration(element, connection);
                maintainKnownKeys(element, connection);
            }

            if (!isClusterConnection(connection)) {
                connection.exec();
            }
            return null;
        }
    }

    /**
     * @author Christoph Strobl
     * @since 1.5
     */
    static class RedisCachePutIfAbsentCallback extends AbstractRedisCacheCallback<byte[]> {

        public RedisCachePutIfAbsentCallback(BinaryRedisCacheElement element, RedisCacheMetadata metadata) {
            super(element, metadata);
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.redis.cache.RedisCache.AbstractRedisPutCallback#doInRedis(com.rocket.summer.framework.data.redis.cache.RedisCache.RedisCacheElement, com.rocket.summer.framework.data.redis.connection.RedisConnection)
         */
        @Override
        public byte[] doInRedis(BinaryRedisCacheElement element, RedisConnection connection) throws DataAccessException {

            waitForLock(connection);

            byte[] keyBytes = element.getKeyBytes();
            byte[] value = element.get();

            if (!connection.setNX(keyBytes, value)) {
                return connection.get(keyBytes);
            }

            maintainKnownKeys(element, connection);
            processKeyExpiration(element, connection);

            return null;
        }
    }

    /**
     * @author Christoph Strobl
     * @since 1.7
     */
    static class RedisWriteThroughCallback extends AbstractRedisCacheCallback<byte[]> {

        public RedisWriteThroughCallback(BinaryRedisCacheElement element, RedisCacheMetadata metadata) {
            super(element, metadata);
        }

        @Override
        public byte[] doInRedis(BinaryRedisCacheElement element, RedisConnection connection) throws DataAccessException {

            try {

                lock(connection);

                try {

                    byte[] value = connection.get(element.getKeyBytes());

                    if (value != null) {
                        return value;
                    }

                    if (!isClusterConnection(connection)) {

                        connection.watch(element.getKeyBytes());
                        connection.multi();
                    }

                    value = element.get();

                    if (value.length == 0) {
                        connection.del(element.getKeyBytes());
                    } else {
                        connection.set(element.getKeyBytes(), value);
                        processKeyExpiration(element, connection);
                        maintainKnownKeys(element, connection);
                    }

                    if (!isClusterConnection(connection)) {
                        connection.exec();
                    }

                    return value;
                } catch (RuntimeException e) {
                    if (!isClusterConnection(connection)) {
                        connection.discard();
                    }
                    throw e;
                }
            } finally {
                unlock(connection);
            }
        }
    };

    /**
     * @author Christoph Strobl
     * @since 1.7 (TODO: remove when upgrading to spring 4.3)
     */
    private static enum CacheValueRetrievalExceptionFactory {

        INSTANCE;

        private static boolean isSpring43;

        static {
            isSpring43 = ClassUtils.isPresent("com.rocket.summer.framework.cache.Cache$ValueRetrievalException",
                    ClassUtils.getDefaultClassLoader());
        }

        public RuntimeException create(Object key, Callable<?> valueLoader, Throwable cause) {

            if (isSpring43) {
                try {
                    Class<?> execption = ClassUtils.forName("com.rocket.summer.framework.cache.Cache$ValueRetrievalException",
                            this.getClass().getClassLoader());
                    Constructor<?> c = ClassUtils.getConstructorIfAvailable(execption, Object.class, Callable.class,
                            Throwable.class);
                    return (RuntimeException) c.newInstance(key, valueLoader, cause);
                } catch (Exception ex) {
                    // ignore
                }
            }

            return new RedisSystemException(
                    String.format("Value for key '%s' could not be loaded using '%s'.", key, valueLoader), cause);
        }
    }

    private static boolean isClusterConnection(RedisConnection connection) {

        while (connection instanceof DecoratedRedisConnection) {
            connection = ((DecoratedRedisConnection) connection).getDelegate();
        }

        return connection instanceof RedisClusterConnection;
    }

}

