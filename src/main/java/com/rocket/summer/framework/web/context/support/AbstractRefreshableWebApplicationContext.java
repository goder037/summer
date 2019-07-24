package com.rocket.summer.framework.web.context.support;

import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.context.support.AbstractRefreshableConfigApplicationContext;
import com.rocket.summer.framework.core.env.ConfigurableEnvironment;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.support.ResourcePatternResolver;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.context.ConfigurableWebApplicationContext;
import com.rocket.summer.framework.web.context.ConfigurableWebEnvironment;
import com.rocket.summer.framework.web.context.ServletConfigAware;
import com.rocket.summer.framework.web.context.ServletContextAware;
import com.rocket.summer.framework.web.ui.context.Theme;
import com.rocket.summer.framework.web.ui.context.ThemeSource;
import com.rocket.summer.framework.web.ui.context.support.UiApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * {@link org.springframework.context.support.AbstractRefreshableApplicationContext}
 * subclass which implements the
 * {@link org.springframework.web.context.ConfigurableWebApplicationContext}
 * interface for web environments. Provides a "configLocations" property,
 * to be populated through the ConfigurableWebApplicationContext interface
 * on web application startup.
 *
 * <p>This class is as easy to subclass as AbstractRefreshableApplicationContext:
 * All you need to implements is the {@link #loadBeanDefinitions} method;
 * see the superclass javadoc for details. Note that implementations are supposed
 * to load bean definitions from the files specified by the locations returned
 * by the {@link #getConfigLocations} method.
 *
 * <p>Interprets resource paths as servlet context resources, i.e. as paths beneath
 * the web application root. Absolute paths, e.g. for files outside the web app root,
 * can be accessed via "file:" URLs, as implemented by
 * {@link org.springframework.core.io.DefaultResourceLoader}.
 *
 * <p>In addition to the special beans detected by
 * {@link org.springframework.context.support.AbstractApplicationContext},
 * this class detects a bean of type {@link org.springframework.ui.context.ThemeSource}
 * in the context, under the special bean name "themeSource".
 *
 * <p><b>This is the web context to be subclassed for a different bean definition format.</b>
 * Such a context implementation can be specified as "contextClass" context-param
 * for {@link org.springframework.web.context.ContextLoader} or as "contextClass"
 * init-param for {@link org.springframework.web.servlet.FrameworkServlet},
 * replacing the default {@link XmlWebApplicationContext}. It will then automatically
 * receive the "contextConfigLocation" context-param or init-param, respectively.
 *
 * <p>Note that WebApplicationContext implementations are generally supposed
 * to configure themselves based on the configuration received through the
 * {@link ConfigurableWebApplicationContext} interface. In contrast, a standalone
 * application context might allow for configuration in custom startup code
 * (for example, {@link org.springframework.context.support.GenericApplicationContext}).
 *
 * @author Juergen Hoeller
 * @since 1.1.3
 * @see #loadBeanDefinitions
 * @see org.springframework.web.context.ConfigurableWebApplicationContext#setConfigLocations
 * @see org.springframework.ui.context.ThemeSource
 */
public abstract class AbstractRefreshableWebApplicationContext extends AbstractRefreshableConfigApplicationContext
        implements ConfigurableWebApplicationContext, ThemeSource {

    /** Servlet context that this context runs in */
    private ServletContext servletContext;

    /** Servlet config that this context runs in, if any */
    private ServletConfig servletConfig;

    /** Namespace of this context, or <code>null</code> if root */
    private String namespace;

    /** the ThemeSource for this ApplicationContext */
    private ThemeSource themeSource;


    public AbstractRefreshableWebApplicationContext() {
        setDisplayName("Root WebApplicationContext");
    }


    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public void setServletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
        if (servletConfig != null && this.servletContext == null) {
            this.servletContext = servletConfig.getServletContext();
        }
    }

    public ServletConfig getServletConfig() {
        return this.servletConfig;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
        if (namespace != null) {
            setDisplayName("WebApplicationContext for namespace '" + namespace + "'");
        }
    }

    public String getNamespace() {
        return this.namespace;
    }

    @Override
    public String[] getConfigLocations() {
        return super.getConfigLocations();
    }


    /**
     * Register request/session scopes, a {@link ServletContextAwareProcessor}, etc.
     */
    @Override
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.addBeanPostProcessor(new ServletContextAwareProcessor(this.servletContext, this.servletConfig));
        beanFactory.ignoreDependencyInterface(ServletContextAware.class);
        beanFactory.ignoreDependencyInterface(ServletConfigAware.class);

        WebApplicationContextUtils.registerWebApplicationScopes(beanFactory, this.servletContext);
        WebApplicationContextUtils.registerEnvironmentBeans(beanFactory, this.servletContext, this.servletConfig);
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

    public Theme getTheme(String themeName) {
        return this.themeSource.getTheme(themeName);
    }


    @Override
    public ConfigurableWebEnvironment getEnvironment() {
        ConfigurableEnvironment env = super.getEnvironment();
        Assert.isInstanceOf(ConfigurableWebEnvironment.class, env,
                "ConfigurableWebApplicationContext environment must be of type " +
                        "ConfigurableWebEnvironment");
        return (ConfigurableWebEnvironment) env;
    }

}
