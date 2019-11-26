package com.rocket.summer.framework.data.redis.core.index;

/**
 * Registry that allows adding {@link IndexDefinition}.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public interface IndexDefinitionRegistry {

    /**
     * Add given {@link RedisIndexSetting}.
     *
     * @param indexDefinition must not be {@literal null}.
     */
    void addIndexDefinition(IndexDefinition indexDefinition);
}

