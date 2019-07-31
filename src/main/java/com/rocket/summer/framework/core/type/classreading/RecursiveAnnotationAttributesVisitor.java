package com.rocket.summer.framework.core.type.classreading;

import com.rocket.summer.framework.core.annotation.AnnotationAttributes;
import com.rocket.summer.framework.core.annotation.AnnotationUtils;

/**
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1.1
 */
class RecursiveAnnotationAttributesVisitor extends AbstractRecursiveAnnotationVisitor {

    protected final String annotationType;


    public RecursiveAnnotationAttributesVisitor(
            String annotationType, AnnotationAttributes attributes, ClassLoader classLoader) {

        super(classLoader, attributes);
        this.annotationType = annotationType;
    }


    @Override
    public void visitEnd() {
        AnnotationUtils.registerDefaultValues(this.attributes);
    }

}
