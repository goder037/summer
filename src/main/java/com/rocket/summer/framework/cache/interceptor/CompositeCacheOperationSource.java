package com.rocket.summer.framework.cache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import com.rocket.summer.framework.util.Assert;

/**
 * Composite {@link CacheOperationSource} implementation that iterates
 * over a given array of {@code CacheOperationSource} instances.
 *
 * @author Costin Leau
 * @author Juergen Hoeller
 * @since 3.1
 */
@SuppressWarnings("serial")
public class CompositeCacheOperationSource implements CacheOperationSource, Serializable {

    private final CacheOperationSource[] cacheOperationSources;


    /**
     * Create a new CompositeCacheOperationSource for the given sources.
     * @param cacheOperationSources the CacheOperationSource instances to combine
     */
    public CompositeCacheOperationSource(CacheOperationSource... cacheOperationSources) {
        Assert.notEmpty(cacheOperationSources, "CacheOperationSource array must not be empty");
        this.cacheOperationSources = cacheOperationSources;
    }

    /**
     * Return the {@code CacheOperationSource} instances that this
     * {@code CompositeCacheOperationSource} combines.
     */
    public final CacheOperationSource[] getCacheOperationSources() {
        return this.cacheOperationSources;
    }


    @Override
    public Collection<CacheOperation> getCacheOperations(Method method, Class<?> targetClass) {
        Collection<CacheOperation> ops = null;
        for (CacheOperationSource source : this.cacheOperationSources) {
            Collection<CacheOperation> cacheOperations = source.getCacheOperations(method, targetClass);
            if (cacheOperations != null) {
                if (ops == null) {
                    ops = new ArrayList<CacheOperation>();
                }
                ops.addAll(cacheOperations);
            }
        }
        return ops;
    }

}

