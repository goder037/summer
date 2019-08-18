package com.rocket.summer.framework.cache.interceptor;

import java.util.Collection;

import com.rocket.summer.framework.cache.Cache;
import com.rocket.summer.framework.cache.CacheManager;

/**
 * A simple {@link CacheResolver} that resolves the {@link Cache} instance(s)
 * based on a configurable {@link CacheManager} and the name of the
 * cache(s) as provided by {@link BasicOperation#getCacheNames() getCacheNames()}
 *
 * @author Stephane Nicoll
 * @since 4.1
 * @see BasicOperation#getCacheNames()
 */
public class SimpleCacheResolver extends AbstractCacheResolver {

    public SimpleCacheResolver() {
    }

    public SimpleCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }

    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        return context.getOperation().getCacheNames();
    }

}

