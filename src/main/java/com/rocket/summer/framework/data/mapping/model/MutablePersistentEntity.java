package com.rocket.summer.framework.data.mapping.model;

import com.rocket.summer.framework.data.mapping.Association;
import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.data.mapping.PersistentPropertyAccessor;

/**
 * Interface capturing mutator methods for {@link PersistentEntity}s.
 *
 * @author Oliver Gierke
 * @author Mark Paluch
 */
public interface MutablePersistentEntity<T, P extends PersistentProperty<P>> extends PersistentEntity<T, P> {

    /**
     * Adds a {@link PersistentProperty} to the entity.
     *
     * @param property
     */
    void addPersistentProperty(P property);

    /**
     * Adds an {@link Association} to the entity.
     *
     * @param association
     */
    void addAssociation(Association<P> association);

    /**
     * Callback method to trigger validation of the {@link PersistentEntity}. As {@link MutablePersistentEntity} is not
     * immutable there might be some verification steps necessary after the object has reached is final state.
     *
     * @throws MappingException in case the entity is invalid
     */
    void verify() throws MappingException;

    /**
     * Sets the {@link PersistentPropertyAccessorFactory} for the entity. A {@link PersistentPropertyAccessorFactory}
     * creates {@link PersistentPropertyAccessor}s for instances of this entity.
     *
     * @param factory must not be {@literal null}.
     */
    void setPersistentPropertyAccessorFactory(PersistentPropertyAccessorFactory factory);
}

