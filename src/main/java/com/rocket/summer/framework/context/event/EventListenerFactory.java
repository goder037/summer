package com.rocket.summer.framework.context.event;

import java.lang.reflect.Method;

import com.rocket.summer.framework.context.ApplicationListener;

/**
 * Strategy interface for creating {@link ApplicationListener} for methods
 * annotated with {@link EventListener}.
 *
 * @author Stephane Nicoll
 * @since 4.2
 */
public interface EventListenerFactory {

    /**
     * Specify if this factory supports the specified {@link Method}.
     * @param method an {@link EventListener} annotated method
     * @return {@code true} if this factory supports the specified method
     */
    boolean supportsMethod(Method method);

    /**
     * Create an {@link ApplicationListener} for the specified method.
     * @param beanName the name of the bean
     * @param type the target type of the instance
     * @param method the {@link EventListener} annotated method
     * @return an application listener, suitable to invoke the specified method
     */
    ApplicationListener<?> createApplicationListener(String beanName, Class<?> type, Method method);

}

