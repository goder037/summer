package com.rocket.summer.framework.beans.factory;

import com.rocket.summer.framework.beans.FatalBeanException;

/**
 * Exception to be thrown from a FactoryBean's <code>getObject()</code> method
 * if the bean is not fully initialized yet, for example because it is involved
 * in a circular reference.
 *
 * <p>Note: A circular reference with a FactoryBean cannot be solved by eagerly
 * caching singleton instances like with normal beans. The reason is that
 * <i>every</i> FactoryBean needs to be fully initialized before it can
 * return the created bean, while only <i>specific</i> normal beans need
 * to be initialized - that is, if a collaborating bean actually invokes
 * them on initialization instead of just storing the reference.
 *
 * @author Juergen Hoeller
 * @since 30.10.2003
 * @see FactoryBean#getObject()
 */
public class FactoryBeanNotInitializedException extends FatalBeanException {

    /**
     * Create a new FactoryBeanNotInitializedException with the default message.
     */
    public FactoryBeanNotInitializedException() {
        super("FactoryBean is not fully initialized yet");
    }

    /**
     * Create a new FactoryBeanNotInitializedException with the given message.
     * @param msg the detail message
     */
    public FactoryBeanNotInitializedException(String msg) {
        super(msg);
    }

}

