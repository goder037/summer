package com.rocket.summer.framework.boot.autoconfigure.cache;

import com.rocket.summer.framework.cache.CacheManager;

/**
 * Callback interface that can be implemented by beans wishing to customize the cache
 * manager before it is fully initialized, in particular to tune its configuration.
 *
 * @param <T> the type of the {@link CacheManager}
 * @author Stephane Nicoll
 * @since 1.3.3
 */
public interface CacheManagerCustomizer<T extends CacheManager> {

    /**
     * Customize the cache manager.
     * @param cacheManager the {@code CacheManager} to customize
     */
    void customize(T cacheManager);

}
