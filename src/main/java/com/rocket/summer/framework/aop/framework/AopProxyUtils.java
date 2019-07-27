package com.rocket.summer.framework.aop.framework;

import com.rocket.summer.framework.aop.SpringProxy;
import com.rocket.summer.framework.aop.support.AopUtils;
import com.rocket.summer.framework.util.Assert;

import java.util.Arrays;

/**
 * Utility methods for AOP proxy factories.
 * Mainly for internal use within the AOP framework.
 *
 * <p>See {@link com.rocket.summer.framework.aop.support.AopUtils} for a collection of
 * generic AOP utility methods which do not depend on AOP framework internals.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see com.rocket.summer.framework.aop.support.AopUtils
 */
public abstract class AopProxyUtils {

    /**
     * Determine the target class of the given bean instance,
     * which might be an AOP proxy.
     * <p>Returns the target class for an AOP proxy and the plain class else.
     * @param candidate the instance to check (might be an AOP proxy)
     * @return the target class (or the plain class of the given object as fallback)
     * @deprecated as of Spring 2.0.3, in favor of <code>AopUtils.getTargetClass</code>
     * @see com.rocket.summer.framework.aop.support.AopUtils#getTargetClass(Object)
     */
    public static Class getTargetClass(Object candidate) {
        Assert.notNull(candidate, "Candidate object must not be null");
        if (AopUtils.isCglibProxy(candidate)) {
            return candidate.getClass().getSuperclass();
        }
        if (candidate instanceof Advised) {
            return ((Advised) candidate).getTargetSource().getTargetClass();
        }
        return candidate.getClass();
    }

    /**
     * Determine the complete set of interfaces to proxy for the given AOP configuration.
     * <p>This will always add the {@link Advised} interface unless the AdvisedSupport's
     * {@link AdvisedSupport#setOpaque "opaque"} flag is on. Always adds the
     * {@link com.rocket.summer.framework.aop.SpringProxy} marker interface.
     * @return the complete set of interfaces to proxy
     * @see Advised
     * @see com.rocket.summer.framework.aop.SpringProxy
     */
    public static Class[] completeProxiedInterfaces(AdvisedSupport advised) {
        Class[] specifiedInterfaces = advised.getProxiedInterfaces();
        if (specifiedInterfaces.length == 0) {
            // No user-specified interfaces: check whether target class is an interface.
            Class targetClass = advised.getTargetClass();
            if (targetClass != null && targetClass.isInterface()) {
                specifiedInterfaces = new Class[] {targetClass};
            }
        }
        boolean addSpringProxy = !advised.isInterfaceProxied(SpringProxy.class);
        boolean addAdvised = !advised.isOpaque() && !advised.isInterfaceProxied(Advised.class);
        int nonUserIfcCount = 0;
        if (addSpringProxy) {
            nonUserIfcCount++;
        }
        if (addAdvised) {
            nonUserIfcCount++;
        }
        Class[] proxiedInterfaces = new Class[specifiedInterfaces.length + nonUserIfcCount];
        System.arraycopy(specifiedInterfaces, 0, proxiedInterfaces, 0, specifiedInterfaces.length);
        if (addSpringProxy) {
            proxiedInterfaces[specifiedInterfaces.length] = SpringProxy.class;
        }
        if (addAdvised) {
            proxiedInterfaces[proxiedInterfaces.length - 1] = Advised.class;
        }
        return proxiedInterfaces;
    }

    /**
     * Extract the user-specified interfaces that the given proxy implements,
     * i.e. all non-Advised interfaces that the proxy implements.
     * @param proxy the proxy to analyze (usually a JDK dynamic proxy)
     * @return all user-specified interfaces that the proxy implements,
     * in the original order (never <code>null</code> or empty)
     * @see Advised
     */
    public static Class[] proxiedUserInterfaces(Object proxy) {
        Class[] proxyInterfaces = proxy.getClass().getInterfaces();
        int nonUserIfcCount = 0;
        if (proxy instanceof SpringProxy) {
            nonUserIfcCount++;
        }
        if (proxy instanceof Advised) {
            nonUserIfcCount++;
        }
        Class[] userInterfaces = new Class[proxyInterfaces.length - nonUserIfcCount];
        System.arraycopy(proxyInterfaces, 0, userInterfaces, 0, userInterfaces.length);
        Assert.notEmpty(userInterfaces, "JDK proxy must implement one or more interfaces");
        return userInterfaces;
    }

    /**
     * Check equality of the proxies behind the given AdvisedSupport objects.
     * Not the same as equality of the AdvisedSupport objects:
     * rather, equality of interfaces, advisors and target sources.
     */
    public static boolean equalsInProxy(AdvisedSupport a, AdvisedSupport b) {
        return (a == b ||
                (equalsProxiedInterfaces(a, b) && equalsAdvisors(a, b) && a.getTargetSource().equals(b.getTargetSource())));
    }

    /**
     * Check equality of the proxied interfaces behind the given AdvisedSupport objects.
     */
    public static boolean equalsProxiedInterfaces(AdvisedSupport a, AdvisedSupport b) {
        return Arrays.equals(a.getProxiedInterfaces(), b.getProxiedInterfaces());
    }

    /**
     * Check equality of the advisors behind the given AdvisedSupport objects.
     */
    public static boolean equalsAdvisors(AdvisedSupport a, AdvisedSupport b) {
        return Arrays.equals(a.getAdvisors(), b.getAdvisors());
    }

}

