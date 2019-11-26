package com.rocket.summer.framework.data.mapping.model;

import com.rocket.summer.framework.data.mapping.PersistentProperty;

/**
 * SPI for components to provide values for as {@link PersistentProperty}.
 *
 * @author Oliver Gierke
 */
public interface PropertyValueProvider<P extends PersistentProperty<P>> {

    /**
     * Returns a value for the given {@link PersistentProperty}.
     *
     * @param property will never be {@literal null}.
     * @return
     */
    <T> T getPropertyValue(P property);
}

