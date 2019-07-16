package com.rocket.summer.framework.core.type.filter;

import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.core.type.classreading.MetadataReader;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;

/**
 * A simple filter which matches classes with a given annotation,
 * checking inherited annotations as well.
 *
 * <p>The matching logic mirrors that of <code>Class.isAnnotationPresent()</code>.
 *
 * @author Mark Fisher
 * @author Ramnivas Laddad
 * @author Juergen Hoeller
 * @since 2.5
 */
public class AnnotationTypeFilter extends AbstractTypeHierarchyTraversingFilter {

    private final Class<? extends Annotation> annotationType;

    private final boolean considerMetaAnnotations;


    /**
     * Create a new AnnotationTypeFilter for the given annotation type.
     * This filter will also match meta-annotations. To disable the
     * meta-annotation matching, use the constructor that accepts a
     * '<code>considerMetaAnnotations</code>' argument.
     * @param annotationType the annotation type to match
     */
    public AnnotationTypeFilter(Class<? extends Annotation> annotationType) {
        this(annotationType, true);
    }

    /**
     * Create a new AnnotationTypeFilter for the given annotation type.
     * @param annotationType the annotation type to match
     * @param considerMetaAnnotations whether to also match on meta-annotations
     */
    public AnnotationTypeFilter(Class<? extends Annotation> annotationType, boolean considerMetaAnnotations) {
        super(annotationType.isAnnotationPresent(Inherited.class), false);
        this.annotationType = annotationType;
        this.considerMetaAnnotations = considerMetaAnnotations;
    }


    @Override
    protected boolean matchSelf(MetadataReader metadataReader) {
        AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
        return metadata.hasAnnotation(this.annotationType.getName()) ||
                (this.considerMetaAnnotations && metadata.hasMetaAnnotation(this.annotationType.getName()));
    }

    @Override
    protected Boolean matchSuperClass(String superClassName) {
        if (Object.class.getName().equals(superClassName)) {
            return Boolean.FALSE;
        }
        else if (superClassName.startsWith("java.")) {
            try {
                Class clazz = getClass().getClassLoader().loadClass(superClassName);
                return Boolean.valueOf(clazz.getAnnotation(this.annotationType) != null);
            }
            catch (ClassNotFoundException ex) {
                // Class not found - can't determine a match that way.
            }
        }
        return null;
    }

}

