package com.rocket.summer.framework.web.servlet.config.annotation;

import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.HttpRequestHandler;
import com.rocket.summer.framework.web.servlet.HandlerMapping;
import com.rocket.summer.framework.web.servlet.handler.AbstractHandlerMapping;
import com.rocket.summer.framework.web.servlet.handler.SimpleUrlHandlerMapping;
import com.rocket.summer.framework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
 *
 * @see DefaultServletHandlerConfigurer
 */
public class ResourceHandlerRegistry {

    private final ServletContext servletContext;

    private final ApplicationContext applicationContext;

    private final List<ResourceHandlerRegistration> registrations = new ArrayList<ResourceHandlerRegistration>();

    private int order = Integer.MAX_VALUE -1;

    public ResourceHandlerRegistry(ApplicationContext applicationContext, ServletContext servletContext) {
        Assert.notNull(applicationContext, "ApplicationContext is required");
        this.applicationContext = applicationContext;
        this.servletContext = servletContext;
    }

    /**
     * Add a resource handler for serving static resources based on the specified URL path patterns.
     * The handler will be invoked for every incoming request that matches to one of the specified path patterns.
     * @return A {@link ResourceHandlerRegistration} to use to further configure the registered resource handler.
     */
    public ResourceHandlerRegistration addResourceHandler(String... pathPatterns) {
        ResourceHandlerRegistration registration = new ResourceHandlerRegistration(applicationContext, pathPatterns);
        registrations.add(registration);
        return registration;
    }

    /**
     * Specify the order to use for resource handling relative to other {@link HandlerMapping}s configured in
     * the Spring MVC application context. The default value used is {@code Integer.MAX_VALUE-1}.
     */
    public ResourceHandlerRegistry setOrder(int order) {
        this.order = order;
        return this;
    }

    /**
     * Return a handler mapping with the mapped resource handlers; or {@code null} in case of no registrations.
     */
    protected AbstractHandlerMapping getHandlerMapping() {
        if (registrations.isEmpty()) {
            return null;
        }

        Map<String, HttpRequestHandler> urlMap = new LinkedHashMap<String, HttpRequestHandler>();
        for (ResourceHandlerRegistration registration : registrations) {
            for (String pathPattern : registration.getPathPatterns()) {
                ResourceHttpRequestHandler requestHandler = registration.getRequestHandler();
                requestHandler.setServletContext(servletContext);
                requestHandler.setApplicationContext(applicationContext);
                urlMap.put(pathPattern, requestHandler);
            }
        }

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(order);
        handlerMapping.setUrlMap(urlMap);
        return handlerMapping;
    }

}
