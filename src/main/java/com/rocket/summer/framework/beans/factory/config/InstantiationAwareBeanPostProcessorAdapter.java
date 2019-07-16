package com.rocket.summer.framework.beans.factory.config;

import com.rocket.summer.framework.beans.PropertyValues;
import com.rocket.summer.framework.context.BeansException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;

/**
 * Adapter that implements all methods on {@link SmartInstantiationAwareBeanPostProcessor}
 * as no-ops, which will not change normal processing of each bean instantiated
 * by the container. Subclasses may override merely those methods that they are
 * actually interested in.
 *
 * <p>Note that this base class is only recommendable if you actually require
 * {@link InstantiationAwareBeanPostProcessor} functionality. If all you need
 * is plain {@link BeanPostProcessor} functionality, prefer a straight
 * implementation of that (simpler) interface.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 */
public abstract class InstantiationAwareBeanPostProcessorAdapter implements SmartInstantiationAwareBeanPostProcessor {

    public Class predictBeanType(Class beanClass, String beanName) {
        return null;
    }

    public Constructor[] determineCandidateConstructors(Class beanClass, String beanName) throws BeansException {
        return null;
    }

    public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessBeforeInstantiation(Class beanClass, String beanName) throws BeansException {
        return null;
    }

    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    public PropertyValues postProcessPropertyValues(
            PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName)
            throws BeansException {

        return pvs;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}

