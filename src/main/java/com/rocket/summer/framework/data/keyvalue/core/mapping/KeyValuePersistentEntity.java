package com.rocket.summer.framework.data.keyvalue.core.mapping;

import com.rocket.summer.framework.data.mapping.model.MutablePersistentEntity;

/**
 * @author Christoph Strobl
 * @param <T>
 */
public interface KeyValuePersistentEntity<T> extends MutablePersistentEntity<T, KeyValuePersistentProperty> {

    /**
     * Get the {@literal keySpace} a given entity assigns to.
     *
     * @return never {@literal null}.
     */
    String getKeySpace();
}

