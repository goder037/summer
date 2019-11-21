package com.rocket.summer.framework.data.mapping;

/**
 * A property handler to work with untyped {@link PersistentProperty} instances.
 *
 * @author Oliver Gierke
 */
public interface SimplePropertyHandler {

    /**
     * Handle the given {@link PersistentProperty}.
     *
     * @param property will never be {@literal null}.
     */
    void doWithPersistentProperty(PersistentProperty<?> property);
}

