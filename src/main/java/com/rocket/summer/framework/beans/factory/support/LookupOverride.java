package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ObjectUtils;

import java.lang.reflect.Method;

/**
 * Represents an override of a method that looks up an object in the same IoC context.
 * <p>Methods eligible for lookup override must not have arguments.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 1.1
 */
public class LookupOverride extends MethodOverride {

    private final String beanName;


    /**
     * Construct a new LookupOverride.
     * @param methodName the name of the method to override.
     * This method must have no arguments.
     * @param beanName name of the bean in the current BeanFactory
     * that the overriden method should return
     */
    public LookupOverride(String methodName, String beanName) {
        super(methodName);
        Assert.notNull(beanName, "Bean name must not be null");
        this.beanName = beanName;
    }

    /**
     * Return the name of the bean that should be returned by this method.
     */
    public String getBeanName() {
        return this.beanName;
    }


    /**
     * Match method of the given name, with no parameters.
     */
    public boolean matches(Method method) {
        return (method.getName().equals(getMethodName()) && method.getParameterTypes().length == 0);
    }


    public String toString() {
        return "LookupOverride for method '" + getMethodName() + "'; will return bean '" + this.beanName + "'";
    }

    public boolean equals(Object other) {
        return (other instanceof LookupOverride && super.equals(other) &&
                ObjectUtils.nullSafeEquals(this.beanName, ((LookupOverride) other).beanName));
    }

    public int hashCode() {
        return (29 * super.hashCode() + ObjectUtils.nullSafeHashCode(this.beanName));
    }

}