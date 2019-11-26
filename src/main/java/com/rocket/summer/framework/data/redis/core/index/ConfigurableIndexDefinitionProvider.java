package com.rocket.summer.framework.data.redis.core.index;

/**
 * {@link IndexDefinitionProvider} that allows registering new {@link IndexDefinition} via
 * {@link IndexDefinitionRegistry}.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public interface ConfigurableIndexDefinitionProvider extends IndexDefinitionProvider, IndexDefinitionRegistry {

}
