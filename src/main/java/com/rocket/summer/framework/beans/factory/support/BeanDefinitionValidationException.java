package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.FatalBeanException;

/**
 * Exception thrown when the validation of a bean definition failed.
 *
 * @author Juergen Hoeller
 * @since 21.11.2003
 * @see AbstractBeanDefinition#validate()
 */
public class BeanDefinitionValidationException extends FatalBeanException {

    /**
     * Create a new BeanDefinitionValidationException with the specified message.
     * @param msg the detail message
     */
    public BeanDefinitionValidationException(String msg) {
        super(msg);
    }

    /**
     * Create a new BeanDefinitionValidationException with the specified message
     * and root cause.
     * @param msg the detail message
     * @param cause the root cause
     */
    public BeanDefinitionValidationException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

