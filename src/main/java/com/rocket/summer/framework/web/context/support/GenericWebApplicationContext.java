package com.rocket.summer.framework.web.context.support;

import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.beans.factory.support.DefaultListableBeanFactory;
import com.rocket.summer.framework.context.support.GenericApplicationContext;
import com.rocket.summer.framework.core.env.ConfigurableEnvironment;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.support.ResourcePatternResolver;
import com.rocket.summer.framework.util.ObjectUtils;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.web.context.ConfigurableWebApplicationContext;
import com.rocket.summer.framework.web.context.ConfigurableWebEnvironment;
import com.rocket.summer.framework.web.context.ServletContextAware;
import com.rocket.summer.framework.web.ui.context.Theme;
import com.rocket.summer.framework.web.ui.context.ThemeSource;
import com.rocket.summer.framework.web.ui.context.support.UiApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * Subclass of {@link GenericApplicationContext}, suitable for web environments.
 *
 * <p>Implements the
 * {@link com.rocket.summer.framework.web.context.ConfigurableWebApplicationContext},
 * but is not intended for declarative setup in {@code web.xml}. Instead,
 * it is designed for programmatic setup, for example for building nested contexts or
 * for use within Spring 3.1 {@link com.rocket.summer.framework.web.WebApplicationInitializer}s.
 *
 * <p><b>If you intend to implement a WebApplicationContext that reads bean definitions
 * from configuration files, consider deriving from AbstractRefreshableWebApplicationContext,
 * reading the bean definitions in an implementation of the {@code loadBeanDefinitions}
 * method.</b>
 *
 * <p>Interprets resource paths as servlet context resources, i.e. as paths beneath
 * the web application root. Absolute paths, e.g. for files outside the web app root,
 * can be accessed via "file:" URLs, as implemented by AbstractApplicationContext.
 *
 * <p>In addition to the special beans detected by
 * {@link com.rocket.summer.framework.context.support.AbstractApplicationContext},
 * this class detects a ThemeSource bean in the context, with the name "themeSource".
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 1.2
 */
public class GenericWebApplicationContext extends GenericApplicationContext
        implements ConfigurableWebApplicationContext, ThemeSource {

    private ServletContext servletContext;

    private ThemeSource themeSource;


    /**
     * Create a new GenericWebApplicationContext.
     * @see #setServletContext
     * @see #registerBeanDefinition
     * @see #refresh
     */
    public GenericWebApplicationContext() {
        super();
    }

    /**
     * Create a new GenericWebApplicationContext for the given ServletContext.
     * @param servletContext the ServletContext to run in
     * @see #registerBeanDefinition
     * @see #refresh
     */
    public GenericWebApplicationContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * Create a new GenericWebApplicationContext with the given DefaultListableBeanFactory.
     * @param beanFactory the DefaultListableBeanFactory instance to use for this context
     * @see #setServletContext
     * @see #registerBeanDefinition
     * @see #refresh
     */
    public GenericWebApplicationContext(DefaultListableBeanFactory beanFactory) {
        super(beanFactory);
    }

    /**
     * Create a new GenericWebApplicationContext with the given DefaultListableBeanFactory.
     * @param beanFactory the DefaultListableBeanFactory instance to use for this context
     * @param servletContext the ServletContext to run in
     * @see #registerBeanDefinition
     * @see #refresh
     */
    public GenericWebApplicationContext(DefaultListableBeanFactory beanFactory, ServletContext servletContext) {
        super(beanFactory);
        this.servletContext = servletContext;
    }

    /**
     * Set the ServletContext that this WebApplicationContext runs in.
     */
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public String getApplicationName() {
        return (this.servletContext != null ? this.servletContext.getContextPath() : "");
    }

    /**
     * Create and return a new {@link StandardServletEnvironment}.
     */
    @Override
    protected ConfigurableEnvironment createEnvironment() {
        return new StandardServletEnvironment();
    }

    /**
     * Register ServletContextAwareProcessor.
     * @see ServletContextAwareProcessor
     */
    @Override
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.addBeanPostProcessor(new ServletContextAwareProcessor(this.servletContext));
        beanFactory.ignoreDependencyInterface(ServletContextAware.class);

        WebApplicationContextUtils.registerWebApplicationScopes(beanFactory, this.servletContext);
        WebApplicationContextUtils.registerEnvironmentBeans(beanFactory, this.servletContext);
    }

    /**
     * This implementation supports file paths beneath the root of the ServletContext.
     * @see ServletContextResource
     */
    @Override
    protected Resource getResourceByPath(String path) {
        return new ServletContextResource(this.servletContext, path);
    }

    /**
     * This implementation supports pattern matching in unexpanded WARs too.
     * @see ServletContextResourcePatternResolver
     */
    @Override
    protected ResourcePatternResolver getResourcePatternResolver() {
        return new ServletContextResourcePatternResolver(this);
    }

    /**
     * Initialize the theme capability.
     */
    @Override
    protected void onRefresh() {
        this.themeSource = UiApplicationContextUtils.initThemeSource(this);
    }

    /**
     * {@inheritDoc}
     * <p>Replace {@code Servlet}-related property sources.
     */
    @Override
    protected void initPropertySources() {
        ConfigurableEnvironment env = getEnvironment();
        if (env instanceof ConfigurableWebEnvironment) {
            ((ConfigurableWebEnvironment) env).initPropertySources(this.servletContext, null);
        }
    }

    @Override
    public Theme getTheme(String themeName) {
        return this.themeSource.getTheme(themeName);
    }


    // ---------------------------------------------------------------------
    // Pseudo-implementation of ConfigurableWebApplicationContext
    // ---------------------------------------------------------------------

    @Override
    public void setServletConfig(ServletConfig servletConfig) {
        // no-op
    }

    @Override
    public ServletConfig getServletConfig() {
        throw new UnsupportedOperationException(
                "GenericWebApplicationContext does not support getServletConfig()");
    }

    @Override
    public void setNamespace(String namespace) {
        // no-op
    }

    @Override
    public String getNamespace() {
        throw new UnsupportedOperationException(
                "GenericWebApplicationContext does not support getNamespace()");
    }

    @Override
    public void setConfigLocation(String configLocation) {
        if (StringUtils.hasText(configLocation)) {
            throw new UnsupportedOperationException(
                    "GenericWebApplicationContext does not support setConfigLocation(). " +
                            "Do you still have an 'contextConfigLocations' init-param set?");
        }
    }

    @Override
    public void setConfigLocations(String... configLocations) {
        if (!ObjectUtils.isEmpty(configLocations)) {
            throw new UnsupportedOperationException(
                    "GenericWebApplicationContext does not support setConfigLocations(). " +
                            "Do you still have an 'contextConfigLocations' init-param set?");
        }
    }

    @Override
    public String[] getConfigLocations() {
        throw new UnsupportedOperationException(
                "GenericWebApplicationContext does not support getConfigLocations()");
    }

}
