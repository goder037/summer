package com.rocket.summer.framework.data.redis.core.index;

/**
 * {@link PathBasedRedisIndexDefinition} for including property values in a secondary index. <br />
 * Uses Redis {@literal SET} for storage. <br />
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public class SimpleIndexDefinition extends RedisIndexDefinition implements PathBasedRedisIndexDefinition {

    /**
     * Creates new {@link SimpleIndexDefinition}.
     *
     * @param keyspace must not be {@literal null}.
     * @param path
     */
    public SimpleIndexDefinition(String keyspace, String path) {
        this(keyspace, path, path);
    }

    /**
     * Creates new {@link SimpleIndexDefinition}.
     *
     * @param keyspace must not be {@literal null}.
     * @param path
     * @param name must not be {@literal null}.
     */
    public SimpleIndexDefinition(String keyspace, String path, String name) {
        super(keyspace, path, name);
        addCondition(new PathCondition(path));
    }

}

