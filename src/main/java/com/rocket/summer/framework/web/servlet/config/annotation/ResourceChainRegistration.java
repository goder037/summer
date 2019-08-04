package com.rocket.summer.framework.web.servlet.config.annotation;

import com.rocket.summer.framework.cache.Cache;
import com.rocket.summer.framework.cache.concurrent.ConcurrentMapCache;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.web.servlet.resource.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Assists with the registration of resource resolvers and transformers.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class ResourceChainRegistration {

    private static final String DEFAULT_CACHE_NAME = "spring-resource-chain-cache";

    private final List<ResourceResolver> resolvers = new ArrayList<ResourceResolver>(4);

    private final List<ResourceTransformer> transformers = new ArrayList<ResourceTransformer>(4);

    private boolean hasVersionResolver;

    private boolean hasPathResolver;


    public ResourceChainRegistration(boolean cacheResources) {
        this(cacheResources, cacheResources ? new ConcurrentMapCache(DEFAULT_CACHE_NAME) : null);
    }

    public ResourceChainRegistration(boolean cacheResources, Cache cache) {
        Assert.isTrue(!cacheResources || cache != null, "'cache' is required when cacheResources=true");
        if (cacheResources) {
            this.resolvers.add(new CachingResourceResolver(cache));
            this.transformers.add(new CachingResourceTransformer(cache));
        }
    }


    /**
     * Add a resource resolver to the chain.
     * @param resolver the resolver to add
     * @return the current instance for chained method invocation
     */
    public ResourceChainRegistration addResolver(ResourceResolver resolver) {
        Assert.notNull(resolver, "The provided ResourceResolver should not be null");
        this.resolvers.add(resolver);
        if (resolver instanceof VersionResourceResolver) {
            this.hasVersionResolver = true;
        }
        else if (resolver instanceof PathResourceResolver) {
            this.hasPathResolver = true;
        }
        return this;
    }

    /**
     * Add a resource transformer to the chain.
     * @param transformer the transformer to add
     * @return the current instance for chained method invocation
     */
    public ResourceChainRegistration addTransformer(ResourceTransformer transformer) {
        Assert.notNull(transformer, "The provided ResourceTransformer should not be null");
        this.transformers.add(transformer);
        return this;
    }

    protected List<ResourceResolver> getResourceResolvers() {
        if (!this.hasPathResolver) {
            List<ResourceResolver> result = new ArrayList<ResourceResolver>(this.resolvers);

            result.add(new PathResourceResolver());
            return result;
        }
        return this.resolvers;
    }

    protected List<ResourceTransformer> getResourceTransformers() {
        return this.transformers;
    }

}

