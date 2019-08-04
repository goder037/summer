package com.rocket.summer.framework.cache.support;

import com.rocket.summer.framework.cache.Cache;

/**
 * Straightforward implementation of {@link com.rocket.summer.framework.cache.Cache.ValueWrapper},
 * simply holding the value as given at construction and returning it from {@link #get()}.
 *
 * @author Costin Leau
 * @since 3.1
 */
public class SimpleValueWrapper implements Cache.ValueWrapper {

    private final Object value;


    /**
     * Create a new SimpleValueWrapper instance for exposing the given value.
     * @param value the value to expose (may be {@code null})
     */
    public SimpleValueWrapper(Object value) {
        this.value = value;
    }


    /**
     * Simply returns the value as given at construction time.
     */
    @Override
    public Object get() {
        return this.value;
    }

}
