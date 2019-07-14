package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.context.BeansException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Interface responsible for creating instances corresponding to a root bean definition.
 *
 * <p>This is pulled out into a strategy as various approaches are possible,
 * including using CGLIB to create subclasses on the fly to support Method Injection.
 *
 * @author Rod Johnson
 * @since 1.1
 */
public interface InstantiationStrategy {

    /**
     * Return an instance of the bean with the given name in this factory.
     * @param beanDefinition the bean definition
     * @param beanName name of the bean when it's created in this context.
     * The name can be <code>null</code> if we're autowiring a bean that
     * doesn't belong to the factory.
     * @param owner owning BeanFactory
     * @return a bean instance for this bean definition
     * @throws BeansException if the instantiation failed
     */
    Object instantiate(
            RootBeanDefinition beanDefinition, String beanName, BeanFactory owner) throws BeansException;

    /**
     * Return an instance of the bean with the given name in this factory,
     * creating it via the given constructor.
     * @param beanDefinition the bean definition
     * @param beanName name of the bean when it's created in this context.
     * The name can be <code>null</code> if we're autowiring a bean
     * that doesn't belong to the factory.
     * @param owner owning BeanFactory
     * @param ctor the constructor to use
     * @param args the constructor arguments to apply
     * @return a bean instance for this bean definition
     * @throws BeansException if the instantiation failed
     */
    Object instantiate(
            RootBeanDefinition beanDefinition, String beanName, BeanFactory owner,
            Constructor ctor, Object[] args) throws BeansException;

    /**
     * Return an instance of the bean with the given name in this factory,
     * creating it via the given factory method.
     * @param beanDefinition bean definition
     * @param beanName name of the bean when it's created in this context.
     * The name can be <code>null</code> if we're autowiring a bean
     * that doesn't belong to the factory.
     * @param owner owning BeanFactory
     * @param factoryBean the factory bean instance to call the factory method on,
     * or <code>null</code> in case of a static factory method
     * @param factoryMethod the factory method to use
     * @param args the factory method arguments to apply
     * @return a bean instance for this bean definition
     * @throws BeansException if the instantiation failed
     */
    Object instantiate(
            RootBeanDefinition beanDefinition, String beanName, BeanFactory owner,
            Object factoryBean, Method factoryMethod, Object[] args) throws BeansException;

}

