package com.rocket.summer.framework.cache.support;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.rocket.summer.framework.cache.Cache;
import com.rocket.summer.framework.cache.CacheManager;

/**
 * A basic, no operation {@link CacheManager} implementation suitable
 * for disabling caching, typically used for backing cache declarations
 * without an actual backing store.
 *
 * <p>Will simply accept any items into the cache not actually storing them.
 *
 * @author Costin Leau
 * @author Stephane Nicoll
 * @since 3.1
 * @see CompositeCacheManager
 */
public class NoOpCacheManager implements CacheManager {

    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>(16);

    private final Set<String> cacheNames = new LinkedHashSet<String>(16);


    /**
     * This implementation always returns a {@link Cache} implementation that will not store items.
     * Additionally, the request cache will be remembered by the manager for consistency.
     */
    @Override
    public Cache getCache(String name) {
        Cache cache = this.caches.get(name);
        if (cache == null) {
            this.caches.putIfAbsent(name, new NoOpCache(name));
            synchronized (this.cacheNames) {
                this.cacheNames.add(name);
            }
        }

        return this.caches.get(name);
    }

    /**
     * This implementation returns the name of the caches previously requested.
     */
    @Override
    public Collection<String> getCacheNames() {
        synchronized (this.cacheNames) {
            return Collections.unmodifiableSet(this.cacheNames);
        }
    }

}

