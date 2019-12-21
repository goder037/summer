package com.rocket.summer.framework.aop;

import java.io.Serializable;

/**
 * Canonical ClassFilter instance that matches all classes.
 *
 * @author Rod Johnson
 */
class TrueClassFilter implements ClassFilter, Serializable {

    public static final TrueClassFilter INSTANCE = new TrueClassFilter();

    /**
     * Enforce Singleton pattern.
     */
    private TrueClassFilter() {
    }

    public boolean matches(Class clazz) {
        return true;
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
        return "ClassFilter.TRUE";
    }

}
