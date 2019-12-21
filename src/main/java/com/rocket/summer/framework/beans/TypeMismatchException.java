package com.rocket.summer.framework.beans;

import com.rocket.summer.framework.util.ClassUtils;

import java.beans.PropertyChangeEvent;

/**
 * Exception thrown on a type mismatch when trying to set a bean property.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class TypeMismatchException extends PropertyAccessException {

    /**
     * Error code that a type mismatch error will be registered with.
     */
    public static final String ERROR_CODE = "typeMismatch";


    private transient Object value;

    private Class requiredType;


    /**
     * Create a new TypeMismatchException.
     * @param propertyChangeEvent the PropertyChangeEvent that resulted in the problem
     * @param requiredType the required target type
     */
    public TypeMismatchException(PropertyChangeEvent propertyChangeEvent, Class requiredType) {
        this(propertyChangeEvent, requiredType, null);
    }

    /**
     * Create a new TypeMismatchException.
     * @param propertyChangeEvent the PropertyChangeEvent that resulted in the problem
     * @param requiredType the required target type (or <code>null</code> if not known)
     * @param cause the root cause (may be <code>null</code>)
     */
    public TypeMismatchException(PropertyChangeEvent propertyChangeEvent, Class requiredType, Throwable cause) {
        super(propertyChangeEvent,
                "Failed to convert property value of type [" +
                        ClassUtils.getDescriptiveType(propertyChangeEvent.getNewValue()) + "]" +
                        (requiredType != null ?
                                " to required type [" + ClassUtils.getQualifiedName(requiredType) + "]" : "") +
                        (propertyChangeEvent.getPropertyName() != null ?
                                " for property '" + propertyChangeEvent.getPropertyName() + "'" : ""),
                cause);
        this.value = propertyChangeEvent.getNewValue();
        this.requiredType = requiredType;
    }

    /**
     * Create a new TypeMismatchException without PropertyChangeEvent.
     * @param value the offending value that couldn't be converted (may be <code>null</code>)
     * @param requiredType the required target type (or <code>null</code> if not known)
     */
    public TypeMismatchException(Object value, Class requiredType) {
        this(value, requiredType, null);
    }

    /**
     * Create a new TypeMismatchException without PropertyChangeEvent.
     * @param value the offending value that couldn't be converted (may be <code>null</code>)
     * @param requiredType the required target type (or <code>null</code> if not known)
     * @param cause the root cause (may be <code>null</code>)
     */
    public TypeMismatchException(Object value, Class requiredType, Throwable cause) {
        super("Failed to convert value of type [" + ClassUtils.getDescriptiveType(value) + "]" +
                        (requiredType != null ? " to required type [" + ClassUtils.getQualifiedName(requiredType) + "]" : ""),
                cause);
        this.value = value;
        this.requiredType = requiredType;
    }


    /**
     * Return the offending value (may be <code>null</code>)
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * Return the required target type, if any.
     */
    public Class getRequiredType() {
        return this.requiredType;
    }

    public String getErrorCode() {
        return ERROR_CODE;
    }

}
