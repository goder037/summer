package com.rocket.summer.framework.data.repository.config;

import static com.rocket.summer.framework.beans.factory.support.BeanDefinitionReaderUtils.*;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.support.AbstractBeanDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionBuilder;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.data.repository.core.RepositoryMetadata;
import com.rocket.summer.framework.data.repository.core.support.AbstractRepositoryMetadata;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Base implementation of {@link RepositoryConfigurationExtension} to ease the implementation of the interface. Will
 * default the default named query location based on a module prefix provided by implementors (see
 * {@link #getModulePrefix()}). Stubs out the post-processing methods as they might not be needed by default.
 *
 * @author Oliver Gierke
 */
public abstract class RepositoryConfigurationExtensionSupport implements RepositoryConfigurationExtension {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryConfigurationExtensionSupport.class);
    private static final String CLASS_LOADING_ERROR = "%s - Could not load type %s using class loader %s.";
    private static final String MULTI_STORE_DROPPED = "Spring Data {} - Could not safely identify store assignment for repository candidate {}.";

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtension#getModuleName()
     */
    @Override
    public String getModuleName() {
        return StringUtils.capitalize(getModulePrefix());
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtension#getRepositoryConfigurations(com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSource, com.rocket.summer.framework.core.io.ResourceLoader)
     */
    public <T extends RepositoryConfigurationSource> Collection<RepositoryConfiguration<T>> getRepositoryConfigurations(
            T configSource, ResourceLoader loader) {
        return getRepositoryConfigurations(configSource, loader, false);
    }

    /*
     *
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtension#getRepositoryConfigurations(com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSource, com.rocket.summer.framework.core.io.ResourceLoader, boolean)
     */
    public <T extends RepositoryConfigurationSource> Collection<RepositoryConfiguration<T>> getRepositoryConfigurations(
            T configSource, ResourceLoader loader, boolean strictMatchesOnly) {

        Assert.notNull(configSource, "ConfigSource must not be null!");
        Assert.notNull(loader, "Loader must not be null!");

        Set<RepositoryConfiguration<T>> result = new HashSet<RepositoryConfiguration<T>>();

        for (BeanDefinition candidate : configSource.getCandidates(loader)) {

            RepositoryConfiguration<T> configuration = getRepositoryConfiguration(candidate, configSource);

            if (!strictMatchesOnly || configSource.usesExplicitFilters()) {
                result.add(configuration);
                continue;
            }

            Class<?> repositoryInterface = loadRepositoryInterface(configuration, loader);

            if (repositoryInterface == null || isStrictRepositoryCandidate(repositoryInterface)) {
                result.add(configuration);
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtension#getDefaultNamedQueryLocation()
     */
    public String getDefaultNamedQueryLocation() {
        return String.format("classpath*:META-INF/%s-named-queries.properties", getModulePrefix());
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtension#registerBeansForRoot(com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry, com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSource)
     */
    public void registerBeansForRoot(BeanDefinitionRegistry registry,
                                     RepositoryConfigurationSource configurationSource) {}

    /**
     * Returns the prefix of the module to be used to create the default location for Spring Data named queries.
     *
     * @return must not be {@literal null}.
     */
    protected abstract String getModulePrefix();

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtension#postProcess(com.rocket.summer.framework.beans.factory.support.BeanDefinitionBuilder, com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSource)
     */
    public void postProcess(BeanDefinitionBuilder builder, RepositoryConfigurationSource source) {}

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtension#postProcess(com.rocket.summer.framework.beans.factory.support.BeanDefinitionBuilder, com.rocket.summer.framework.data.repository.config.AnnotationRepositoryConfigurationSource)
     */
    public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {}

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtension#postProcess(com.rocket.summer.framework.beans.factory.support.BeanDefinitionBuilder, com.rocket.summer.framework.data.repository.config.XmlRepositoryConfigurationSource)
     */
    public void postProcess(BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config) {}

    /**
     * Return the annotations to scan domain types for when evaluating repository interfaces for store assignment. Modules
     * should return the annotations that identify a domain type as managed by the store explicitly.
     *
     * @return
     * @since 1.9
     */
    protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
        return Collections.emptySet();
    }

    /**
     * Returns the types that indicate a store match when inspecting repositories for strict matches.
     *
     * @return
     * @since 1.9
     */
    protected Collection<Class<?>> getIdentifyingTypes() {
        return Collections.emptySet();
    }

    /**
     * Sets the given source on the given {@link AbstractBeanDefinition} and registers it inside the given
     * {@link BeanDefinitionRegistry}. For {@link BeanDefinition}s to be registerd once-and-only-once for all
     * configuration elements (annotation or XML), prefer calling
     * {@link #registerIfNotAlreadyRegistered(AbstractBeanDefinition, BeanDefinitionRegistry, String, Object)} with a
     * dedicated bean name to avoid the bead definition being registered multiple times. *
     *
     * @param registry must not be {@literal null}.
     * @param bean must not be {@literal null}.
     * @param source must not be {@literal null}.
     * @return the bean name generated for the given {@link BeanDefinition}
     */
    public static String registerWithSourceAndGeneratedBeanName(BeanDefinitionRegistry registry,
                                                                AbstractBeanDefinition bean, Object source) {

        bean.setSource(source);

        String beanName = generateBeanName(bean, registry);
        registry.registerBeanDefinition(beanName, bean);

        return beanName;
    }

    /**
     * Registers the given {@link AbstractBeanDefinition} with the given registry with the given bean name unless the
     * registry already contains a bean with that name.
     *
     * @param bean must not be {@literal null}.
     * @param registry must not be {@literal null}.
     * @param beanName must not be {@literal null} or empty.
     * @param source must not be {@literal null}.
     */
    public static void registerIfNotAlreadyRegistered(AbstractBeanDefinition bean, BeanDefinitionRegistry registry,
                                                      String beanName, Object source) {

        if (registry.containsBeanDefinition(beanName)) {
            return;
        }

        bean.setSource(source);
        registry.registerBeanDefinition(beanName, bean);
    }

    /**
     * Returns whether the given {@link BeanDefinitionRegistry} already contains a bean of the given type assuming the
     * bean name has been autogenerated.
     *
     * @param type
     * @param registry
     * @return
     */
    public static boolean hasBean(Class<?> type, BeanDefinitionRegistry registry) {

        String name = String.format("%s%s0", type.getName(), GENERATED_BEAN_NAME_SEPARATOR);
        return registry.containsBeanDefinition(name);
    }

    /**
     * Creates a actual {@link RepositoryConfiguration} instance for the given {@link RepositoryConfigurationSource} and
     * interface name. Defaults to the {@link DefaultRepositoryConfiguration} but allows sub-classes to override this to
     * customize the behaviour.
     *
     * @param definition will never be {@literal null} or empty.
     * @param configSource will never be {@literal null}.
     * @return
     */
    protected <T extends RepositoryConfigurationSource> RepositoryConfiguration<T> getRepositoryConfiguration(
            BeanDefinition definition, T configSource) {
        return new DefaultRepositoryConfiguration<T>(configSource, definition);
    }

    /**
     * Returns whether the given repository interface is a candidate for bean definition creation in the strict repository
     * detection mode. The default implementation inspects the domain type managed for a set of well-known annotations
     * (see {@link #getIdentifyingAnnotations()}). If none of them is found, the candidate is discarded. Implementations
     * should make sure, the only return {@literal true} if they're really sure the interface handed to the method is
     * really a store interface.
     *
     * @param repositoryInterface
     * @return
     * @since 1.9
     */
    protected boolean isStrictRepositoryCandidate(Class<?> repositoryInterface) {

        RepositoryMetadata metadata = AbstractRepositoryMetadata.getMetadata(repositoryInterface);

        Collection<Class<?>> types = getIdentifyingTypes();

        for (Class<?> type : types) {
            if (type.isAssignableFrom(repositoryInterface)) {
                return true;
            }
        }

        Class<?> domainType = metadata.getDomainType();
        Collection<Class<? extends Annotation>> annotations = getIdentifyingAnnotations();

        if (annotations.isEmpty()) {
            return true;
        }

        for (Class<? extends Annotation> annotationType : annotations) {
            if (AnnotationUtils.findAnnotation(domainType, annotationType) != null) {
                return true;
            }
        }

        LOGGER.info(MULTI_STORE_DROPPED, getModuleName(), repositoryInterface);

        return false;
    }

    /**
     * Loads the repository interface contained in the given {@link RepositoryConfiguration} using the given
     * {@link ResourceLoader}.
     *
     * @param configuration must not be {@literal null}.
     * @param loader must not be {@literal null}.
     * @return the repository interface or {@literal null} if it can't be loaded.
     */
    private Class<?> loadRepositoryInterface(RepositoryConfiguration<?> configuration, ResourceLoader loader) {

        String repositoryInterface = configuration.getRepositoryInterface();
        ClassLoader classLoader = loader.getClassLoader();

        try {
            return com.rocket.summer.framework.util.ClassUtils.forName(repositoryInterface, classLoader);
        } catch (ClassNotFoundException e) {
            LOGGER.warn(String.format(CLASS_LOADING_ERROR, getModuleName(), repositoryInterface, classLoader), e);
        } catch (LinkageError e) {
            LOGGER.warn(String.format(CLASS_LOADING_ERROR, getModuleName(), repositoryInterface, classLoader), e);
        }

        return null;
    }
}

