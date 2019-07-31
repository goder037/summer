package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.core.type.AnnotationMetadata;

/**
 * @author Juergen Hoeller
 * @author Phil Webb
 */
interface ImportRegistry {

    AnnotationMetadata getImportingClassFor(String importedClass);

    void removeImportingClass(String importingClass);

}