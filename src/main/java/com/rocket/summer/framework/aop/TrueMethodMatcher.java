package com.rocket.summer.framework.aop;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Canonical MethodMatcher instance that matches all methods.
 *
 * @author Rod Johnson
 */
class TrueMethodMatcher implements MethodMatcher, Serializable {

    public static final TrueMethodMatcher INSTANCE = new TrueMethodMatcher();

    /**
     * Enforce Singleton pattern.
     */
    private TrueMethodMatcher() {
    }

    public boolean isRuntime() {
        return false;
    }

    public boolean matches(Method method, Class targetClass) {
        return true;
    }

    public boolean matches(Method method, Class targetClass, Object[] args) {
        // Should never be invoked as isRuntime returns false.
        throw new UnsupportedOperationException();
    }

    /**
     * Required to support serialization. Replaces with canonical
     * instance on deserialization, protecting Singleton pattern.
     * Alternative to overriding <code>equals()</code>.
     */
    private Object readResolve() {
        return INSTANCE;
    }

    public String toString() {
        return "MethodMatcher.TRUE";
    }

}

