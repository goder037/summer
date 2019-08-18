package com.rocket.summer.framework.aop.support;

import java.lang.reflect.Method;

import com.rocket.summer.framework.aop.MethodMatcher;

/**
 * Convenient abstract superclass for dynamic method matchers,
 * which do care about arguments at runtime.
 *
 * @author Rod Johnson
 */
public abstract class DynamicMethodMatcher implements MethodMatcher {

    @Override
    public final boolean isRuntime() {
        return true;
    }

    /**
     * Can override to add preconditions for dynamic matching. This implementation
     * always returns true.
     */
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return true;
    }

}

