package com.rocket.summer.framework.boot.autoconfigure.cache;

/**
 * Supported cache types (defined in order of precedence).
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 * @author Eddú Meléndez
 * @since 1.3.0
 */
public enum CacheType {

    /**
     * Generic caching using 'Cache' beans from the context.
     */
    GENERIC,

    /**
     * JCache (JSR-107) backed caching.
     */
    JCACHE,

    /**
     * EhCache backed caching.
     */
    EHCACHE,

    /**
     * Hazelcast backed caching.
     */
    HAZELCAST,

    /**
     * Infinispan backed caching.
     */
    INFINISPAN,

    /**
     * Couchbase backed caching.
     */
    COUCHBASE,

    /**
     * Redis backed caching.
     */
    REDIS,

    /**
     * Caffeine backed caching.
     */
    CAFFEINE,

    /**
     * Guava backed caching.
     */
    @Deprecated
    GUAVA,

    /**
     * Simple in-memory caching.
     */
    SIMPLE,

    /**
     * No caching.
     */
    NONE;

}

