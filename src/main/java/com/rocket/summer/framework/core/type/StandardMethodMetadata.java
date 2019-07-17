package com.rocket.summer.framework.core.type;

import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * {@link MethodMetadata} implementation that uses standard reflection
 * to introspect a given <code>Method</code>.
 *
 * @author Juergen Hoeller
 * @author Mark Pollack
 * @author Chris Beams
 * @since 3.0
 */
public class StandardMethodMetadata implements MethodMetadata {

    private final Method introspectedMethod;


    /**
     * Create a new StandardMethodMetadata wrapper for the given Method.
     * @param introspectedMethod the Method to introspect
     */
    public StandardMethodMetadata(Method introspectedMethod) {
        Assert.notNull(introspectedMethod, "Method must not be null");
        this.introspectedMethod = introspectedMethod;
    }

    /**
     * Return the underlying Method.
     */
    public final Method getIntrospectedMethod() {
        return this.introspectedMethod;
    }


    public String getMethodName() {
        return this.introspectedMethod.getName();
    }

    public String getDeclaringClassName() {
        return this.introspectedMethod.getDeclaringClass().getName();
    }

    public boolean isStatic() {
        return Modifier.isStatic(this.introspectedMethod.getModifiers());
    }

    public boolean isFinal() {
        return Modifier.isFinal(this.introspectedMethod.getModifiers());
    }

    public boolean isOverridable() {
        return (!isStatic() && !isFinal() && !Modifier.isPrivate(this.introspectedMethod.getModifiers()));
    }

    public boolean isAnnotated(String annotationType) {
        Annotation[] anns = this.introspectedMethod.getAnnotations();
        for (Annotation ann : anns) {
            if (ann.annotationType().getName().equals(annotationType)) {
                return true;
            }
            for (Annotation metaAnn : ann.annotationType().getAnnotations()) {
                if (metaAnn.annotationType().getName().equals(annotationType)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Map<String, Object> getAnnotationAttributes(String annotationType) {
        Annotation[] anns = this.introspectedMethod.getAnnotations();
        for (Annotation ann : anns) {
            if (ann.annotationType().getName().equals(annotationType)) {
                return AnnotationUtils.getAnnotationAttributes(ann, true);
            }
            for (Annotation metaAnn : ann.annotationType().getAnnotations()) {
                if (metaAnn.annotationType().getName().equals(annotationType)) {
                    return AnnotationUtils.getAnnotationAttributes(metaAnn, true);
                }
            }
        }
        return null;
    }

}
