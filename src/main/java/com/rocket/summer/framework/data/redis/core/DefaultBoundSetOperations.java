package com.rocket.summer.framework.data.redis.core;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.rocket.summer.framework.data.redis.connection.DataType;

/**
 * Default implementation for {@link BoundSetOperations}.
 *
 * @author Costin Leau
 * @author Christoph Strobl
 */
class DefaultBoundSetOperations<K, V> extends DefaultBoundKeyOperations<K> implements BoundSetOperations<K, V> {

    private final SetOperations<K, V> ops;

    /**
     * Constructs a new <code>DefaultBoundSetOperations</code> instance.
     *
     * @param key
     * @param operations
     */
    DefaultBoundSetOperations(K key, RedisOperations<K, V> operations) {
        super(key, operations);
        this.ops = operations.opsForSet();
    }

    public Long add(V... values) {
        return ops.add(getKey(), values);
    }

    public Set<V> diff(K key) {
        return ops.difference(getKey(), key);
    }

    public Set<V> diff(Collection<K> keys) {
        return ops.difference(getKey(), keys);
    }

    public void diffAndStore(K key, K destKey) {
        ops.differenceAndStore(getKey(), key, destKey);
    }

    public void diffAndStore(Collection<K> keys, K destKey) {
        ops.differenceAndStore(getKey(), keys, destKey);
    }

    public RedisOperations<K, V> getOperations() {
        return ops.getOperations();
    }

    public Set<V> intersect(K key) {
        return ops.intersect(getKey(), key);
    }

    public Set<V> intersect(Collection<K> keys) {
        return ops.intersect(getKey(), keys);
    }

    public void intersectAndStore(K key, K destKey) {
        ops.intersectAndStore(getKey(), key, destKey);
    }

    public void intersectAndStore(Collection<K> keys, K destKey) {
        ops.intersectAndStore(getKey(), keys, destKey);
    }

    public Boolean isMember(Object o) {
        return ops.isMember(getKey(), o);
    }

    public Set<V> members() {
        return ops.members(getKey());
    }

    public Boolean move(K destKey, V value) {
        return ops.move(getKey(), value, destKey);
    }

    public V randomMember() {
        return ops.randomMember(getKey());
    }

    public Set<V> distinctRandomMembers(long count) {
        return ops.distinctRandomMembers(getKey(), count);
    }

    public List<V> randomMembers(long count) {
        return ops.randomMembers(getKey(), count);
    }

    public Long remove(Object... values) {
        return ops.remove(getKey(), values);
    }

    public V pop() {
        return ops.pop(getKey());
    }

    public Long size() {
        return ops.size(getKey());
    }

    public Set<V> union(K key) {
        return ops.union(getKey(), key);
    }

    public Set<V> union(Collection<K> keys) {
        return ops.union(getKey(), keys);
    }

    public void unionAndStore(K key, K destKey) {
        ops.unionAndStore(getKey(), key, destKey);
    }

    public void unionAndStore(Collection<K> keys, K destKey) {
        ops.unionAndStore(getKey(), keys, destKey);
    }

    public DataType getType() {
        return DataType.SET;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundSetOperations#sScan(com.rocket.summer.framework.data.redis.core.ScanOptions)
     */
    @Override
    public Cursor<V> scan(ScanOptions options) {
        return ops.scan(getKey(), options);
    }
}

