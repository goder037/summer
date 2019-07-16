package com.rocket.summer.framework.aop.framework.autoproxy;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.core.Conventions;

/**
 * Utilities for auto-proxy aware components.
 * Mainly for internal use within the framework.
 *
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see AbstractAutoProxyCreator
 */
public abstract class AutoProxyUtils {

    /**
     * Bean definition attribute that may indicate whether a given bean is supposed
     * to be proxied with its target class (in case of it getting proxied in the first
     * place). The value is <code>Boolean.TRUE</code> or <code>Boolean.FALSE</code>.
     * <p>Proxy factories can set this attribute if they built a target class proxy
     * for a specific bean, and want to enforce that that bean can always be cast
     * to its target class (even if AOP advices get applied through auto-proxying).
     */
    public static final String PRESERVE_TARGET_CLASS_ATTRIBUTE =
            Conventions.getQualifiedAttributeName(AutoProxyUtils.class, "preserveTargetClass");


    /**
     * Determine whether the given bean should be proxied with its target
     * class rather than its interfaces. Checks the
     * {@link #PRESERVE_TARGET_CLASS_ATTRIBUTE "preserveTargetClass" attribute}
     * of the corresponding bean definition.
     * @param beanFactory the containing ConfigurableListableBeanFactory
     * @param beanName the name of the bean
     * @return whether the given bean should be proxied with its target class
     */
    public static boolean shouldProxyTargetClass(ConfigurableListableBeanFactory beanFactory, String beanName) {
        if (beanFactory.containsBeanDefinition(beanName)) {
            BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
            return Boolean.TRUE.equals(bd.getAttribute(PRESERVE_TARGET_CLASS_ATTRIBUTE));
        }
        return false;
    }

}
