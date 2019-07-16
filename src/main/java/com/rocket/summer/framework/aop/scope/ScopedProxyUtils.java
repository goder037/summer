package com.rocket.summer.framework.aop.scope;

import com.rocket.summer.framework.aop.framework.autoproxy.AutoProxyUtils;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.BeanDefinitionHolder;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.beans.factory.support.RootBeanDefinition;

/**
 * Utility class for creating a scoped proxy.
 * Used by ScopedProxyBeanDefinitionDecorator and ClassPathBeanDefinitionScanner.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2.5
 */
public abstract class ScopedProxyUtils {

    private static final String TARGET_NAME_PREFIX = "scopedTarget.";

    /**
     * Generates a scoped proxy for the supplied target bean, registering the target
     * bean with an internal name and setting 'targetBeanName' on the scoped proxy.
     * @param definition the original bean definition
     * @param registry the bean definition registry
     * @param proxyTargetClass whether to create a target class proxy
     * @return the scoped proxy definition
     */
    public static BeanDefinitionHolder createScopedProxy(BeanDefinitionHolder definition,
                                                         BeanDefinitionRegistry registry, boolean proxyTargetClass) {

        String originalBeanName = definition.getBeanName();
        BeanDefinition targetDefinition = definition.getBeanDefinition();

        // Create a scoped proxy definition for the original bean name,
        // "hiding" the target bean in an internal target definition.
        RootBeanDefinition scopedProxyDefinition = new RootBeanDefinition(ScopedProxyFactoryBean.class);
        scopedProxyDefinition.setOriginatingBeanDefinition(definition.getBeanDefinition());
        scopedProxyDefinition.setSource(definition.getSource());
        scopedProxyDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

        String targetBeanName = getTargetBeanName(originalBeanName);
        scopedProxyDefinition.getPropertyValues().addPropertyValue("targetBeanName", targetBeanName);

        if (proxyTargetClass) {
            targetDefinition.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
            // ScopedFactoryBean's "proxyTargetClass" default is TRUE, so we don't need to set it explicitly here.
        }
        else {
            scopedProxyDefinition.getPropertyValues().addPropertyValue("proxyTargetClass", Boolean.FALSE);
        }

        scopedProxyDefinition.setAutowireCandidate(targetDefinition.isAutowireCandidate());
        // The target bean should be ignored in favor of the scoped proxy.
        targetDefinition.setAutowireCandidate(false);

        // Register the target bean as separate bean in the factory.
        registry.registerBeanDefinition(targetBeanName, targetDefinition);

        // Return the scoped proxy definition as primary bean definition
        // (potentially an inner bean).
        return new BeanDefinitionHolder(scopedProxyDefinition, originalBeanName, definition.getAliases());
    }

    /**
     * Generates the bean name that is used within the scoped proxy to reference the target bean.
     * @param originalBeanName the original name of bean
     * @return the generated bean to be used to reference the target bean
     */
    public static String getTargetBeanName(String originalBeanName) {
        return TARGET_NAME_PREFIX + originalBeanName;
    }
}