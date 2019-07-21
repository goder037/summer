package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.BeanInstantiationException;
import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.beans.factory.BeanDefinitionStoreException;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.util.ReflectionUtils;
import com.rocket.summer.framework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Simple object instantiation strategy for use in a BeanFactory.
 *
 * <p>Does not support Method Injection, although it provides hooks for subclasses
 * to override to add Method Injection support, for example by overriding methods.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 1.1
 */
public class SimpleInstantiationStrategy implements InstantiationStrategy {

    public Object instantiate(
            RootBeanDefinition beanDefinition, String beanName, BeanFactory owner) {

        // Don't override the class with CGLIB if no overrides.
        if (beanDefinition.getMethodOverrides().isEmpty()) {
            Constructor constructorToUse = (Constructor) beanDefinition.resolvedConstructorOrFactoryMethod;
            if (constructorToUse == null) {
                Class clazz = beanDefinition.getBeanClass();
                if (clazz.isInterface()) {
                    throw new BeanInstantiationException(clazz, "Specified class is an interface");
                }
                try {
                    constructorToUse = clazz.getDeclaredConstructor((Class[]) null);
                    beanDefinition.resolvedConstructorOrFactoryMethod = constructorToUse;
                }
                catch (Exception ex) {
                    throw new BeanInstantiationException(clazz, "No default constructor found", ex);
                }
            }
            return BeanUtils.instantiateClass(constructorToUse);
        }
        else {
            // Must generate CGLIB subclass.
            return instantiateWithMethodInjection(beanDefinition, beanName, owner);
        }
    }

    /**
     * Subclasses can override this method, which is implemented to throw
     * UnsupportedOperationException, if they can instantiate an object with
     * the Method Injection specified in the given RootBeanDefinition.
     * Instantiation should use a no-arg constructor.
     */
    protected Object instantiateWithMethodInjection(
            RootBeanDefinition beanDefinition, String beanName, BeanFactory owner) {

        throw new UnsupportedOperationException(
                "Method Injection not supported in SimpleInstantiationStrategy");
    }

    public Object instantiate(
            RootBeanDefinition beanDefinition, String beanName, BeanFactory owner,
            Constructor ctor, Object[] args) {

        if (beanDefinition.getMethodOverrides().isEmpty()) {
            return BeanUtils.instantiateClass(ctor, args);
        }
        else {
            return instantiateWithMethodInjection(beanDefinition, beanName, owner, ctor, args);
        }
    }

    /**
     * Subclasses can override this method, which is implemented to throw
     * UnsupportedOperationException, if they can instantiate an object with
     * the Method Injection specified in the given RootBeanDefinition.
     * Instantiation should use the given constructor and parameters.
     */
    protected Object instantiateWithMethodInjection(
            RootBeanDefinition beanDefinition, String beanName, BeanFactory owner,
            Constructor ctor, Object[] args) {

        throw new UnsupportedOperationException(
                "Method Injection not supported in SimpleInstantiationStrategy");
    }

    public Object instantiate(
            RootBeanDefinition beanDefinition, String beanName, BeanFactory owner,
            Object factoryBean, Method factoryMethod, Object[] args) {

        try {
            // It's a static method if the target is null.
            ReflectionUtils.makeAccessible(factoryMethod);
            return factoryMethod.invoke(factoryBean, args);
        }
        catch (IllegalArgumentException ex) {
            throw new BeanDefinitionStoreException(
                    "Illegal arguments to factory method [" + factoryMethod + "]; " +
                            "args: " + StringUtils.arrayToCommaDelimitedString(args));
        }
        catch (IllegalAccessException ex) {
            throw new BeanDefinitionStoreException(
                    "Cannot access factory method [" + factoryMethod + "]; is it public?");
        }
        catch (InvocationTargetException ex) {
            throw new BeanDefinitionStoreException(
                    "Factory method [" + factoryMethod + "] threw exception", ex.getTargetException());
        }
    }

}
