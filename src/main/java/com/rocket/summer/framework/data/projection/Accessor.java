package com.rocket.summer.framework.data.projection;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.util.Assert;

/**
 * Helper value to abstract an accessor.
 *
 * @author Oliver Gierke
 * @soundtrack Benny Greb - Soulfood (Live)
 * @since 1.13
 */
public final class Accessor {

    private final PropertyDescriptor descriptor;
    private final Method method;

    /**
     * Creates an {@link Accessor} for the given {@link Method}.
     *
     * @param method must not be {@literal null}.
     * @throws IllegalArgumentException in case the given method is not an accessor method.
     */
    public Accessor(Method method) {

        Assert.notNull(method, "Method must not be null!");

        this.descriptor = BeanUtils.findPropertyForMethod(method);
        this.method = method;

        Assert.notNull(descriptor, String.format("Invoked method %s is no accessor method!", method));
    }

    /**
     * Returns whether the accessor is a getter.
     *
     * @return
     */
    public boolean isGetter() {
        return method.equals(descriptor.getReadMethod());
    }

    /**
     * Returns whether the accessor is a setter.
     *
     * @return
     */
    public boolean isSetter() {
        return method.equals(descriptor.getWriteMethod());
    }

    /**
     * Returns the name of the property this accessor handles.
     *
     * @return will never be {@literal null}.
     */
    public String getPropertyName() {
        return descriptor.getName();
    }
}

