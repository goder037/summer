package com.rocket.summer.framework.data.projection;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * SPI to create {@link MethodInterceptor} instances based on the given source object and the target type to produce. To
 * be registered with a {@link ProxyProjectionFactory} to customize the way method executions on projection proxies are
 * handled.
 *
 * @author Oliver Gierke
 * @see ProxyProjectionFactory
 * @soundtrack Henrik Freischlader Trio - Nobody Else To Blame (Openness)
 * @since 1.13
 */
public interface MethodInterceptorFactory {

    /**
     * Returns the {@link MethodInterceptor} to be used for the given source object and target type.
     *
     * @param source will never be {@literal null}.
     * @param targetType will never be {@literal null}.
     * @return
     */
    MethodInterceptor createMethodInterceptor(Object source, Class<?> targetType);

    /**
     * Returns whether the current factory is supposed to be used to create a {@link MethodInterceptor} for proxy of the
     * given target type.
     *
     * @param source will never be {@literal null}.
     * @param targetType will never be {@literal null}.
     * @return
     */
    boolean supports(Object source, Class<?> targetType);
}

