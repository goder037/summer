package com.rocket.summer.framework.data.domain;

import java.io.Serializable;

/**
 * Simple interface for entities.
 *
 * @param <ID> the type of the identifier
 * @author Oliver Gierke
 */
public interface Persistable<ID extends Serializable> extends Serializable {

    /**
     * Returns the id of the entity.
     *
     * @return the id
     */
    ID getId();

    /**
     * Returns if the {@code Persistable} is new or was persisted already.
     *
     * @return if the object is new
     */
    boolean isNew();
}

