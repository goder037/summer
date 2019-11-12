package com.rocket.summer.framework.boot.autoconfigure.cache;

import java.util.List;

import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.cache.CacheManager;
import com.rocket.summer.framework.cache.concurrent.ConcurrentMapCacheManager;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Conditional;
import com.rocket.summer.framework.context.annotation.Configuration;

/**
 * Simplest cache configuration, usually used as a fallback.
 *
 * @author Stephane Nicoll
 * @since 1.3.0
 */
@Configuration
@ConditionalOnMissingBean(CacheManager.class)
@Conditional(CacheCondition.class)
class SimpleCacheConfiguration {

    private final CacheProperties cacheProperties;

    private final CacheManagerCustomizers customizerInvoker;

    SimpleCacheConfiguration(CacheProperties cacheProperties,
                             CacheManagerCustomizers customizerInvoker) {
        this.cacheProperties = cacheProperties;
        this.customizerInvoker = customizerInvoker;
    }

    @Bean
    public ConcurrentMapCacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        List<String> cacheNames = this.cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            cacheManager.setCacheNames(cacheNames);
        }
        return this.customizerInvoker.customize(cacheManager);
    }

}

