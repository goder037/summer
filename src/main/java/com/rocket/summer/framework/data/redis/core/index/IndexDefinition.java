package com.rocket.summer.framework.data.redis.core.index;

import java.util.Collection;

import com.rocket.summer.framework.data.util.TypeInformation;

/**
 * {@link IndexDefinition} allow to set up a blueprint for creating secondary index structures in Redis. Setting up
 * conditions allows to define {@link Condition} that have to be passed in order to add a value to the index. This
 * allows to fine grained tune the index structure. {@link IndexValueTransformer} gets applied to the raw value for
 * creating the actual index entry.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public interface IndexDefinition {

    /**
     * @return never {@literal null}.
     */
    String getKeyspace();

    /**
     * @return never {@literal null}.
     */
    Collection<Condition<?>> getConditions();

    /**
     * @return never {@literal null}.
     */
    IndexValueTransformer valueTransformer();

    /**
     * @return never {@literal null}.
     */
    String getIndexName();

    /**
     * @author Christoph Strobl
     * @since 1.7
     * @param <T>
     */
    public static interface Condition<T> {
        boolean matches(T value, IndexingContext context);
    }

    /**
     * Context in which a particular value is about to get indexed.
     *
     * @author Christoph Strobl
     * @since 1.7
     */
    public class IndexingContext {

        private final String keyspace;
        private final String path;
        private final TypeInformation<?> typeInformation;

        public IndexingContext(String keyspace, String path, TypeInformation<?> typeInformation) {

            this.keyspace = keyspace;
            this.path = path;
            this.typeInformation = typeInformation;
        }

        public String getKeyspace() {
            return keyspace;
        }

        public String getPath() {
            return path;
        }

        public TypeInformation<?> getTypeInformation() {
            return typeInformation;
        }
    }

}

