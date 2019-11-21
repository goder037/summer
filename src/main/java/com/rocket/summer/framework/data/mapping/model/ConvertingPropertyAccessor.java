package com.rocket.summer.framework.data.mapping.model;

import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.data.mapping.PersistentPropertyAccessor;
import com.rocket.summer.framework.util.Assert;

/**
 * {@link PersistentPropertyAccessor} that potentially converts the value handed to
 * {@link #setProperty(PersistentProperty, Object)} to the type of the {@link PersistentProperty} using a
 * {@link ConversionService}. Exposes {@link #getProperty(PersistentProperty, Class)} to allow obtaining the value of a
 * property in a type the {@link ConversionService} can convert the raw type to.
 *
 * @author Oliver Gierke
 */
public class ConvertingPropertyAccessor implements PersistentPropertyAccessor {

    private final PersistentPropertyAccessor accessor;
    private final ConversionService conversionService;

    /**
     * Creates a new {@link ConvertingPropertyAccessor} for the given delegate {@link PersistentPropertyAccessor} and
     * {@link ConversionService}.
     *
     * @param accessor must not be {@literal null}.
     * @param conversionService must not be {@literal null}.
     */
    public ConvertingPropertyAccessor(PersistentPropertyAccessor accessor, ConversionService conversionService) {

        Assert.notNull(accessor, "PersistentPropertyAccessor must not be null!");
        Assert.notNull(conversionService, "ConversionService must not be null!");

        this.accessor = accessor;
        this.conversionService = conversionService;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentPropertyAccessor#setProperty(com.rocket.summer.framework.data.mapping.PersistentProperty, java.lang.Object)
     */
    @Override
    public void setProperty(PersistentProperty<?> property, Object value) {
        accessor.setProperty(property, convertIfNecessary(value, property.getType()));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentPropertyAccessor#getProperty(com.rocket.summer.framework.data.mapping.PersistentProperty)
     */
    @Override
    public Object getProperty(PersistentProperty<?> property) {
        return accessor.getProperty(property);
    }

    /**
     * Returns the value of the given {@link PersistentProperty} converted to the given type.
     *
     * @param property must not be {@literal null}.
     * @param targetType must not be {@literal null}.
     * @return
     */
    public <T> T getProperty(PersistentProperty<?> property, Class<T> targetType) {

        Assert.notNull(property, "PersistentProperty must not be null!");
        Assert.notNull(targetType, "Target type must not be null!");

        return convertIfNecessary(getProperty(property), targetType);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentPropertyAccessor#getBean()
     */
    @Override
    public Object getBean() {
        return accessor.getBean();
    }

    /**
     * Triggers the conversion of the source value into the target type unless the value already is a value of given
     * target type.
     *
     * @param source can be {@literal null}.
     * @param type must not be {@literal null}.
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T convertIfNecessary(Object source, Class<T> type) {
        return (T) (source == null ? source : type.isAssignableFrom(source.getClass()) ? source : conversionService
                .convert(source, type));
    }
}

