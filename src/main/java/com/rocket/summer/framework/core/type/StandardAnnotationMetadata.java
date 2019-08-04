package com.rocket.summer.framework.core.type;

import com.rocket.summer.framework.core.annotation.AnnotatedElementUtils;
import com.rocket.summer.framework.util.MultiValueMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link AnnotationMetadata} implementation that uses standard reflection
 * to introspect a given {@link Class}.
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Chris Beams
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 2.5
 */
public class StandardAnnotationMetadata extends StandardClassMetadata implements AnnotationMetadata {

    private final Annotation[] annotations;

    private final boolean nestedAnnotationsAsMap;


    /**
     * Create a new {@code StandardAnnotationMetadata} wrapper for the given Class.
     * @param introspectedClass the Class to introspect
     * @see #StandardAnnotationMetadata(Class, boolean)
     */
    public StandardAnnotationMetadata(Class<?> introspectedClass) {
        this(introspectedClass, false);
    }

    /**
     * Create a new {@link StandardAnnotationMetadata} wrapper for the given Class,
     * providing the option to return any nested annotations or annotation arrays in the
     * form of {@link com.rocket.summer.framework.core.annotation.AnnotationAttributes} instead
     * of actual {@link Annotation} instances.
     * @param introspectedClass the Class to introspect
     * @param nestedAnnotationsAsMap return nested annotations and annotation arrays as
     * {@link com.rocket.summer.framework.core.annotation.AnnotationAttributes} for compatibility
     * with ASM-based {@link AnnotationMetadata} implementations
     * @since 3.1.1
     */
    public StandardAnnotationMetadata(Class<?> introspectedClass, boolean nestedAnnotationsAsMap) {
        super(introspectedClass);
        this.annotations = introspectedClass.getAnnotations();
        this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
    }


    @Override
    public Set<String> getAnnotationTypes() {
        Set<String> types = new LinkedHashSet<String>();
        for (Annotation ann : this.annotations) {
            types.add(ann.annotationType().getName());
        }
        return types;
    }

    @Override
    public Set<String> getMetaAnnotationTypes(String annotationName) {
        return (this.annotations.length > 0 ?
                AnnotatedElementUtils.getMetaAnnotationTypes(getIntrospectedClass(), annotationName) : null);
    }

    @Override
    public boolean hasAnnotation(String annotationName) {
        for (Annotation ann : this.annotations) {
            if (ann.annotationType().getName().equals(annotationName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasMetaAnnotation(String annotationName) {
        return (this.annotations.length > 0 &&
                AnnotatedElementUtils.hasMetaAnnotationTypes(getIntrospectedClass(), annotationName));
    }

    @Override
    public boolean isAnnotated(String annotationName) {
        return (this.annotations.length > 0 &&
                AnnotatedElementUtils.isAnnotated(getIntrospectedClass(), annotationName));
    }

    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName) {
        return getAnnotationAttributes(annotationName, false);
    }

    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        return (this.annotations.length > 0 ? AnnotatedElementUtils.getMergedAnnotationAttributes(
                getIntrospectedClass(), annotationName, classValuesAsString, this.nestedAnnotationsAsMap) : null);
    }

    @Override
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
        return getAllAnnotationAttributes(annotationName, false);
    }

    @Override
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        return (this.annotations.length > 0 ? AnnotatedElementUtils.getAllAnnotationAttributes(
                getIntrospectedClass(), annotationName, classValuesAsString, this.nestedAnnotationsAsMap) : null);
    }

    @Override
    public boolean hasAnnotatedMethods(String annotationName) {
        try {
            Method[] methods = getIntrospectedClass().getDeclaredMethods();
            for (Method method : methods) {
                if (!method.isBridge() && method.getAnnotations().length > 0 &&
                        AnnotatedElementUtils.isAnnotated(method, annotationName)) {
                    return true;
                }
            }
            return false;
        }
        catch (Throwable ex) {
            throw new IllegalStateException("Failed to introspect annotated methods on " + getIntrospectedClass(), ex);
        }
    }

    @Override
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        try {
            Method[] methods = getIntrospectedClass().getDeclaredMethods();
            Set<MethodMetadata> annotatedMethods = new LinkedHashSet<MethodMetadata>(4);
            for (Method method : methods) {
                if (!method.isBridge() && method.getAnnotations().length > 0 &&
                        AnnotatedElementUtils.isAnnotated(method, annotationName)) {
                    annotatedMethods.add(new StandardMethodMetadata(method, this.nestedAnnotationsAsMap));
                }
            }
            return annotatedMethods;
        }
        catch (Throwable ex) {
            throw new IllegalStateException("Failed to introspect annotated methods on " + getIntrospectedClass(), ex);
        }
    }
}