package com.rocket.summer.framework.boot.domain;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.boot.autoconfigure.AutoConfigurationPackages;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.annotation.ClassPathScanningCandidateComponentProvider;
import com.rocket.summer.framework.core.type.filter.AnnotationTypeFilter;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;

/**
 * An entity scanner that searches the classpath from a {@link EntityScan @EntityScan}
 * specified packages.
 *
 * @author Phillip Webb
 * @since 1.4.0
 */
public class EntityScanner {

    private final ApplicationContext context;

    /**
     * Create a new {@link EntityScanner} instance.
     * @param context the source application context
     */
    public EntityScanner(ApplicationContext context) {
        Assert.notNull(context, "Context must not be null");
        this.context = context;
    }

    /**
     * Scan for entities with the specified annotations.
     * @param annotationTypes the annotation types used on the entities
     * @return a set of entity classes
     * @throws ClassNotFoundException if an entity class cannot be loaded
     */
    @SafeVarargs
    public final Set<Class<?>> scan(Class<? extends Annotation>... annotationTypes)
            throws ClassNotFoundException {
        List<String> packages = getPackages();
        if (packages.isEmpty()) {
            return Collections.<Class<?>>emptySet();
        }
        Set<Class<?>> entitySet = new HashSet<Class<?>>();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
                false);
        scanner.setEnvironment(this.context.getEnvironment());
        scanner.setResourceLoader(this.context);
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            scanner.addIncludeFilter(new AnnotationTypeFilter(annotationType));
        }
        for (String basePackage : packages) {
            if (StringUtils.hasText(basePackage)) {
                for (BeanDefinition candidate : scanner
                        .findCandidateComponents(basePackage)) {
                    entitySet.add(ClassUtils.forName(candidate.getBeanClassName(),
                            this.context.getClassLoader()));
                }
            }
        }
        return entitySet;
    }

    private List<String> getPackages() {
        List<String> packages = EntityScanPackages.get(this.context).getPackageNames();
        if (packages.isEmpty() && AutoConfigurationPackages.has(this.context)) {
            packages = AutoConfigurationPackages.get(this.context);
        }
        return packages;
    }

}

