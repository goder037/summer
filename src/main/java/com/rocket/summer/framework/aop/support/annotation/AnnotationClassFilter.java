package com.rocket.summer.framework.aop.support.annotation;

import com.rocket.summer.framework.aop.ClassFilter;
import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.util.Assert;

import java.lang.annotation.Annotation;

/**
 * Simple ClassFilter that looks for a specific Java 5 annotation
 * being present on a class.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see AnnotationMatchingPointcut
 */
public class AnnotationClassFilter implements ClassFilter {

    private final Class<? extends Annotation> annotationType;

    private final boolean checkInherited;


    /**
     * Create a new AnnotationClassFilter for the given annotation type.
     * @param annotationType the annotation type to look for
     */
    public AnnotationClassFilter(Class<? extends Annotation> annotationType) {
        this(annotationType, false);
    }

    /**
     * Create a new AnnotationClassFilter for the given annotation type.
     * @param annotationType the annotation type to look for
     * @param checkInherited whether to explicitly check the superclasses and
     * interfaces for the annotation type as well (even if the annotation type
     * is not marked as inherited itself)
     */
    public AnnotationClassFilter(Class<? extends Annotation> annotationType, boolean checkInherited) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        this.annotationType = annotationType;
        this.checkInherited = checkInherited;
    }


    @Override
    public boolean matches(Class<?> clazz) {
        return (this.checkInherited ?
                (AnnotationUtils.findAnnotation(clazz, this.annotationType) != null) :
                clazz.isAnnotationPresent(this.annotationType));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AnnotationClassFilter)) {
            return false;
        }
        AnnotationClassFilter otherCf = (AnnotationClassFilter) other;
        return (this.annotationType.equals(otherCf.annotationType) && this.checkInherited == otherCf.checkInherited);
    }

    @Override
    public int hashCode() {
        return this.annotationType.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + this.annotationType;
    }

}

