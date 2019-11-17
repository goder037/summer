package com.rocket.summer.framework.data.redis.core;

import java.util.concurrent.TimeUnit;

import com.rocket.summer.framework.data.redis.connection.DataType;

/**
 * @author Costin Leau
 */
class DefaultBoundValueOperations<K, V> extends DefaultBoundKeyOperations<K> implements BoundValueOperations<K, V> {

    private final ValueOperations<K, V> ops;

    /**
     * Constructs a new <code>DefaultBoundValueOperations</code> instance.
     *
     * @param key
     * @param operations
     */
    public DefaultBoundValueOperations(K key, RedisOperations<K, V> operations) {
        super(key, operations);
        this.ops = operations.opsForValue();
    }

    public V get() {
        return ops.get(getKey());
    }

    public V getAndSet(V value) {
        return ops.getAndSet(getKey(), value);
    }

    public Long increment(long delta) {
        return ops.increment(getKey(), delta);
    }

    public Double increment(double delta) {
        return ops.increment(getKey(), delta);
    }

    public Integer append(String value) {
        return ops.append(getKey(), value);
    }

    public String get(long start, long end) {
        return ops.get(getKey(), start, end);
    }

    public void set(V value, long timeout, TimeUnit unit) {
        ops.set(getKey(), value, timeout, unit);
    }

    public void set(V value) {
        ops.set(getKey(), value);
    }

    public Boolean setIfAbsent(V value) {
        return ops.setIfAbsent(getKey(), value);
    }

    public void set(V value, long offset) {
        ops.set(getKey(), value, offset);
    }

    public Long size() {
        return ops.size(getKey());
    }

    public RedisOperations<K, V> getOperations() {
        return ops.getOperations();
    }

    public DataType getType() {
        return DataType.STRING;
    }
}

