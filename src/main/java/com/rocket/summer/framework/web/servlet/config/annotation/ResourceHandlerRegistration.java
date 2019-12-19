package com.rocket.summer.framework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rocket.summer.framework.cache.Cache;
import com.rocket.summer.framework.http.CacheControl;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.servlet.resource.PathResourceResolver;
import com.rocket.summer.framework.web.servlet.resource.ResourceHttpRequestHandler;

/**
 * Encapsulates information required to create a resource handler.
 *
 * @author Rossen Stoyanchev
 * @author Keith Donald
 * @author Brian Clozel
 * @since 3.1
 */
public class ResourceHandlerRegistration {

    private final String[] pathPatterns;

    private final List<String> locationValues = new ArrayList<String>();

    private Integer cachePeriod;

    private CacheControl cacheControl;

    private ResourceChainRegistration resourceChainRegistration;


    /**
     * Create a {@link ResourceHandlerRegistration} instance.
     * @param pathPatterns one or more resource URL path patterns
     */
    public ResourceHandlerRegistration(String... pathPatterns) {
        Assert.notEmpty(pathPatterns, "At least one path pattern is required for resource handling.");
        this.pathPatterns = pathPatterns;
    }


    /**
     * Add one or more resource locations from which to serve static content.
     * Each location must point to a valid directory. Multiple locations may
     * be specified as a comma-separated list, and the locations will be checked
     * for a given resource in the order specified.
     * <p>For example, {{@code "/"}, {@code "classpath:/META-INF/public-web-resources/"}}
     * allows resources to be served both from the web application root and
     * from any JAR on the classpath that contains a
     * {@code /META-INF/public-web-resources/} directory, with resources in the
     * web application root taking precedence.
     * <p>For {@link com.rocket.summer.framework.core.io.UrlResource URL-based resources}
     * (e.g. files, HTTP URLs, etc) this method supports a special prefix to
     * indicate the charset associated with the URL so that relative paths
     * appended to it can be encoded correctly, e.g.
     * {@code [charset=Windows-31J]https://example.org/path}.
     * @return the same {@link ResourceHandlerRegistration} instance, for
     * chained method invocation
     */
    public ResourceHandlerRegistration addResourceLocations(String... resourceLocations) {
        this.locationValues.addAll(Arrays.asList(resourceLocations));
        return this;
    }

    /**
     * Specify the cache period for the resources served by the resource handler, in seconds. The default is to not
     * send any cache headers but to rely on last-modified timestamps only. Set to 0 in order to send cache headers
     * that prevent caching, or to a positive number of seconds to send cache headers with the given max-age value.
     * @param cachePeriod the time to cache resources in seconds
     * @return the same {@link ResourceHandlerRegistration} instance, for chained method invocation
     */
    public ResourceHandlerRegistration setCachePeriod(Integer cachePeriod) {
        this.cachePeriod = cachePeriod;
        return this;
    }

    /**
     * Specify the {@link com.rocket.summer.framework.http.CacheControl} which should be used
     * by the resource handler.
     * <p>Setting a custom value here will override the configuration set with {@link #setCachePeriod}.
     * @param cacheControl the CacheControl configuration to use
     * @return the same {@link ResourceHandlerRegistration} instance, for chained method invocation
     * @since 4.2
     */
    public ResourceHandlerRegistration setCacheControl(CacheControl cacheControl) {
        this.cacheControl = cacheControl;
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
     * Configure a chain of resource resolvers and transformers to use. This
     * can be useful, for example, to apply a version strategy to resource URLs.
     * <p>If this method is not invoked, by default only a simple
     * {@link PathResourceResolver} is used in order to match URL paths to
     * resources under the configured locations.
     * @param cacheResources whether to cache the result of resource resolution;
     * setting this to "true" is recommended for production (and "false" for
     * development, especially when applying a version strategy
     * @param cache the cache to use for storing resolved and transformed resources;
     * by default a {@link com.rocket.summer.framework.cache.concurrent.ConcurrentMapCache}
     * is used. Since Resources aren't serializable and can be dependent on the
     * application host, one should not use a distributed cache but rather an
     * in-memory cache.
     * @return the same {@link ResourceHandlerRegistration} instance, for chained method invocation
     * @since 4.1
     */
    public ResourceChainRegistration resourceChain(boolean cacheResources, Cache cache) {
        this.resourceChainRegistration = new ResourceChainRegistration(cacheResources, cache);
        return this.resourceChainRegistration;
    }


    /**
     * Return the URL path patterns for the resource handler.
     */
    protected String[] getPathPatterns() {
        return this.pathPatterns;
    }

    /**
     * Return a {@link ResourceHttpRequestHandler} instance.
     */
    protected ResourceHttpRequestHandler getRequestHandler() {
        ResourceHttpRequestHandler handler = new ResourceHttpRequestHandler();
        if (this.resourceChainRegistration != null) {
            handler.setResourceResolvers(this.resourceChainRegistration.getResourceResolvers());
            handler.setResourceTransformers(this.resourceChainRegistration.getResourceTransformers());
        }
        handler.setLocationValues(this.locationValues);
        if (this.cacheControl != null) {
            handler.setCacheControl(this.cacheControl);
        }
        else if (this.cachePeriod != null) {
            handler.setCacheSeconds(this.cachePeriod);
        }
        return handler;
    }

}
