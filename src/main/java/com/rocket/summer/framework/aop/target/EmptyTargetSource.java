package com.rocket.summer.framework.aop.target;

import com.rocket.summer.framework.aop.TargetSource;
import com.rocket.summer.framework.util.ObjectUtils;

import java.io.Serializable;

/**
 * Canonical <code>TargetSource</code> when there is no target
 * (or just the target class known), and behavior is supplied
 * by interfaces and advisors only.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class EmptyTargetSource implements TargetSource, Serializable {

    /** use serialVersionUID from Spring 1.2 for interoperability */
    private static final long serialVersionUID = 3680494563553489691L;


    //---------------------------------------------------------------------
    // Static factory methods
    //---------------------------------------------------------------------

    /**
     * The canonical (Singleton) instance of this {@link EmptyTargetSource}.
     */
    public static final EmptyTargetSource INSTANCE = new EmptyTargetSource(null, true);


    /**
     * Return an EmptyTargetSource for the given target Class.
     * @param targetClass the target Class (may be <code>null</code>)
     * @see #getTargetClass()
     */
    public static EmptyTargetSource forClass(Class targetClass) {
        return forClass(targetClass, true);
    }

    /**
     * Return an EmptyTargetSource for the given target Class.
     * @param targetClass the target Class (may be <code>null</code>)
     * @param isStatic whether the TargetSource should be marked as static
     * @see #getTargetClass()
     */
    public static EmptyTargetSource forClass(Class targetClass, boolean isStatic) {
        return (targetClass == null && isStatic ? INSTANCE : new EmptyTargetSource(targetClass, isStatic));
    }


    //---------------------------------------------------------------------
    // Instance implementation
    //---------------------------------------------------------------------

    private final Class targetClass;

    private final boolean isStatic;


    /**
     * Create a new instance of the {@link EmptyTargetSource} class.
     * <p>This constructor is <code>private</code> to enforce the
     * Singleton pattern / factory method pattern.
     * @param targetClass the target class to expose (may be <code>null</code>)
     * @param isStatic whether the TargetSource is marked as static
     */
    private EmptyTargetSource(Class targetClass, boolean isStatic) {
        this.targetClass = targetClass;
        this.isStatic = isStatic;
    }

    /**
     * Always returns the specified target Class, or <code>null</code> if none.
     */
    public Class getTargetClass() {
        return this.targetClass;
    }

    /**
     * Always returns <code>true</code>.
     */
    public boolean isStatic() {
        return this.isStatic;
    }

    /**
     * Always returns <code>null</code>.
     */
    public Object getTarget() {
        return null;
    }

    /**
     * Nothing to release.
     */
    public void releaseTarget(Object target) {
    }


    /**
     * Returns the canonical instance on deserialization in case
     * of no target class, thus protecting the Singleton pattern.
     */
    private Object readResolve() {
        return (this.targetClass == null && this.isStatic ? INSTANCE : this);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EmptyTargetSource)) {
            return false;
        }
        EmptyTargetSource otherTs = (EmptyTargetSource) other;
        return (ObjectUtils.nullSafeEquals(this.targetClass, otherTs.targetClass) && this.isStatic == otherTs.isStatic);
    }

    public int hashCode() {
        return EmptyTargetSource.class.hashCode() * 13 + ObjectUtils.nullSafeHashCode(this.targetClass);
    }

    public String toString() {
        return "EmptyTargetSource: " +
                (this.targetClass != null ? "target class [" + this.targetClass.getName() + "]" : "no target class") +
                ", " + (this.isStatic ? "static" : "dynamic");
    }

}


