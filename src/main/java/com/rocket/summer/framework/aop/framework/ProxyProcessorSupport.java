package com.rocket.summer.framework.aop.framework;

import java.io.Closeable;

import com.rocket.summer.framework.beans.factory.Aware;
import com.rocket.summer.framework.beans.factory.BeanClassLoaderAware;
import com.rocket.summer.framework.beans.factory.DisposableBean;
import com.rocket.summer.framework.beans.factory.InitializingBean;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ObjectUtils;

/**
 * Base class with common functionality for proxy processors, in particular
 * ClassLoader management and the {@link #evaluateProxyInterfaces} algorithm.
 *
 * @author Juergen Hoeller
 * @since 4.1
 * @see AbstractAdvisingBeanPostProcessor
 * @see com.rocket.summer.framework.aop.framework.autoproxy.AbstractAutoProxyCreator
 */
@SuppressWarnings("serial")
public class ProxyProcessorSupport extends ProxyConfig implements Ordered, BeanClassLoaderAware, AopInfrastructureBean {

    /**
     * This should run after all other processors, so that it can just add
     * an advisor to existing proxies rather than double-proxy.
     */
    private int order = Ordered.LOWEST_PRECEDENCE;

    private ClassLoader proxyClassLoader = ClassUtils.getDefaultClassLoader();

    private boolean classLoaderConfigured = false;


    /**
     * Set the ordering which will apply to this processor's implementation
     * of {@link Ordered}, used when applying multiple processors.
     * <p>The default value is {@code Ordered.LOWEST_PRECEDENCE}, meaning non-ordered.
     * @param order the ordering value
     */
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    /**
     * Set the ClassLoader to generate the proxy class in.
     * <p>Default is the bean ClassLoader, i.e. the ClassLoader used by the containing
     * {@link com.rocket.summer.framework.beans.factory.BeanFactory} for loading all bean classes.
     * This can be overridden here for specific proxies.
     */
    public void setProxyClassLoader(ClassLoader classLoader) {
        this.proxyClassLoader = classLoader;
        this.classLoaderConfigured = (classLoader != null);
    }

    /**
     * Return the configured proxy ClassLoader for this processor.
     */
    protected ClassLoader getProxyClassLoader() {
        return this.proxyClassLoader;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        if (!this.classLoaderConfigured) {
            this.proxyClassLoader = classLoader;
        }
    }


    /**
     * Check the interfaces on the given bean class and apply them to the {@link ProxyFactory},
     * if appropriate.
     * <p>Calls {@link #isConfigurationCallbackInterface} and {@link #isInternalLanguageInterface}
     * to filter for reasonable proxy interfaces, falling back to a target-class proxy otherwise.
     * @param beanClass the class of the bean
     * @param proxyFactory the ProxyFactory for the bean
     */
    protected void evaluateProxyInterfaces(Class<?> beanClass, ProxyFactory proxyFactory) {
        Class<?>[] targetInterfaces = ClassUtils.getAllInterfacesForClass(beanClass, getProxyClassLoader());
        boolean hasReasonableProxyInterface = false;
        for (Class<?> ifc : targetInterfaces) {
            if (!isConfigurationCallbackInterface(ifc) && !isInternalLanguageInterface(ifc) &&
                    ifc.getMethods().length > 0) {
                hasReasonableProxyInterface = true;
                break;
            }
        }
        if (hasReasonableProxyInterface) {
            // Must allow for introductions; can't just set interfaces to the target's interfaces only.
            for (Class<?> ifc : targetInterfaces) {
                proxyFactory.addInterface(ifc);
            }
        }
        else {
            proxyFactory.setProxyTargetClass(true);
        }
    }

    /**
     * Determine whether the given interface is just a container callback and
     * therefore not to be considered as a reasonable proxy interface.
     * <p>If no reasonable proxy interface is found for a given bean, it will get
     * proxied with its full target class, assuming that as the user's intention.
     * @param ifc the interface to check
     * @return whether the given interface is just a container callback
     */
    protected boolean isConfigurationCallbackInterface(Class<?> ifc) {
        return (InitializingBean.class == ifc || DisposableBean.class == ifc ||
                Closeable.class == ifc || "java.lang.AutoCloseable".equals(ifc.getName()) ||
                ObjectUtils.containsElement(ifc.getInterfaces(), Aware.class));
    }

    /**
     * Determine whether the given interface is a well-known internal language interface
     * and therefore not to be considered as a reasonable proxy interface.
     * <p>If no reasonable proxy interface is found for a given bean, it will get
     * proxied with its full target class, assuming that as the user's intention.
     * @param ifc the interface to check
     * @return whether the given interface is an internal language interface
     */
    protected boolean isInternalLanguageInterface(Class<?> ifc) {
        return (ifc.getName().equals("groovy.lang.GroovyObject") ||
                ifc.getName().endsWith(".cglib.proxy.Factory") ||
                ifc.getName().endsWith(".bytebuddy.MockAccess"));
    }

}
