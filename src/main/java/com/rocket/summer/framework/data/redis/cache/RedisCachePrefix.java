package com.rocket.summer.framework.data.redis.cache;

/**
 * Contract for generating 'prefixes' for Cache keys saved in Redis. Due to the 'flat' nature of the Redis storage, the
 * prefix is used as a 'namespace' for grouping the key/values inside a cache (and to avoid collision with other caches
 * or keys inside Redis).
 *
 * @author Costin Leau
 */
public interface RedisCachePrefix {

    /**
     * Returns the prefix for the given cache (identified by name). Note the prefix is returned in raw form so it can be
     * saved directly to Redis without any serialization.
     *
     * @param cacheName the name of the cache using the prefix
     * @return the prefix for the given cache.
     */
    byte[] prefix(String cacheName);

}

