package com.rocket.summer.framework.data.repository.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.support.AbstractBeanDefinition;
import com.rocket.summer.framework.context.annotation.ClassPathScanningCandidateComponentProvider;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.core.type.classreading.MetadataReader;
import com.rocket.summer.framework.core.type.classreading.MetadataReaderFactory;
import com.rocket.summer.framework.core.type.filter.TypeFilter;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Detects the custom implementation for a {@link com.rocket.summer.framework.data.repository.Repository}
 *
 * @author Oliver Gierke
 * @author Mark Paluch
 * @author Peter Rietzler
 * @author Mark Paluch
 */
public class CustomRepositoryImplementationDetector {

    private static final String CUSTOM_IMPLEMENTATION_RESOURCE_PATTERN = "**/%s.class";

    private final MetadataReaderFactory metadataReaderFactory;
    private final Environment environment;
    private final ResourceLoader resourceLoader;

    /**
     * Creates a new {@link CustomRepositoryImplementationDetector} from the given
     * {@link com.rocket.summer.framework.core.type.classreading.MetadataReaderFactory},
     * {@link com.rocket.summer.framework.core.env.Environment} and {@link com.rocket.summer.framework.core.io.ResourceLoader}.
     *
     * @param metadataReaderFactory must not be {@literal null}.
     * @param environment must not be {@literal null}.
     * @param resourceLoader must not be {@literal null}.
     */
    public CustomRepositoryImplementationDetector(MetadataReaderFactory metadataReaderFactory, Environment environment,
                                                  ResourceLoader resourceLoader) {

        Assert.notNull(metadataReaderFactory, "MetadataReaderFactory must not be null!");
        Assert.notNull(resourceLoader, "ResourceLoader must not be null!");
        Assert.notNull(environment, "Environment must not be null!");

        this.metadataReaderFactory = metadataReaderFactory;
        this.environment = environment;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Tries to detect a custom implementation for a repository bean by classpath scanning.
     *
     * @param configuration the {@link RepositoryConfiguration} to consider.
     * @return the {@code AbstractBeanDefinition} of the custom implementation or {@literal null} if none found.
     */
    public AbstractBeanDefinition detectCustomImplementation(RepositoryConfiguration<?> configuration) {

        // TODO 2.0: Extract into dedicated interface for custom implementation lookup configuration.

        return detectCustomImplementation(configuration.getImplementationClassName(), //
                configuration.getImplementationBasePackages(), //
                configuration.getExcludeFilters());
    }

    /**
     * Tries to detect a custom implementation for a repository bean by classpath scanning.
     *
     * @param className must not be {@literal null}.
     * @param basePackages must not be {@literal null}.
     * @return the {@code AbstractBeanDefinition} of the custom implementation or {@literal null} if none found.
     */
    public AbstractBeanDefinition detectCustomImplementation(String className, Iterable<String> basePackages,
                                                             Iterable<TypeFilter> excludeFilters) {

        Assert.notNull(className, "ClassName must not be null!");
        Assert.notNull(basePackages, "BasePackages must not be null!");

        // Build classpath scanner and lookup bean definition
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false,
                environment);
        provider.setResourceLoader(resourceLoader);
        provider.setResourcePattern(String.format(CUSTOM_IMPLEMENTATION_RESOURCE_PATTERN, className));
        provider.setMetadataReaderFactory(metadataReaderFactory);
        provider.addIncludeFilter(AlwaysIncludeFilter.INSTANCE);

        for (TypeFilter excludeFilter : excludeFilters) {
            provider.addExcludeFilter(excludeFilter);
        }

        Set<BeanDefinition> definitions = new HashSet<BeanDefinition>();

        for (String basePackage : basePackages) {
            definitions.addAll(provider.findCandidateComponents(basePackage));
        }

        if (definitions.isEmpty()) {
            return null;
        }

        if (definitions.size() == 1) {
            return (AbstractBeanDefinition) definitions.iterator().next();
        }

        List<String> implementationClassNames = new ArrayList<String>();
        for (BeanDefinition bean : definitions) {
            implementationClassNames.add(bean.getBeanClassName());
        }

        throw new IllegalStateException(
                String.format("Ambiguous custom implementations detected! Found %s but expected a single implementation!",
                        StringUtils.collectionToCommaDelimitedString(implementationClassNames)));
    }

    private enum AlwaysIncludeFilter implements TypeFilter {

        INSTANCE;

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.core.type.filter.TypeFilter#match(com.rocket.summer.framework.core.type.classreading.MetadataReader, com.rocket.summer.framework.core.type.classreading.MetadataReaderFactory)
         */
        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
                throws IOException {
            return true;
        }
    }
}
