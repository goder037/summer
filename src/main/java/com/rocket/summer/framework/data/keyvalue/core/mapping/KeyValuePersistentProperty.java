package com.rocket.summer.framework.data.keyvalue.core.mapping;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import com.rocket.summer.framework.data.mapping.Association;
import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.data.mapping.model.AnnotationBasedPersistentProperty;
import com.rocket.summer.framework.data.mapping.model.SimpleTypeHolder;

/**
 * Most trivial implementation of {@link PersistentProperty}.
 *
 * @author Christoph Strobl
 */
public class KeyValuePersistentProperty extends AnnotationBasedPersistentProperty<KeyValuePersistentProperty> {

    public KeyValuePersistentProperty(Field field, PropertyDescriptor propertyDescriptor,
                                      PersistentEntity<?, KeyValuePersistentProperty> owner, SimpleTypeHolder simpleTypeHolder) {
        super(field, propertyDescriptor, owner, simpleTypeHolder);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.model.AbstractPersistentProperty#createAssociation()
     */
    @Override
    protected Association<KeyValuePersistentProperty> createAssociation() {
        return new Association<KeyValuePersistentProperty>(this, null);
    }
}
