package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;

/**
 * Strategy interface for resolving the scope of bean definitions.
 *
 * @author Mark Fisher
 * @since 2.5
 * @see Scope
 */
public interface ScopeMetadataResolver {

    /**
     * Resolve the {@link ScopeMetadata} appropriate to the supplied
     * bean <code>definition</code>.
     * <p>Implementations can of course use any strategy they like to
     * determine the scope metadata, but some implementations that spring
     * immediately to mind might be to use source level annotations
     * present on {@link BeanDefinition#getBeanClassName() the class} of the
     * supplied <code>definition</code>, or to use metadata present in the
     * {@link BeanDefinition#attributeNames()} of the supplied <code>definition</code>.
     * @param definition the target bean definition
     * @return the relevant scope metadata; never <code>null</code>
     */
    ScopeMetadata resolveScopeMetadata(BeanDefinition definition);

}
