package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.factory.config.BeanDefinitionHolder;
import com.rocket.summer.framework.beans.factory.config.DependencyDescriptor;

/**
 * {@link AutowireCandidateResolver} implementation to use when no annotation
 * support is available. This implementation checks the bean definition only.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 2.5
 */
public class SimpleAutowireCandidateResolver implements AutowireCandidateResolver {

    @Override
    public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        return bdHolder.getBeanDefinition().isAutowireCandidate();
    }

    /**
     * Determine whether the given descriptor is effectively required.
     * <p>The default implementation checks {@link DependencyDescriptor#isRequired()}.
     * @param descriptor the descriptor for the target method parameter or field
     * @return whether the descriptor is marked as required or possibly indicating
     * non-required status some other way (e.g. through a parameter annotation)
     * @since 4.3.9
     * @see DependencyDescriptor#isRequired()
     */
    public boolean isRequired(DependencyDescriptor descriptor) {
        return descriptor.isRequired();
    }

    @Override
    public Object getSuggestedValue(DependencyDescriptor descriptor) {
        return null;
    }

    @Override
    public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, String beanName) {
        return null;
    }

}

