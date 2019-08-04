package com.rocket.summer.framework.beans.factory;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;

/**
 * A simple descriptor for an injection point, pointing to a method/constructor
 * parameter or a field. Exposed by {@link UnsatisfiedDependencyException}.
 * Also available as an argument for factory methods, reacting to the
 * requesting injection point for building a customized bean instance.
 *
 * @author Juergen Hoeller
 * @since 4.3
 * @see UnsatisfiedDependencyException#getInjectionPoint()
 * @see com.rocket.summer.framework.beans.factory.config.DependencyDescriptor
 */
public class InjectionPoint {

    protected MethodParameter methodParameter;

    protected Field field;

    private volatile Annotation[] fieldAnnotations;


    /**
     * Create an injection point descriptor for a method or constructor parameter.
     * @param methodParameter the MethodParameter to wrap
     */
    public InjectionPoint(MethodParameter methodParameter) {
        Assert.notNull(methodParameter, "MethodParameter must not be null");
        this.methodParameter = methodParameter;
    }

    /**
     * Create an injection point descriptor for a field.
     * @param field the field to wrap
     */
    public InjectionPoint(Field field) {
        Assert.notNull(field, "Field must not be null");
        this.field = field;
    }

    /**
     * Copy constructor.
     * @param original the original descriptor to create a copy from
     */
    protected InjectionPoint(InjectionPoint original) {
        this.methodParameter = (original.methodParameter != null ?
                new MethodParameter(original.methodParameter) : null);
        this.field = original.field;
        this.fieldAnnotations = original.fieldAnnotations;
    }

    /**
     * Just available for serialization purposes in subclasses.
     */
    protected InjectionPoint() {
    }


    /**
     * Return the wrapped MethodParameter, if any.
     * <p>Note: Either MethodParameter or Field is available.
     * @return the MethodParameter, or {@code null} if none
     */
    public MethodParameter getMethodParameter() {
        return this.methodParameter;
    }

    /**
     * Return the wrapped Field, if any.
     * <p>Note: Either MethodParameter or Field is available.
     * @return the Field, or {@code null} if none
     */
    public Field getField() {
        return this.field;
    }

    /**
     * Obtain the annotations associated with the wrapped field or method/constructor parameter.
     */
    public Annotation[] getAnnotations() {
        if (this.field != null) {
            Annotation[] fieldAnnotations = this.fieldAnnotations;
            if (fieldAnnotations == null) {
                fieldAnnotations = this.field.getAnnotations();
                this.fieldAnnotations = fieldAnnotations;
            }
            return fieldAnnotations;
        }
        else {
            return this.methodParameter.getParameterAnnotations();
        }
    }

    /**
     * Retrieve a field/parameter annotation of the given type, if any.
     * @param annotationType the annotation type to retrieve
     * @return the annotation instance, or {@code null} if none found
     * @since 4.3.9
     */
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return (this.field != null ? this.field.getAnnotation(annotationType) :
                this.methodParameter.getParameterAnnotation(annotationType));
    }

    /**
     * Return the type declared by the underlying field or method/constructor parameter,
     * indicating the injection type.
     */
    public Class<?> getDeclaredType() {
        return (this.field != null ? this.field.getType() : this.methodParameter.getParameterType());
    }

    /**
     * Returns the wrapped member, containing the injection point.
     * @return the Field / Method / Constructor as Member
     */
    public Member getMember() {
        return (this.field != null ? this.field : this.methodParameter.getMember());
    }

    /**
     * Return the wrapped annotated element.
     * <p>Note: In case of a method/constructor parameter, this exposes
     * the annotations declared on the method or constructor itself
     * (i.e. at the method/constructor level, not at the parameter level).
     * Use {@link #getAnnotations()} to obtain parameter-level annotations in
     * such a scenario, transparently with corresponding field annotations.
     * @return the Field / Method / Constructor as AnnotatedElement
     */
    public AnnotatedElement getAnnotatedElement() {
        return (this.field != null ? this.field : this.methodParameter.getAnnotatedElement());
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (getClass() != other.getClass()) {
            return false;
        }
        InjectionPoint otherPoint = (InjectionPoint) other;
        return (this.field != null ? this.field.equals(otherPoint.field) :
                this.methodParameter.equals(otherPoint.methodParameter));
    }

    @Override
    public int hashCode() {
        return (this.field != null ? this.field.hashCode() : this.methodParameter.hashCode());
    }

    @Override
    public String toString() {
        return (this.field != null ? "field '" + this.field.getName() + "'" : this.methodParameter.toString());
    }

}
