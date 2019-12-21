package com.rocket.summer.framework.beans;

/**
 * Exception thrown when navigation of a valid nested property
 * path encounters a NullPointerException.
 *
 * <p>For example, navigating "spouse.age" could fail because the
 * spouse property of the target object has a null value.
 *
 * @author Rod Johnson
 */
public class NullValueInNestedPathException extends InvalidPropertyException {

    /**
     * Create a new NullValueInNestedPathException.
     * @param beanClass the offending bean class
     * @param propertyName the offending property
     */
    public NullValueInNestedPathException(Class<?> beanClass, String propertyName) {
        super(beanClass, propertyName, "Value of nested property '" + propertyName + "' is null");
    }

    /**
     * Create a new NullValueInNestedPathException.
     * @param beanClass the offending bean class
     * @param propertyName the offending property
     * @param msg the detail message
     */
    public NullValueInNestedPathException(Class<?> beanClass, String propertyName, String msg) {
        super(beanClass, propertyName, msg);
    }

    /**
     * Create a new NullValueInNestedPathException.
     * @param beanClass the offending bean class
     * @param propertyName the offending property
     * @param msg the detail message
     * @param cause the root cause
     * @since 4.3.2
     */
    public NullValueInNestedPathException(Class<?> beanClass, String propertyName, String msg, Throwable cause) {
        super(beanClass, propertyName, msg, cause);
    }

}

