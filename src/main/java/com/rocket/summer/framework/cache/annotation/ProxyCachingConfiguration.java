package com.rocket.summer.framework.cache.annotation;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.cache.config.CacheManagementConfigUtils;
import com.rocket.summer.framework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import com.rocket.summer.framework.cache.interceptor.CacheInterceptor;
import com.rocket.summer.framework.cache.interceptor.CacheOperationSource;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.context.annotation.Role;

/**
 * {@code @Configuration} class that registers the Spring infrastructure beans necessary
 * to enable proxy-based annotation-driven cache management.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see EnableCaching
 * @see CachingConfigurationSelector
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyCachingConfiguration extends AbstractCachingConfiguration {

    @Bean(name = CacheManagementConfigUtils.CACHE_ADVISOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public BeanFactoryCacheOperationSourceAdvisor cacheAdvisor() {
        BeanFactoryCacheOperationSourceAdvisor advisor = new BeanFactoryCacheOperationSourceAdvisor();
        advisor.setCacheOperationSource(cacheOperationSource());
        advisor.setAdvice(cacheInterceptor());
        advisor.setOrder(this.enableCaching.<Integer>getNumber("order"));
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CacheOperationSource cacheOperationSource() {
        return new AnnotationCacheOperationSource();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CacheInterceptor cacheInterceptor() {
        CacheInterceptor interceptor = new CacheInterceptor();
        interceptor.setCacheOperationSources(cacheOperationSource());
        if (this.cacheResolver != null) {
            interceptor.setCacheResolver(this.cacheResolver);
        }
        else if (this.cacheManager != null) {
            interceptor.setCacheManager(this.cacheManager);
        }
        if (this.keyGenerator != null) {
            interceptor.setKeyGenerator(this.keyGenerator);
        }
        if (this.errorHandler != null) {
            interceptor.setErrorHandler(this.errorHandler);
        }
        return interceptor;
    }

}

