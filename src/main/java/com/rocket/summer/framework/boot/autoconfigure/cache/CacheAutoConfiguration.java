package com.rocket.summer.framework.boot.autoconfigure.cache;

import java.util.List;

import com.rocket.summer.framework.beans.factory.InitializingBean;
import com.rocket.summer.framework.beans.factory.ObjectProvider;
import com.rocket.summer.framework.boot.autoconfigure.AutoConfigureAfter;
import com.rocket.summer.framework.boot.autoconfigure.AutoConfigureBefore;
import com.rocket.summer.framework.boot.autoconfigure.EnableAutoConfiguration;
import com.rocket.summer.framework.boot.autoconfigure.cache.CacheAutoConfiguration.CacheConfigurationImportSelector;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnClass;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import com.rocket.summer.framework.boot.context.properties.EnableConfigurationProperties;
import com.rocket.summer.framework.cache.CacheManager;
import com.rocket.summer.framework.cache.annotation.EnableCaching;
import com.rocket.summer.framework.cache.interceptor.CacheAspectSupport;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.context.annotation.Import;
import com.rocket.summer.framework.context.annotation.ImportSelector;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.util.Assert;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for the cache abstraction. Creates a
 * {@link CacheManager} if necessary when caching is enabled via {@link EnableCaching}.
 * <p>
 * Cache store can be auto-detected or specified explicitly via configuration.
 *
 * @author Stephane Nicoll
 * @since 1.3.0
 * @see EnableCaching
 */
@Configuration
@ConditionalOnClass(CacheManager.class)
@ConditionalOnBean(CacheAspectSupport.class)
@ConditionalOnMissingBean(value = CacheManager.class, name = "cacheResolver")
@EnableConfigurationProperties(CacheProperties.class)
@AutoConfigureAfter({ RedisAutoConfiguration.class })
@Import(CacheConfigurationImportSelector.class)
public class CacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CacheManagerCustomizers cacheManagerCustomizers(
            ObjectProvider<List<CacheManagerCustomizer<?>>> customizers) {
        return new CacheManagerCustomizers(customizers.getIfAvailable());
    }

    @Bean
    public CacheManagerValidator cacheAutoConfigurationValidator(
            CacheProperties cacheProperties, ObjectProvider<CacheManager> cacheManager) {
        return new CacheManagerValidator(cacheProperties, cacheManager);
    }

    /**
     * Bean used to validate that a CacheManager exists and provide a more meaningful
     * exception.
     */
    static class CacheManagerValidator implements InitializingBean {

        private final CacheProperties cacheProperties;

        private final ObjectProvider<CacheManager> cacheManager;

        CacheManagerValidator(CacheProperties cacheProperties,
                              ObjectProvider<CacheManager> cacheManager) {
            this.cacheProperties = cacheProperties;
            this.cacheManager = cacheManager;
        }

        @Override
        public void afterPropertiesSet() {
            Assert.notNull(this.cacheManager.getIfAvailable(),
                    "No cache manager could "
                            + "be auto-configured, check your configuration (caching "
                            + "type is '" + this.cacheProperties.getType() + "')");
        }

    }

    /**
     * {@link ImportSelector} to add {@link CacheType} configuration classes.
     */
    static class CacheConfigurationImportSelector implements ImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            CacheType[] types = CacheType.values();
            String[] imports = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                imports[i] = CacheConfigurations.getConfigurationClass(types[i]);
            }
            return imports;
        }

    }

}

