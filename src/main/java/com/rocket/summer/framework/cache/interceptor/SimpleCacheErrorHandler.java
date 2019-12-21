package com.rocket.summer.framework.cache.interceptor;

import com.rocket.summer.framework.cache.Cache;

/**
 * A simple {@link CacheErrorHandler} that does not handle the
 * exception at all, simply throwing it back at the client.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
public class SimpleCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        throw exception;
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        throw exception;
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        throw exception;
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        throw exception;
    }
}

