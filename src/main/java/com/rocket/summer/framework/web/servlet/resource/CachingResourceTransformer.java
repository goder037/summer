package com.rocket.summer.framework.web.servlet.resource;

import com.rocket.summer.framework.cache.Cache;
import com.rocket.summer.framework.cache.CacheManager;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.util.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * A {@link com.rocket.summer.framework.web.servlet.resource.ResourceTransformer} that checks a
 * {@link com.rocket.summer.framework.cache.Cache} to see if a previously transformed resource
 * exists in the cache and returns it if found, and otherwise delegates to the resolver
 * chain and saves the result in the cache.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class CachingResourceTransformer implements ResourceTransformer {

    private static final Log logger = LogFactory.getLog(CachingResourceTransformer.class);

    private final Cache cache;


    public CachingResourceTransformer(CacheManager cacheManager, String cacheName) {
        this(cacheManager.getCache(cacheName));
    }

    public CachingResourceTransformer(Cache cache) {
        Assert.notNull(cache, "Cache is required");
        this.cache = cache;
    }


    /**
     * Return the configured {@code Cache}.
     */
    public Cache getCache() {
        return this.cache;
    }


    @Override
    public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain)
            throws IOException {

        Resource transformed = this.cache.get(resource, Resource.class);
        if (transformed != null) {
            if (logger.isTraceEnabled()) {
                logger.trace("Found match: " + transformed);
            }
            return transformed;
        }

        transformed = transformerChain.transform(request, resource);

        if (logger.isTraceEnabled()) {
            logger.trace("Putting transformed resource in cache: " + transformed);
        }
        this.cache.put(resource, transformed);

        return transformed;
    }

}

