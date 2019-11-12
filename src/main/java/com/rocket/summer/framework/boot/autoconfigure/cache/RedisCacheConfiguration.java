package com.rocket.summer.framework.boot.autoconfigure.cache;

import java.util.List;

import com.rocket.summer.framework.boot.autoconfigure.AutoConfigureAfter;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import com.rocket.summer.framework.cache.CacheManager;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Conditional;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.data.redis.cache.RedisCacheManager;
import com.rocket.summer.framework.data.redis.core.RedisTemplate;

/**
 * Redis cache configuration.
 *
 * @author Stephane Nicoll
 * @since 1.3.0
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnBean(RedisTemplate.class)
@ConditionalOnMissingBean(CacheManager.class)
@Conditional(CacheCondition.class)
class RedisCacheConfiguration {

    private final CacheProperties cacheProperties;

    private final CacheManagerCustomizers customizerInvoker;

    RedisCacheConfiguration(CacheProperties cacheProperties,
                            CacheManagerCustomizers customizerInvoker) {
        this.cacheProperties = cacheProperties;
        this.customizerInvoker = customizerInvoker;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisTemplate<Object, Object> redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        cacheManager.setUsePrefix(true);
        List<String> cacheNames = this.cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            cacheManager.setCacheNames(cacheNames);
        }
        return this.customizerInvoker.customize(cacheManager);
    }

}

