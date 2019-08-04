package com.rocket.summer.framework.core.type;

import com.rocket.summer.framework.core.annotation.AnnotatedElementUtils;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * {@link MethodMetadata} implementation that uses standard reflection
 * to introspect a given {@code Method}.
 *
 * @author Juergen Hoeller
 * @author Mark Pollack
 * @author Chris Beams
 * @author Phillip Webb
 * @since 3.0
 */
public class StandardMethodMetadata implements MethodMetadata {

    private final Method introspectedMethod;

    private final boolean nestedAnnotationsAsMap;


    /**
     * Create a new StandardMethodMetadata wrapper for the given Method.
     * @param introspectedMethod the Method to introspect
     */
    public StandardMethodMetadata(Method introspectedMethod) {
        this(introspectedMethod, false);
    }

    /**
     * Create a new StandardMethodMetadata wrapper for the given Method,
     * providing the option to return any nested annotations or annotation arrays in the
     * form of {@link com.rocket.summer.framework.core.annotation.AnnotationAttributes} instead
     * of actual {@link java.lang.annotation.Annotation} instances.
     * @param introspectedMethod the Method to introspect
     * @param nestedAnnotationsAsMap return nested annotations and annotation arrays as
     * {@link com.rocket.summer.framework.core.annotation.AnnotationAttributes} for compatibility
     * with ASM-based {@link AnnotationMetadata} implementations
     * @since 3.1.1
     */
    public StandardMethodMetadata(Method introspectedMethod, boolean nestedAnnotationsAsMap) {
        Assert.notNull(introspectedMethod, "Method must not be null");
        this.introspectedMethod = introspectedMethod;
        this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
    }


    /**
     * Return the underlying Method.
     */
    public final Method getIntrospectedMethod() {
        return this.introspectedMethod;
    }

    @Override
    public String getMethodName() {
        return this.introspectedMethod.getName();
    }

    @Override
    public String getDeclaringClassName() {
        return this.introspectedMethod.getDeclaringClass().getName();
    }

    @Override
    public String getReturnTypeName() {
        return this.introspectedMethod.getReturnType().getName();
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(this.introspectedMethod.getModifiers());
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(this.introspectedMethod.getModifiers());
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(this.introspectedMethod.getModifiers());
    }

    @Override
    public boolean isOverridable() {
        return (!isStatic() && !isFinal() && !Modifier.isPrivate(this.introspectedMethod.getModifiers()));
    }

    @Override
    public boolean isAnnotated(String annotationName) {
        return AnnotatedElementUtils.isAnnotated(this.introspectedMethod, annotationName);
    }

    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName) {
        return getAnnotationAttributes(annotationName, false);
    }

    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        return AnnotatedElementUtils.getMergedAnnotationAttributes(this.introspectedMethod,
                annotationName, classValuesAsString, this.nestedAnnotationsAsMap);
    }

    @Override
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
        return getAllAnnotationAttributes(annotationName, false);
    }

    @Override
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        return AnnotatedElementUtils.getAllAnnotationAttributes(this.introspectedMethod,
                annotationName, classValuesAsString, this.nestedAnnotationsAsMap);
    }

}
