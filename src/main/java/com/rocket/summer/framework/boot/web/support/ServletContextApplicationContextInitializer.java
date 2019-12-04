package com.rocket.summer.framework.boot.web.support;

import javax.servlet.ServletContext;

import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.ApplicationContextInitializer;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.web.context.ConfigurableWebApplicationContext;
import com.rocket.summer.framework.web.context.WebApplicationContext;

/**
 * {@link ApplicationContextInitializer} for setting the servlet context.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @since 1.4.0
 */
public class ServletContextApplicationContextInitializer implements
        ApplicationContextInitializer<ConfigurableWebApplicationContext>, Ordered {

    private int order = Ordered.HIGHEST_PRECEDENCE;

    private final ServletContext servletContext;

    private final boolean addApplicationContextAttribute;

    /**
     * Create a new {@link ServletContextApplicationContextInitializer} instance.
     * @param servletContext the servlet that should be ultimately set.
     */
    public ServletContextApplicationContextInitializer(ServletContext servletContext) {
        this(servletContext, false);
    }

    /**
     * Create a new {@link ServletContextApplicationContextInitializer} instance.
     * @param servletContext the servlet that should be ultimately set.
     * @param addApplicationContextAttribute if the {@link ApplicationContext} should be
     * stored as an attribute in the {@link ServletContext}
     * @since 1.3.4
     */
    public ServletContextApplicationContextInitializer(ServletContext servletContext,
                                                       boolean addApplicationContextAttribute) {
        this.servletContext = servletContext;
        this.addApplicationContextAttribute = addApplicationContextAttribute;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public void initialize(ConfigurableWebApplicationContext applicationContext) {
        applicationContext.setServletContext(this.servletContext);
        if (this.addApplicationContextAttribute) {
            this.servletContext.setAttribute(
                    WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                    applicationContext);
        }

    }

}

