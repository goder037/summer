package com.rocket.summer.framework.beans.factory.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.beans.factory.BeanFactory;

/**
 * Interface responsible for creating instances corresponding to a root bean definition.
 *
 * <p>This is pulled out into a strategy as various approaches are possible,
 * including using CGLIB to create subclasses on the fly to support Method Injection.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 1.1
 */
public interface InstantiationStrategy {

    /**
     * Return an instance of the bean with the given name in this factory.
     * @param bd the bean definition
     * @param beanName the name of the bean when it's created in this context.
     * The name can be {@code null} if we're autowiring a bean which doesn't
     * belong to the factory.
     * @param owner the owning BeanFactory
     * @return a bean instance for this bean definition
     * @throws BeansException if the instantiation attempt failed
     */
    Object instantiate(RootBeanDefinition bd, String beanName, BeanFactory owner)
            throws BeansException;

    /**
     * Return an instance of the bean with the given name in this factory,
     * creating it via the given constructor.
     * @param bd the bean definition
     * @param beanName the name of the bean when it's created in this context.
     * The name can be {@code null} if we're autowiring a bean which doesn't
     * belong to the factory.
     * @param owner the owning BeanFactory
     * @param ctor the constructor to use
     * @param args the constructor arguments to apply
     * @return a bean instance for this bean definition
     * @throws BeansException if the instantiation attempt failed
     */
    Object instantiate(RootBeanDefinition bd, String beanName, BeanFactory owner,
                       Constructor<?> ctor, Object... args) throws BeansException;

    /**
     * Return an instance of the bean with the given name in this factory,
     * creating it via the given factory method.
     * @param bd the bean definition
     * @param beanName the name of the bean when it's created in this context.
     * The name can be {@code null} if we're autowiring a bean which doesn't
     * belong to the factory.
     * @param owner the owning BeanFactory
     * @param factoryBean the factory bean instance to call the factory method on,
     * or {@code null} in case of a static factory method
     * @param factoryMethod the factory method to use
     * @param args the factory method arguments to apply
     * @return a bean instance for this bean definition
     * @throws BeansException if the instantiation attempt failed
     */
    Object instantiate(RootBeanDefinition bd, String beanName, BeanFactory owner,
                       Object factoryBean, Method factoryMethod, Object... args) throws BeansException;

}
