package com.rocket.summer.framework.beans.factory.config;


import com.rocket.summer.framework.context.BeansException;

/**
 * Subinterface of BeanPostProcessor that adds a before-destruction callback.
 *
 * <p>The typical usage will be to invoke custom destruction callbacks on
 * specific bean types, matching corresponding initialization callbacks.
 *
 * @author Juergen Hoeller
 * @since 1.0.1
 * @see com.rocket.summer.framework.web.struts.ActionServletAwareProcessor
 */
public interface DestructionAwareBeanPostProcessor extends BeanPostProcessor {

    /**
     * Apply this BeanPostProcessor to the given bean instance before
     * its destruction. Can invoke custom destruction callbacks.
     * <p>Like DisposableBean's <code>destroy</code> and a custom destroy method,
     * this callback just applies to singleton beans in the factory (including
     * inner beans).
     * @param bean the bean instance to be destroyed
     * @param beanName the name of the bean
     * @throws com.rocket.summer.framework.beans.BeansException in case of errors
     * @see com.rocket.summer.framework.beans.factory.DisposableBean
     * @see com.rocket.summer.framework.beans.factory.support.AbstractBeanDefinition#setDestroyMethodName
     */
    void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException;

}

