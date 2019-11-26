package com.rocket.summer.framework.data.redis.core.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ObjectUtils;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Base {@link IndexDefinition} implementation.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public abstract class RedisIndexDefinition implements IndexDefinition {

    private final String keyspace;
    private final String indexName;
    private final String path;
    private List<Condition<?>> conditions;
    private IndexValueTransformer valueTransformer;

    /**
     * Creates new {@link RedisIndexDefinition}.
     *
     * @param keyspace
     * @param path
     * @param indexName
     */
    protected RedisIndexDefinition(String keyspace, String path, String indexName) {

        this.keyspace = keyspace;
        this.indexName = indexName;
        this.path = path;
        this.conditions = new ArrayList<IndexDefinition.Condition<?>>();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.index.IndexDefinition#getKeyspace()
     */
    @Override
    public String getKeyspace() {
        return keyspace;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.index.IndexDefinition#getConditions()
     */
    @Override
    public Collection<Condition<?>> getConditions() {
        return Collections.unmodifiableCollection(conditions);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.index.IndexDefinition#valueTransformer()
     */
    @Override
    public IndexValueTransformer valueTransformer() {
        return valueTransformer != null ? valueTransformer : NoOpValueTransformer.INSTANCE;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.index.IndexDefinition#getIndexName()
     */
    @Override
    public String getIndexName() {
        return indexName;
    }

    public String getPath() {
        return this.path;
    }

    protected void addCondition(Condition<?> condition) {

        Assert.notNull(condition, "Condition must not be null!");
        this.conditions.add(condition);
    }

    public void setValueTransformer(IndexValueTransformer valueTransformer) {
        this.valueTransformer = valueTransformer;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(indexName);
        return result + ObjectUtils.nullSafeHashCode(keyspace);
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
        if (!(obj instanceof RedisIndexDefinition)) {
            return false;
        }
        RedisIndexDefinition that = (RedisIndexDefinition) obj;

        if (!ObjectUtils.nullSafeEquals(this.keyspace, that.keyspace)) {
            return false;
        }

        return ObjectUtils.nullSafeEquals(this.indexName, that.indexName);
    }

    /**
     * @author Christoph Strobl
     * @since 1.7
     */
    public static enum NoOpValueTransformer implements IndexValueTransformer {
        INSTANCE;

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.core.convert.converter.Converter#convert(java.lang.Object)
         */
        @Override
        public Object convert(Object source) {
            return source;
        }
    }

    /**
     * @author Christoph Strobl
     * @since 1.7
     */
    public static enum LowercaseIndexValueTransformer implements IndexValueTransformer {
        INSTANCE;

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.core.convert.converter.Converter#convert(java.lang.Object)
         */
        @Override
        public Object convert(Object source) {

            if (!(source instanceof String)) {
                return source;
            }

            return ((String) source).toLowerCase();
        }
    }

    /**
     * @author Christoph Strobl
     * @since 1.7
     */
    public static class CompositeValueTransformer implements IndexValueTransformer {

        private final List<IndexValueTransformer> transformers = new ArrayList<IndexValueTransformer>();

        public CompositeValueTransformer(Collection<IndexValueTransformer> transformers) {
            this.transformers.addAll(transformers);
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.core.convert.converter.Converter#convert(java.lang.Object)
         */
        @Override
        public Object convert(Object source) {

            Object tmp = source;
            for (IndexValueTransformer transformer : transformers) {
                tmp = transformer.convert(tmp);
            }
            return tmp;
        }

    }

    /**
     * @author Christoph Strobl
     * @since 1.7
     * @param <T>
     */
    public static class OrCondition<T> implements Condition<T> {

        private final List<Condition<T>> conditions = new ArrayList<Condition<T>>();

        public OrCondition(Collection<Condition<T>> conditions) {
            this.conditions.addAll(conditions);
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.redis.core.index.IndexDefinition.Condition#matches(java.lang.Object, com.rocket.summer.framework.data.redis.core.index.IndexDefinition.IndexingContext)
         */
        @Override
        public boolean matches(T value, IndexingContext context) {

            for (Condition<T> condition : conditions) {
                if (condition.matches(value, context)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * @author Christoph Strobl
     * @since 1.7
     */
    public static class PathCondition implements Condition<Object> {

        private final String path;

        public PathCondition(String path) {
            this.path = path;
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.redis.core.index.IndexDefinition.Condition#matches(java.lang.Object, com.rocket.summer.framework.data.redis.core.index.IndexDefinition.IndexingContext)
         */
        @Override
        public boolean matches(Object value, IndexingContext context) {

            if (!StringUtils.hasText(path)) {
                return true;
            }

            return ObjectUtils.nullSafeEquals(context.getPath(), path);
        }
    }
}

