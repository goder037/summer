package com.rocket.summer.framework.core.type.classreading;

import com.rocket.summer.framework.core.annotation.AnnotationAttributes;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.core.type.MethodMetadata;
import com.rocket.summer.framework.util.CollectionUtils;
import com.rocket.summer.framework.util.LinkedMultiValueMap;
import com.rocket.summer.framework.util.MultiValueMap;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;

import java.util.*;

/**
 * ASM class visitor which looks for the class name and implemented types as
 * well as for the annotations defined on the class, exposing them through
 * the {@link com.rocket.summer.framework.core.type.AnnotationMetadata} interface.
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @since 2.5
 */
class AnnotationMetadataReadingVisitor extends ClassMetadataReadingVisitor implements AnnotationMetadata {

    /**
     * Declared as a {@link LinkedMultiValueMap} instead of a {@link MultiValueMap}
     * to ensure that the hierarchical ordering of the entries is preserved.
     * @see AnnotationReadingVisitorUtils#getMergedAnnotationAttributes
     */
    protected final LinkedMultiValueMap<String, AnnotationAttributes> attributesMap =
            new LinkedMultiValueMap<String, AnnotationAttributes>(4);

    protected final Set<String> annotationSet = new LinkedHashSet<String>(4);

    private final Map<String, Set<String>> metaAnnotationMap = new LinkedHashMap<>();

    private final Map<String, Map<String, Object>> attributeMap = new LinkedHashMap<>(4);

    private final ClassLoader classLoader;

    private final MultiValueMap<String, MethodMetadata> methodMetadataMap = new LinkedMultiValueMap<String, MethodMetadata>();


    public AnnotationMetadataReadingVisitor(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        String className = Type.getType(desc).getClassName();
        this.annotationSet.add(className);
        return new AnnotationAttributesReadingVisitor(
                className, this.attributesMap, this.metaAnnotationMap, this.classLoader);
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
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
        return getAllAnnotationAttributes(annotationName, false);
    }

    @Override
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        MultiValueMap<String, Object> allAttributes = new LinkedMultiValueMap<String, Object>();
        List<AnnotationAttributes> attributes = this.attributesMap.get(annotationName);
        if (attributes == null) {
            return null;
        }
        for (AnnotationAttributes raw : attributes) {
            for (Map.Entry<String, Object> entry : AnnotationReadingVisitorUtils.convertClassValues(
                    "class '" + getClassName() + "'", this.classLoader, raw, classValuesAsString).entrySet()) {
                allAttributes.add(entry.getKey(), entry.getValue());
            }
        }
        return allAttributes;
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

