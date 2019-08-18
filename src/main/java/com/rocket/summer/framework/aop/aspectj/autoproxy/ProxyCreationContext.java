package com.rocket.summer.framework.aop.aspectj.autoproxy;

import com.rocket.summer.framework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import com.rocket.summer.framework.core.NamedThreadLocal;

/**
 * Holder for the current proxy creation context, as exposed by auto-proxy creators
 * such as {@link AbstractAdvisorAutoProxyCreator}.
 *
 * @author Juergen Hoeller
 * @author Ramnivas Laddad
 * @since 2.5
 */
public class ProxyCreationContext {

    /** ThreadLocal holding the current proxied bean name during Advisor matching */
    private static final ThreadLocal<String> currentProxiedBeanName =
            new NamedThreadLocal<String>("Name of currently proxied bean");


    /**
     * Return the name of the currently proxied bean instance.
     * @return the name of the bean, or {@code null} if none available
     */
    public static String getCurrentProxiedBeanName() {
        return currentProxiedBeanName.get();
    }

    /**
     * Set the name of the currently proxied bean instance.
     * @param beanName the name of the bean, or {@code null} to reset it
     */
    public static void setCurrentProxiedBeanName(String beanName) {
        if (beanName != null) {
            currentProxiedBeanName.set(beanName);
        }
        else {
            currentProxiedBeanName.remove();
        }
    }

}

