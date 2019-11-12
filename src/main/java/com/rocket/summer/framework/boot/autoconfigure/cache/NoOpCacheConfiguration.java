package com.rocket.summer.framework.boot.autoconfigure.cache;

import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.cache.CacheManager;
import com.rocket.summer.framework.cache.support.NoOpCacheManager;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Conditional;
import com.rocket.summer.framework.context.annotation.Configuration;

/**
 * No-op cache configuration used to disable caching via configuration.
 *
 * @author Stephane Nicoll
 * @since 1.3.0
 */
@Configuration
@ConditionalOnMissingBean(CacheManager.class)
@Conditional(CacheCondition.class)
class NoOpCacheConfiguration {

    @Bean
    public NoOpCacheManager cacheManager() {
        return new NoOpCacheManager();
    }

}
