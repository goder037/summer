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
     * Redis backed caching.
     */
    REDIS,

    /**
     * Simple in-memory caching.
     */
    SIMPLE,

    /**
     * No caching.
     */
    NONE;

}

