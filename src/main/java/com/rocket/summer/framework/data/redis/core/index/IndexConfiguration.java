package com.rocket.summer.framework.data.redis.core.index;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ObjectUtils;

/**
 * {@link IndexConfiguration} allows programmatic setup of indexes. This is suitable for cases where there is no option
 * to use the equivalent {@link Indexed} annotation.
 *
 * @author Christoph Strobl
 * @author Rob Winch
 * @since 1.7
 */
public class IndexConfiguration implements ConfigurableIndexDefinitionProvider {

    private final Set<IndexDefinition> definitions;

    /**
     * Creates new empty {@link IndexConfiguration}.
     */
    public IndexConfiguration() {

        this.definitions = new CopyOnWriteArraySet<IndexDefinition>();
        for (IndexDefinition initial : initialConfiguration()) {
            addIndexDefinition(initial);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.index.IndexDefinitionProvider#hasIndexFor(java.io.Serializable)
     */
    @Override
    public boolean hasIndexFor(Serializable keyspace) {
        return !getIndexDefinitionsFor(keyspace).isEmpty();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.index.IndexDefinitionProvider#hasIndexFor(java.io.Serializable, java.lang.String)
     */
    public boolean hasIndexFor(Serializable keyspace, String path) {
        return !getIndexDefinitionsFor(keyspace, path).isEmpty();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.index.IndexDefinitionProvider#getIndexDefinitionsFor(java.io.Serializable, java.lang.String)
     */
    public Set<IndexDefinition> getIndexDefinitionsFor(Serializable keyspace, String path) {
        return getIndexDefinitions(keyspace, path, Object.class);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.index.IndexDefinitionProvider#getIndexDefinitionsFor(java.io.Serializable)
     */
    public Set<IndexDefinition> getIndexDefinitionsFor(Serializable keyspace) {

        Set<IndexDefinition> indexDefinitions = new LinkedHashSet<IndexDefinition>();

        for (IndexDefinition indexDef : definitions) {
            if (indexDef.getKeyspace().equals(keyspace)) {
                indexDefinitions.add(indexDef);
            }
        }

        return indexDefinitions;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.index.IndexDefinitionRegistry#addIndexDefinition(com.rocket.summer.framework.data.redis.core.index.IndexDefinition)
     */
    public void addIndexDefinition(IndexDefinition indexDefinition) {

        Assert.notNull(indexDefinition, "RedisIndexDefinition must not be null in order to be added.");
        this.definitions.add(indexDefinition);
    }

    private Set<IndexDefinition> getIndexDefinitions(Serializable keyspace, String path, Class<?> type) {

        Set<IndexDefinition> def = new LinkedHashSet<IndexDefinition>();
        for (IndexDefinition indexDef : definitions) {
            if (ClassUtils.isAssignable(type, indexDef.getClass()) && indexDef.getKeyspace().equals(keyspace)) {

                if (indexDef instanceof PathBasedRedisIndexDefinition) {
                    if (ObjectUtils.nullSafeEquals(((PathBasedRedisIndexDefinition) indexDef).getPath(), path)) {
                        def.add(indexDef);
                    }
                } else {
                    def.add(indexDef);
                }
            }
        }

        return def;
    }

    /**
     * Customization hook.
     *
     * @return must not return {@literal null}.
     */
    protected Iterable<? extends IndexDefinition> initialConfiguration() {
        return Collections.emptySet();
    }

}

