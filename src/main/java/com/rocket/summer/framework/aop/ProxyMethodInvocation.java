package com.rocket.summer.framework.aop;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Extension of the AOP Alliance {@link org.aopalliance.intercept.MethodInvocation}
 * interface, allowing access to the proxy that the method invocation was made through.
 *
 * <p>Useful to be able to substitute return values with the proxy,
 * if necessary, for example if the invocation target returned itself.
 *
 * @author Juergen Hoeller
 * @author Adrian Colyer
 * @since 1.1.3
 * @see org.springframework.aop.framework.ReflectiveMethodInvocation
 * @see org.springframework.aop.support.DelegatingIntroductionInterceptor
 */
public interface ProxyMethodInvocation extends MethodInvocation {

    /**
     * Return the proxy that this method invocation was made through.
     * @return the original proxy object
     */
    Object getProxy();

    /**
     * Create a clone of this object. If cloning is done before <code>proceed()</code>
     * is invoked on this object, <code>proceed()</code> can be invoked once per clone
     * to invoke the joinpoint (and the rest of the advice chain) more than once.
     * @return an invocable clone of this invocation.
     * <code>proceed()</code> can be called once per clone.
     */
    MethodInvocation invocableClone();

    /**
     * Create a clone of this object. If cloning is done before <code>proceed()</code>
     * is invoked on this object, <code>proceed()</code> can be invoked once per clone
     * to invoke the joinpoint (and the rest of the advice chain) more than once.
     * @param arguments the arguments that the cloned invocation is supposed to use,
     * overriding the original arguments
     * @return an invocable clone of this invocation.
     * <code>proceed()</code> can be called once per clone.
     */
    MethodInvocation invocableClone(Object[] arguments);

    /**
     * Set the arguments to be used on subsequent invocations in the any advice
     * in this chain.
     * @param arguments the argument array
     */
    void setArguments(Object[] arguments);

    /**
     * Add the specified user attribute with the given value to this invocation.
     * <p>Such attributes are not used within the AOP framework itself. They are
     * just kept as part of the invocation object, for use in special interceptors.
     * @param key the name of the attribute
     * @param value the value of the attribute, or <code>null</code> to reset it
     */
    void setUserAttribute(String key, Object value);

    /**
     * Return the value of the specified user attribute.
     * @param key the name of the attribute
     * @return the value of the attribute, or <code>null</code> if not set
     * @see #setUserAttribute
     */
    Object getUserAttribute(String key);

}
