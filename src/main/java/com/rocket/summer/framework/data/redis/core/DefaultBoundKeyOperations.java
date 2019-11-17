package com.rocket.summer.framework.data.redis.core;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Default {@link BoundKeyOperations} implementation. Meant for internal usage.
 *
 * @author Costin Leau
 */
abstract class DefaultBoundKeyOperations<K> implements BoundKeyOperations<K> {

    private K key;
    private final RedisOperations<K, ?> ops;

    public DefaultBoundKeyOperations(K key, RedisOperations<K, ?> operations) {
        setKey(key);
        this.ops = operations;
    }

    public K getKey() {
        return key;
    }

    protected void setKey(K key) {
        this.key = key;
    }

    public Boolean expire(long timeout, TimeUnit unit) {
        return ops.expire(key, timeout, unit);
    }

    public Boolean expireAt(Date date) {
        return ops.expireAt(key, date);
    }

    public Long getExpire() {
        return ops.getExpire(key);
    }

    public Boolean persist() {
        return ops.persist(key);
    }

    public void rename(K newKey) {
        if (ops.hasKey(key)) {
            ops.rename(key, newKey);
        }
        key = newKey;
    }
}

