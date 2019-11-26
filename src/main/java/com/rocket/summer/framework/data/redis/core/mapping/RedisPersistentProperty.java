package com.rocket.summer.framework.data.redis.core.mapping;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import com.rocket.summer.framework.data.keyvalue.core.mapping.KeyValuePersistentProperty;
import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.data.mapping.model.SimpleTypeHolder;

/**
 * Redis specific {@link PersistentProperty} implementation.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public class RedisPersistentProperty extends KeyValuePersistentProperty {

    private static final Set<String> SUPPORTED_ID_PROPERTY_NAMES = new HashSet<String>();

    static {
        SUPPORTED_ID_PROPERTY_NAMES.add("id");
    }

    /**
     * Creates new {@link RedisPersistentProperty}.
     *
     * @param field
     * @param propertyDescriptor
     * @param owner
     * @param simpleTypeHolder
     */
    public RedisPersistentProperty(Field field, PropertyDescriptor propertyDescriptor,
                                   PersistentEntity<?, KeyValuePersistentProperty> owner, SimpleTypeHolder simpleTypeHolder) {
        super(field, propertyDescriptor, owner, simpleTypeHolder);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.model.AnnotationBasedPersistentProperty#isIdProperty()
     */
    @Override
    public boolean isIdProperty() {

        if (super.isIdProperty()) {
            return true;
        }

        return SUPPORTED_ID_PROPERTY_NAMES.contains(getName());
    }
}

