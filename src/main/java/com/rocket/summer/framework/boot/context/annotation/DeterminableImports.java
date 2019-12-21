package com.rocket.summer.framework.boot.context.annotation;

import com.rocket.summer.framework.beans.factory.Aware;
import com.rocket.summer.framework.context.annotation.ImportBeanDefinitionRegistrar;
import com.rocket.summer.framework.core.type.AnnotationMetadata;

import java.util.Set;

/**
 * Interface that can be implemented by {@link ImportSelector} and
 * {@link ImportBeanDefinitionRegistrar} implementations when they can determine imports
 * early. The {@link ImportSelector} and {@link ImportBeanDefinitionRegistrar} interfaces
 * are quite flexible which can make it hard to tell exactly what bean definitions they
 * will add. This interface should be used when an implementation consistently results in
 * the same imports, given the same source.
 * <p>
 * Using {@link DeterminableImports} is particularly useful when working with Spring's
 * testing support. It allows for better generation of {@link ApplicationContext} cache
 * keys.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @since 1.5.0
 */
public interface DeterminableImports {

    /**
     * Return a set of objects that represent the imports. Objects within the returned
     * {@code Set} must implement a valid {@link Object#hashCode() hashCode} and
     * {@link Object#equals(Object) equals}.
     * <p>
     * Imports from multiple {@link DeterminableImports} instances may be combined by the
     * caller to create a complete set.
     * <p>
     * Unlike {@link ImportSelector} and {@link ImportBeanDefinitionRegistrar} any
     * {@link Aware} callbacks will not be invoked before this method is called.
     * @param metadata the source meta-data
     * @return a key representing the annotations that actually drive the import
     */
    Set<Object> determineImports(AnnotationMetadata metadata);

}

