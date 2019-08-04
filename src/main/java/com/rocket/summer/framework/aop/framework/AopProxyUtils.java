package com.rocket.summer.framework.aop.framework;

import com.rocket.summer.framework.aop.SpringProxy;
import com.rocket.summer.framework.aop.TargetClassAware;
import com.rocket.summer.framework.aop.TargetSource;
import com.rocket.summer.framework.aop.support.AopUtils;
import com.rocket.summer.framework.aop.target.SingletonTargetSource;
import com.rocket.summer.framework.core.DecoratingProxy;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ObjectUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
     * Obtain the singleton target object behind the given proxy, if any.
     * @param candidate the (potential) proxy to check
     * @return the singleton target object managed in a {@link SingletonTargetSource},
     * or {@code null} in any other case (not a proxy, not an existing singleton target)
     * @since 4.3.8
     * @see Advised#getTargetSource()
     * @see SingletonTargetSource#getTarget()
     */
    public static Object getSingletonTarget(Object candidate) {
        if (candidate instanceof Advised) {
            TargetSource targetSource = ((Advised) candidate).getTargetSource();
            if (targetSource instanceof SingletonTargetSource) {
                return ((SingletonTargetSource) targetSource).getTarget();
            }
        }
        return null;
    }

    /**
     * Determine the ultimate target class of the given bean instance, traversing
     * not only a top-level proxy but any number of nested proxies as well &mdash;
     * as long as possible without side effects, that is, just for singleton targets.
     * @param candidate the instance to check (might be an AOP proxy)
     * @return the ultimate target class (or the plain class of the given
     * object as fallback; never {@code null})
     * @see com.rocket.summer.framework.aop.TargetClassAware#getTargetClass()
     * @see Advised#getTargetSource()
     */
    public static Class<?> ultimateTargetClass(Object candidate) {
        Assert.notNull(candidate, "Candidate object must not be null");
        Object current = candidate;
        Class<?> result = null;
        while (current instanceof TargetClassAware) {
            result = ((TargetClassAware) current).getTargetClass();
            current = getSingletonTarget(current);
        }
        if (result == null) {
            result = (AopUtils.isCglibProxy(candidate) ? candidate.getClass().getSuperclass() : candidate.getClass());
        }
        return result;
    }

    /**
     * Determine the complete set of interfaces to proxy for the given AOP configuration.
     * <p>This will always add the {@link Advised} interface unless the AdvisedSupport's
     * {@link AdvisedSupport#setOpaque "opaque"} flag is on. Always adds the
     * {@link com.rocket.summer.framework.aop.SpringProxy} marker interface.
     * @param advised the proxy config
     * @return the complete set of interfaces to proxy
     * @see SpringProxy
     * @see Advised
     */
    public static Class<?>[] completeProxiedInterfaces(AdvisedSupport advised) {
        return completeProxiedInterfaces(advised, false);
    }

    /**
     * Determine the complete set of interfaces to proxy for the given AOP configuration.
     * <p>This will always add the {@link Advised} interface unless the AdvisedSupport's
     * {@link AdvisedSupport#setOpaque "opaque"} flag is on. Always adds the
     * {@link com.rocket.summer.framework.aop.SpringProxy} marker interface.
     * @param advised the proxy config
     * @param decoratingProxy whether to expose the {@link DecoratingProxy} interface
     * @return the complete set of interfaces to proxy
     * @since 4.3
     * @see SpringProxy
     * @see Advised
     * @see DecoratingProxy
     */
    static Class<?>[] completeProxiedInterfaces(AdvisedSupport advised, boolean decoratingProxy) {
        Class<?>[] specifiedInterfaces = advised.getProxiedInterfaces();
        if (specifiedInterfaces.length == 0) {
            // No user-specified interfaces: check whether target class is an interface.
            Class<?> targetClass = advised.getTargetClass();
            if (targetClass != null) {
                if (targetClass.isInterface()) {
                    advised.setInterfaces(targetClass);
                }
                else if (Proxy.isProxyClass(targetClass)) {
                    advised.setInterfaces(targetClass.getInterfaces());
                }
                specifiedInterfaces = advised.getProxiedInterfaces();
            }
        }
        boolean addSpringProxy = !advised.isInterfaceProxied(SpringProxy.class);
        boolean addAdvised = !advised.isOpaque() && !advised.isInterfaceProxied(Advised.class);
        boolean addDecoratingProxy = (decoratingProxy && !advised.isInterfaceProxied(DecoratingProxy.class));
        int nonUserIfcCount = 0;
        if (addSpringProxy) {
            nonUserIfcCount++;
        }
        if (addAdvised) {
            nonUserIfcCount++;
        }
        if (addDecoratingProxy) {
            nonUserIfcCount++;
        }
        Class<?>[] proxiedInterfaces = new Class<?>[specifiedInterfaces.length + nonUserIfcCount];
        System.arraycopy(specifiedInterfaces, 0, proxiedInterfaces, 0, specifiedInterfaces.length);
        int index = specifiedInterfaces.length;
        if (addSpringProxy) {
            proxiedInterfaces[index] = SpringProxy.class;
            index++;
        }
        if (addAdvised) {
            proxiedInterfaces[index] = Advised.class;
            index++;
        }
        if (addDecoratingProxy) {
            proxiedInterfaces[index] = DecoratingProxy.class;
        }
        return proxiedInterfaces;
    }

    /**
     * Extract the user-specified interfaces that the given proxy implements,
     * i.e. all non-Advised interfaces that the proxy implements.
     * @param proxy the proxy to analyze (usually a JDK dynamic proxy)
     * @return all user-specified interfaces that the proxy implements,
     * in the original order (never {@code null} or empty)
     * @see Advised
     */
    public static Class<?>[] proxiedUserInterfaces(Object proxy) {
        Class<?>[] proxyInterfaces = proxy.getClass().getInterfaces();
        int nonUserIfcCount = 0;
        if (proxy instanceof SpringProxy) {
            nonUserIfcCount++;
        }
        if (proxy instanceof Advised) {
            nonUserIfcCount++;
        }
        if (proxy instanceof DecoratingProxy) {
            nonUserIfcCount++;
        }
        Class<?>[] userInterfaces = new Class<?>[proxyInterfaces.length - nonUserIfcCount];
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


    /**
     * Adapt the given arguments to the target signature in the given method,
     * if necessary: in particular, if a given vararg argument array does not
     * match the array type of the declared vararg parameter in the method.
     * @param method the target method
     * @param arguments the given arguments
     * @return a cloned argument array, or the original if no adaptation is needed
     * @since 4.2.3
     */
    static Object[] adaptArgumentsIfNecessary(Method method, Object... arguments) {
        if (method.isVarArgs() && !ObjectUtils.isEmpty(arguments)) {
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length == arguments.length) {
                int varargIndex = paramTypes.length - 1;
                Class<?> varargType = paramTypes[varargIndex];
                if (varargType.isArray()) {
                    Object varargArray = arguments[varargIndex];
                    if (varargArray instanceof Object[] && !varargType.isInstance(varargArray)) {
                        Object[] newArguments = new Object[arguments.length];
                        System.arraycopy(arguments, 0, newArguments, 0, varargIndex);
                        Class<?> targetElementType = varargType.getComponentType();
                        int varargLength = Array.getLength(varargArray);
                        Object newVarargArray = Array.newInstance(targetElementType, varargLength);
                        System.arraycopy(varargArray, 0, newVarargArray, 0, varargLength);
                        newArguments[varargIndex] = newVarargArray;
                        return newArguments;
                    }
                }
            }
        }
        return arguments;
    }

}
