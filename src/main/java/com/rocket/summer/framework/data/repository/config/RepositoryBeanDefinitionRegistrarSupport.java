package com.rocket.summer.framework.data.repository.config;

import java.lang.annotation.Annotation;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.context.EnvironmentAware;
import com.rocket.summer.framework.context.ResourceLoaderAware;
import com.rocket.summer.framework.context.annotation.ImportBeanDefinitionRegistrar;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.util.Assert;

/**
 * Base class to implement {@link ImportBeanDefinitionRegistrar}s to enable repository
 *
 * @author Oliver Gierke
 */
public abstract class RepositoryBeanDefinitionRegistrarSupport
        implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;
    private Environment environment;

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.context.ResourceLoaderAware#setResourceLoader(com.rocket.summer.framework.core.io.ResourceLoader)
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.context.EnvironmentAware#setEnvironment(com.rocket.summer.framework.core.env.Environment)
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.context.annotation.ImportBeanDefinitionRegistrar#registerBeanDefinitions(com.rocket.summer.framework.core.type.AnnotationMetadata, com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry)
     */
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {

        Assert.notNull(resourceLoader, "ResourceLoader must not be null!");
        Assert.notNull(annotationMetadata, "AnnotationMetadata must not be null!");
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");

        // Guard against calls for sub-classes
        if (annotationMetadata.getAnnotationAttributes(getAnnotation().getName()) == null) {
            return;
        }

        AnnotationRepositoryConfigurationSource configurationSource = new AnnotationRepositoryConfigurationSource(
                annotationMetadata, getAnnotation(), resourceLoader, environment, registry);

        RepositoryConfigurationExtension extension = getExtension();
        RepositoryConfigurationUtils.exposeRegistration(extension, registry, configurationSource);

        RepositoryConfigurationDelegate delegate = new RepositoryConfigurationDelegate(configurationSource, resourceLoader,
                environment);

        delegate.registerRepositoriesIn(registry, extension);
    }

    /**
     * Return the annotation to obtain configuration information from. Will be wrappen into an
     * {@link AnnotationRepositoryConfigurationSource} so have a look at the constants in there for what annotation
     * attributes it expects.
     *
     * @return
     */
    protected abstract Class<? extends Annotation> getAnnotation();

    /**
     * Returns the {@link RepositoryConfigurationExtension} for store specific callbacks and {@link BeanDefinition}
     * post-processing.
     *
     * @see RepositoryConfigurationExtensionSupport
     * @return
     */
    protected abstract RepositoryConfigurationExtension getExtension();
}

