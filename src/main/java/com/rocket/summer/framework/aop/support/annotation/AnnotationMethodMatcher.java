package com.rocket.summer.framework.aop.support.annotation;

import com.rocket.summer.framework.aop.support.AopUtils;
import com.rocket.summer.framework.aop.support.StaticMethodMatcher;
import com.rocket.summer.framework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Simple MethodMatcher that looks for a specific Java 5 annotation
 * being present on a method (checking both the method on the invoked
 * interface, if any, and the corresponding method on the target class).
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see AnnotationMatchingPointcut
 */
public class AnnotationMethodMatcher extends StaticMethodMatcher {

    private final Class<? extends Annotation> annotationType;


    /**
     * Create a new AnnotationClassFilter for the given annotation type.
     * @param annotationType the annotation type to look for
     */
    public AnnotationMethodMatcher(Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        this.annotationType = annotationType;
    }


    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        if (method.isAnnotationPresent(this.annotationType)) {
            return true;
        }
        // The method may be on an interface, so let's check on the target class as well.
        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
        return (specificMethod != method && specificMethod.isAnnotationPresent(this.annotationType));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AnnotationMethodMatcher)) {
            return false;
        }
        AnnotationMethodMatcher otherMm = (AnnotationMethodMatcher) other;
        return this.annotationType.equals(otherMm.annotationType);
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
