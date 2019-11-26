package com.rocket.summer.framework.data.redis.core;

/**
 * {@link TimeToLiveAccessor} extracts the objects time to live used for {@code EXPIRE}.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public interface TimeToLiveAccessor {

    /**
     * @param source must not be {@literal null}.
     * @return {@literal null} if not configured.
     */
    Long getTimeToLive(Object source);
}

