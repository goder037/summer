package com.rocket.summer.framework.data.keyvalue.core.mapping;

import com.rocket.summer.framework.data.mapping.model.BasicPersistentEntity;
import com.rocket.summer.framework.data.util.TypeInformation;
import com.rocket.summer.framework.util.StringUtils;

/**
 * {@link KeyValuePersistentEntity} implementation that adds specific meta-data such as the {@literal keySpace}..
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 * @param <T>
 */
public class BasicKeyValuePersistentEntity<T> extends BasicPersistentEntity<T, KeyValuePersistentProperty> implements
        KeyValuePersistentEntity<T> {

    private static final KeySpaceResolver DEFAULT_FALLBACK_RESOLVER = ClassNameKeySpaceResolver.INSTANCE;

    private final String keyspace;

    /**
     * @param information must not be {@literal null}.
     * @param keySpaceResolver can be {@literal null}.
     */
    public BasicKeyValuePersistentEntity(TypeInformation<T> information, KeySpaceResolver fallbackKeySpaceResolver) {

        super(information);

        this.keyspace = detectKeySpace(information.getType(), fallbackKeySpaceResolver);
    }

    private static String detectKeySpace(Class<?> type, KeySpaceResolver fallback) {

        String keySpace = AnnotationBasedKeySpaceResolver.INSTANCE.resolveKeySpace(type);

        if (StringUtils.hasText(keySpace))
            return keySpace;

        return (fallback == null ? DEFAULT_FALLBACK_RESOLVER : fallback).resolveKeySpace(type);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.mapping.KeyValuePersistentEntity#getKeySpace()
     */
    @Override
    public String getKeySpace() {
        return this.keyspace;
    }
}

