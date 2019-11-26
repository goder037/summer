package com.rocket.summer.framework.data.redis.core.mapping;

import com.rocket.summer.framework.data.keyvalue.core.mapping.KeyValuePersistentEntity;
import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.data.redis.core.TimeToLiveAccessor;

/**
 * Redis specific {@link PersistentEntity}.
 *
 * @author Christoph Strobl
 * @param <T>
 * @since 1.7
 */
public interface RedisPersistentEntity<T> extends KeyValuePersistentEntity<T> {

    /**
     * Get the {@link TimeToLiveAccessor} associated with the entity.
     *
     * @return never {@literal null}.
     */
    TimeToLiveAccessor getTimeToLiveAccessor();

    /**
     * @return {@literal true} when a property is annotated with {@link com.rocket.summer.framework.data.redis.core.TimeToLive}.
     * @since 1.8
     */
    boolean hasExplictTimeToLiveProperty();

    /**
     * Get the {@link PersistentProperty} that is annotated with {@link com.rocket.summer.framework.data.redis.core.TimeToLive}.
     *
     * @return can be {@null}.
     * @since 1.8
     */
    PersistentProperty<? extends PersistentProperty<?>> getExplicitTimeToLiveProperty();

}

