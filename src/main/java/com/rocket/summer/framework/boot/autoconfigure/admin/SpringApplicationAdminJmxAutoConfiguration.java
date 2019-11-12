package com.rocket.summer.framework.boot.autoconfigure.admin;

import java.util.List;

import javax.management.MalformedObjectNameException;

import com.rocket.summer.framework.beans.factory.ObjectProvider;
import com.rocket.summer.framework.boot.admin.SpringApplicationAdminMXBean;
import com.rocket.summer.framework.boot.admin.SpringApplicationAdminMXBeanRegistrar;
import com.rocket.summer.framework.boot.autoconfigure.AutoConfigureAfter;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.rocket.summer.framework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.jmx.export.MBeanExporter;

/**
 * Register a JMX component that allows to administer the current application. Intended
 * for internal use only.
 *
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @since 1.3.0
 * @see SpringApplicationAdminMXBean
 */
@Configuration
@AutoConfigureAfter(JmxAutoConfiguration.class)
@ConditionalOnProperty(prefix = "spring.application.admin", value = "enabled",
        havingValue = "true", matchIfMissing = false)
public class SpringApplicationAdminJmxAutoConfiguration {

    /**
     * The property to use to customize the {@code ObjectName} of the application admin
     * mbean.
     */
    private static final String JMX_NAME_PROPERTY = "spring.application.admin.jmx-name";

    /**
     * The default {@code ObjectName} of the application admin mbean.
     */
    private static final String DEFAULT_JMX_NAME = "com.rocket.summer.framework.boot:type=Admin,name=SpringApplication";

    private final List<MBeanExporter> mbeanExporters;

    private final Environment environment;

    public SpringApplicationAdminJmxAutoConfiguration(
            ObjectProvider<List<MBeanExporter>> mbeanExporters, Environment environment) {
        this.mbeanExporters = mbeanExporters.getIfAvailable();
        this.environment = environment;
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringApplicationAdminMXBeanRegistrar springApplicationAdminRegistrar()
            throws MalformedObjectNameException {
        String jmxName = this.environment.getProperty(JMX_NAME_PROPERTY,
                DEFAULT_JMX_NAME);
        if (this.mbeanExporters != null) { // Make sure to not register that MBean twice
            for (MBeanExporter mbeanExporter : this.mbeanExporters) {
                mbeanExporter.addExcludedBean(jmxName);
            }
        }
        return new SpringApplicationAdminMXBeanRegistrar(jmxName);
    }

}

