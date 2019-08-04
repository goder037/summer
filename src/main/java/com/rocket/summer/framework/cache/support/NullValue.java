package com.rocket.summer.framework.cache.support;

import java.io.Serializable;

/**
 * Simple serializable class that serves as a {@code null} replacement
 * for cache stores which otherwise do not support {@code null} values.
 *
 * @author Juergen Hoeller
 * @since 4.2.2
 * @see AbstractValueAdaptingCache
 */
public final class NullValue implements Serializable {

    /**
     * The canonical representation of a {@code null} replacement, as used by the
     * default implementation of {@link AbstractValueAdaptingCache#toStoreValue}/
     * {@link AbstractValueAdaptingCache#fromStoreValue}.
     * @since 4.3.10
     */
    public static final Object INSTANCE = new NullValue();

    private static final long serialVersionUID = 1L;


    private NullValue() {
    }

    private Object readResolve() {
        return INSTANCE;
    }

}
