package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.factory.config.BeanDefinitionHolder;
import com.rocket.summer.framework.beans.factory.config.DependencyDescriptor;

/**
 * {@link AutowireCandidateResolver} implementation to use when Java version
 * is less than 1.5 and therefore no annotation support is available. This
 * implementation checks the bean definition only.
 *
 * @author Mark Fisher
 * @since 2.5
 * @see BeanDefinition#isAutowireCandidate()
 */
public class SimpleAutowireCandidateResolver implements AutowireCandidateResolver {

    /**
     * Determine if the provided bean definition is an autowire candidate.
     * <p>To be considered a candidate the bean's <em>autowire-candidate</em>
     * attribute must not have been set to 'false'.
     */
    public boolean isAutowireCandidate(
            BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {

        return bdHolder.getBeanDefinition().isAutowireCandidate();
    }

}
