package com.rocket.summer.framework.cache.annotation;

import com.rocket.summer.framework.cache.CacheManager;
import com.rocket.summer.framework.cache.interceptor.CacheErrorHandler;
import com.rocket.summer.framework.cache.interceptor.CacheResolver;
import com.rocket.summer.framework.cache.interceptor.KeyGenerator;

/**
 * An implementation of {@link CachingConfigurer} with empty methods allowing
 * sub-classes to override only the methods they're interested in.
 *
 * @author Stephane Nicoll
 * @since 4.1
 * @see CachingConfigurer
 */
public class CachingConfigurerSupport implements CachingConfigurer {

    @Override
    public CacheManager cacheManager() {
        return null;
    }

    @Override
    public CacheResolver cacheResolver() {
        return null;
    }

    @Override
    public KeyGenerator keyGenerator() {
        return null;
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return null;
    }

}
