package com.rocket.summer.framework.beans.factory;

import com.rocket.summer.framework.context.BeansException;

/**
 * Thrown when a bean doesn't match the expected type.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class BeanNotOfRequiredTypeException extends BeansException {

    /** The name of the instance that was of the wrong type */
    private String beanName;

    /** The required type */
    private Class requiredType;

    /** The offending type */
    private Class actualType;


    /**
     * Create a new BeanNotOfRequiredTypeException.
     * @param beanName the name of the bean requested
     * @param requiredType the required type
     * @param actualType the actual type returned, which did not match
     * the expected type
     */
    public BeanNotOfRequiredTypeException(String beanName, Class requiredType, Class actualType) {
        super("Bean named '" + beanName + "' must be of type [" + requiredType.getName() +
                "], but was actually of type [" + actualType.getName() + "]");
        this.beanName = beanName;
        this.requiredType = requiredType;
        this.actualType = actualType;
    }


    /**
     * Return the name of the instance that was of the wrong type.
     */
    public String getBeanName() {
        return this.beanName;
    }

    /**
     * Return the expected type for the bean.
     */
    public Class getRequiredType() {
        return this.requiredType;
    }

    /**
     * Return the actual type of the instance found.
     */
    public Class getActualType() {
        return this.actualType;
    }

}
