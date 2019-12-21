package com.rocket.summer.framework.beans.factory;

/**
 * Exception thrown in case of a bean being requested despite
 * bean creation currently not being allowed (for example, during
 * the shutdown phase of a bean factory).
 *
 * @author Juergen Hoeller
 * @since 2.0
 */
public class BeanCreationNotAllowedException extends BeanCreationException {

    /**
     * Create a new BeanCreationNotAllowedException.
     * @param beanName the name of the bean requested
     * @param msg the detail message
     */
    public BeanCreationNotAllowedException(String beanName, String msg) {
        super(beanName, msg);
    }

}
