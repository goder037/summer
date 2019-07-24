package com.rocket.summer.framework.web.context.support;

import com.rocket.summer.framework.context.support.ServletConfigPropertySource;
import com.rocket.summer.framework.context.support.ServletContextPropertySource;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.env.MutablePropertySources;
import com.rocket.summer.framework.core.env.PropertySource;
import com.rocket.summer.framework.core.env.StandardEnvironment;
import com.rocket.summer.framework.web.context.ConfigurableWebEnvironment;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * {@link Environment} implementation to be used by {@code Servlet}-based web
 * applications. All web-related (servlet-based) {@code ApplicationContext} classes
 * initialize an instance by default.
 *
 * <p>Contributes {@code ServletConfig}, {@code ServletContext}, and JNDI-based
 * {@link PropertySource} instances. See {@link #customizePropertySources} method
 * documentation for details.
 *
 * @author Chris Beams
 * @since 3.1
 * @see StandardEnvironment
 */
public class StandardServletEnvironment extends StandardEnvironment
        implements ConfigurableWebEnvironment {

    /** Servlet context init parameters property source name: {@value} */
    public static final String SERVLET_CONTEXT_PROPERTY_SOURCE_NAME = "servletContextInitParams";

    /** Servlet config init parameters property source name: {@value} */
    public static final String SERVLET_CONFIG_PROPERTY_SOURCE_NAME = "servletConfigInitParams";

    /** JNDI property source name: {@value} */
    public static final String JNDI_PROPERTY_SOURCE_NAME = "jndiProperties";


    /**
     * Customize the set of property sources with those contributed by superclasses as
     * well as those appropriate for standard servlet-based environments:
     * <ul>
     * <li>{@value #SERVLET_CONFIG_PROPERTY_SOURCE_NAME}
     * <li>{@value #SERVLET_CONTEXT_PROPERTY_SOURCE_NAME}
     * <li>{@value #JNDI_PROPERTY_SOURCE_NAME}
     * </ul>
     * <p>Properties present in {@value #SERVLET_CONFIG_PROPERTY_SOURCE_NAME} will
     * take precedence over those in {@value #SERVLET_CONTEXT_PROPERTY_SOURCE_NAME}, and
     * properties found in either of the above take precedence over those found in
     * {@value #JNDI_PROPERTY_SOURCE_NAME}.
     * <p>Properties in any of the above will take precedence over system properties and
     * environment variables contributed by the {@link StandardEnvironment} superclass.
     * <p>The {@code Servlet}-related property sources are added as {@link
     * StubPropertySource stubs} at this stage, and will be {@linkplain
     * #initPropertySources(ServletContext) fully initialized} once the actual
     * {@link ServletContext} object becomes available.
     * @see StandardEnvironment#customizePropertySources
     * @see org.springframework.core.env.AbstractEnvironment#customizePropertySources
     * @see ServletConfigPropertySource
     * @see ServletContextPropertySource
     * @see org.springframework.jndi.JndiPropertySource
     * @see org.springframework.context.support.AbstractApplicationContext#initPropertySources
     * @see #initPropertySources(ServletContext)
     */
    @Override
    protected void customizePropertySources(MutablePropertySources propertySources) {
        propertySources.addLast(new PropertySource.StubPropertySource(SERVLET_CONFIG_PROPERTY_SOURCE_NAME));
        propertySources.addLast(new PropertySource.StubPropertySource(SERVLET_CONTEXT_PROPERTY_SOURCE_NAME));
//        if (JndiLocatorDelegate.isDefaultJndiEnvironmentAvailable()) {
//            propertySources.addLast(new JndiPropertySource(JNDI_PROPERTY_SOURCE_NAME));
//        }
        super.customizePropertySources(propertySources);
    }

    public void initPropertySources(ServletContext servletContext, ServletConfig servletConfig) {
        WebApplicationContextUtils.initServletPropertySources(
                this.getPropertySources(), servletContext, servletConfig);
    }

}

