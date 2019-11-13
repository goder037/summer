package com.rocket.summer.framework.data.util;

import static com.rocket.summer.framework.util.ReflectionUtils.*;

import java.lang.reflect.Field;

import com.rocket.summer.framework.beans.BeanWrapperImpl;
import com.rocket.summer.framework.beans.NotReadablePropertyException;
import com.rocket.summer.framework.beans.NotWritablePropertyException;

/**
 * Custom extension of {@link BeanWrapperImpl} that falls back to direct field access in case the object or type being
 * wrapped does not use accessor methods.
 *
 * @author Oliver Gierke
 */
public class DirectFieldAccessFallbackBeanWrapper extends BeanWrapperImpl {

    public DirectFieldAccessFallbackBeanWrapper(Object entity) {
        super(entity);
    }

    public DirectFieldAccessFallbackBeanWrapper(Class<?> type) {
        super(type);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.beans.BeanWrapperImpl#getPropertyValue(java.lang.String)
     */
    @Override
    public Object getPropertyValue(String propertyName) {

        try {
            return super.getPropertyValue(propertyName);
        } catch (NotReadablePropertyException e) {

            Field field = findField(getWrappedClass(), propertyName);

            if (field == null) {
                throw new NotReadablePropertyException(getWrappedClass(), propertyName,
                        "Could not find field for property during fallback access!");
            }

            makeAccessible(field);
            return getField(field, getWrappedInstance());
        }
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.beans.BeanWrapperImpl#setPropertyValue(java.lang.String, java.lang.Object)
     */
    @Override
    public void setPropertyValue(String propertyName, Object value) {

        try {
            super.setPropertyValue(propertyName, value);
        } catch (NotWritablePropertyException e) {

            Field field = findField(getWrappedClass(), propertyName);

            if (field == null) {
                throw new NotWritablePropertyException(getWrappedClass(), propertyName,
                        "Could not find field for property during fallback access!", e);
            }

            makeAccessible(field);
            setField(field, getWrappedInstance(), value);
        }
    }
}

