package com.rocket.summer.framework.aop;

import java.io.Serializable;

/**
 * Canonical Pointcut instance that always matches.
 *
 * @author Rod Johnson
 */
class TruePointcut implements Pointcut, Serializable {

    public static final TruePointcut INSTANCE = new TruePointcut();

    /**
     * Enforce Singleton pattern.
     */
    private TruePointcut() {
    }

    public ClassFilter getClassFilter() {
        return ClassFilter.TRUE;
    }

    public MethodMatcher getMethodMatcher() {
        return MethodMatcher.TRUE;
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
        return "Pointcut.TRUE";
    }

}
