package com.rocket.summer.framework.aop.target;

import com.rocket.summer.framework.aop.TargetSource;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ObjectUtils;

import java.io.Serializable;

/**
 * Implementation of the {@link org.springframework.aop.TargetSource} interface
 * that holds a given object. This is the default implementation of the TargetSource
 * interface, as used by the Spring AOP framework. There is usually no need to
 * create objects of this class in application code.
 *
 * <p>This class is serializable. However, the actual serializability of a
 * SingletonTargetSource will depend on whether the target is serializable.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see org.springframework.aop.framework.AdvisedSupport#setTarget(Object)
 */
public class SingletonTargetSource implements TargetSource, Serializable {

    /** use serialVersionUID from Spring 1.2 for interoperability */
    private static final long serialVersionUID = 9031246629662423738L;


    /** Target cached and invoked using reflection */
    private final Object target;


    /**
     * Create a new SingletonTargetSource for the given target.
     * @param target the target object
     */
    public SingletonTargetSource(Object target) {
        Assert.notNull(target, "Target object must not be null");
        this.target = target;
    }


    public Class getTargetClass() {
        return this.target.getClass();
    }

    public Object getTarget() {
        return this.target;
    }

    public void releaseTarget(Object target) {
        // nothing to do
    }

    public boolean isStatic() {
        return true;
    }


    /**
     * Two invoker interceptors are equal if they have the same target or if the
     * targets or the targets are equal.
     */
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SingletonTargetSource)) {
            return false;
        }
        SingletonTargetSource otherTargetSource = (SingletonTargetSource) other;
        return this.target.equals(otherTargetSource.target);
    }

    /**
     * SingletonTargetSource uses the hash code of the target object.
     */
    public int hashCode() {
        return this.target.hashCode();
    }

    public String toString() {
        return "SingletonTargetSource for target object [" + ObjectUtils.identityToString(this.target) + "]";
    }

}
