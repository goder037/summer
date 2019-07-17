package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.parsing.Location;
import com.rocket.summer.framework.beans.factory.parsing.Problem;
import com.rocket.summer.framework.beans.factory.parsing.ProblemReporter;
import com.rocket.summer.framework.core.io.DescriptiveResource;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.core.type.StandardAnnotationMetadata;
import com.rocket.summer.framework.core.type.classreading.MetadataReader;
import com.rocket.summer.framework.util.ClassUtils;

import java.util.*;

/**
 * Represents a user-defined {@link Configuration @Configuration} class.
 * Includes a set of {@link Bean} methods, including all such methods defined in the
 * ancestry of the class, in a 'flattened-out' manner.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see ConfigurationClassMethod
 * @see ConfigurationClassParser
 */
final class ConfigurationClass {

    private final AnnotationMetadata metadata;

    private final Resource resource;

    private final Map<String, Class<?>> importedResources = new LinkedHashMap<String, Class<?>>();

    private final Set<ConfigurationClassMethod> methods = new LinkedHashSet<ConfigurationClassMethod>();

    private String beanName;


    public ConfigurationClass(MetadataReader metadataReader, String beanName) {
        this.metadata = metadataReader.getAnnotationMetadata();
        this.resource = metadataReader.getResource();
        this.beanName = beanName;
    }

    public ConfigurationClass(Class<?> clazz, String beanName) {
        this.metadata = new StandardAnnotationMetadata(clazz);
        this.resource = new DescriptiveResource(clazz.toString());
        this.beanName = beanName;
    }


    public AnnotationMetadata getMetadata() {
        return this.metadata;
    }

    public Resource getResource() {
        return this.resource;
    }

    public String getSimpleName() {
        return ClassUtils.getShortName(getMetadata().getClassName());
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return this.beanName;
    }

    public void addMethod(ConfigurationClassMethod method) {
        this.methods.add(method);
    }

    public Set<ConfigurationClassMethod> getMethods() {
        return this.methods;
    }

    public void addImportedResource(String importedResource, Class<?> readerClass) {
        this.importedResources.put(importedResource, readerClass);
    }

    public Map<String, Class<?>> getImportedResources() {
        return this.importedResources;
    }


    public void validate(ProblemReporter problemReporter) {
        // An @Bean method may only be overloaded through inheritance. No single
        // @Configuration class may declare two @Bean methods with the same name.
        final char hashDelim = '#';
        Map<String, Integer> methodNameCounts = new HashMap<String, Integer>();
        for (ConfigurationClassMethod method : methods) {
            String dClassName = method.getMetadata().getDeclaringClassName();
            String methodName = method.getMetadata().getMethodName();
            String fqMethodName = dClassName + hashDelim + methodName;
            Integer currentCount = methodNameCounts.get(fqMethodName);
            int newCount = currentCount != null ? currentCount + 1 : 1;
            methodNameCounts.put(fqMethodName, newCount);
        }

        for (String methodName : methodNameCounts.keySet()) {
            int count = methodNameCounts.get(methodName);
            if (count > 1) {
                String shortMethodName = methodName.substring(methodName.indexOf(hashDelim)+1);
                problemReporter.error(new BeanMethodOverloadingProblem(shortMethodName, count));
            }
        }

        // A configuration class may not be final (CGLIB limitation)
        if (getMetadata().isAnnotated(Configuration.class.getName())) {
            if (getMetadata().isFinal()) {
                problemReporter.error(new FinalConfigurationProblem());
            }
        }

        for (ConfigurationClassMethod method : this.methods) {
            method.validate(problemReporter);
        }
    }


    @Override
    public boolean equals(Object other) {
        return (this == other || (other instanceof ConfigurationClass &&
                getMetadata().getClassName().equals(((ConfigurationClass) other).getMetadata().getClassName())));
    }

    @Override
    public int hashCode() {
        return getMetadata().getClassName().hashCode();
    }


    /**
     * Configuration classes must be non-final to accommodate CGLIB subclassing.
     */
    private class FinalConfigurationProblem extends Problem {

        public FinalConfigurationProblem() {
            super(String.format("@Configuration class '%s' may not be final. Remove the final modifier to continue.",
                    getSimpleName()), new Location(getResource(), getMetadata()));
        }
    }


    /**
     * Bean methods on configuration classes may only be overloaded through inheritance.
     */
    private class BeanMethodOverloadingProblem extends Problem {

        public BeanMethodOverloadingProblem(String methodName, int count) {
            super(String.format("@Configuration class '%s' has %s overloaded @Bean methods named '%s'. " +
                            "Only one @Bean method of a given name is allowed within each @Configuration class.",
                    getSimpleName(), count, methodName), new Location(getResource(), getMetadata()));
        }
    }

}
