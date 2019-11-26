package com.rocket.summer.framework.data.repository.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rocket.summer.framework.beans.factory.support.AbstractBeanDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionBuilder;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.beans.factory.support.RootBeanDefinition;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.core.type.classreading.CachingMetadataReaderFactory;
import com.rocket.summer.framework.core.type.classreading.MetadataReaderFactory;
import com.rocket.summer.framework.data.repository.query.ExtensionAwareEvaluationContextProvider;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Builder to create {@link BeanDefinitionBuilder} instance to eventually create Spring Data repository instances.
 *
 * @author Oliver Gierke
 * @author Peter Rietzler
 */
class RepositoryBeanDefinitionBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryBeanDefinitionBuilder.class);

    private final BeanDefinitionRegistry registry;
    private final RepositoryConfigurationExtension extension;
    private final ResourceLoader resourceLoader;

    private final MetadataReaderFactory metadataReaderFactory;
    private CustomRepositoryImplementationDetector implementationDetector;

    /**
     * Creates a new {@link RepositoryBeanDefinitionBuilder} from the given {@link BeanDefinitionRegistry},
     * {@link RepositoryConfigurationExtension} and {@link ResourceLoader}.
     *
     * @param registry must not be {@literal null}.
     * @param extension must not be {@literal null}.
     * @param resourceLoader must not be {@literal null}.
     * @param environment must not be {@literal null}.
     */
    public RepositoryBeanDefinitionBuilder(BeanDefinitionRegistry registry, RepositoryConfigurationExtension extension,
                                           ResourceLoader resourceLoader, Environment environment) {

        Assert.notNull(extension, "RepositoryConfigurationExtension must not be null!");
        Assert.notNull(resourceLoader, "ResourceLoader must not be null!");
        Assert.notNull(environment, "Environment must not be null!");

        this.registry = registry;
        this.extension = extension;
        this.resourceLoader = resourceLoader;
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
        this.implementationDetector = new CustomRepositoryImplementationDetector(metadataReaderFactory, environment,
                resourceLoader);
    }

    /**
     * Builds a new {@link BeanDefinitionBuilder} from the given {@link BeanDefinitionRegistry} and {@link ResourceLoader}
     * .
     *
     * @param configuration must not be {@literal null}.
     * @return
     */
    public BeanDefinitionBuilder build(RepositoryConfiguration<?> configuration) {

        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");
        Assert.notNull(resourceLoader, "ResourceLoader must not be null!");

        String factoryBeanName = configuration.getRepositoryFactoryBeanName();
        factoryBeanName = StringUtils.hasText(factoryBeanName) ? factoryBeanName
                : extension.getRepositoryFactoryClassName();

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(factoryBeanName);

        builder.getRawBeanDefinition().setSource(configuration.getSource());
        builder.addConstructorArgValue(configuration.getRepositoryInterface());
        builder.addPropertyValue("queryLookupStrategyKey", configuration.getQueryLookupStrategyKey());
        builder.addPropertyValue("lazyInit", configuration.isLazyInit());
        builder.addPropertyValue("repositoryBaseClass", configuration.getRepositoryBaseClassName());

        NamedQueriesBeanDefinitionBuilder definitionBuilder = new NamedQueriesBeanDefinitionBuilder(
                extension.getDefaultNamedQueryLocation());

        if (StringUtils.hasText(configuration.getNamedQueriesLocation())) {
            definitionBuilder.setLocations(configuration.getNamedQueriesLocation());
        }

        builder.addPropertyValue("namedQueries", definitionBuilder.build(configuration.getSource()));

        String customImplementationBeanName = registerCustomImplementation(configuration);

        if (customImplementationBeanName != null) {
            builder.addPropertyReference("customImplementation", customImplementationBeanName);
            builder.addDependsOn(customImplementationBeanName);
        }

        RootBeanDefinition evaluationContextProviderDefinition = new RootBeanDefinition(
                ExtensionAwareEvaluationContextProvider.class);
        evaluationContextProviderDefinition.setSource(configuration.getSource());

        builder.addPropertyValue("evaluationContextProvider", evaluationContextProviderDefinition);

        return builder;
    }

    private String registerCustomImplementation(RepositoryConfiguration<?> configuration) {

        String beanName = configuration.getImplementationBeanName();

        // Already a bean configured?
        if (registry.containsBeanDefinition(beanName)) {
            return beanName;
        }

        AbstractBeanDefinition beanDefinition = implementationDetector.detectCustomImplementation(configuration);

        if (null == beanDefinition) {
            return null;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Registering custom repository implementation: " + configuration.getImplementationBeanName() + " "
                    + beanDefinition.getBeanClassName());
        }

        beanDefinition.setSource(configuration.getSource());

        registry.registerBeanDefinition(beanName, beanDefinition);

        return beanName;
    }
}

