package com.rocket.summer.framework.boot.admin;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.beans.factory.DisposableBean;
import com.rocket.summer.framework.beans.factory.InitializingBean;
import com.rocket.summer.framework.boot.context.embedded.EmbeddedWebApplicationContext;
import com.rocket.summer.framework.boot.context.event.ApplicationReadyEvent;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.ApplicationContextAware;
import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.context.ConfigurableApplicationContext;
import com.rocket.summer.framework.context.EnvironmentAware;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.env.StandardEnvironment;
import com.rocket.summer.framework.util.Assert;

/**
 * Register a {@link SpringApplicationAdminMXBean} implementation to the platform
 * {@link MBeanServer}.
 *
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @since 1.3.0
 */
public class SpringApplicationAdminMXBeanRegistrar
        implements ApplicationContextAware, EnvironmentAware, InitializingBean,
        DisposableBean, ApplicationListener<ApplicationReadyEvent> {

    private static final Log logger = LogFactory.getLog(SpringApplicationAdmin.class);

    private ConfigurableApplicationContext applicationContext;

    private Environment environment = new StandardEnvironment();

    private final ObjectName objectName;

    private boolean ready = false;

    public SpringApplicationAdminMXBeanRegistrar(String name)
            throws MalformedObjectNameException {
        this.objectName = new ObjectName(name);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        Assert.state(applicationContext instanceof ConfigurableApplicationContext,
                "ApplicationContext does not implement ConfigurableApplicationContext");
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (this.applicationContext.equals(event.getApplicationContext())) {
            this.ready = true;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        server.registerMBean(new SpringApplicationAdmin(), this.objectName);
        if (logger.isDebugEnabled()) {
            logger.debug("Application Admin MBean registered with name '"
                    + this.objectName + "'");
        }
    }

    @Override
    public void destroy() throws Exception {
        ManagementFactory.getPlatformMBeanServer().unregisterMBean(this.objectName);
    }

    private class SpringApplicationAdmin implements SpringApplicationAdminMXBean {

        @Override
        public boolean isReady() {
            return SpringApplicationAdminMXBeanRegistrar.this.ready;
        }

        @Override
        public boolean isEmbeddedWebApplication() {
            return (SpringApplicationAdminMXBeanRegistrar.this.applicationContext != null
                    && SpringApplicationAdminMXBeanRegistrar.this.applicationContext instanceof EmbeddedWebApplicationContext);
        }

        @Override
        public String getProperty(String key) {
            return SpringApplicationAdminMXBeanRegistrar.this.environment
                    .getProperty(key);
        }

        @Override
        public void shutdown() {
            logger.info("Application shutdown requested.");
            SpringApplicationAdminMXBeanRegistrar.this.applicationContext.close();
        }

    }

}

