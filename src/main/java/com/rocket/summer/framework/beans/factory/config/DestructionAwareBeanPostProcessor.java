package com.rocket.summer.framework.beans.factory.config;


import com.rocket.summer.framework.context.BeansException;

/**
 * Subinterface of {@link BeanPostProcessor} that adds a before-destruction callback.
 *
 * <p>The typical usage will be to invoke custom destruction callbacks on
 * specific bean types, matching corresponding initialization callbacks.
 *
 * @author Juergen Hoeller
 * @since 1.0.1
 */
public interface DestructionAwareBeanPostProcessor extends BeanPostProcessor {

    /**
     * Apply this BeanPostProcessor to the given bean instance before its
     * destruction, e.g. invoking custom destruction callbacks.
     * <p>Like DisposableBean's {@code destroy} and a custom destroy method, this
     * callback will only apply to beans which the container fully manages the
     * lifecycle for. This is usually the case for singletons and scoped beans.
     * @param bean the bean instance to be destroyed
     * @param beanName the name of the bean
     * @throws com.rocket.summer.framework.context.BeansException in case of errors
     * @see com.rocket.summer.framework.beans.factory.DisposableBean#destroy()
     * @see com.rocket.summer.framework.beans.factory.support.AbstractBeanDefinition#setDestroyMethodName(String)
     */
    void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException;

    /**
     * Determine whether the given bean instance requires destruction by this
     * post-processor.
     * <p><b>NOTE:</b> Even as a late addition, this method has been introduced on
     * {@code DestructionAwareBeanPostProcessor} itself instead of on a SmartDABPP
     * subinterface. This allows existing {@code DestructionAwareBeanPostProcessor}
     * implementations to easily provide {@code requiresDestruction} logic while
     * retaining compatibility with Spring <4.3, and it is also an easier onramp to
     * declaring {@code requiresDestruction} as a Java 8 default method in Spring 5.
     * <p>If an implementation of {@code DestructionAwareBeanPostProcessor} does
     * not provide a concrete implementation of this method, Spring's invocation
     * mechanism silently assumes a method returning {@code true} (the effective
     * default before 4.3, and the to-be-default in the Java 8 method in Spring 5).
     * @param bean the bean instance to check
     * @return {@code true} if {@link #postProcessBeforeDestruction} is supposed to
     * be called for this bean instance eventually, or {@code false} if not needed
     * @since 4.3
     */
    boolean requiresDestruction(Object bean);

}


