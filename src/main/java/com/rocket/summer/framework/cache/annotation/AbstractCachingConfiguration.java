package com.rocket.summer.framework.cache.annotation;

import java.util.Collection;

import com.rocket.summer.framework.beans.factory.annotation.Autowired;
import com.rocket.summer.framework.cache.CacheManager;
import com.rocket.summer.framework.cache.interceptor.CacheErrorHandler;
import com.rocket.summer.framework.cache.interceptor.CacheResolver;
import com.rocket.summer.framework.cache.interceptor.KeyGenerator;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.context.annotation.ImportAware;
import com.rocket.summer.framework.core.annotation.AnnotationAttributes;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.util.CollectionUtils;

/**
 * Abstract base {@code @Configuration} class providing common structure
 * for enabling Spring's annotation-driven cache management capability.
 *
 * @author Chris Beams
 * @author Stephane Nicoll
 * @since 3.1
 * @see EnableCaching
 */
@Configuration
public abstract class AbstractCachingConfiguration implements ImportAware {

    protected AnnotationAttributes enableCaching;

    protected CacheManager cacheManager;

    protected CacheResolver cacheResolver;

    protected KeyGenerator keyGenerator;

    protected CacheErrorHandler errorHandler;


    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableCaching = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableCaching.class.getName(), false));
        if (this.enableCaching == null) {
            throw new IllegalArgumentException(
                    "@EnableCaching is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Autowired(required = false)
    void setConfigurers(Collection<CachingConfigurer> configurers) {
        if (CollectionUtils.isEmpty(configurers)) {
            return;
        }
        if (configurers.size() > 1) {
            throw new IllegalStateException(configurers.size() + " implementations of " +
                    "CachingConfigurer were found when only 1 was expected. " +
                    "Refactor the configuration such that CachingConfigurer is " +
                    "implemented only once or not at all.");
        }
        CachingConfigurer configurer = configurers.iterator().next();
        useCachingConfigurer(configurer);
    }

    /**
     * Extract the configuration from the nominated {@link CachingConfigurer}.
     */
    protected void useCachingConfigurer(CachingConfigurer config) {
        this.cacheManager = config.cacheManager();
        this.cacheResolver = config.cacheResolver();
        this.keyGenerator = config.keyGenerator();
        this.errorHandler = config.errorHandler();
    }

}

