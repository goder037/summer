package com.rocket.summer.framework.data.repository.config;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.core.type.filter.TypeFilter;
import com.rocket.summer.framework.util.Assert;

/**
 * Base class to implement {@link RepositoryConfigurationSource}s.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Peter Rietzler
 */
public abstract class RepositoryConfigurationSourceSupport implements RepositoryConfigurationSource {

    protected static final String DEFAULT_REPOSITORY_IMPL_POSTFIX = "Impl";

    private final Environment environment;
    private final BeanDefinitionRegistry registry;

    /**
     * Creates a new {@link RepositoryConfigurationSourceSupport} with the given environment.
     *
     * @param environment must not be {@literal null}.
     */
    public RepositoryConfigurationSourceSupport(Environment environment, BeanDefinitionRegistry registry) {

        Assert.notNull(environment, "Environment must not be null!");
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");

        this.environment = environment;
        this.registry = registry;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfiguration#getCandidates(com.rocket.summer.framework.context.annotation.ClassPathScanningCandidateComponentProvider)
     */
    public Collection<BeanDefinition> getCandidates(ResourceLoader loader) {

        RepositoryComponentProvider scanner = new RepositoryComponentProvider(getIncludeFilters(), registry);
        scanner.setConsiderNestedRepositoryInterfaces(shouldConsiderNestedRepositories());
        scanner.setEnvironment(environment);
        scanner.setResourceLoader(loader);

        for (TypeFilter filter : getExcludeFilters()) {
            scanner.addExcludeFilter(filter);
        }

        Set<BeanDefinition> result = new HashSet<BeanDefinition>();

        for (String basePackage : getBasePackages()) {
            Set<BeanDefinition> candidate = scanner.findCandidateComponents(basePackage);
            result.addAll(candidate);
        }

        return result;
    }

    /**
     * Return the {@link TypeFilter}s to define which types to exclude when scanning for repositories. Default
     * implementation returns an empty collection.
     *
     * @return must not be {@literal null}.
     */
    public Iterable<TypeFilter> getExcludeFilters() {
        return Collections.emptySet();
    }

    /**
     * Return the {@link TypeFilter}s to define which types to include when scanning for repositories. Default
     * implementation returns an empty collection.
     *
     * @return must not be {@literal null}.
     */
    protected Iterable<TypeFilter> getIncludeFilters() {
        return Collections.emptySet();
    }

    /**
     * Returns whether we should consider nested repositories, i.e. repository interface definitions nested in other
     * classes.
     *
     * @return {@literal true} if the container should look for nested repository interface definitions.
     */
    public boolean shouldConsiderNestedRepositories() {
        return false;
    }
}

