package com.rocket.summer.framework.boot.autoconfigure.web;

import javax.servlet.Servlet;


import org.apache.catalina.startup.Tomcat;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.beans.factory.support.RootBeanDefinition;
import com.rocket.summer.framework.boot.autoconfigure.AutoConfigureOrder;
import com.rocket.summer.framework.boot.autoconfigure.EnableAutoConfiguration;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnClass;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import com.rocket.summer.framework.boot.autoconfigure.condition.SearchStrategy;
import com.rocket.summer.framework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration.BeanPostProcessorsRegistrar;
import com.rocket.summer.framework.boot.context.embedded.EmbeddedServletContainerCustomizerBeanPostProcessor;
import com.rocket.summer.framework.boot.context.embedded.EmbeddedServletContainerFactory;
import com.rocket.summer.framework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import com.rocket.summer.framework.boot.web.servlet.ErrorPageRegistrarBeanPostProcessor;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.context.annotation.Import;
import com.rocket.summer.framework.context.annotation.ImportBeanDefinitionRegistrar;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.util.ObjectUtils;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for an embedded servlet containers.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Ivan Sopov
 * @author Stephane Nicoll
 */
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@ConditionalOnWebApplication
@Import(BeanPostProcessorsRegistrar.class)
public class EmbeddedServletContainerAutoConfiguration {

    /**
     * Nested configuration if Tomcat is being used.
     */
    @Configuration
    @ConditionalOnClass({ Servlet.class, Tomcat.class })
    @ConditionalOnMissingBean(value = EmbeddedServletContainerFactory.class,
            search = SearchStrategy.CURRENT)
    public static class EmbeddedTomcat {

        @Bean
        public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
            return new TomcatEmbeddedServletContainerFactory();
        }

    }

    /**
     * Registers a {@link EmbeddedServletContainerCustomizerBeanPostProcessor}. Registered
     * via {@link ImportBeanDefinitionRegistrar} for early registration.
     */
    public static class BeanPostProcessorsRegistrar
            implements ImportBeanDefinitionRegistrar, BeanFactoryAware {

        private ConfigurableListableBeanFactory beanFactory;

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            if (beanFactory instanceof ConfigurableListableBeanFactory) {
                this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
            }
        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                            BeanDefinitionRegistry registry) {
            if (this.beanFactory == null) {
                return;
            }
            registerSyntheticBeanIfMissing(registry,
                    "embeddedServletContainerCustomizerBeanPostProcessor",
                    EmbeddedServletContainerCustomizerBeanPostProcessor.class);
            registerSyntheticBeanIfMissing(registry,
                    "errorPageRegistrarBeanPostProcessor",
                    ErrorPageRegistrarBeanPostProcessor.class);
        }

        private void registerSyntheticBeanIfMissing(BeanDefinitionRegistry registry,
                                                    String name, Class<?> beanClass) {
            if (ObjectUtils.isEmpty(
                    this.beanFactory.getBeanNamesForType(beanClass, true, false))) {
                RootBeanDefinition beanDefinition = new RootBeanDefinition(beanClass);
                beanDefinition.setSynthetic(true);
                registry.registerBeanDefinition(name, beanDefinition);
            }
        }

    }

}
