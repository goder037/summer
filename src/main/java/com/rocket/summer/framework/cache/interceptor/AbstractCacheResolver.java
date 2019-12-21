package com.rocket.summer.framework.cache.interceptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.rocket.summer.framework.beans.factory.InitializingBean;
import com.rocket.summer.framework.cache.Cache;
import com.rocket.summer.framework.cache.CacheManager;
import com.rocket.summer.framework.util.Assert;

/**
 * A base {@link CacheResolver} implementation that requires the concrete
 * implementation to provide the collection of cache name(s) based on the
 * invocation context.
 *
 * @author Stephane Nicoll
 * @author Juergen Hoeller
 * @since 4.1
 */
public abstract class AbstractCacheResolver implements CacheResolver, InitializingBean {

    private CacheManager cacheManager;


    /**
     * Construct a new {@code AbstractCacheResolver}.
     * @see #setCacheManager
     */
    protected AbstractCacheResolver() {
    }

    /**
     * Construct a new {@code AbstractCacheResolver} for the given {@link CacheManager}.
     * @param cacheManager the CacheManager to use
     */
    protected AbstractCacheResolver(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }


    /**
     * Set the {@link CacheManager} that this instance should use.
     */
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Return the {@link CacheManager} that this instance uses.
     */
    public CacheManager getCacheManager() {
        return this.cacheManager;
    }

    @Override
    public void afterPropertiesSet()  {
        Assert.notNull(this.cacheManager, "CacheManager is required");
    }


    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        Collection<String> cacheNames = getCacheNames(context);
        if (cacheNames == null) {
            return Collections.emptyList();
        }
        Collection<Cache> result = new ArrayList<Cache>(cacheNames.size());
        for (String cacheName : cacheNames) {
            Cache cache = getCacheManager().getCache(cacheName);
            if (cache == null) {
                throw new IllegalArgumentException("Cannot find cache named '" +
                        cacheName + "' for " + context.getOperation());
            }
            result.add(cache);
        }
        return result;
    }

    /**
     * Provide the name of the cache(s) to resolve against the current cache manager.
     * <p>It is acceptable to return {@code null} to indicate that no cache could
     * be resolved for this invocation.
     * @param context the context of the particular invocation
     * @return the cache name(s) to resolve, or {@code null} if no cache should be resolved
     */
    protected abstract Collection<String> getCacheNames(CacheOperationInvocationContext<?> context);

}

