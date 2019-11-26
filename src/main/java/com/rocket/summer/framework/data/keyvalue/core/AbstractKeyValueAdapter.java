package com.rocket.summer.framework.data.keyvalue.core;

import java.io.Serializable;
import java.util.Collection;

import com.rocket.summer.framework.data.keyvalue.core.query.KeyValueQuery;

/**
 * Base implementation of {@link KeyValueAdapter} holds {@link QueryEngine} to delegate {@literal find} and
 * {@literal count} execution to.
 *
 * @author Christoph Strobl
 */
public abstract class AbstractKeyValueAdapter implements KeyValueAdapter {

    private final QueryEngine<? extends KeyValueAdapter, ?, ?> engine;

    /**
     * Creates new {@link AbstractKeyValueAdapter} with using the default query engine.
     */
    protected AbstractKeyValueAdapter() {
        this(null);
    }

    /**
     * Creates new {@link AbstractKeyValueAdapter} with using the default query engine.
     *
     * @param engine will be defaulted to {@link SpelQueryEngine} if {@literal null}.
     */
    protected AbstractKeyValueAdapter(QueryEngine<? extends KeyValueAdapter, ?, ?> engine) {

        this.engine = engine != null ? engine : new SpelQueryEngine<KeyValueAdapter>();
        this.engine.registerAdapter(this);
    }

    /**
     * Get the {@link QueryEngine} used.
     *
     * @return
     */
    protected QueryEngine<? extends KeyValueAdapter, ?, ?> getQueryEngine() {
        return engine;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.KeyValueAdapter#get(java.io.Serializable, java.io.Serializable, java.lang.Class)
     */
    @Override
    public <T> T get(Serializable id, Serializable keyspace, Class<T> type) {
        return (T) get(id, keyspace);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.KeyValueAdapter#get(java.io.Serializable, java.io.Serializable, java.lang.Class)
     */
    @Override
    public <T> T delete(Serializable id, Serializable keyspace, Class<T> type) {
        return (T) delete(id, keyspace);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.KeyValueAdapter#get(java.io.Serializable, java.io.Serializable, java.lang.Class)
     */
    @Override
    public <T> Iterable<T> find(KeyValueQuery<?> query, Serializable keyspace, Class<T> type) {
        return (Iterable<T>) engine.execute(query, keyspace, type);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.KeyValueAdapter#find(com.rocket.summer.framework.data.keyvalue.core.query.KeyValueQuery, java.io.Serializable)
     */
    @Override
    public Collection<?> find(KeyValueQuery<?> query, Serializable keyspace) {
        return engine.execute(query, keyspace);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.KeyValueAdapter#count(com.rocket.summer.framework.data.keyvalue.core.query.KeyValueQuery, java.io.Serializable)
     */
    @Override
    public long count(KeyValueQuery<?> query, Serializable keyspace) {
        return engine.count(query, keyspace);
    }
}

