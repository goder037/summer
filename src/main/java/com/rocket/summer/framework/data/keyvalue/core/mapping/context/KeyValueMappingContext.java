package com.rocket.summer.framework.data.keyvalue.core.mapping.context;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import com.rocket.summer.framework.data.keyvalue.core.mapping.BasicKeyValuePersistentEntity;
import com.rocket.summer.framework.data.keyvalue.core.mapping.KeySpaceResolver;
import com.rocket.summer.framework.data.keyvalue.core.mapping.KeyValuePersistentEntity;
import com.rocket.summer.framework.data.keyvalue.core.mapping.KeyValuePersistentProperty;
import com.rocket.summer.framework.data.mapping.context.AbstractMappingContext;
import com.rocket.summer.framework.data.mapping.context.MappingContext;
import com.rocket.summer.framework.data.mapping.model.SimpleTypeHolder;
import com.rocket.summer.framework.data.util.TypeInformation;

/**
 * Default implementation of a {@link MappingContext} using {@link KeyValuePersistentEntity} and
 * {@link KeyValuePersistentProperty} as primary abstractions.
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 */
public class KeyValueMappingContext extends
        AbstractMappingContext<KeyValuePersistentEntity<?>, KeyValuePersistentProperty> {

    private KeySpaceResolver fallbackKeySpaceResolver;

    /**
     * Configures the {@link KeySpaceResolver} to be used if not explicit key space is annotated to the domain type.
     *
     * @param fallbackKeySpaceResolver can be {@literal null}.
     */
    public void setFallbackKeySpaceResolver(KeySpaceResolver fallbackKeySpaceResolver) {
        this.fallbackKeySpaceResolver = fallbackKeySpaceResolver;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.context.AbstractMappingContext#createPersistentEntity(com.rocket.summer.framework.data.util.TypeInformation)
     */
    @Override
    protected <T> KeyValuePersistentEntity<T> createPersistentEntity(TypeInformation<T> typeInformation) {
        return new BasicKeyValuePersistentEntity<T>(typeInformation, fallbackKeySpaceResolver);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.context.AbstractMappingContext#createPersistentProperty(java.lang.reflect.Field, java.beans.PropertyDescriptor, com.rocket.summer.framework.data.mapping.model.MutablePersistentEntity, com.rocket.summer.framework.data.mapping.model.SimpleTypeHolder)
     */
    @Override
    protected KeyValuePersistentProperty createPersistentProperty(Field field, PropertyDescriptor descriptor,
                                                                  KeyValuePersistentEntity<?> owner, SimpleTypeHolder simpleTypeHolder) {
        return new KeyValuePersistentProperty(field, descriptor, owner, simpleTypeHolder);
    }
}

