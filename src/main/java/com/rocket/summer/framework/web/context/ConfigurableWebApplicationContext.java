package com.rocket.summer.framework.web.context;

import com.rocket.summer.framework.context.ConfigurableApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * Interface to be implemented by configurable web application contexts.
 * Supported by {@link ContextLoader} and
 * {@link com.rocket.summer.framework.web.servlet.FrameworkServlet}.
 *
 * <p>Note: The setters of this interface need to be called before an
 * invocation of the {@link #refresh} method inherited from
 * {@link com.rocket.summer.framework.context.ConfigurableApplicationContext}.
 * They do not cause an initialization of the context on their own.
 *
 * @author Juergen Hoeller
 * @since 05.12.2003
 * @see #refresh
 * @see ContextLoader#createWebApplicationContext
 * @see com.rocket.summer.framework.web.servlet.FrameworkServlet#createWebApplicationContext
 */
public interface ConfigurableWebApplicationContext extends WebApplicationContext, ConfigurableApplicationContext {

    /**
     * Prefix for ApplicationContext ids that refer to context path and/or servlet name.
     */
    String APPLICATION_CONTEXT_ID_PREFIX = WebApplicationContext.class.getName() + ":";

    /**
     * Name of the ServletConfig environment bean in the factory.
     * @see javax.servlet.ServletConfig
     */
    String SERVLET_CONFIG_BEAN_NAME = "servletConfig";


    /**
     * Set the ServletContext for this web application context.
     * <p>Does not cause an initialization of the context: refresh needs to be
     * called after the setting of all configuration properties.
     * @see #refresh()
     */
    void setServletContext(ServletContext servletContext);

    /**
     * Set the ServletConfig for this web application context.
     * Only called for a WebApplicationContext that belongs to a specific Servlet.
     * @see #refresh()
     */
    void setServletConfig(ServletConfig servletConfig);

    /**
     * Return the ServletConfig for this web application context, if any.
     */
    ServletConfig getServletConfig();

    /**
     * Set the namespace for this web application context,
     * to be used for building a default context config location.
     * The root web application context does not have a namespace.
     */
    void setNamespace(String namespace);

    /**
     * Return the namespace for this web application context, if any.
     */
    String getNamespace();

    /**
     * Set the config locations for this web application context in init-param style,
     * i.e. with distinct locations separated by commas, semicolons or whitespace.
     * <p>If not set, the implementation is supposed to use a default for the
     * given namespace or the root web application context, as appropriate.
     */
    void setConfigLocation(String configLocation);

    /**
     * Set the config locations for this web application context.
     * <p>If not set, the implementation is supposed to use a default for the
     * given namespace or the root web application context, as appropriate.
     */
    void setConfigLocations(String[] configLocations);

    /**
     * Return the config locations for this web application context,
     * or <code>null</code> if none specified.
     */
    String[] getConfigLocations();

    /**
     * Return the {@link ConfigurableWebEnvironment} used by this web application context.
     */
    ConfigurableWebEnvironment getEnvironment();

}

