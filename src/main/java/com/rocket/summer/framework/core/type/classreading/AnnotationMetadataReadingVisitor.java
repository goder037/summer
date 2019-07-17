package com.rocket.summer.framework.core.type.classreading;

import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.core.type.MethodMetadata;
import com.rocket.summer.framework.util.CollectionUtils;
import com.rocket.summer.framework.util.LinkedMultiValueMap;
import com.rocket.summer.framework.util.MultiValueMap;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.EmptyVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * ASM class visitor which looks for the class name and implemented types as
 * well as for the annotations defined on the class, exposing them through
 * the {@link org.springframework.core.type.AnnotationMetadata} interface.
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @since 2.5
 */
class AnnotationMetadataReadingVisitor extends ClassMetadataReadingVisitor implements AnnotationMetadata {

    private final Map<String, Map<String, Object>> attributesMap = new LinkedHashMap<>();

    private final Map<String, Set<String>> metaAnnotationMap = new LinkedHashMap<>();

    private final Map<String, Map<String, Object>> attributeMap = new LinkedHashMap<>(4);

    private final ClassLoader classLoader;

    private final MultiValueMap<String, MethodMetadata> methodMetadataMap = new LinkedMultiValueMap<String, MethodMetadata>();


    public AnnotationMetadataReadingVisitor(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc, boolean visible) {
        final String className = Type.getType(desc).getClassName();
        final Map<String, Object> attributes = new LinkedHashMap<String, Object>();
        return new EmptyVisitor() {
            public void visit(String name, Object value) {
                // Explicitly defined annotation attribute value.
                attributes.put(name, value);
            }
            public void visitEnd() {
                try {
                    Class annotationClass = classLoader.loadClass(className);
                    // Check declared default values of attributes in the annotation type.
                    Method[] annotationAttributes = annotationClass.getMethods();
                    for (Method annotationAttribute : annotationAttributes) {
                        String attributeName = annotationAttribute.getName();
                        Object defaultValue = annotationAttribute.getDefaultValue();
                        if (defaultValue != null && !attributes.containsKey(attributeName)) {
                            attributes.put(attributeName, defaultValue);
                        }
                    }
                    // Register annotations that the annotation type is annotated with.
                    Annotation[] metaAnnotations = annotationClass.getAnnotations();
                    Set<String> metaAnnotationTypeNames = new HashSet<>();
                    for (Annotation metaAnnotation : metaAnnotations) {
                        metaAnnotationTypeNames.add(metaAnnotation.annotationType().getName());
                    }
                    metaAnnotationMap.put(className, metaAnnotationTypeNames);
                }
                catch (ClassNotFoundException ex) {
                    // Class not found - can't determine meta-annotations.
                }
                attributesMap.put(className, attributes);
            }
        };
    }


    public Set<String> getAnnotationTypes() {
        return this.attributesMap.keySet();
    }

    public boolean hasAnnotation(String annotationType) {
        return this.attributesMap.containsKey(annotationType);
    }

    public Set<String> getMetaAnnotationTypes(String annotationType) {
        return this.metaAnnotationMap.get(annotationType);
    }

    public boolean hasMetaAnnotation(String metaAnnotationType) {
        Collection<Set<String>> allMetaTypes = this.metaAnnotationMap.values();
        for (Set<String> metaTypes : allMetaTypes) {
            if (metaTypes.contains(metaAnnotationType)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnnotated(String annotationType) {
        return this.attributeMap.containsKey(annotationType);
    }

    public Map<String, Object> getAnnotationAttributes(String annotationType) {
        return getAnnotationAttributes(annotationType, false);
    }

    public Map<String, Object> getAnnotationAttributes(String annotationType, boolean classValuesAsString) {
        Map<String, Object> raw = this.attributeMap.get(annotationType);
        if (raw == null) {
            return null;
        }
        Map<String, Object> result = new LinkedHashMap<>(raw.size());
        for (Map.Entry<String, Object> entry : raw.entrySet()) {
            try {
                Object value = entry.getValue();
                if (value instanceof Type) {
                    value = (classValuesAsString ? ((Type) value).getClassName() :
                            this.classLoader.loadClass(((Type) value).getClassName()));
                }
                else if (value instanceof Type[]) {
                    Type[] array = (Type[]) value;
                    Object[] convArray = (classValuesAsString ? new String[array.length] : new Class[array.length]);
                    for (int i = 0; i < array.length; i++) {
                        convArray[i] = (classValuesAsString ? array[i].getClassName() :
                                this.classLoader.loadClass(array[i].getClassName()));
                    }
                    value = convArray;
                }
                result.put(entry.getKey(), value);
            }
            catch (Exception ex) {
                // Class not found - can't resolve class reference in annotation attribute.
            }
        }
        return result;
    }

    @Override
    public boolean hasAnnotatedMethods(String annotationType) {
        return this.methodMetadataMap.containsKey(annotationType);
    }

    @Override
    public Set<MethodMetadata> getAnnotatedMethods(String annotationType) {
        List<MethodMetadata> list = this.methodMetadataMap.get(annotationType);
        if (CollectionUtils.isEmpty(list)) {
            return new LinkedHashSet<>(0);
        }
        Set<MethodMetadata> annotatedMethods = new LinkedHashSet<MethodMetadata>(list.size());
        annotatedMethods.addAll(list);
        return annotatedMethods;
    }

}

