package com.rocket.summer.framework.data.repository.core.support;

import java.io.Serializable;

import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.repository.core.EntityInformation;

/**
 * {@link EntityInformation} implementation that uses a {@link PersistentEntity} to obtain id type information and uses
 * a {@link com.rocket.summer.framework.data.mapping.IdentifierAccessor} to access the property value if requested.
 *
 * @author Oliver Gierke
 */
@SuppressWarnings("unchecked")
public class PersistentEntityInformation<T, ID extends Serializable> extends AbstractEntityInformation<T, ID> {

    private final PersistentEntity<T, ?> persistentEntity;

    /**
     * Creates a new {@link PersistableEntityInformation} for the given {@link PersistentEntity}.
     *
     * @param entity must not be {@literal null}.
     */
    public PersistentEntityInformation(PersistentEntity<T, ?> entity) {

        super(entity.getType());
        this.persistentEntity = entity;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.core.EntityInformation#getId(java.lang.Object)
     */
    @Override
    public ID getId(T entity) {
        return (ID) persistentEntity.getIdentifierAccessor(entity).getIdentifier();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.core.EntityInformation#getIdType()
     */
    @Override
    public Class<ID> getIdType() {
        return (Class<ID>) persistentEntity.getIdProperty().getType();
    }
}

