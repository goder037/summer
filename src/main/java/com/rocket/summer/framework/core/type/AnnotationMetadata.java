package com.rocket.summer.framework.core.type;

import java.util.Map;
import java.util.Set;

/**
 * Interface that defines abstract access to the annotations of a specific
 * class, in a form that does not require that class to be loaded yet.
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @since 2.5
 * @see StandardAnnotationMetadata
 * @see org.springframework.core.type.classreading.MetadataReader#getAnnotationMetadata()
 */
public interface AnnotationMetadata extends ClassMetadata {

    /**
     * Return the names of all annotation types defined on the underlying class.
     * @return the annotation type names
     */
    Set<String> getAnnotationTypes();

    /**
     * Determine whether the underlying class has an annotation of the given
     * type defined.
     * @param annotationType the annotation type to look for
     * @return whether a matching annotation is defined
     */
    boolean hasAnnotation(String annotationType);

    /**
     * Return the names of all meta-annotation types defined on the
     * given annotation type of the underlying class.
     * @return the meta-annotation type names
     */
    Set<String> getMetaAnnotationTypes(String annotationType);

    /**
     * Determine whether the underlying class has an annotation that
     * is itself annotated with the meta-annotation of the given type.
     * @param metaAnnotationType the meta-annotation type to look for
     * @return whether a matching meta-annotation is defined
     */
    boolean hasMetaAnnotation(String metaAnnotationType);

    /**
     * Retrieve the attributes of the annotation of the given type,
     * if any (i.e. if defined on the underlying class).
     * @param annotationType the annotation type to look for
     * @return a Map of attributes, with the attribute name as key
     * (e.g. "value") and the defined attribute value as Map value.
     * This return value will be <code>null</code> if no matching
     * annotation is defined.
     */
    Map<String, Object> getAnnotationAttributes(String annotationType);

    /**
     * Determine whether the underlying class has an annotation or
     * meta-annotation of the given type defined.
     * <p>This is equivalent to a "hasAnnotation || hasMetaAnnotation"
     * check. If this method returns <code>true</code>, then
     * {@link #getAnnotationAttributes} will return a non-null Map.
     * @param annotationType the annotation type to look for
     * @return whether a matching annotation is defined
     */
    boolean isAnnotated(String annotationType);

}

