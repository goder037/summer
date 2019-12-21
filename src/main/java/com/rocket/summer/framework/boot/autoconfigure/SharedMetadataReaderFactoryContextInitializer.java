package com.rocket.summer.framework.boot.autoconfigure;

import com.rocket.summer.framework.beans.factory.BeanClassLoaderAware;
import com.rocket.summer.framework.beans.factory.FactoryBean;
import com.rocket.summer.framework.beans.factory.NoSuchBeanDefinitionException;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.beans.factory.config.RuntimeBeanReference;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import com.rocket.summer.framework.beans.factory.support.RootBeanDefinition;
import com.rocket.summer.framework.boot.type.classreading.ConcurrentReferenceCachingMetadataReaderFactory;
import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.context.ConfigurableApplicationContext;
import com.rocket.summer.framework.context.annotation.AnnotationConfigUtils;
import com.rocket.summer.framework.context.annotation.ConfigurationClassPostProcessor;
import com.rocket.summer.framework.context.event.ContextRefreshedEvent;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.PriorityOrdered;
import com.rocket.summer.framework.core.type.classreading.CachingMetadataReaderFactory;
import com.rocket.summer.framework.core.type.classreading.MetadataReaderFactory;
import com.rocket.summer.framework.context.ApplicationContextInitializer;

/**
 * {@link ApplicationContextInitializer} to create a shared
 * {@link CachingMetadataReaderFactory} between the
 * {@link ConfigurationClassPostProcessor} and Spring Boot.
 *
 * @author Phillip Webb
 * @since 1.4.0
 */
class SharedMetadataReaderFactoryContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final String BEAN_NAME = "com.rocket.summer.framework.boot.autoconfigure."
            + "internalCachingMetadataReaderFactory";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.addBeanFactoryPostProcessor(
                new CachingMetadataReaderFactoryPostProcessor());
    }

    /**
     * {@link BeanDefinitionRegistryPostProcessor} to register the
     * {@link CachingMetadataReaderFactory} and configure the
     * {@link ConfigurationClassPostProcessor}.
     */
    private static class CachingMetadataReaderFactoryPostProcessor
            implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

        @Override
        public int getOrder() {
            // Must happen before the ConfigurationClassPostProcessor is created
            return Ordered.HIGHEST_PRECEDENCE;
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                throws BeansException {
        }

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
                throws BeansException {
            register(registry);
            configureConfigurationClassPostProcessor(registry);
        }

        private void register(BeanDefinitionRegistry registry) {
            RootBeanDefinition definition = new RootBeanDefinition(
                    SharedMetadataReaderFactoryBean.class);
            registry.registerBeanDefinition(BEAN_NAME, definition);
        }

        private void configureConfigurationClassPostProcessor(
                BeanDefinitionRegistry registry) {
            try {
                BeanDefinition definition = registry.getBeanDefinition(
                        AnnotationConfigUtils.CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME);
                definition.getPropertyValues().add("metadataReaderFactory",
                        new RuntimeBeanReference(BEAN_NAME));
            }
            catch (NoSuchBeanDefinitionException ex) {
            }
        }

    }

    /**
     * {@link FactoryBean} to create the shared {@link MetadataReaderFactory}.
     */
    static class SharedMetadataReaderFactoryBean
            implements FactoryBean<ConcurrentReferenceCachingMetadataReaderFactory>,
            BeanClassLoaderAware, ApplicationListener<ContextRefreshedEvent> {

        private ConcurrentReferenceCachingMetadataReaderFactory metadataReaderFactory;

        @Override
        public void setBeanClassLoader(ClassLoader classLoader) {
            this.metadataReaderFactory = new ConcurrentReferenceCachingMetadataReaderFactory(
                    classLoader);
        }

        @Override
        public ConcurrentReferenceCachingMetadataReaderFactory getObject()
                throws Exception {
            return this.metadataReaderFactory;
        }

        @Override
        public Class<?> getObjectType() {
            return CachingMetadataReaderFactory.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            this.metadataReaderFactory.clearCache();
        }

    }

}

