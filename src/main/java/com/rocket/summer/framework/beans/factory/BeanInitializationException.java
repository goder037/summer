package com.rocket.summer.framework.beans.factory;

import com.rocket.summer.framework.beans.FatalBeanException;

/**
 * Exception that a bean implementation is suggested to throw if its own
 * factory-aware initialization code fails. BeansExceptions thrown by
 * bean factory methods themselves should simply be propagated as-is.
 *
 * <p>Note that non-factory-aware initialization methods like afterPropertiesSet()
 * or a custom "init-method" can throw any exception.
 *
 * @author Juergen Hoeller
 * @since 13.11.2003
 * @see BeanFactoryAware#setBeanFactory
 * @see InitializingBean#afterPropertiesSet
 */
public class BeanInitializationException extends FatalBeanException {

    /**
     * Create a new BeanInitializationException with the specified message.
     * @param msg the detail message
     */
    public BeanInitializationException(String msg) {
        super(msg);
    }

    /**
     * Create a new BeanInitializationException with the specified message
     * and root cause.
     * @param msg the detail message
     * @param cause the root cause
     */
    public BeanInitializationException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

