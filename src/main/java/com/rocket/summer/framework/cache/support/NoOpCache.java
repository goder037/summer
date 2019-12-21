package com.rocket.summer.framework.cache.support;

import java.util.concurrent.Callable;

import com.rocket.summer.framework.cache.Cache;
import com.rocket.summer.framework.util.Assert;

/**
 * A no operation {@link Cache} implementation suitable for disabling caching.
 *
 * <p>Will simply accept any items into the cache not actually storing them.
 *
 * @author Costin Leau
 * @author Stephane Nicoll
 * @since 4.3.4
 */
public class NoOpCache implements Cache {

    private final String name;


    /**
     * Create a {@link NoOpCache} instance with the specified name
     * @param name the name of the cache
     */
    public NoOpCache(String name) {
        Assert.notNull(name, "Cache name must not be null");
        this.name = name;
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return null;
    }

    @Override
    public ValueWrapper get(Object key) {
        return null;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        try {
            return valueLoader.call();
        }
        catch (Exception ex) {
            throw new ValueRetrievalException(key, valueLoader, ex);
        }
    }

    @Override
    public void put(Object key, Object value) {
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return null;
    }

    @Override
    public void evict(Object key) {
    }

    @Override
    public void clear() {
    }

}

