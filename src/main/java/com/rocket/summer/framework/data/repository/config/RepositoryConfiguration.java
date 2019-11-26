package com.rocket.summer.framework.data.repository.config;

import com.rocket.summer.framework.beans.factory.FactoryBean;
import com.rocket.summer.framework.core.type.filter.TypeFilter;
import com.rocket.summer.framework.data.repository.query.QueryLookupStrategy;

/**
 * Configuration information for a single repository instance.
 *
 * @author Oliver Gierke
 */
public interface RepositoryConfiguration<T extends RepositoryConfigurationSource> {

    /**
     * Returns the base packages that the repository was scanned under.
     *
     * @return
     */
    Iterable<String> getBasePackages();

    /**
     * Returns the base packages to scan for repository implementations.
     *
     * @return
     * @since 1.13.8
     */
    Iterable<String> getImplementationBasePackages();

    /**
     * Returns the interface name of the repository.
     *
     * @return
     */
    String getRepositoryInterface();

    /**
     * Returns the key to resolve a {@link QueryLookupStrategy} from eventually.
     *
     * @see QueryLookupStrategy.Key
     * @return
     */
    Object getQueryLookupStrategyKey();

    /**
     * Returns the location of the file containing Spring Data named queries.
     *
     * @return
     */
    String getNamedQueriesLocation();

    /**
     * Returns the class name of the custom implementation.
     *
     * @return
     */
    String getImplementationClassName();

    /**
     * Returns the bean name of the custom implementation.
     *
     * @return
     */
    String getImplementationBeanName();

    /**
     * Returns the name of the {@link FactoryBean} class to be used to create repository instances.
     *
     * @return
     * @deprecated as of 1.11 in favor of a dedicated repository class name, see {@link #getRepositoryBaseClassName()}.
     */
    @Deprecated
    String getRepositoryFactoryBeanName();

    /**
     * Returns the name of the repository base class to be used or {@literal null} if the store specific defaults shall be
     * applied.
     *
     * @return
     * @since 1.11
     */
    String getRepositoryBaseClassName();

    /**
     * Returns the source of the {@link RepositoryConfiguration}.
     *
     * @return
     */
    Object getSource();

    /**
     * Returns the {@link RepositoryConfigurationSource} that backs the {@link RepositoryConfiguration}.
     *
     * @return
     */
    T getConfigurationSource();

    /**
     * Returns whether to initialize the repository proxy lazily.
     *
     * @return
     */
    boolean isLazyInit();

    /**
     * Returns the {@link TypeFilter}s to be used to exclude packages from repository scanning.
     *
     * @return
     */
    Iterable<TypeFilter> getExcludeFilters();
}

