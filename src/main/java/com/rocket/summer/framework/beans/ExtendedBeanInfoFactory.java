package com.rocket.summer.framework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Method;

import com.rocket.summer.framework.core.Ordered;

/**
 * {@link BeanInfoFactory} implementation that evaluates whether bean classes have
 * "non-standard" JavaBeans setter methods and are thus candidates for introspection
 * by Spring's (package-visible) {@code ExtendedBeanInfo} implementation.
 *
 * <p>Ordered at {@link Ordered#LOWEST_PRECEDENCE} to allow other user-defined
 * {@link BeanInfoFactory} types to take precedence.
 *
 * @author Chris Beams
 * @since 3.2
 * @see BeanInfoFactory
 * @see CachedIntrospectionResults
 */
public class ExtendedBeanInfoFactory implements BeanInfoFactory, Ordered {

    /**
     * Return an {@link ExtendedBeanInfo} for the given bean class, if applicable.
     */
    @Override
    public BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
        return (supports(beanClass) ? new ExtendedBeanInfo(Introspector.getBeanInfo(beanClass)) : null);
    }

    /**
     * Return whether the given bean class declares or inherits any non-void
     * returning bean property or indexed property setter methods.
     */
    private boolean supports(Class<?> beanClass) {
        for (Method method : beanClass.getMethods()) {
            if (ExtendedBeanInfo.isCandidateWriteMethod(method)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}

