package com.rocket.summer.framework.data.keyvalue.core.query;

import com.rocket.summer.framework.data.domain.Sort;

/**
 * @author Christoph Strobl
 * @param <T> Criteria type
 */
public class KeyValueQuery<T> {

    private Sort sort;
    private int offset = -1;
    private int rows = -1;
    private T criteria;

    /**
     * Creates new instance of {@link KeyValueQuery}.
     */
    public KeyValueQuery() {}

    /**
     * Creates new instance of {@link KeyValueQuery} with given criteria.
     *
     * @param criteria can be {@literal null}.
     */
    public KeyValueQuery(T criteria) {
        this.criteria = criteria;
    }

    /**
     * Creates new instance of {@link KeyValueQuery} with given {@link Sort}.
     *
     * @param sort can be {@literal null}.
     */
    public KeyValueQuery(Sort sort) {
        this.sort = sort;
    }

    /**
     * Get the criteria object.
     *
     * @return
     * @deprecated will be removed in favor of {@link #getCriteria()}.
     */
    @Deprecated
    public T getCritieria() {
        return criteria;
    }

    /**
     * Get the criteria object.
     *
     * @return
     * @since 1.2.4
     */
    public T getCriteria() {
        return criteria;
    }

    /**
     * Get {@link Sort}.
     *
     * @return
     */
    public Sort getSort() {
        return sort;
    }

    /**
     * Number of elements to skip.
     *
     * @return negative value if not set.
     */
    public int getOffset() {
        return this.offset;
    }

    /**
     * Number of elements to read.
     *
     * @return negative value if not set.
     */
    public int getRows() {
        return this.rows;
    }

    /**
     * Set the number of elements to skip.
     *
     * @param offset use negative value for none.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Set the number of elements to read.
     *
     * @param rows use negative value for all.
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * Set {@link Sort} to be applied.
     *
     * @param sort
     */
    public void setSort(Sort sort) {
        this.sort = sort;
    }

    /**
     * Add given {@link Sort}.
     *
     * @param sort {@literal null} {@link Sort} will be ignored.
     * @return
     */
    public KeyValueQuery<T> orderBy(Sort sort) {

        if (sort == null) {
            return this;
        }

        if (this.sort != null) {
            this.sort.and(sort);
        } else {
            this.sort = sort;
        }
        return this;
    }

    /**
     * @see KeyValueQuery#setOffset(int)
     * @param offset
     * @return
     */
    public KeyValueQuery<T> skip(int offset) {
        setOffset(offset);
        return this;
    }

    /**
     * @see KeyValueQuery#setRows(int)
     * @param rows
     * @return
     */
    public KeyValueQuery<T> limit(int rows) {
        setRows(rows);
        return this;
    }

}

