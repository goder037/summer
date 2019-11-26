package com.rocket.summer.framework.data.redis.core.convert;

import com.rocket.summer.framework.util.ObjectUtils;

/**
 * {@link IndexedData} implementation indicating storage of data within a Redis Set.
 *
 * @author Christoph Strobl
 * @author Rob Winch
 * @since 1.7
 */
public class SimpleIndexedPropertyValue implements IndexedData {

    private final String keyspace;
    private final String indexName;
    private final Object value;

    /**
     * Creates new {@link SimpleIndexedPropertyValue}.
     *
     * @param keyspace must not be {@literal null}.
     * @param indexName must not be {@literal null}.
     * @param value can be {@literal null}.
     */
    public SimpleIndexedPropertyValue(String keyspace, String indexName, Object value) {

        this.keyspace = keyspace;
        this.indexName = indexName;
        this.value = value;
    }

    /**
     * Get the value to index.
     *
     * @return can be {@literal null}.
     */
    public Object getValue() {
        return value;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.convert.IndexedData#getIndexName()
     */
    @Override
    public String getIndexName() {
        return indexName;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.convert.IndexedData#getKeySpace()
     */
    @Override
    public String getKeyspace() {
        return this.keyspace;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        int result = 1;
        result += ObjectUtils.nullSafeHashCode(keyspace);
        result += ObjectUtils.nullSafeHashCode(indexName);
        result += ObjectUtils.nullSafeHashCode(value);
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SimpleIndexedPropertyValue)) {
            return false;
        }

        SimpleIndexedPropertyValue that = (SimpleIndexedPropertyValue) obj;

        if (!ObjectUtils.nullSafeEquals(this.keyspace, that.keyspace)) {
            return false;
        }
        if (!ObjectUtils.nullSafeEquals(this.indexName, that.indexName)) {
            return false;
        }
        return ObjectUtils.nullSafeEquals(this.value, that.value);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SimpleIndexedPropertyValue [keyspace=" + keyspace + ", indexName=" + indexName + ", value=" + value + "]";
    }

}

