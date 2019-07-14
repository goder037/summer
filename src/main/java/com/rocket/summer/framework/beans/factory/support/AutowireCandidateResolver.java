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

}
