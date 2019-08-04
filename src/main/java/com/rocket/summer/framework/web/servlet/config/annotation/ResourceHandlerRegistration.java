package com.rocket.summer.framework.web.servlet.config.annotation;

import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.CollectionUtils;
import com.rocket.summer.framework.web.servlet.resource.PathResourceResolver;
import com.rocket.summer.framework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates information required to create a resource handlers.
 *
 * @author Rossen Stoyanchev
 * @author Keith Donald
 *
 * @since 3.1
 */
public class ResourceHandlerRegistration {

    private final ResourceLoader resourceLoader;

    private final String[] pathPatterns;

    private final List<Resource> locations = new ArrayList<Resource>();

    private Integer cachePeriod;

    private ResourceChainRegistration resourceChainRegistration;

    /**
     * Create a {@link ResourceHandlerRegistration} instance.
     * @param resourceLoader a resource loader for turning a String location into a {@link Resource}
     * @param pathPatterns one or more resource URL path patterns
     */
    public ResourceHandlerRegistration(ResourceLoader resourceLoader, String... pathPatterns) {
        Assert.notEmpty(pathPatterns, "At least one path pattern is required for resource handling.");
        this.resourceLoader = resourceLoader;
        this.pathPatterns = pathPatterns;
    }

    /**
     * Add one or more resource locations from which to serve static content. Each location must point to a valid
     * directory. Multiple locations may be specified as a comma-separated list, and the locations will be checked
     * for a given resource in the order specified.
     * <p>For example, {{@code "/"}, {@code "classpath:/META-INF/public-web-resources/"}} allows resources to
     * be served both from the web application root and from any JAR on the classpath that contains a
     * {@code /META-INF/public-web-resources/} directory, with resources in the web application root taking precedence.
     * @return the same {@link ResourceHandlerRegistration} instance for chained method invocation
     */
    public ResourceHandlerRegistration addResourceLocations(String...resourceLocations) {
        for (String location : resourceLocations) {
            this.locations.add(resourceLoader.getResource(location));
        }
        return this;
    }

    /**
     * Configure a chain of resource resolvers and transformers to use. This
     * can be useful, for example, to apply a version strategy to resource URLs.
     * <p>If this method is not invoked, by default only a simple
     * {@link PathResourceResolver} is used in order to match URL paths to
     * resources under the configured locations.
     * @param cacheResources whether to cache the result of resource resolution;
     * setting this to "true" is recommended for production (and "false" for
     * development, especially when applying a version strategy)
     * @return the same {@link ResourceHandlerRegistration} instance, for chained method invocation
     * @since 4.1
     */
    public ResourceChainRegistration resourceChain(boolean cacheResources) {
        this.resourceChainRegistration = new ResourceChainRegistration(cacheResources);
        return this.resourceChainRegistration;
    }

    /**
     * Specify the cache period for the resources served by the resource handler, in seconds. The default is to not
     * send any cache headers but to rely on last-modified timestamps only. Set to 0 in order to send cache headers
     * that prevent caching, or to a positive number of seconds to send cache headers with the given max-age value.
     * @param cachePeriod the time to cache resources in seconds
     * @return the same {@link ResourceHandlerRegistration} instance for chained method invocation
     */
    public ResourceHandlerRegistration setCachePeriod(Integer cachePeriod) {
        this.cachePeriod = cachePeriod;
        return this;
    }

    /**
     * Returns the URL path patterns for the resource handler.
     */
    protected String[] getPathPatterns() {
        return pathPatterns;
    }

    /**
     * Returns a {@link ResourceHttpRequestHandler} instance.
     */
    protected ResourceHttpRequestHandler getRequestHandler() {
        Assert.isTrue(!CollectionUtils.isEmpty(locations), "At least one location is required for resource handling.");
        ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
        requestHandler.setLocations(locations);
        if (cachePeriod != null) {
            requestHandler.setCacheSeconds(cachePeriod);
        }
        return requestHandler;
    }

}


