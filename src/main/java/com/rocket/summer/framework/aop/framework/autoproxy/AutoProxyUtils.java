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
     * Bean definition attribute that indicates the original target class of an
     * auto-proxied bean, e.g. to be used for the introspection of annotations
     * on the target class behind an interface-based proxy.
     * @since 4.2.3
     * @see #determineTargetClass
     */
    public static final String ORIGINAL_TARGET_CLASS_ATTRIBUTE =
            Conventions.getQualifiedAttributeName(AutoProxyUtils.class, "originalTargetClass");

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

    /**
     * Determine the original target class for the specified bean, if possible,
     * otherwise falling back to a regular {@code getType} lookup.
     * @param beanFactory the containing ConfigurableListableBeanFactory
     * @param beanName the name of the bean
     * @return the original target class as stored in the bean definition, if any
     * @since 4.2.3
     * @see com.rocket.summer.framework.beans.factory.BeanFactory#getType(String)
     */
    public static Class<?> determineTargetClass(ConfigurableListableBeanFactory beanFactory, String beanName) {
        if (beanName == null) {
            return null;
        }
        if (beanFactory.containsBeanDefinition(beanName)) {
            BeanDefinition bd = beanFactory.getMergedBeanDefinition(beanName);
            Class<?> targetClass = (Class<?>) bd.getAttribute(ORIGINAL_TARGET_CLASS_ATTRIBUTE);
            if (targetClass != null) {
                return targetClass;
            }
        }
        return beanFactory.getType(beanName);
    }

    /**
     * Expose the given target class for the specified bean, if possible.
     * @param beanFactory the containing ConfigurableListableBeanFactory
     * @param beanName the name of the bean
     * @param targetClass the corresponding target class
     * @since 4.2.3
     */
    static void exposeTargetClass(ConfigurableListableBeanFactory beanFactory, String beanName, Class<?> targetClass) {
        if (beanName != null && beanFactory.containsBeanDefinition(beanName)) {
            beanFactory.getMergedBeanDefinition(beanName).setAttribute(ORIGINAL_TARGET_CLASS_ATTRIBUTE, targetClass);
        }
    }

}
