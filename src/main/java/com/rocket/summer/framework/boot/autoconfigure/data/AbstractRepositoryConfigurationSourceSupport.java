package com.rocket.summer.framework.boot.autoconfigure.data;

import java.lang.annotation.Annotation;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.boot.autoconfigure.AutoConfigurationPackages;
import com.rocket.summer.framework.context.EnvironmentAware;
import com.rocket.summer.framework.context.ResourceLoaderAware;
import com.rocket.summer.framework.context.annotation.ImportBeanDefinitionRegistrar;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.core.type.StandardAnnotationMetadata;
import com.rocket.summer.framework.data.repository.config.AnnotationRepositoryConfigurationSource;
import com.rocket.summer.framework.data.repository.config.RepositoryConfigurationDelegate;
import com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtension;

/**
 * Base {@link ImportBeanDefinitionRegistrar} used to auto-configure Spring Data
 * Repositories.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Oliver Gierke
 */
public abstract class AbstractRepositoryConfigurationSourceSupport
        implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware,
        EnvironmentAware {

    private ResourceLoader resourceLoader;

    private BeanFactory beanFactory;

    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        new RepositoryConfigurationDelegate(getConfigurationSource(registry),
                this.resourceLoader, this.environment).registerRepositoriesIn(registry,
                getRepositoryConfigurationExtension());
    }

    private AnnotationRepositoryConfigurationSource getConfigurationSource(
            BeanDefinitionRegistry registry) {
        StandardAnnotationMetadata metadata = new StandardAnnotationMetadata(
                getConfiguration(), true);
        return new AnnotationRepositoryConfigurationSource(metadata, getAnnotation(),
                this.resourceLoader, this.environment, registry) {
            @Override
            public java.lang.Iterable<String> getBasePackages() {
                return AbstractRepositoryConfigurationSourceSupport.this
                        .getBasePackages();
            }
        };
    }

    protected Iterable<String> getBasePackages() {
        return AutoConfigurationPackages.get(this.beanFactory);
    }

    /**
     * The Spring Data annotation used to enable the particular repository support.
     * @return the annotation class
     */
    protected abstract Class<? extends Annotation> getAnnotation();

    /**
     * The configuration class that will be used by Spring Boot as a template.
     * @return the configuration class
     */
    protected abstract Class<?> getConfiguration();

    /**
     * The {@link RepositoryConfigurationExtension} for the particular repository support.
     * @return the repository configuration extension
     */
    protected abstract RepositoryConfigurationExtension getRepositoryConfigurationExtension();

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}

