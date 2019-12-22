package com.rocket.summer.framework.aop.interceptor;

import java.io.Serializable;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.rocket.summer.framework.util.ConcurrencyThrottleSupport;

/**
 * Interceptor that throttles concurrent access, blocking invocations
 * if a specified concurrency limit is reached.
 *
 * <p>Can be applied to methods of local services that involve heavy use
 * of system resources, in a scenario where it is more efficient to
 * throttle concurrency for a specific service rather than restricting
 * the entire thread pool (e.g. the web container's thread pool).
 *
 * <p>The default concurrency limit of this interceptor is 1.
 * Specify the "concurrencyLimit" bean property to change this value.
 *
 * @author Juergen Hoeller
 * @since 11.02.2004
 * @see #setConcurrencyLimit
 */
@SuppressWarnings("serial")
public class ConcurrencyThrottleInterceptor extends ConcurrencyThrottleSupport
        implements MethodInterceptor, Serializable {

    public ConcurrencyThrottleInterceptor() {
        setConcurrencyLimit(1);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        beforeAccess();
        try {
            return methodInvocation.proceed();
        }
        finally {
            afterAccess();
        }
    }

}
