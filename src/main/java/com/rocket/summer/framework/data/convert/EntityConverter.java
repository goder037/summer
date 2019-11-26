package com.rocket.summer.framework.data.convert;

import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.data.mapping.context.MappingContext;

/**
 * Combined {@link EntityReader} and {@link EntityWriter} and add the ability to access a {@link MappingContext} and
 * {@link ConversionService}.
 *
 * @param <E> the concrete {@link PersistentEntity} implementation the converter is based on.
 * @param <P> the concrete {@link PersistentProperty} implementation the converter is based on.
 * @param <T> the most common type the {@link EntityConverter} can actually convert.
 * @param <S> the store specific source and sink an {@link EntityConverter} can deal with.
 * @author Oliver Gierke
 */
public interface EntityConverter<E extends PersistentEntity<?, P>, P extends PersistentProperty<P>, T, S> extends
        EntityReader<T, S>, EntityWriter<T, S> {

    /**
     * Returns the underlying {@link MappingContext} used by the converter.
     *
     * @return never {@literal null}
     */
    MappingContext<? extends E, P> getMappingContext();

    /**
     * Returns the underlying {@link ConversionService} used by the converter.
     *
     * @return never {@literal null}.
     */
    ConversionService getConversionService();
}

