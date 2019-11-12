package com.rocket.summer.framework.boot.autoconfigure.cache;

import java.util.Collection;

import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.cache.Cache;
import com.rocket.summer.framework.cache.CacheManager;
import com.rocket.summer.framework.cache.support.SimpleCacheManager;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Conditional;
import com.rocket.summer.framework.context.annotation.Configuration;

/**
 * Generic cache configuration based on arbitrary {@link Cache} instances defined in the
 * context.
 *
 * @author Stephane Nicoll
 * @since 1.3.0
 */
@Configuration
@ConditionalOnBean(Cache.class)
@ConditionalOnMissingBean(CacheManager.class)
@Conditional(CacheCondition.class)
class GenericCacheConfiguration {

    private final CacheManagerCustomizers customizers;

    GenericCacheConfiguration(CacheManagerCustomizers customizers) {
        this.customizers = customizers;
    }

    @Bean
    public SimpleCacheManager cacheManager(Collection<Cache> caches) {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return this.customizers.customize(cacheManager);
    }

}
