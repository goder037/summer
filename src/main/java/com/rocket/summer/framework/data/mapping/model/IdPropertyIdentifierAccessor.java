package com.rocket.summer.framework.data.mapping.model;

import com.rocket.summer.framework.data.mapping.IdentifierAccessor;
import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.data.mapping.PersistentPropertyAccessor;
import com.rocket.summer.framework.util.Assert;

/**
 * Default implementation of {@link IdentifierAccessor}.
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 * @since 1.10
 */
public class IdPropertyIdentifierAccessor implements IdentifierAccessor {

    private final PersistentPropertyAccessor accessor;
    private final PersistentProperty<?> idProperty;

    /**
     * Creates a new {@link IdPropertyIdentifierAccessor} for the given {@link PersistentEntity} and
     * {@link ConvertingPropertyAccessor}.
     *
     * @param entity must not be {@literal null}.
     * @param target must not be {@literal null}.
     */

    public IdPropertyIdentifierAccessor(PersistentEntity<?, ?> entity, Object target) {

        Assert.notNull(entity, "PersistentEntity must not be 'null'");
        Assert.isTrue(entity.hasIdProperty(), "PersistentEntity does not have an identifier property!");

        this.idProperty = entity.getIdProperty();
        this.accessor = entity.getPropertyAccessor(target);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.IdentifierAccessor#getIdentifier()
     */
    public Object getIdentifier() {
        return accessor.getProperty(idProperty);
    }
}
