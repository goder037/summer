package com.rocket.summer.framework.data.redis.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.rocket.summer.framework.data.redis.connection.DataType;

/**
 * Default implementation for {@link HashOperations}.
 *
 * @author Costin Leau
 * @author Christoph Strobl
 * @author Ninad Divadkar
 */
class DefaultBoundHashOperations<H, HK, HV> extends DefaultBoundKeyOperations<H> implements
        BoundHashOperations<H, HK, HV> {

    private final HashOperations<H, HK, HV> ops;

    /**
     * Constructs a new <code>DefaultBoundHashOperations</code> instance.
     *
     * @param key
     * @param operations
     */
    public DefaultBoundHashOperations(H key, RedisOperations<H, ?> operations) {
        super(key, operations);
        this.ops = operations.opsForHash();
    }

    public Long delete(Object... keys) {
        return ops.delete(getKey(), keys);
    }

    public HV get(Object key) {
        return ops.get(getKey(), key);
    }

    public List<HV> multiGet(Collection<HK> hashKeys) {
        return ops.multiGet(getKey(), hashKeys);
    }

    public RedisOperations<H, ?> getOperations() {
        return ops.getOperations();
    }

    public Boolean hasKey(Object key) {
        return ops.hasKey(getKey(), key);
    }

    public Long increment(HK key, long delta) {
        return ops.increment(getKey(), key, delta);
    }

    public Double increment(HK key, double delta) {
        return ops.increment(getKey(), key, delta);
    }

    public Set<HK> keys() {
        return ops.keys(getKey());
    }

    public Long size() {
        return ops.size(getKey());
    }

    public void putAll(Map<? extends HK, ? extends HV> m) {
        ops.putAll(getKey(), m);
    }

    public void put(HK key, HV value) {
        ops.put(getKey(), key, value);
    }

    public Boolean putIfAbsent(HK key, HV value) {
        return ops.putIfAbsent(getKey(), key, value);
    }

    public List<HV> values() {
        return ops.values(getKey());
    }

    public Map<HK, HV> entries() {
        return ops.entries(getKey());
    }

    public DataType getType() {
        return DataType.HASH;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundHashOperations#hscan(java.lang.Object)
     */
    @Override
    public Cursor<Entry<HK, HV>> scan(ScanOptions options) {
        return ops.scan(getKey(), options);
    }
}

