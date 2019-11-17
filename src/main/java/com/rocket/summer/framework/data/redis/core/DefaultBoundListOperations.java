package com.rocket.summer.framework.data.redis.core;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.rocket.summer.framework.data.redis.connection.DataType;

/**
 * Default implementation for {@link BoundListOperations}.
 *
 * @author Costin Leau
 */
class DefaultBoundListOperations<K, V> extends DefaultBoundKeyOperations<K> implements BoundListOperations<K, V> {

    private final ListOperations<K, V> ops;

    /**
     * Constructs a new <code>DefaultBoundListOperations</code> instance.
     *
     * @param key
     * @param operations
     */
    public DefaultBoundListOperations(K key, RedisOperations<K, V> operations) {
        super(key, operations);
        this.ops = operations.opsForList();
    }

    public RedisOperations<K, V> getOperations() {
        return ops.getOperations();
    }

    public V index(long index) {
        return ops.index(getKey(), index);
    }

    public V leftPop() {
        return ops.leftPop(getKey());
    }

    public V leftPop(long timeout, TimeUnit unit) {
        return ops.leftPop(getKey(), timeout, unit);
    }

    public Long leftPush(V value) {
        return ops.leftPush(getKey(), value);
    }

    public Long leftPushAll(V... values) {
        return ops.leftPushAll(getKey(), values);
    }

    public Long leftPushIfPresent(V value) {
        return ops.leftPushIfPresent(getKey(), value);
    }

    public Long leftPush(V pivot, V value) {
        return ops.leftPush(getKey(), pivot, value);
    }

    public Long size() {
        return ops.size(getKey());
    }

    public List<V> range(long start, long end) {
        return ops.range(getKey(), start, end);
    }

    public Long remove(long i, Object value) {
        return ops.remove(getKey(), i, value);
    }

    public V rightPop() {
        return ops.rightPop(getKey());
    }

    public V rightPop(long timeout, TimeUnit unit) {
        return ops.rightPop(getKey(), timeout, unit);
    }

    public Long rightPushIfPresent(V value) {
        return ops.rightPushIfPresent(getKey(), value);
    }

    public Long rightPush(V value) {
        return ops.rightPush(getKey(), value);
    }

    public Long rightPushAll(V... values) {
        return ops.rightPushAll(getKey(), values);
    }

    public Long rightPush(V pivot, V value) {
        return ops.rightPush(getKey(), pivot, value);
    }

    public void trim(long start, long end) {
        ops.trim(getKey(), start, end);
    }

    public void set(long index, V value) {
        ops.set(getKey(), index, value);
    }

    public DataType getType() {
        return DataType.LIST;
    }
}

