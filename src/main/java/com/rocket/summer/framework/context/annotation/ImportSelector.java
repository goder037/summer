package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.core.type.AnnotationMetadata;

/**
 * Interface to be implemented by types that determine which @{@link Configuration}
 * class(es) should be imported based on a given selection criteria, usually one or
 * more annotation attributes.
 *
 * <p>An {@link ImportSelector} may implement any of the following
 * {@link com.rocket.summer.framework.beans.factory.Aware Aware} interfaces,
 * and their respective methods will be called prior to {@link #selectImports}:
 * <ul>
 * <li>{@link com.rocket.summer.framework.context.EnvironmentAware EnvironmentAware}</li>
 * <li>{@link com.rocket.summer.framework.beans.factory.BeanFactoryAware BeanFactoryAware}</li>
 * <li>{@link com.rocket.summer.framework.beans.factory.BeanClassLoaderAware BeanClassLoaderAware}</li>
 * <li>{@link com.rocket.summer.framework.context.ResourceLoaderAware ResourceLoaderAware}</li>
 * </ul>
 *
 * <p>{@code ImportSelector} implementations are usually processed in the same way
 * as regular {@code @Import} annotations, however, it is also possible to defer
 * selection of imports until all {@code @Configuration} classes have been processed
 * (see {@link DeferredImportSelector} for details).
 *
 * @author Chris Beams
 * @since 3.1
 * @see DeferredImportSelector
 * @see Import
 * @see ImportBeanDefinitionRegistrar
 * @see Configuration
 */
public interface ImportSelector {

    /**
     * Select and return the names of which class(es) should be imported based on
     * the {@link AnnotationMetadata} of the importing @{@link Configuration} class.
     */
    String[] selectImports(AnnotationMetadata importingClassMetadata);

}
