package com.rocket.summer.framework.boot.autoconfigure;

import com.rocket.summer.framework.boot.context.annotation.DeterminableImports;
import com.rocket.summer.framework.core.annotation.AnnotatedElementUtils;
import com.rocket.summer.framework.core.annotation.AnnotationAttributes;
import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.core.io.support.SpringFactoriesLoader;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.LinkedMultiValueMap;
import com.rocket.summer.framework.util.MultiValueMap;
import com.rocket.summer.framework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Variant of {@link EnableAutoConfigurationImportSelector} for
 * {@link ImportAutoConfiguration}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
class ImportAutoConfigurationImportSelector extends AutoConfigurationImportSelector
        implements DeterminableImports {

    private static final Set<String> ANNOTATION_NAMES;

    static {
        Set<String> names = new LinkedHashSet<String>();
        names.add(ImportAutoConfiguration.class.getName());
        names.add("com.rocket.summer.framework.boot.autoconfigure.test.ImportAutoConfiguration");
        ANNOTATION_NAMES = Collections.unmodifiableSet(names);
    }

    @Override
    public Set<Object> determineImports(AnnotationMetadata metadata) {
        Set<String> result = new LinkedHashSet<String>();
        result.addAll(getCandidateConfigurations(metadata, null));
        result.removeAll(getExclusions(metadata, null));
        return Collections.<Object>unmodifiableSet(result);
    }

    @Override
    protected AnnotationAttributes getAttributes(AnnotationMetadata metadata) {
        return null;
    }

    @Override
    protected List<String> getCandidateConfigurations(AnnotationMetadata metadata,
                                                      AnnotationAttributes attributes) {
        List<String> candidates = new ArrayList<String>();
        Map<Class<?>, List<Annotation>> annotations = getAnnotations(metadata);
        for (Map.Entry<Class<?>, List<Annotation>> entry : annotations.entrySet()) {
            collectCandidateConfigurations(entry.getKey(), entry.getValue(), candidates);
        }
        return candidates;
    }

    private void collectCandidateConfigurations(Class<?> source,
                                                List<Annotation> annotations, List<String> candidates) {
        for (Annotation annotation : annotations) {
            candidates.addAll(getConfigurationsForAnnotation(source, annotation));
        }
    }

    private Collection<String> getConfigurationsForAnnotation(Class<?> source,
                                                              Annotation annotation) {
        String[] classes = (String[]) AnnotationUtils
                .getAnnotationAttributes(annotation, true).get("classes");
        if (classes.length > 0) {
            return Arrays.asList(classes);
        }
        return loadFactoryNames(source);
    }

    protected Collection<String> loadFactoryNames(Class<?> source) {
        return SpringFactoriesLoader.loadFactoryNames(source,
                getClass().getClassLoader());
    }

    @Override
    protected Set<String> getExclusions(AnnotationMetadata metadata,
                                        AnnotationAttributes attributes) {
        Set<String> exclusions = new LinkedHashSet<String>();
        Class<?> source = ClassUtils.resolveClassName(metadata.getClassName(), null);
        for (String annotationName : ANNOTATION_NAMES) {
            AnnotationAttributes merged = AnnotatedElementUtils
                    .getMergedAnnotationAttributes(source, annotationName);
            Class<?>[] exclude = (merged != null) ? merged.getClassArray("exclude")
                    : null;
            if (exclude != null) {
                for (Class<?> excludeClass : exclude) {
                    exclusions.add(excludeClass.getName());
                }
            }
        }
        for (List<Annotation> annotations : getAnnotations(metadata).values()) {
            for (Annotation annotation : annotations) {
                String[] exclude = (String[]) AnnotationUtils
                        .getAnnotationAttributes(annotation, true).get("exclude");
                if (!ObjectUtils.isEmpty(exclude)) {
                    exclusions.addAll(Arrays.asList(exclude));
                }
            }
        }
        return exclusions;
    }

    protected final Map<Class<?>, List<Annotation>> getAnnotations(
            AnnotationMetadata metadata) {
        MultiValueMap<Class<?>, Annotation> annotations = new LinkedMultiValueMap<Class<?>, Annotation>();
        Class<?> source = ClassUtils.resolveClassName(metadata.getClassName(), null);
        collectAnnotations(source, annotations, new HashSet<Class<?>>());
        return Collections.unmodifiableMap(annotations);
    }

    private void collectAnnotations(Class<?> source,
                                    MultiValueMap<Class<?>, Annotation> annotations, HashSet<Class<?>> seen) {
        if (source != null && seen.add(source)) {
            for (Annotation annotation : source.getDeclaredAnnotations()) {
                if (!AnnotationUtils.isInJavaLangAnnotationPackage(annotation)) {
                    if (ANNOTATION_NAMES
                            .contains(annotation.annotationType().getName())) {
                        annotations.add(source, annotation);
                    }
                    collectAnnotations(annotation.annotationType(), annotations, seen);
                }
            }
            collectAnnotations(source.getSuperclass(), annotations, seen);
        }
    }

    @Override
    public int getOrder() {
        return super.getOrder() - 1;
    }

    @Override
    protected void handleInvalidExcludes(List<String> invalidExcludes) {
        // Ignore for test
    }

}

