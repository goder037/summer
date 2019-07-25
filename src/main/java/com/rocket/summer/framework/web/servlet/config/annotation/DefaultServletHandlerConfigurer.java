package com.rocket.summer.framework.web.servlet.config.annotation;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.HttpRequestHandler;
import com.rocket.summer.framework.web.servlet.DispatcherServlet;
import com.rocket.summer.framework.web.servlet.handler.AbstractHandlerMapping;
import com.rocket.summer.framework.web.servlet.handler.SimpleUrlHandlerMapping;
import com.rocket.summer.framework.web.servlet.resource.DefaultServletHttpRequestHandler;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Configures a request handler for serving static resources by forwarding the request to the Servlet container's
 * "default" Servlet. This is indended to be used when the Spring MVC {@link DispatcherServlet} is mapped to "/"
 * thus overriding the Servlet container's default handling of static resources. Since this handler is configured
 * at the lowest precedence, effectively it allows all other handler mappings to handle the request, and if none
 * of them do, this handler can forward it to the "default" Servlet.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 *
 * @see DefaultServletHttpRequestHandler
 */
public class DefaultServletHandlerConfigurer {

    private final ServletContext servletContext;

    private DefaultServletHttpRequestHandler handler;

    /**
     * Create a {@link DefaultServletHandlerConfigurer} instance.
     * @param servletContext the ServletContext to use to configure the underlying DefaultServletHttpRequestHandler.
     */
    public DefaultServletHandlerConfigurer(ServletContext servletContext) {
        Assert.notNull(servletContext, "A ServletContext is required to configure default servlet handling");
        this.servletContext = servletContext;
    }

    /**
     * Enable forwarding to the "default" Servlet. When this method is used the {@link DefaultServletHttpRequestHandler}
     * will try to auto-detect the "default" Servlet name. Alternatively, you can specify the name of the default
     * Servlet via {@link #enable(String)}.
     * @see DefaultServletHttpRequestHandler
     */
    public void enable() {
        enable(null);
    }

    /**
     * Enable forwarding to the "default" Servlet identified by the given name.
     * This is useful when the default Servlet cannot be auto-detected, for example when it has been manually configured.
     * @see DefaultServletHttpRequestHandler
     */
    public void enable(String defaultServletName) {
        handler = new DefaultServletHttpRequestHandler();
        handler.setDefaultServletName(defaultServletName);
        handler.setServletContext(servletContext);
    }

    /**
     * Return a handler mapping instance ordered at {@link Integer#MAX_VALUE} containing the
     * {@link DefaultServletHttpRequestHandler} instance mapped to {@code "/**"}; or {@code null} if
     * default servlet handling was not been enabled.
     */
    protected AbstractHandlerMapping getHandlerMapping() {
        if (handler == null) {
            return null;
        }

        Map<String, HttpRequestHandler> urlMap = new HashMap<String, HttpRequestHandler>();
        urlMap.put("/**", handler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(Integer.MAX_VALUE);
        handlerMapping.setUrlMap(urlMap);
        return handlerMapping;
    }

}
