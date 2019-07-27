package com.rocket.summer.framework.aop.framework;

import com.rocket.summer.framework.aop.SpringProxy;
import com.rocket.summer.framework.util.ClassUtils;

import java.io.Serializable;

/**
 * Default {@link AopProxyFactory} implementation,
 * creating either a CGLIB proxy or a JDK dynamic proxy.
 *
 * <p>Creates a CGLIB proxy if one the following is true
 * for a given {@link AdvisedSupport} instance:
 * <ul>
 * <li>the "optimize" flag is set
 * <li>the "proxyTargetClass" flag is set
 * <li>no proxy interfaces have been specified
 * </ul>
 *
 * <p>Note that the CGLIB library classes have to be present on
 * the class path if an actual CGLIB proxy needs to be created.
 *
 * <p>In general, specify "proxyTargetClass" to enforce a CGLIB proxy,
 * or specify one or more interfaces to use a JDK dynamic proxy.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 12.03.2004
 * @see AdvisedSupport#setOptimize
 * @see AdvisedSupport#setProxyTargetClass
 * @see AdvisedSupport#setInterfaces
 */
public class DefaultAopProxyFactory implements AopProxyFactory, Serializable {

    /** Whether the CGLIB2 library is present on the classpath */
    private static final boolean cglibAvailable =
            ClassUtils.isPresent("net.sf.cglib.proxy.Enhancer", DefaultAopProxyFactory.class.getClassLoader());


    public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
        if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {
            Class targetClass = config.getTargetClass();
            if (targetClass == null) {
                throw new AopConfigException("TargetSource cannot determine target class: " +
                        "Either an interface or a target is required for proxy creation.");
            }
            if (targetClass.isInterface()) {
                return new JdkDynamicAopProxy(config);
            }
            if (!cglibAvailable) {
                throw new AopConfigException(
                        "Cannot proxy target class because CGLIB2 is not available. " +
                                "Add CGLIB to the class path or specify proxy interfaces.");
            }
            return CglibProxyFactory.createCglibProxy(config);
        }
        else {
            return new JdkDynamicAopProxy(config);
        }
    }

    /**
     * Determine whether the supplied {@link AdvisedSupport} has only the
     * {@link com.rocket.summer.framework.aop.SpringProxy} interface specified
     * (or no proxy interfaces specified at all).
     */
    private boolean hasNoUserSuppliedProxyInterfaces(AdvisedSupport config) {
        Class[] interfaces = config.getProxiedInterfaces();
        return (interfaces.length == 0 || (interfaces.length == 1 && SpringProxy.class.equals(interfaces[0])));
    }


    /**
     * Inner factory class used to just introduce a CGLIB2 dependency
     * when actually creating a CGLIB proxy.
     */
    private static class CglibProxyFactory {

        public static AopProxy createCglibProxy(AdvisedSupport advisedSupport) {
            return new Cglib2AopProxy(advisedSupport);
        }
    }

}

