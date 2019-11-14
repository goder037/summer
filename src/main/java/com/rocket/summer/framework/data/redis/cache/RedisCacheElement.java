package com.rocket.summer.framework.data.redis.cache;

import static com.rocket.summer.framework.util.Assert.*;

import com.rocket.summer.framework.cache.support.SimpleValueWrapper;

/**
 * Element to be stored inside {@link RedisCache}.
 *
 * @author Christoph Strobl
 * @since 1.5
 */
public class RedisCacheElement extends SimpleValueWrapper {

    private final RedisCacheKey cacheKey;
    private long timeToLive;

    /**
     * @param cacheKey the key to be used for storing value in {@link RedisCache}. Must not be {@literal null}.
     * @param value
     */
    public RedisCacheElement(RedisCacheKey cacheKey, Object value) {
        super(value);

        notNull(cacheKey, "CacheKey must not be null!");
        this.cacheKey = cacheKey;
    }

    /**
     * Get the binary key representation.
     *
     * @return
     */
    public byte[] getKeyBytes() {
        return cacheKey.getKeyBytes();
    }

    /**
     * @return
     */
    public RedisCacheKey getKey() {
        return cacheKey;
    }

    /**
     * Set the elements time to live. Use {@literal zero} to store eternally.
     *
     * @param timeToLive
     */
    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    /**
     * @return
     */
    public long getTimeToLive() {
        return timeToLive;
    }

    /**
     * @return true in case {@link RedisCacheKey} is prefixed.
     */
    public boolean hasKeyPrefix() {
        return cacheKey.hasPrefix();
    }

    /**
     * @return true if timeToLive is 0
     */
    public boolean isEternal() {
        return 0 == timeToLive;
    }

    /**
     * Expire the element after given seconds.
     *
     * @param seconds
     * @return
     */
    public RedisCacheElement expireAfter(long seconds) {

        setTimeToLive(seconds);
        return this;
    }
}

