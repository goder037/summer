package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.factory.config.BeanDefinitionHolder;
import com.rocket.summer.framework.beans.factory.config.DependencyDescriptor;

/**
 * Strategy interface for determining whether a specific bean definition
 * qualifies as an autowire candidate for a specific dependency.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 2.5
 */
public interface AutowireCandidateResolver {

    /**
     * Determine whether the given bean definition qualifies as an
     * autowire candidate for the given dependency.
     * @param bdHolder the bean definition including bean name and aliases
     * @param descriptor the descriptor for the target method parameter or field
     * @return whether the bean definition qualifies as autowire candidate
     */
    boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor);

    /**
     * Determine whether a default value is suggested for the given dependency.
     * @param descriptor the descriptor for the target method parameter or field
     * @return the value suggested (typically an expression String),
     * or {@code null} if none found
     * @since 3.0
     */
    Object getSuggestedValue(DependencyDescriptor descriptor);

    /**
     * Build a proxy for lazy resolution of the actual dependency target,
     * if demanded by the injection point.
     * @param descriptor the descriptor for the target method parameter or field
     * @param beanName the name of the bean that contains the injection point
     * @return the lazy resolution proxy for the actual dependency target,
     * or {@code null} if straight resolution is to be performed
     * @since 4.0
     */
    Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, String beanName);
}
