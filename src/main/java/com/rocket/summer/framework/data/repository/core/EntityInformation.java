package com.rocket.summer.framework.data.repository.core;

import java.io.Serializable;

/**
 * Extension of {@link EntityMetadata} to add functionality to query information of entity instances.
 *
 * @author Oliver Gierke
 */
public interface EntityInformation<T, ID extends Serializable> extends EntityMetadata<T> {

    /**
     * Returns whether the given entity is considered to be new.
     *
     * @param entity must never be {@literal null}
     * @return
     */
    boolean isNew(T entity);

    /**
     * Returns the id of the given entity.
     *
     * @param entity must never be {@literal null}
     * @return
     */
    ID getId(T entity);

    /**
     * Returns the type of the id of the entity.
     *
     * @return
     */
    Class<ID> getIdType();
}

