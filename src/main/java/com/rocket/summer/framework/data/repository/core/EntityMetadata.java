package com.rocket.summer.framework.data.repository.core;

/**
 * Metadata for entity types.
 *
 * @author Oliver Gierke
 */
public interface EntityMetadata<T> {

    /**
     * Returns the actual domain class type.
     *
     * @return
     */
    Class<T> getJavaType();
}

