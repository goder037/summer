package com.rocket.summer.framework.data.redis.core.index;

/**
 * {@link IndexDefinition} that is based on a property paths.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public interface PathBasedRedisIndexDefinition extends IndexDefinition {

    /**
     * @return can be {@literal null}.
     */
    String getPath();

}

