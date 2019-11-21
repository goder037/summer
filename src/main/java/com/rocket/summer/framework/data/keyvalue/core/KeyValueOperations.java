package com.rocket.summer.framework.data.keyvalue.core;

import java.io.Serializable;

import com.rocket.summer.framework.beans.factory.DisposableBean;
import com.rocket.summer.framework.data.domain.Sort;
import com.rocket.summer.framework.data.keyvalue.annotation.KeySpace;
import com.rocket.summer.framework.data.keyvalue.core.query.KeyValueQuery;
import com.rocket.summer.framework.data.mapping.context.MappingContext;

/**
 * Interface that specifies a basic set of key/value operations. Implemented by {@link KeyValueTemplate}.
 *
 * @author Christoph Strobl
 */
public interface KeyValueOperations extends DisposableBean {

    /**
     * Add given object. Object needs to have id property to which a generated value will be assigned.
     *
     * @param objectToInsert
     * @return
     */
    <T> T insert(T objectToInsert);

    /**
     * Add object with given id.
     *
     * @param id must not be {@literal null}.
     * @param objectToInsert must not be {@literal null}.
     */
    void insert(Serializable id, Object objectToInsert);

    /**
     * Get all elements of given type. Respects {@link KeySpace} if present and therefore returns all elements that can be
     * assigned to requested type.
     *
     * @param type must not be {@literal null}.
     * @return empty iterable if no elements found.
     */
    <T> Iterable<T> findAll(Class<T> type);

    /**
     * Get all elements ordered by sort. Respects {@link KeySpace} if present and therefore returns all elements that can
     * be assigned to requested type.
     *
     * @param sort must not be {@literal null}.
     * @param type must not be {@literal null}.
     * @return
     */
    <T> Iterable<T> findAll(Sort sort, Class<T> type);

    /**
     * Get element of given type with given id. Respects {@link KeySpace} if present and therefore returns all elements
     * that can be assigned to requested type.
     *
     * @param id must not be {@literal null}.
     * @param type must not be {@literal null}.
     * @return null if not found.
     */
    <T> T findById(Serializable id, Class<T> type);

    /**
     * Execute operation against underlying store.
     *
     * @param action must not be {@literal null}.
     * @return
     */
    <T> T execute(KeyValueCallback<T> action);

    /**
     * Get all elements matching the given query. <br />
     * Respects {@link KeySpace} if present and therefore returns all elements that can be assigned to requested type..
     *
     * @param query must not be {@literal null}.
     * @param type must not be {@literal null}.
     * @return empty iterable if no match found.
     */
    <T> Iterable<T> find(KeyValueQuery<?> query, Class<T> type);

    /**
     * Get all elements in given range. Respects {@link KeySpace} if present and therefore returns all elements that can
     * be assigned to requested type.
     *
     * @param offset
     * @param rows
     * @param type must not be {@literal null}.
     * @return
     */
    <T> Iterable<T> findInRange(int offset, int rows, Class<T> type);

    /**
     * Get all elements in given range ordered by sort. Respects {@link KeySpace} if present and therefore returns all
     * elements that can be assigned to requested type.
     *
     * @param offset
     * @param rows
     * @param sort
     * @param type
     * @return
     */
    <T> Iterable<T> findInRange(int offset, int rows, Sort sort, Class<T> type);

    /**
     * @param objectToUpdate must not be {@literal null}.
     */
    void update(Object objectToUpdate);

    /**
     * @param id must not be {@literal null}.
     * @param objectToUpdate must not be {@literal null}.
     */
    void update(Serializable id, Object objectToUpdate);

    /**
     * Remove all elements of type. Respects {@link KeySpace} if present and therefore removes all elements that can be
     * assigned to requested type.
     *
     * @param type must not be {@literal null}.
     */
    void delete(Class<?> type);

    /**
     * @param objectToDelete must not be {@literal null}.
     * @return
     */
    <T> T delete(T objectToDelete);

    /**
     * Delete item of type with given id.
     *
     * @param id must not be {@literal null}.
     * @param type must not be {@literal null}.
     * @return the deleted item or {@literal null} if no match found.
     */
    <T> T delete(Serializable id, Class<T> type);

    /**
     * Total number of elements with given type available. Respects {@link KeySpace} if present and therefore counts all
     * elements that can be assigned to requested type.
     *
     * @param type must not be {@literal null}.
     * @return
     */
    long count(Class<?> type);

    /**
     * Total number of elements matching given query. Respects {@link KeySpace} if present and therefore counts all
     * elements that can be assigned to requested type.
     *
     * @param query
     * @param type
     * @return
     */
    long count(KeyValueQuery<?> query, Class<?> type);

    /**
     * @return mapping context in use.
     */
    MappingContext<?, ?> getMappingContext();
}

