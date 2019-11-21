package com.rocket.summer.framework.data.keyvalue.core;

/**
 * Generic callback interface for code that operates on a {@link KeyValueAdapter}. This is particularly useful for
 * delegating code that needs to work closely on the underlying key/value store implementation.
 *
 * @author Christoph Strobl
 * @param <T>
 */
public interface KeyValueCallback<T> {

    /**
     * Gets called by {@code KeyValueTemplate#execute(KeyValueCallback)}. Allows for returning a result object created
     * within the callback, i.e. a domain object or a collection of domain objects.
     *
     * @param adapter
     * @return
     */
    T doInKeyValue(KeyValueAdapter adapter);
}
