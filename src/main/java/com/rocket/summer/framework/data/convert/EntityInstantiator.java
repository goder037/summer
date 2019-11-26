package com.rocket.summer.framework.data.convert;

import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.data.mapping.model.ParameterValueProvider;

/**
 * SPI to abstract strategies to create instances for {@link PersistentEntity}s.
 *
 * @author Oliver Gierke
 */
public interface EntityInstantiator {

    /**
     * Creates a new instance of the given entity using the given source to pull data from.
     *
     * @param entity will not be {@literal null}.
     * @param provider will not be {@literal null}.
     * @return
     */
    <T, E extends PersistentEntity<? extends T, P>, P extends PersistentProperty<P>> T createInstance(E entity,
                                                                                                      ParameterValueProvider<P> provider);
}

