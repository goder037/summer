package com.rocket.summer.framework.cache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * AOP Alliance MethodInterceptor for declarative cache
 * management using the common Spring caching infrastructure
 * ({@link com.rocket.summer.framework.cache.Cache}).
 *
 * <p>Derives from the {@link CacheAspectSupport} class which
 * contains the integration with Spring's underlying caching API.
 * CacheInterceptor simply calls the relevant superclass methods
 * in the correct order.
 *
 * <p>CacheInterceptors are thread-safe.
 *
 * @author Costin Leau
 * @author Juergen Hoeller
 * @since 3.1
 */
@SuppressWarnings("serial")
public class CacheInterceptor extends CacheAspectSupport implements MethodInterceptor, Serializable {

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();

        CacheOperationInvoker aopAllianceInvoker = new CacheOperationInvoker() {
            @Override
            public Object invoke() {
                try {
                    return invocation.proceed();
                }
                catch (Throwable ex) {
                    throw new ThrowableWrapper(ex);
                }
            }
        };

        try {
            return execute(aopAllianceInvoker, invocation.getThis(), method, invocation.getArguments());
        }
        catch (CacheOperationInvoker.ThrowableWrapper th) {
            throw th.getOriginal();
        }
    }

}

