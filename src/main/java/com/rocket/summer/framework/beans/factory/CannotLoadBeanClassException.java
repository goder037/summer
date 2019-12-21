package com.rocket.summer.framework.beans.factory;

import com.rocket.summer.framework.beans.FatalBeanException;

/**
 * Exception thrown when the BeanFactory cannot load the specified class
 * of a given bean.
 *
 * @author Juergen Hoeller
 * @since 2.0
 */
public class CannotLoadBeanClassException extends FatalBeanException {

    private String resourceDescription;

    private String beanName;

    private String beanClassName;


    /**
     * Create a new CannotLoadBeanClassException.
     * @param resourceDescription description of the resource
     * that the bean definition came from
     * @param beanName the name of the bean requested
     * @param beanClassName the name of the bean class
     * @param cause the root cause
     */
    public CannotLoadBeanClassException(
            String resourceDescription, String beanName, String beanClassName, ClassNotFoundException cause) {

        super("Cannot find class [" + beanClassName + "] for bean with name '" + beanName +
                "' defined in " + resourceDescription, cause);
        this.resourceDescription = resourceDescription;
        this.beanName = beanName;
        this.beanClassName = beanClassName;
    }

    /**
     * Create a new CannotLoadBeanClassException.
     * @param resourceDescription description of the resource
     * that the bean definition came from
     * @param beanName the name of the bean requested
     * @param beanClassName the name of the bean class
     * @param cause the root cause
     */
    public CannotLoadBeanClassException(
            String resourceDescription, String beanName, String beanClassName, LinkageError cause) {

        super("Error loading class [" + beanClassName + "] for bean with name '" + beanName +
                "' defined in " + resourceDescription + ": problem with class file or dependent class", cause);
        this.resourceDescription = resourceDescription;
        this.beanName = beanName;
        this.beanClassName = beanClassName;
    }


    /**
     * Return the description of the resource that the bean
     * definition came from.
     */
    public String getResourceDescription() {
        return this.resourceDescription;
    }

    /**
     * Return the name of the bean requested.
     */
    public String getBeanName() {
        return this.beanName;
    }

    /**
     * Return the name of the class we were trying to load.
     */
    public String getBeanClassName() {
        return this.beanClassName;
    }

}
