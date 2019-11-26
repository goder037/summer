package com.rocket.summer.framework.data.mapping.model;

import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.data.mapping.PreferredConstructor.Parameter;

/**
 * Callback interface to lookup values for a given {@link Parameter}.
 *
 * @author Oliver Gierke
 */
public interface ParameterValueProvider<P extends PersistentProperty<P>> {

    /**
     * Returns the value to be used for the given {@link Parameter} (usually when entity instances are created).
     *
     * @param parameter must not be {@literal null}.
     * @return
     */
    <T> T getParameterValue(Parameter<T, P> parameter);
}
