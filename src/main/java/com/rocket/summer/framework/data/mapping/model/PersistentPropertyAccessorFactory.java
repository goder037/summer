package com.rocket.summer.framework.data.mapping.model;

import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.PersistentPropertyAccessor;

/**
 * Factory to create {@link PersistentPropertyAccessor} for a given {@link PersistentEntity} and bean instance.
 *
 * @author Mark Paluch
 * @author Oliver Gierke
 * @since 1.13
 */
public interface PersistentPropertyAccessorFactory {

    /**
     * Returns a {@link PersistentPropertyAccessor} for a given {@link PersistentEntity} and {@code bean}.
     *
     * @param entity must not be {@literal null}.
     * @param bean must not be {@literal null}.
     * @return will never be {@literal null}.
     */
    PersistentPropertyAccessor getPropertyAccessor(PersistentEntity<?, ?> entity, Object bean);

    /**
     * Returns whether given {@link PersistentEntity} is supported by this {@link PersistentPropertyAccessorFactory}.
     *
     * @param entity must not be {@literal null}.
     * @return
     */
    boolean isSupported(PersistentEntity<?, ?> entity);
}
