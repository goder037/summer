package com.rocket.summer.framework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;

import com.rocket.summer.framework.beans.factory.BeanInitializationException;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.HttpRequestHandler;
import com.rocket.summer.framework.web.accept.ContentNegotiationManager;
import com.rocket.summer.framework.web.servlet.HandlerMapping;
import com.rocket.summer.framework.web.servlet.handler.AbstractHandlerMapping;
import com.rocket.summer.framework.web.servlet.handler.SimpleUrlHandlerMapping;
import com.rocket.summer.framework.web.servlet.resource.ResourceHttpRequestHandler;
import com.rocket.summer.framework.web.util.UrlPathHelper;

/**
 * Stores registrations of resource handlers for serving static resources such as images, css files and others
 * through Spring MVC including setting cache headers optimized for efficient loading in a web browser.
 * Resources can be served out of locations under web application root, from the classpath, and others.
 *
 * <p>To create a resource handler, use {@link #addResourceHandler(String...)} providing the URL path patterns
 * for which the handler should be invoked to serve static resources (e.g. {@code "/resources/**"}).
 *
 * <p>Then use additional methods on the returned {@link ResourceHandlerRegistration} to add one or more
 * locations from which to serve static content from (e.g. {{@code "/"},
 * {@code "classpath:/META-INF/public-web-resources/"}}) or to specify a cache period for served resources.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 * @see DefaultServletHandlerConfigurer
 */
public class ResourceHandlerRegistry {

    private final ServletContext servletContext;

    private final ApplicationContext applicationContext;

    private final ContentNegotiationManager contentNegotiationManager;

    private final UrlPathHelper pathHelper;

    private final List<ResourceHandlerRegistration> registrations = new ArrayList<ResourceHandlerRegistration>();

    private int order = Ordered.LOWEST_PRECEDENCE - 1;


    /**
     * Create a new resource handler registry for the given application context.
     * @param applicationContext the Spring application context
     * @param servletContext the corresponding Servlet context
     */
    public ResourceHandlerRegistry(ApplicationContext applicationContext, ServletContext servletContext) {
        this(applicationContext, servletContext, null);
    }

    /**
     * Create a new resource handler registry for the given application context.
     * @param applicationContext the Spring application context
     * @param servletContext the corresponding Servlet context
     * @param contentNegotiationManager the content negotiation manager to use
     * @since 4.3
     */
    public ResourceHandlerRegistry(ApplicationContext applicationContext, ServletContext servletContext,
                                   ContentNegotiationManager contentNegotiationManager) {

        this(applicationContext, servletContext, contentNegotiationManager, null);
    }

    /**
     * A variant of
     * {@link #ResourceHandlerRegistry(ApplicationContext, ServletContext, ContentNegotiationManager)}
     * that also accepts the {@link UrlPathHelper} used for mapping requests to static resources.
     * @since 4.3.13
     */
    public ResourceHandlerRegistry(ApplicationContext applicationContext, ServletContext servletContext,
                                   ContentNegotiationManager contentNegotiationManager, UrlPathHelper pathHelper) {

        Assert.notNull(applicationContext, "ApplicationContext is required");
        this.applicationContext = applicationContext;
        this.servletContext = servletContext;
        this.contentNegotiationManager = contentNegotiationManager;
        this.pathHelper = pathHelper;
    }


    /**
     * Add a resource handler for serving static resources based on the specified URL path patterns.
     * The handler will be invoked for every incoming request that matches to one of the specified
     * path patterns.
     * <p>Patterns like {@code "/static/**"} or {@code "/css/{filename:\\w+\\.css}"} are allowed.
     * See {@link com.rocket.summer.framework.util.AntPathMatcher} for more details on the syntax.
     * @return a {@link ResourceHandlerRegistration} to use to further configure the
     * registered resource handler
     */
    public ResourceHandlerRegistration addResourceHandler(String... pathPatterns) {
        ResourceHandlerRegistration registration = new ResourceHandlerRegistration(pathPatterns);
        this.registrations.add(registration);
        return registration;
    }

    /**
     * Whether a resource handler has already been registered for the given path pattern.
     */
    public boolean hasMappingForPattern(String pathPattern) {
        for (ResourceHandlerRegistration registration : this.registrations) {
            if (Arrays.asList(registration.getPathPatterns()).contains(pathPattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Specify the order to use for resource handling relative to other {@link HandlerMapping}s
     * configured in the Spring MVC application context.
     * <p>The default value used is {@code Integer.MAX_VALUE-1}.
     */
    public ResourceHandlerRegistry setOrder(int order) {
        this.order = order;
        return this;
    }

    /**
     * Return a handler mapping with the mapped resource handlers; or {@code null} in case
     * of no registrations.
     */
    protected AbstractHandlerMapping getHandlerMapping() {
        if (this.registrations.isEmpty()) {
            return null;
        }

        Map<String, HttpRequestHandler> urlMap = new LinkedHashMap<String, HttpRequestHandler>();
        for (ResourceHandlerRegistration registration : this.registrations) {
            for (String pathPattern : registration.getPathPatterns()) {
                ResourceHttpRequestHandler handler = registration.getRequestHandler();
                if (this.pathHelper != null) {
                    handler.setUrlPathHelper(this.pathHelper);
                }
                if (this.contentNegotiationManager != null) {
                    handler.setContentNegotiationManager(this.contentNegotiationManager);
                }
                handler.setServletContext(this.servletContext);
                handler.setApplicationContext(this.applicationContext);
                try {
                    handler.afterPropertiesSet();
                }
                catch (Throwable ex) {
                    throw new BeanInitializationException("Failed to init ResourceHttpRequestHandler", ex);
                }
                urlMap.put(pathPattern, handler);
            }
        }

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(order);
        handlerMapping.setUrlMap(urlMap);
        return handlerMapping;
    }

}
