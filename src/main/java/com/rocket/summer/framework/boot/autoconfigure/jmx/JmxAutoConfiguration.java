package com.rocket.summer.framework.boot.autoconfigure.jmx;

import javax.management.MBeanServer;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.boot.autoconfigure.EnableAutoConfiguration;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnClass;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.rocket.summer.framework.boot.autoconfigure.condition.SearchStrategy;
import com.rocket.summer.framework.boot.bind.RelaxedPropertyResolver;
import com.rocket.summer.framework.context.EnvironmentAware;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.context.annotation.EnableMBeanExport;
import com.rocket.summer.framework.context.annotation.Primary;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.jmx.export.MBeanExporter;
import com.rocket.summer.framework.jmx.export.annotation.AnnotationJmxAttributeSource;
import com.rocket.summer.framework.jmx.export.annotation.AnnotationMBeanExporter;
import com.rocket.summer.framework.jmx.export.naming.ObjectNamingStrategy;
import com.rocket.summer.framework.jmx.support.MBeanServerFactoryBean;
import com.rocket.summer.framework.jmx.support.RegistrationPolicy;
import com.rocket.summer.framework.util.StringUtils;

/**
 * {@link EnableAutoConfiguration Auto-configuration} to enable/disable Spring's
 * {@link EnableMBeanExport} mechanism based on configuration properties.
 * <p>
 * To disable auto export of annotation beans set {@code spring.jmx.enabled: false}.
 *
 * @author Christian Dupuis
 */
@Configuration
@ConditionalOnClass({ MBeanExporter.class })
@ConditionalOnProperty(prefix = "spring.jmx", name = "enabled", havingValue = "true",
        matchIfMissing = true)
public class JmxAutoConfiguration implements EnvironmentAware, BeanFactoryAware {

    private RelaxedPropertyResolver propertyResolver;

    private BeanFactory beanFactory;

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, "spring.jmx.");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(value = MBeanExporter.class,
            search = SearchStrategy.CURRENT)
    public AnnotationMBeanExporter mbeanExporter(ObjectNamingStrategy namingStrategy) {
        AnnotationMBeanExporter exporter = new AnnotationMBeanExporter();
        exporter.setRegistrationPolicy(RegistrationPolicy.FAIL_ON_EXISTING);
        exporter.setNamingStrategy(namingStrategy);
        String server = this.propertyResolver.getProperty("server", "mbeanServer");
        if (StringUtils.hasLength(server)) {
            exporter.setServer(this.beanFactory.getBean(server, MBeanServer.class));
        }
        return exporter;
    }

    @Bean
    @ConditionalOnMissingBean(value = ObjectNamingStrategy.class,
            search = SearchStrategy.CURRENT)
    public ParentAwareNamingStrategy objectNamingStrategy() {
        ParentAwareNamingStrategy namingStrategy = new ParentAwareNamingStrategy(
                new AnnotationJmxAttributeSource());
        String defaultDomain = this.propertyResolver.getProperty("default-domain");
        if (StringUtils.hasLength(defaultDomain)) {
            namingStrategy.setDefaultDomain(defaultDomain);
        }
        return namingStrategy;
    }

}

