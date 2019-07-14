package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.BeanMetadataElement;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ObjectUtils;

import java.lang.reflect.Method;

/**
 * Object representing the override of a method on a managed
 * object by the IoC container.
 *
 * <p>Note that the override mechanism is <i>not</i> intended as a
 * generic means of inserting crosscutting code: use AOP for that.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 1.1
 */
public abstract class MethodOverride implements BeanMetadataElement {

    private final String methodName;

    private boolean overloaded = true;

    private Object source;


    /**
     * Construct a new override for the given method.
     * @param methodName the name of the method to override
     */
    protected MethodOverride(String methodName) {
        Assert.notNull(methodName, "Method name must not be null");
        this.methodName = methodName;
    }

    /**
     * Return the name of the method to be overridden.
     */
    public String getMethodName() {
        return this.methodName;
    }

    /**
     * Set whether the overridden method has to be considered as overloaded
     * (that is, whether arg type matching has to happen).
     * <p>Default is "true"; can be switched to "false" to optimize runtime performance.
     */
    protected void setOverloaded(boolean overloaded) {
        this.overloaded = overloaded;
    }

    /**
     * Return whether the overridden method has to be considered as overloaded
     * (that is, whether arg type matching has to happen).
     */
    protected boolean isOverloaded() {
        return this.overloaded;
    }

    /**
     * Set the configuration source <code>Object</code> for this metadata element.
     * <p>The exact type of the object will depend on the configuration mechanism used.
     */
    public void setSource(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return this.source;
    }


    /**
     * Subclasses must override this to indicate whether they match
     * the given method. This allows for argument list checking
     * as well as method name checking.
     * @param method the method to check
     * @return whether this override matches the given method
     */
    public abstract boolean matches(Method method);


    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MethodOverride)) {
            return false;
        }
        MethodOverride that = (MethodOverride) other;
        return (ObjectUtils.nullSafeEquals(this.methodName, that.methodName) &&
                this.overloaded == that.overloaded &&
                ObjectUtils.nullSafeEquals(this.source, that.source));
    }

    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(this.methodName);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.source);
        hashCode = 29 * hashCode + (this.overloaded ? 1 : 0);
        return hashCode;
    }

}
