package com.rocket.summer.framework.data.repository.config;

import java.util.Collection;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionBuilder;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.core.io.ResourceLoader;

/**
 * SPI to implement store specific extension to the repository bean definition registration process.
 *
 * @see RepositoryConfigurationExtensionSupport
 * @author Oliver Gierke
 */
public interface RepositoryConfigurationExtension {

    /**
     * Returns the descriptive name of the module.
     *
     * @return
     */
    String getModuleName();

    /**
     * Returns all {@link RepositoryConfiguration}s obtained through the given {@link RepositoryConfigurationSource}.
     *
     * @param configSource must not be {@literal null}.
     * @param loader must not be {@literal null}.
     * @deprecated call or implement
     *             {@link #getRepositoryConfigurations(RepositoryConfigurationSource, ResourceLoader, boolean)} instead.
     * @return
     */
    @Deprecated
    <T extends RepositoryConfigurationSource> Collection<RepositoryConfiguration<T>> getRepositoryConfigurations(
            T configSource, ResourceLoader loader);

    /**
     * Returns all {@link RepositoryConfiguration}s obtained through the given {@link RepositoryConfigurationSource}.
     *
     * @param configSource
     * @param loader
     * @param strictMatchesOnly whether to return strict repository matches only. Handing in {@literal true} will cause
     *          the repository interfaces and domain types handled to be checked whether they are managed by the current
     *          store.
     * @return
     * @since 1.9
     */
    <T extends RepositoryConfigurationSource> Collection<RepositoryConfiguration<T>> getRepositoryConfigurations(
            T configSource, ResourceLoader loader, boolean strictMatchesOnly);

    /**
     * Returns the default location of the Spring Data named queries.
     *
     * @return must not be {@literal null} or empty.
     */
    String getDefaultNamedQueryLocation();

    /**
     * Returns the name of the repository factory class to be used.
     *
     * @return
     */
    String getRepositoryFactoryClassName();

    /**
     * Callback to register additional bean definitions for a {@literal repositories} root node. This usually includes
     * beans you have to set up once independently of the number of repositories to be created. Will be called before any
     * repositories bean definitions have been registered.
     *
     * @param registry
     * @param configurationSource
     */
    void registerBeansForRoot(BeanDefinitionRegistry registry, RepositoryConfigurationSource configurationSource);

    /**
     * Callback to post process the {@link BeanDefinition} and tweak the configuration if necessary.
     *
     * @param builder will never be {@literal null}.
     * @param config will never be {@literal null}.
     */
    void postProcess(BeanDefinitionBuilder builder, RepositoryConfigurationSource config);

    /**
     * Callback to post process the {@link BeanDefinition} built from annotations and tweak the configuration if
     * necessary.
     *
     * @param builder will never be {@literal null}.
     * @param config will never be {@literal null}.
     */
    void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config);

    /**
     * Callback to post process the {@link BeanDefinition} built from XML and tweak the configuration if necessary.
     *
     * @param builder will never be {@literal null}.
     * @param config will never be {@literal null}.
     */
    void postProcess(BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config);
}

