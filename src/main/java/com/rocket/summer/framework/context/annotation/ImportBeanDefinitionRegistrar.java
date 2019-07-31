package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.core.type.AnnotationMetadata;

/**
 * Interface to be implemented by types that register additional bean definitions when
 * processing @{@link Configuration} classes. Useful when operating at the bean definition
 * level (as opposed to {@code @Bean} method/instance level) is desired or necessary.
 *
 * <p>Along with {@code @Configuration} and {@link ImportSelector}, classes of this type
 * may be provided to the @{@link Import} annotation (or may also be returned from an
 * {@code ImportSelector}).
 *
 * <p>An {@link ImportBeanDefinitionRegistrar} may implement any of the following
 * {@link com.rocket.summer.framework.beans.factory.Aware Aware} interfaces, and their respective
 * methods will be called prior to {@link #registerBeanDefinitions}:
 * <ul>
 * <li>{@link com.rocket.summer.framework.context.EnvironmentAware EnvironmentAware}</li>
 * <li>{@link com.rocket.summer.framework.beans.factory.BeanFactoryAware BeanFactoryAware}
 * <li>{@link com.rocket.summer.framework.beans.factory.BeanClassLoaderAware BeanClassLoaderAware}
 * <li>{@link com.rocket.summer.framework.context.ResourceLoaderAware ResourceLoaderAware}
 * </ul>
 *
 * <p>See implementations and associated unit tests for usage examples.
 *
 * @author Chris Beams
 * @since 3.1
 * @see Import
 * @see ImportSelector
 * @see Configuration
 */
public interface ImportBeanDefinitionRegistrar {

    /**
     * Register bean definitions as necessary based on the given annotation metadata of
     * the importing {@code @Configuration} class.
     * <p>Note that {@link BeanDefinitionRegistryPostProcessor} types may <em>not</em> be
     * registered here, due to lifecycle constraints related to {@code @Configuration}
     * class processing.
     * @param importingClassMetadata annotation metadata of the importing class
     * @param registry current bean definition registry
     */
    void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry);

}

