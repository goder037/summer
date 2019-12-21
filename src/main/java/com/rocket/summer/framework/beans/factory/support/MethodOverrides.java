package com.rocket.summer.framework.beans.factory.support;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Set of method overrides, determining which, if any, methods on a
 * managed object the Spring IoC container will override at runtime.
 *
 * <p>The currently supported {@link MethodOverride} variants are
 * {@link LookupOverride} and {@link ReplaceOverride}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 1.1
 * @see MethodOverride
 */
public class MethodOverrides {

    private final Set overrides = new HashSet();


    /**
     * Create new MethodOverrides.
     */
    public MethodOverrides() {
    }

    /**
     * Deep copy constructor.
     */
    public MethodOverrides(MethodOverrides other) {
        addOverrides(other);
    }


    /**
     * Copy all given method overrides into this object.
     */
    public void addOverrides(MethodOverrides other) {
        if (other != null) {
            this.overrides.addAll(other.getOverrides());
        }
    }

    /**
     * Add the given method override.
     */
    public void addOverride(MethodOverride override) {
        this.overrides.add(override);
    }

    /**
     * Return all method overrides contained by this object.
     * @return Set of MethodOverride objects
     * @see MethodOverride
     */
    public Set getOverrides() {
        return this.overrides;
    }

    /**
     * Return whether the set of method overrides is empty.
     */
    public boolean isEmpty() {
        return this.overrides.isEmpty();
    }

    /**
     * Return the override for the given method, if any.
     * @param method method to check for overrides for
     * @return the method override, or <code>null</code> if none
     */
    public MethodOverride getOverride(Method method) {
        for (Iterator it = this.overrides.iterator(); it.hasNext();) {
            MethodOverride methodOverride = (MethodOverride) it.next();
            if (methodOverride.matches(method)) {
                return methodOverride;
            }
        }
        return null;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodOverrides that = (MethodOverrides) o;

        if (!this.overrides.equals(that.overrides)) return false;

        return true;
    }

    public int hashCode() {
        return this.overrides.hashCode();
    }

}
