package com.rocket.summer.framework.aop.framework;

import com.rocket.summer.framework.core.NamedThreadLocal;

/**
 * Class containing static methods used to obtain information about the current AOP invocation.
 *
 * <p>The <code>currentProxy()</code> method is usable if the AOP framework is configured to
 * expose the current proxy (not the default). It returns the AOP proxy in use. Target objects
 * or advice can use this to make advised calls, in the same way as <code>getEJBObject()</code>
 * can be used in EJBs. They can also use it to find advice configuration.
 *
 * <p>Spring's AOP framework does not expose proxies by default, as there is a performance cost
 * in doing so.
 *
 * <p>The functionality in this class might be used by a target object that needed access
 * to resources on the invocation. However, this approach should not be used when there is
 * a reasonable alternative, as it makes application code dependent on usage under AOP and
 * the Spring AOP framework in particular.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 13.03.2003
 */
public abstract class AopContext {

    /**
     * ThreadLocal holder for AOP proxy associated with this thread.
     * Will contain <code>null</code> unless the "exposeProxy" property on
     * the controlling proxy configuration has been set to "true".
     * @see ProxyConfig#setExposeProxy
     */
    private static final ThreadLocal currentProxy = new NamedThreadLocal("Current AOP proxy");


    /**
     * Try to return the current AOP proxy. This method is usable only if the
     * calling method has been invoked via AOP, and the AOP framework has been set
     * to expose proxies. Otherwise, this method will throw an IllegalStateException.
     * @return Object the current AOP proxy (never returns <code>null</code>)
     * @throws IllegalStateException if the proxy cannot be found, because the
     * method was invoked outside an AOP invocation context, or because the
     * AOP framework has not been configured to expose the proxy
     */
    public static Object currentProxy() throws IllegalStateException {
        Object proxy = currentProxy.get();
        if (proxy == null) {
            throw new IllegalStateException(
                    "Cannot find current proxy: Set 'exposeProxy' property on Advised to 'true' to make it available.");
        }
        return proxy;
    }

    /**
     * Make the given proxy available via the <code>currentProxy()</code> method.
     * <p>Note that the caller should be careful to keep the old value as appropriate.
     * @param proxy the proxy to expose (or <code>null</code> to reset it)
     * @return the old proxy, which may be <code>null</code> if none was bound
     * @see #currentProxy()
     */
    static Object setCurrentProxy(Object proxy) {
        Object old = currentProxy.get();
        currentProxy.set(proxy);
        return old;
    }

}
