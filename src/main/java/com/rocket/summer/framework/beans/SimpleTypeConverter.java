package com.rocket.summer.framework.beans;

import com.rocket.summer.framework.core.MethodParameter;

/**
 * Simple implementation of the TypeConverter interface that does not operate
 * on any specific target object. This is an alternative to using a full-blown
 * BeanWrapperImpl instance for arbitrary type conversion needs.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see BeanWrapperImpl
 */
public class SimpleTypeConverter extends PropertyEditorRegistrySupport implements TypeConverter {

    private final TypeConverterDelegate typeConverterDelegate = new TypeConverterDelegate(this);


    public SimpleTypeConverter() {
        registerDefaultEditors();
    }


    public Object convertIfNecessary(Object value, Class requiredType) throws TypeMismatchException {
        return convertIfNecessary(value, requiredType, null);
    }

    public Object convertIfNecessary(
            Object value, Class requiredType, MethodParameter methodParam) throws TypeMismatchException {
        try {
            return this.typeConverterDelegate.convertIfNecessary(value, requiredType, methodParam);
        }
        catch (IllegalArgumentException ex) {
            throw new TypeMismatchException(value, requiredType, ex);
        }
    }

}
