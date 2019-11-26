package com.rocket.summer.framework.data.repository.core.support;

import java.io.Serializable;

import com.rocket.summer.framework.data.repository.core.EntityInformation;
import com.rocket.summer.framework.util.Assert;

/**
 * Base class for implementations of {@link EntityInformation}. Considers an entity to be new whenever
 * {@link #getId(Object)} returns {@literal null}.
 *
 * @author Oliver Gierke
 * @author Nick Williams
 */
public abstract class AbstractEntityInformation<T, ID extends Serializable> implements EntityInformation<T, ID> {

    private final Class<T> domainClass;

    /**
     * Creates a new {@link AbstractEntityInformation} from the given domain class.
     *
     * @param domainClass must not be {@literal null}.
     */
    public AbstractEntityInformation(Class<T> domainClass) {

        Assert.notNull(domainClass, "DomainClass must not be null!");
        this.domainClass = domainClass;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.core.EntityInformation#isNew(java.lang.Object)
     */
    public boolean isNew(T entity) {

        ID id = getId(entity);
        Class<ID> idType = getIdType();

        if (!idType.isPrimitive()) {
            return id == null;
        }

        if (id instanceof Number) {
            return ((Number) id).longValue() == 0L;
        }

        throw new IllegalArgumentException(String.format("Unsupported primitive id type %s!", idType));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.core.EntityMetadata#getJavaType()
     */
    public Class<T> getJavaType() {
        return this.domainClass;
    }
}

