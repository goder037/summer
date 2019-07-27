package com.rocket.summer.framework.beans.factory;

/**
 * Exception thrown when a bean is not a factory, but a user tries to get
 * at the factory for the given bean name. Whether a bean is a factory is
 * determined by whether it implements the FactoryBean interface.
 *
 * @author Rod Johnson
 * @since 10.03.2003
 * @see com.rocket.summer.framework.beans.factory.FactoryBean
 */
public class BeanIsNotAFactoryException extends BeanNotOfRequiredTypeException {

    /**
     * Create a new BeanIsNotAFactoryException.
     * @param name the name of the bean requested
     * @param actualType the actual type returned, which did not match
     * the expected type
     */
    public BeanIsNotAFactoryException(String name, Class actualType) {
        super(name, FactoryBean.class, actualType);
    }

}
