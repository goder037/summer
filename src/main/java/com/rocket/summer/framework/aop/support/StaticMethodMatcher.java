package com.rocket.summer.framework.aop.support;

import com.rocket.summer.framework.aop.MethodMatcher;

import java.lang.reflect.Method;

/**
 * Convenient abstract superclass for static method matchers, which don't care
 * about arguments at runtime.
 *
 * @author Rod Johnson
 */
public abstract class StaticMethodMatcher implements MethodMatcher {

    @Override
    public final boolean isRuntime() {
        return false;
    }

    @Override
    public final boolean matches(Method method, Class<?> targetClass, Object... args) {
        // should never be invoked because isRuntime() returns false
        throw new UnsupportedOperationException("Illegal MethodMatcher usage");
    }

}
