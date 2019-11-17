package com.rocket.summer.framework.data.redis.core;

import java.util.Collection;
import java.util.Set;

import com.rocket.summer.framework.data.redis.connection.DataType;
import com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Limit;
import com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Range;
import com.rocket.summer.framework.data.redis.core.ZSetOperations.TypedTuple;

/**
 * Default implementation for {@link BoundZSetOperations}.
 *
 * @author Costin Leau
 * @author Christoph Strobl
 * @author Mark Paluch
 */
class DefaultBoundZSetOperations<K, V> extends DefaultBoundKeyOperations<K> implements BoundZSetOperations<K, V> {

    private final ZSetOperations<K, V> ops;

    /**
     * Constructs a new <code>DefaultBoundZSetOperations</code> instance.
     *
     * @param key
     * @param operations
     */
    public DefaultBoundZSetOperations(K key, RedisOperations<K, V> operations) {
        super(key, operations);
        this.ops = operations.opsForZSet();
    }

    public Boolean add(V value, double score) {
        return ops.add(getKey(), value, score);
    }

    public Long add(Set<TypedTuple<V>> tuples) {
        return ops.add(getKey(), tuples);
    }

    public Double incrementScore(V value, double delta) {
        return ops.incrementScore(getKey(), value, delta);
    }

    public RedisOperations<K, V> getOperations() {
        return ops.getOperations();
    }

    public void intersectAndStore(K otherKey, K destKey) {
        ops.intersectAndStore(getKey(), otherKey, destKey);
    }

    public void intersectAndStore(Collection<K> otherKeys, K destKey) {
        ops.intersectAndStore(getKey(), otherKeys, destKey);
    }

    public Set<V> range(long start, long end) {
        return ops.range(getKey(), start, end);
    }

    public Set<V> rangeByScore(double min, double max) {
        return ops.rangeByScore(getKey(), min, max);
    }

    public Set<TypedTuple<V>> rangeByScoreWithScores(double min, double max) {
        return ops.rangeByScoreWithScores(getKey(), min, max);
    }

    public Set<TypedTuple<V>> rangeWithScores(long start, long end) {
        return ops.rangeWithScores(getKey(), start, end);
    }

    public Set<V> reverseRangeByScore(double min, double max) {
        return ops.reverseRangeByScore(getKey(), min, max);
    }

    public Set<TypedTuple<V>> reverseRangeByScoreWithScores(double min, double max) {
        return ops.reverseRangeByScoreWithScores(getKey(), min, max);
    }

    public Set<TypedTuple<V>> reverseRangeWithScores(long start, long end) {
        return ops.reverseRangeWithScores(getKey(), start, end);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundZSetOperations#rangeByLex(com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Range)
     */
    @Override
    public Set<V> rangeByLex(Range range) {
        return rangeByLex(range, null);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundZSetOperations#rangeByLex(com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Range, com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Limit)
     */
    @Override
    public Set<V> rangeByLex(Range range, Limit limit) {
        return ops.rangeByLex(getKey(), range, limit);
    }

    public Long rank(Object o) {
        return ops.rank(getKey(), o);
    }

    public Long reverseRank(Object o) {
        return ops.reverseRank(getKey(), o);
    }

    public Double score(Object o) {
        return ops.score(getKey(), o);
    }

    public Long remove(Object... values) {
        return ops.remove(getKey(), values);
    }

    public void removeRange(long start, long end) {
        ops.removeRange(getKey(), start, end);
    }

    public void removeRangeByScore(double min, double max) {
        ops.removeRangeByScore(getKey(), min, max);
    }

    public Set<V> reverseRange(long start, long end) {
        return ops.reverseRange(getKey(), start, end);
    }

    public Long count(double min, double max) {
        return ops.count(getKey(), min, max);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundZSetOperations#size()
     */
    @Override
    public Long size() {
        return zCard();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundZSetOperations#zCard()
     */
    @Override
    public Long zCard() {
        return ops.zCard(getKey());
    }

    public void unionAndStore(K otherKey, K destKey) {
        ops.unionAndStore(getKey(), otherKey, destKey);
    }

    public void unionAndStore(Collection<K> otherKeys, K destKey) {
        ops.unionAndStore(getKey(), otherKeys, destKey);
    }

    public DataType getType() {
        return DataType.ZSET;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundZSetOperations#scan(com.rocket.summer.framework.data.redis.core.ScanOptions)
     */
    @Override
    public Cursor<TypedTuple<V>> scan(ScanOptions options) {
        return ops.scan(getKey(), options);
    }
}

