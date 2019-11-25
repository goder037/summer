package com.rocket.summer.framework.data.repository.util;

import com.rocket.summer.framework.core.convert.converter.Converter;

/**
 * Simple value object to wrap a nullable delegate. Used to be able to write {@link Converter} implementations that
 * convert {@literal null} into an object of some sort.
 *
 * @author Oliver Gierke
 * @since 1.8
 * @see QueryExecutionConverters
 */
public class NullableWrapper {

    private final Object value;

    /**
     * Creates a new {@link NullableWrapper} for the given value.
     *
     * @param value can be {@literal null}.
     */
    public NullableWrapper(Object value) {
        this.value = value;
    }

    /**
     * Returns the type of the contained value. WIll fall back to {@link Object} in case the value is {@literal null}.
     *
     * @return will never be {@literal null}.
     */
    public Class<?> getValueType() {
        return value == null ? Object.class : value.getClass();
    }

    /**
     * Returns the backing value.
     *
     * @return the value can be {@literal null}.
     */
    public Object getValue() {
        return value;
    }
}

