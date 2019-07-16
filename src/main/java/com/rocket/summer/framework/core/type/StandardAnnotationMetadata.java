package com.rocket.summer.framework.core.type;

import com.rocket.summer.framework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link AnnotationMetadata} implementation that uses standard reflection
 * to introspect a given <code>Class</code>.
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @since 2.5
 */
public class StandardAnnotationMetadata extends StandardClassMetadata implements AnnotationMetadata {

    public StandardAnnotationMetadata(Class introspectedClass) {
        super(introspectedClass);
    }


    public Set<String> getAnnotationTypes() {
        Set<String> types = new HashSet<String>();
        Annotation[] anns = getIntrospectedClass().getAnnotations();
        for (Annotation ann : anns) {
            types.add(ann.annotationType().getName());
        }
        return types;
    }

    public boolean hasAnnotation(String annotationType) {
        Annotation[] anns = getIntrospectedClass().getAnnotations();
        for (Annotation ann : anns) {
            if (ann.annotationType().getName().equals(annotationType)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getMetaAnnotationTypes(String annotationType) {
        Annotation[] anns = getIntrospectedClass().getAnnotations();
        for (Annotation ann : anns) {
            if (ann.annotationType().getName().equals(annotationType)) {
                Set<String> types = new HashSet<String>();
                Annotation[] metaAnns = ann.annotationType().getAnnotations();
                for (Annotation meta : metaAnns) {
                    types.add(meta.annotationType().getName());
                }
                return types;
            }
        }
        return null;
    }

    public boolean hasMetaAnnotation(String annotationType) {
        Annotation[] anns = getIntrospectedClass().getAnnotations();
        for (Annotation ann : anns) {
            Annotation[] metaAnns = ann.annotationType().getAnnotations();
            for (Annotation meta : metaAnns) {
                if (meta.annotationType().getName().equals(annotationType)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Map<String, Object> getAnnotationAttributes(String annotationType) {
        Annotation[] anns = getIntrospectedClass().getAnnotations();
        for (int i = 0; i < anns.length; i++) {
            Annotation ann = anns[i];
            if (ann.annotationType().getName().equals(annotationType)) {
                return AnnotationUtils.getAnnotationAttributes(ann);
            }
        }
        return null;
    }

    public boolean isAnnotated(String annotationType) {
        Annotation[] anns = getIntrospectedClass().getAnnotations();
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

}

