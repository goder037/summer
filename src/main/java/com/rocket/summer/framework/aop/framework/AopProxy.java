package com.rocket.summer.framework.aop.framework;

/**
 * Delegate interface for a configured AOP proxy, allowing for the creation
 * of actual proxy objects.
 *
 * <p>Out-of-the-box implementations are available for JDK dynamic proxies
 * and for CGLIB proxies, as applied by {@link DefaultAopProxyFactory}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see DefaultAopProxyFactory
 */
public interface AopProxy {

    /**
     * Create a new proxy object.
     * <p>Uses the AopProxy's default class loader (if necessary for proxy creation):
     * usually, the thread context class loader.
     * @return the new proxy object (never <code>null</code>)
     * @see java.lang.Thread#getContextClassLoader()
     */
    Object getProxy();

    /**
     * Create a new proxy object.
     * <p>Uses the given class loader (if necessary for proxy creation).
     * <code>null</code> will simply be passed down and thus lead to the low-level
     * proxy facility's default, which is usually different from the default chosen
     * by the AopProxy implementation's {@link #getProxy()} method.
     * @param classLoader the class loader to create the proxy with
     * (or <code>null</code> for the low-level proxy facility's default)
     * @return the new proxy object (never <code>null</code>)
     */
    Object getProxy(ClassLoader classLoader);

}

