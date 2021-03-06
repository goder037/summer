package com.rocket.summer.framework.aop.support.annotation;

import com.rocket.summer.framework.aop.ClassFilter;
import com.rocket.summer.framework.aop.MethodMatcher;
import com.rocket.summer.framework.aop.Pointcut;
import com.rocket.summer.framework.util.Assert;

import java.lang.annotation.Annotation;

/**
 * Simple Pointcut that looks for a specific Java 5 annotation
 * being present on a {@link #forClassAnnotation class} or
 * {@link #forMethodAnnotation method}.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see AnnotationClassFilter
 * @see AnnotationMethodMatcher
 */
public class AnnotationMatchingPointcut implements Pointcut {

    private final ClassFilter classFilter;

    private final MethodMatcher methodMatcher;


    /**
     * Create a new AnnotationMatchingPointcut for the given annotation type.
     * @param classAnnotationType the annotation type to look for at the class level
     */
    public AnnotationMatchingPointcut(Class<? extends Annotation> classAnnotationType) {
        this(classAnnotationType, false);
    }

    /**
     * Create a new AnnotationMatchingPointcut for the given annotation type.
     * @param classAnnotationType the annotation type to look for at the class level
     * @param checkInherited whether to also check the superclasses and interfaces
     * as well as meta-annotations for the annotation type
     * @see AnnotationClassFilter#AnnotationClassFilter(Class, boolean)
     */
    public AnnotationMatchingPointcut(Class<? extends Annotation> classAnnotationType, boolean checkInherited) {
        this.classFilter = new AnnotationClassFilter(classAnnotationType, checkInherited);
        this.methodMatcher = MethodMatcher.TRUE;
    }

    /**
     * Create a new AnnotationMatchingPointcut for the given annotation type.
     * @param classAnnotationType the annotation type to look for at the class level
     * (can be {@code null})
     * @param methodAnnotationType the annotation type to look for at the method level
     * (can be {@code null})
     */
    public AnnotationMatchingPointcut(
            Class<? extends Annotation> classAnnotationType, Class<? extends Annotation> methodAnnotationType) {

        Assert.isTrue((classAnnotationType != null || methodAnnotationType != null),
                "Either Class annotation type or Method annotation type needs to be specified (or both)");

        if (classAnnotationType != null) {
            this.classFilter = new AnnotationClassFilter(classAnnotationType);
        }
        else {
            this.classFilter = ClassFilter.TRUE;
        }

        if (methodAnnotationType != null) {
            this.methodMatcher = new AnnotationMethodMatcher(methodAnnotationType);
        }
        else {
            this.methodMatcher = MethodMatcher.TRUE;
        }
    }


    @Override
    public ClassFilter getClassFilter() {
        return this.classFilter;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this.methodMatcher;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AnnotationMatchingPointcut)) {
            return false;
        }
        AnnotationMatchingPointcut otherPointcut = (AnnotationMatchingPointcut) other;
        return (this.classFilter.equals(otherPointcut.classFilter) &&
                this.methodMatcher.equals(otherPointcut.methodMatcher));
    }

    @Override
    public int hashCode() {
        return this.classFilter.hashCode() * 37 + this.methodMatcher.hashCode();
    }

    @Override
    public String toString() {
        return "AnnotationMatchingPointcut: " + this.classFilter + ", " +this.methodMatcher;
    }


    /**
     * Factory method for an AnnotationMatchingPointcut that matches
     * for the specified annotation at the class level.
     * @param annotationType the annotation type to look for at the class level
     * @return the corresponding AnnotationMatchingPointcut
     */
    public static AnnotationMatchingPointcut forClassAnnotation(Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        return new AnnotationMatchingPointcut(annotationType);
    }

    /**
     * Factory method for an AnnotationMatchingPointcut that matches
     * for the specified annotation at the method level.
     * @param annotationType the annotation type to look for at the method level
     * @return the corresponding AnnotationMatchingPointcut
     */
    public static AnnotationMatchingPointcut forMethodAnnotation(Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        return new AnnotationMatchingPointcut(null, annotationType);
    }

}

