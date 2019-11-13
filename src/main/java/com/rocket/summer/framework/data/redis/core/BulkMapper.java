package com.rocket.summer.framework.data.redis.core;

import java.util.List;

/**
 * Mapper translating Redis bulk value responses (typically returned by a sort query) to actual objects. Implementations
 * of this interface do not have to worry about exception or connection handling.
 * <p>
 * Typically used by {@link RedisTemplate} <tt>sort</tt> methods.
 *
 * @author Costin Leau
 */
public interface BulkMapper<T, V> {

    T mapBulk(List<V> tuple);
}

