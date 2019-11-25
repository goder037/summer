package com.rocket.summer.framework.data.mapping.model;

import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.PersistentPropertyAccessor;

/**
 * PersistentPropertyAccessorFactory that uses a {@link BeanWrapper}.
 *
 * @author Oliver Gierke
 */
enum BeanWrapperPropertyAccessorFactory implements PersistentPropertyAccessorFactory {

    INSTANCE;

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.model.PersistentPropertyAccessorFactory#getPropertyAccessor(com.rocket.summer.framework.data.mapping.PersistentEntity, java.lang.Object)
     */
    @Override
    public PersistentPropertyAccessor getPropertyAccessor(PersistentEntity<?, ?> entity, Object bean) {
        return new BeanWrapper<Object>(bean);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.model.PersistentPropertyAccessorFactory#isSupported(com.rocket.summer.framework.data.mapping.PersistentEntity)
     */
    @Override
    public boolean isSupported(PersistentEntity<?, ?> entity) {
        return true;
    }
}

