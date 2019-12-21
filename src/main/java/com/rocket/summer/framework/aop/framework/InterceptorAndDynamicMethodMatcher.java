package com.rocket.summer.framework.aop.framework;

import com.rocket.summer.framework.aop.MethodMatcher;
import org.aopalliance.intercept.MethodInterceptor;

/**
 * Internal framework class, combining a MethodInterceptor instance
 * with a MethodMatcher for use as an element in the advisor chain.
 *
 * @author Rod Johnson
 */
class InterceptorAndDynamicMethodMatcher {

    final MethodInterceptor interceptor;

    final MethodMatcher methodMatcher;

    public InterceptorAndDynamicMethodMatcher(MethodInterceptor interceptor, MethodMatcher methodMatcher) {
        this.interceptor = interceptor;
        this.methodMatcher = methodMatcher;
    }

}
