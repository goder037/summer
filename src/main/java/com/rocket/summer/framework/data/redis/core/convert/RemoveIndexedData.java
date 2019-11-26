package com.rocket.summer.framework.data.redis.core.convert;

/**
 * {@link RemoveIndexedData} represents a removed index entry from a secondary index for a property path in a given keyspace.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 */
public class RemoveIndexedData implements IndexedData {

    private final IndexedData delegate;

    RemoveIndexedData(IndexedData delegate) {
        super();
        this.delegate = delegate;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.convert.IndexedData#getIndexName()
     */
    @Override
    public String getIndexName() {
        return delegate.getIndexName();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.convert.IndexedData#getKeyspace()
     */
    @Override
    public String getKeyspace() {
        return delegate.getKeyspace();
    }

    @Override
    public String toString() {
        return "RemoveIndexedData [indexName=" + getIndexName() + ", keyspace()=" + getKeyspace() + "]";
    }

}