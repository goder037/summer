package com.rocket.summer.framework.data.repository.config;

import java.util.Collections;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.core.type.filter.TypeFilter;
import com.rocket.summer.framework.data.repository.query.QueryLookupStrategy.Key;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Default implementation of {@link RepositoryConfiguration}.
 *
 * @author Oliver Gierke
 * @author Mark Paluch
 */
public class DefaultRepositoryConfiguration<T extends RepositoryConfigurationSource>
        implements RepositoryConfiguration<T> {

    public static final String DEFAULT_REPOSITORY_IMPLEMENTATION_POSTFIX = "Impl";
    private static final Key DEFAULT_QUERY_LOOKUP_STRATEGY = Key.CREATE_IF_NOT_FOUND;

    private final T configurationSource;
    private final BeanDefinition definition;

    /**
     * Creates a new {@link DefaultRepositoryConfiguration} from the given {@link RepositoryConfigurationSource} and
     * source {@link BeanDefinition}.
     *
     * @param configurationSource must not be {@literal null}.
     * @param definition must not be {@literal null}.
     */
    public DefaultRepositoryConfiguration(T configurationSource, BeanDefinition definition) {

        Assert.notNull(configurationSource, "ConfigurationSource must not be null!");
        Assert.notNull(definition, "BeanDefinition must not be null!");

        this.configurationSource = configurationSource;
        this.definition = definition;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfiguration#getBeanId()
     */
    public String getBeanId() {
        return StringUtils.uncapitalize(ClassUtils.getShortName(getRepositoryFactoryBeanName()));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfiguration#getQueryLookupStrategyKey()
     */
    public Object getQueryLookupStrategyKey() {

        Object configuredStrategy = configurationSource.getQueryLookupStrategyKey();
        return configuredStrategy != null ? configuredStrategy : DEFAULT_QUERY_LOOKUP_STRATEGY;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfiguration#getBasePackages()
     */
    public Iterable<String> getBasePackages() {
        return configurationSource.getBasePackages();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfiguration#getImplementationBasePackages()
     */
    @Override
    public Iterable<String> getImplementationBasePackages() {
        return Collections.singleton(ClassUtils.getPackageName(getRepositoryInterface()));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfiguration#getRepositoryInterface()
     */
    public String getRepositoryInterface() {
        return definition.getBeanClassName();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfiguration#getConfigSource()
     */
    public RepositoryConfigurationSource getConfigSource() {
        return configurationSource;
    }

    /* (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfiguration#getNamedQueryLocation()
     */
    public String getNamedQueriesLocation() {
        return configurationSource.getNamedQueryLocation();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfiguration#getImplementationClassName()
     */
    public String getImplementationClassName() {
        return ClassUtils.getShortName(getRepositoryInterface()) + getImplementationPostfix();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfiguration#getImplementationBeanName()
     */
    public String getImplementationBeanName() {
        return StringUtils.uncapitalize(getImplementationClassName());
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfiguration#getImplementationPostfix()
     */
    public String getImplementationPostfix() {

        String configuredPostfix = configurationSource.getRepositoryImplementationPostfix();
        return StringUtils.hasText(configuredPostfix) ? configuredPostfix : DEFAULT_REPOSITORY_IMPLEMENTATION_POSTFIX;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfiguration#getSource()
     */
    public Object getSource() {
        return configurationSource.getSource();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfiguration#getConfigurationSource()
     */
    public T getConfigurationSource() {
        return configurationSource;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfiguration#getRepositoryFactoryBeanName()
     */
    public String getRepositoryFactoryBeanName() {
        return configurationSource.getRepositoryFactoryBeanName();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfiguration#getRepositoryBaseClassName()
     */
    @Override
    public String getRepositoryBaseClassName() {
        return configurationSource.getRepositoryBaseClassName();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfiguration#isLazyInit()
     */
    @Override
    public boolean isLazyInit() {
        return definition.isLazyInit();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfiguration#getExcludeFilters()
     */
    @Override
    public Iterable<TypeFilter> getExcludeFilters() {
        return configurationSource.getExcludeFilters();
    }
}

