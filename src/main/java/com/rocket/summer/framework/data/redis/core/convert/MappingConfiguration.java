package com.rocket.summer.framework.data.redis.core.convert;

import com.rocket.summer.framework.data.redis.core.index.ConfigurableIndexDefinitionProvider;

/**
 * {@link MappingConfiguration} is used for programmatic configuration of secondary indexes, key prefixes, expirations
 * and the such.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public class MappingConfiguration {

    private final ConfigurableIndexDefinitionProvider indexConfiguration;
    private final KeyspaceConfiguration keyspaceConfiguration;

    /**
     * Creates new {@link MappingConfiguration}.
     *
     * @param indexConfiguration must not be {@literal null}.
     * @param keyspaceConfiguration must not be {@literal null}.
     */
    public MappingConfiguration(ConfigurableIndexDefinitionProvider indexConfiguration,
                                KeyspaceConfiguration keyspaceConfiguration) {

        this.indexConfiguration = indexConfiguration;
        this.keyspaceConfiguration = keyspaceConfiguration;
    }

    /**
     * @return never {@literal null}.
     */
    public ConfigurableIndexDefinitionProvider getIndexConfiguration() {
        return indexConfiguration;
    }

    /**
     * @return never {@literal null}.
     */
    public KeyspaceConfiguration getKeyspaceConfiguration() {
        return keyspaceConfiguration;
    }
}

