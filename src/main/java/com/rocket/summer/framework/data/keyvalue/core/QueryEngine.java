package com.rocket.summer.framework.data.keyvalue.core;

import java.io.Serializable;
import java.util.Collection;

import com.rocket.summer.framework.data.keyvalue.core.query.KeyValueQuery;

/**
 * Base implementation for accessing and executing {@link KeyValueQuery} against a {@link KeyValueAdapter}.
 *
 * @author Christoph Strobl
 * @param <ADAPTER>
 * @param <CRITERIA>
 * @param <SORT>
 */
public abstract class QueryEngine<ADAPTER extends KeyValueAdapter, CRITERIA, SORT> {

    private final CriteriaAccessor<CRITERIA> criteriaAccessor;
    private final SortAccessor<SORT> sortAccessor;

    private ADAPTER adapter;

    public QueryEngine(CriteriaAccessor<CRITERIA> criteriaAccessor, SortAccessor<SORT> sortAccessor) {

        this.criteriaAccessor = criteriaAccessor;
        this.sortAccessor = sortAccessor;
    }

    /**
     * Extract query attributes and delegate to concrete execution.
     *
     * @param query
     * @param keyspace
     * @return
     */
    public Collection<?> execute(KeyValueQuery<?> query, Serializable keyspace) {

        CRITERIA criteria = this.criteriaAccessor != null ? this.criteriaAccessor.resolve(query) : null;
        SORT sort = this.sortAccessor != null ? this.sortAccessor.resolve(query) : null;

        return execute(criteria, sort, query.getOffset(), query.getRows(), keyspace);
    }

    /**
     * Extract query attributes and delegate to concrete execution.
     *
     * @param query
     * @param keyspace
     * @return
     */
    public <T> Collection<T> execute(KeyValueQuery<?> query, Serializable keyspace, Class<T> type) {

        CRITERIA criteria = this.criteriaAccessor != null ? this.criteriaAccessor.resolve(query) : null;
        SORT sort = this.sortAccessor != null ? this.sortAccessor.resolve(query) : null;

        return execute(criteria, sort, query.getOffset(), query.getRows(), keyspace, type);
    }

    /**
     * Extract query attributes and delegate to concrete execution.
     *
     * @param query
     * @param keyspace
     * @return
     */
    public long count(KeyValueQuery<?> query, Serializable keyspace) {

        CRITERIA criteria = this.criteriaAccessor != null ? this.criteriaAccessor.resolve(query) : null;
        return count(criteria, keyspace);
    }

    /**
     * @param criteria
     * @param sort
     * @param offset
     * @param rows
     * @param keyspace
     * @return
     */
    public abstract Collection<?> execute(CRITERIA criteria, SORT sort, int offset, int rows, Serializable keyspace);

    /**
     * @param criteria
     * @param sort
     * @param offset
     * @param rows
     * @param keyspace
     * @param type
     * @return
     * @since 1.1
     */
    public <T> Collection<T> execute(CRITERIA criteria, SORT sort, int offset, int rows, Serializable keyspace,
                                     Class<T> type) {
        return (Collection<T>) execute(criteria, sort, offset, rows, keyspace);
    }

    /**
     * @param criteria
     * @param keyspace
     * @return
     */
    public abstract long count(CRITERIA criteria, Serializable keyspace);

    /**
     * Get the {@link KeyValueAdapter} used.
     *
     * @return
     */
    protected ADAPTER getAdapter() {
        return this.adapter;
    }

    /**
     * @param adapter
     */
    @SuppressWarnings("unchecked")
    public void registerAdapter(KeyValueAdapter adapter) {

        if (this.adapter == null) {
            this.adapter = (ADAPTER) adapter;
        } else {
            throw new IllegalArgumentException("Cannot register more than one adapter for this QueryEngine.");
        }
    }
}

