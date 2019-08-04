package com.rocket.summer.framework.web.context.support;

import com.rocket.summer.framework.beans.factory.config.BeanPostProcessor;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.web.context.ServletConfigAware;
import com.rocket.summer.framework.web.context.ServletContextAware;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * {@link com.rocket.summer.framework.beans.factory.config.BeanPostProcessor}
 * implementation that passes the ServletContext to beans that implement
 * the {@link ServletContextAware} interface.
 *
 * <p>Web application contexts will automatically register this with their
 * underlying bean factory. Applications do not use this directly.
 *
 * @author Juergen Hoeller
 * @since 12.03.2004
 * @see com.rocket.summer.framework.web.context.ServletContextAware
 * @see com.rocket.summer.framework.web.context.support.XmlWebApplicationContext#postProcessBeanFactory
 */
public class ServletContextAwareProcessor implements BeanPostProcessor {

    private ServletContext servletContext;

    private ServletConfig servletConfig;

    /**
     * Create a new ServletContextAwareProcessor without an initial context or config.
     * When this constructor is used the {@link #getServletContext()} and/or
     * {@link #getServletConfig()} methods should be overridden.
     */
    protected ServletContextAwareProcessor() {
    }

    /**
     * Create a new ServletContextAwareProcessor for the given context.
     */
    public ServletContextAwareProcessor(ServletContext servletContext) {
        this(servletContext, null);
    }

    /**
     * Create a new ServletContextAwareProcessor for the given config.
     */
    public ServletContextAwareProcessor(ServletConfig servletConfig) {
        this(null, servletConfig);
    }

    /**
     * Create a new ServletContextAwareProcessor for the given context and config.
     */
    public ServletContextAwareProcessor(ServletContext servletContext, ServletConfig servletConfig) {
        this.servletContext = servletContext;
        this.servletConfig = servletConfig;
        if (servletContext == null && servletConfig != null) {
            this.servletContext = servletConfig.getServletContext();
        }
    }

    /**
     * Returns the {@link ServletContext} to be injected or {@code null}. This method
     * can be overridden by subclasses when a context is obtained after the post-processor
     * has been registered.
     */
    protected ServletContext getServletContext() {
        if (this.servletContext == null && getServletConfig() != null) {
            return getServletConfig().getServletContext();
        }
        return this.servletContext;
    }

    /**
     * Returns the {@link ServletContext} to be injected or {@code null}. This method
     * can be overridden by subclasses when a context is obtained after the post-processor
     * has been registered.
     */
    protected ServletConfig getServletConfig() {
        return this.servletConfig;
    }


    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (this.servletContext != null && bean instanceof ServletContextAware) {
            ((ServletContextAware) bean).setServletContext(this.servletContext);
        }
        if (this.servletConfig != null && bean instanceof ServletConfigAware) {
            ((ServletConfigAware) bean).setServletConfig(this.servletConfig);
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

}

