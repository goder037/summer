package com.rocket.summer.framework.data.mapping;

/**
 * Domain service to allow accessing and setting {@link PersistentProperty}s of an entity. Usually obtained through
 * {@link PersistentEntity#getPropertyAccessor(Object)}. In case type conversion shall be applied on property access,
 * use a {@link ConvertingPropertyAccessor}.
 *
 * @author Oliver Gierke
 * @since 1.10
 * @see PersistentEntity#getPropertyAccessor(Object)
 * @see ConvertingPropertyAccessor
 */
public interface PersistentPropertyAccessor {

    /**
     * Sets the given {@link PersistentProperty} to the given value. Will do type conversion if a
     * {@link com.rocket.summer.framework.core.convert.ConversionService} is configured.
     *
     * @param property must not be {@literal null}.
     * @param value can be {@literal null}.
     * @throws com.rocket.summer.framework.data.mapping.model.MappingException in case an exception occurred when setting the
     *           property value.
     */
    void setProperty(PersistentProperty<?> property, Object value);

    /**
     * Returns the value of the given {@link PersistentProperty} of the underlying bean instance.
     *
     * @param <S>
     * @param property must not be {@literal null}.
     * @return can be {@literal null}.
     */
    Object getProperty(PersistentProperty<?> property);

    /**
     * Returns the underlying bean.
     *
     * @return will never be {@literal null}.
     */
    Object getBean();
}

