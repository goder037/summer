package com.rocket.summer.framework.boot.context.embedded;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.context.ConfigurableWebApplicationContext;
import com.rocket.summer.framework.web.context.support.ServletContextAwareProcessor;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * Variant of {@link ServletContextAwareProcessor} for use with a
 * {@link ConfigurableWebApplicationContext}. Can be used when registering the processor
 * can occur before the {@link ServletContext} or {@link ServletConfig} have been
 * initialized.
 *
 * @author Phillip Webb
 */
public class WebApplicationContextServletContextAwareProcessor
        extends ServletContextAwareProcessor {

    private final ConfigurableWebApplicationContext webApplicationContext;

    public WebApplicationContextServletContextAwareProcessor(
            ConfigurableWebApplicationContext webApplicationContext) {
        Assert.notNull(webApplicationContext, "WebApplicationContext must not be null");
        this.webApplicationContext = webApplicationContext;
    }

    @Override
    protected ServletContext getServletContext() {
        ServletContext servletContext = this.webApplicationContext.getServletContext();
        return (servletContext != null) ? servletContext : super.getServletContext();
    }

    @Override
    protected ServletConfig getServletConfig() {
        ServletConfig servletConfig = this.webApplicationContext.getServletConfig();
        return (servletConfig != null) ? servletConfig : super.getServletConfig();
    }

}
