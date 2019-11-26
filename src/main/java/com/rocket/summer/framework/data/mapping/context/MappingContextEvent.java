package com.rocket.summer.framework.data.mapping.context;

import com.rocket.summer.framework.context.event.ApplicationEvent;
import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.util.Assert;

/**
 * Base implementation of an {@link ApplicationEvent} refering to a {@link PersistentEntity}.
 *
 * @author Oliver Gierke
 * @author Jon Brisbin
 * @param <E> the {@link PersistentEntity} the context was created for
 * @param <P> the {@link PersistentProperty} the {@link PersistentEntity} consists of
 */
public class MappingContextEvent<E extends PersistentEntity<?, P>, P extends PersistentProperty<P>> extends
        ApplicationEvent {

    private static final long serialVersionUID = 1336466833846092490L;

    private final MappingContext<?, ?> source;
    private final E entity;

    /**
     * Creates a new {@link MappingContextEvent} for the given {@link MappingContext} and {@link PersistentEntity}.
     *
     * @param source must not be {@literal null}.
     * @param entity must not be {@literal null}.
     */
    public MappingContextEvent(MappingContext<?, ?> source, E entity) {

        super(source);

        Assert.notNull(source, "Source MappingContext must not be null!");
        Assert.notNull(entity, "Entity must not be null!");

        this.source = source;
        this.entity = entity;
    }

    /**
     * Returns the {@link PersistentEntity} the event was created for.
     *
     * @return
     */
    public E getPersistentEntity() {
        return entity;
    }

    /**
     * Returns whether the {@link MappingContextEvent} was triggered by the given {@link MappingContext}.
     *
     * @param context the {@link MappingContext} that potentially created the event.
     * @return
     */
    public boolean wasEmittedBy(MappingContext<?, ?> context) {
        return this.source.equals(context);
    }
}

