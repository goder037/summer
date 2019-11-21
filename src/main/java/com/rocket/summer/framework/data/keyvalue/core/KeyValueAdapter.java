package com.rocket.summer.framework.data.keyvalue.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import com.rocket.summer.framework.beans.factory.DisposableBean;
import com.rocket.summer.framework.data.keyvalue.core.query.KeyValueQuery;
import com.rocket.summer.framework.data.util.CloseableIterator;

/**
 * {@link KeyValueAdapter} unifies access and shields the underlying key/value specific implementation.
 *
 * @author Christoph Strobl
 * @author Thomas Darimont
 */
public interface KeyValueAdapter extends DisposableBean {

    /**
     * Add object with given id to keyspace.
     *
     * @param id must not be {@literal null}.
     * @param keyspace must not be {@literal null}.
     * @return the item previously associated with the id.
     */
    Object put(Serializable id, Object item, Serializable keyspace);

    /**
     * Check if a object with given id exists in keyspace.
     *
     * @param id must not be {@literal null}.
     * @param keyspace must not be {@literal null}.
     * @return true if item of type with id exists.
     */
    boolean contains(Serializable id, Serializable keyspace);

    /**
     * Get the object with given id from keyspace.
     *
     * @param id must not be {@literal null}.
     * @param keyspace must not be {@literal null}.
     * @return {@literal null} in case no matching item exists.
     */
    Object get(Serializable id, Serializable keyspace);

    /**
     * @param id
     * @param keyspace
     * @param type
     * @return
     * @since 1.1
     */
    <T> T get(Serializable id, Serializable keyspace, Class<T> type);

    /**
     * Delete and return the obect with given type and id.
     *
     * @param id must not be {@literal null}.
     * @param keyspace must not be {@literal null}.
     * @return {@literal null} if object could not be found
     */
    Object delete(Serializable id, Serializable keyspace);

    /**
     * @param id
     * @param keyspace
     * @param type
     * @return
     * @since 1.1
     */
    <T> T delete(Serializable id, Serializable keyspace, Class<T> type);

    /**
     * Get all elements for given keyspace.
     *
     * @param keyspace must not be {@literal null}.
     * @return empty {@link Collection} if nothing found.
     */
    Iterable<?> getAllOf(Serializable keyspace);

    /**
     * Returns a {@link CloseableIterator} that iterates over all entries.
     *
     * @param keyspace
     * @return
     */
    CloseableIterator<Map.Entry<Serializable, Object>> entries(Serializable keyspace);

    /**
     * Remove all objects of given type.
     *
     * @param keyspace must not be {@literal null}.
     */
    void deleteAllOf(Serializable keyspace);

    /**
     * Removes all objects.
     */
    void clear();

    /**
     * Find all matching objects within {@literal keyspace}.
     *
     * @param query
     * @param keyspace must not be {@literal null}.
     * @return empty {@link Collection} if no match found.
     */
    Iterable<?> find(KeyValueQuery<?> query, Serializable keyspace);

    /**
     * @param query
     * @param keyspace
     * @param type
     * @return
     * @since 1.1
     */
    <T> Iterable<T> find(KeyValueQuery<?> query, Serializable keyspace, Class<T> type);

    /**
     * Count number of objects within {@literal keyspace}.
     *
     * @param keyspace must not be {@literal null}.
     */
    long count(Serializable keyspace);

    /**
     * Count all matching objects within {@literal keyspace}.
     *
     * @param query
     * @param keyspace must not be {@literal null}.
     * @return
     */
    long count(KeyValueQuery<?> query, Serializable keyspace);
}

