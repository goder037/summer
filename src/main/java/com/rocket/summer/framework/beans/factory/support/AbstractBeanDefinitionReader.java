package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.factory.BeanDefinitionStoreException;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.env.EnvironmentCapable;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.core.io.support.PathMatchingResourcePatternResolver;
import com.rocket.summer.framework.core.io.support.ResourcePatternResolver;
import com.rocket.summer.framework.util.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

/**
 * Abstract base class for bean definition readers which implement
 * the {@link BeanDefinitionReader} interface.
 *
 * <p>Provides common properties like the bean factory to work on
 * and the class loader to use for loading bean classes.
 *
 * @author Juergen Hoeller
 * @see BeanDefinitionReaderUtils
 * @since 11.12.2003
 */
public abstract class AbstractBeanDefinitionReader implements EnvironmentCapable, BeanDefinitionReader {

    /**
     * Logger available to subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());

    private final BeanDefinitionRegistry registry;

    private ResourceLoader resourceLoader;

    private ClassLoader beanClassLoader;

    private Environment environment;

    private BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();

    /**
     * Create a new AbstractBeanDefinitionReader for the given bean factory.
     * <p>If the passed-in bean factory does not only implement the BeanDefinitionRegistry
     * interface but also the ResourceLoader interface, it will be used as default
     * ResourceLoader as well. This will usually be the case for
     * {@link com.rocket.summer.framework.context.ApplicationContext} implementations.
     * <p>If given a plain BeanDefinitionRegistry, the default ResourceLoader will be a
     * {@link com.rocket.summer.framework.core.io.support.PathMatchingResourcePatternResolver}.
     * @param registry the BeanFactory to load bean definitions into,
     * in the form of a BeanDefinitionRegistry
     * @see #setResourceLoader
     */
    protected AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        this.registry = registry;

        // Determine ResourceLoader to use.
        if (this.registry instanceof ResourceLoader) {
            this.resourceLoader = (ResourceLoader) this.registry;
        }
        else {
            this.resourceLoader = new PathMatchingResourcePatternResolver();
        }
    }

    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    /**
     * Set the Environment to use when reading bean definitions. Most often used
     * for evaluating profile information to determine which bean definitions
     * should be read and which should be omitted.
     */
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public final BeanDefinitionRegistry getBeanFactory() {
        return this.registry;
    }

    public final BeanDefinitionRegistry getRegistry() {
        return this.registry;
    }

    /**
     * Set the ResourceLoader to use for resource locations.
     * If specifying a ResourcePatternResolver, the bean definition reader
     * will be capable of resolving resource patterns to Resource arrays.
     * <p>Default is PathMatchingResourcePatternResolver, also capable of
     * resource pattern resolving through the ResourcePatternResolver interface.
     * <p>Setting this to <code>null</code> suggests that absolute resource loading
     * is not available for this bean definition reader.
     * @see com.rocket.summer.framework.core.io.support.ResourcePatternResolver
     * @see com.rocket.summer.framework.core.io.support.PathMatchingResourcePatternResolver
     */
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Set the ClassLoader to use for bean classes.
     * <p>Default is <code>null</code>, which suggests to not load bean classes
     * eagerly but rather to just register bean definitions with class names,
     * with the corresponding Classes to be resolved later (or never).
     * @see java.lang.Thread#getContextClassLoader()
     */
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    /**
     * Set the BeanNameGenerator to use for anonymous beans
     * (without explicit bean name specified).
     * <p>Default is a {@link DefaultBeanNameGenerator}.
     */
    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = (beanNameGenerator != null ? beanNameGenerator : new DefaultBeanNameGenerator());
    }

    public BeanNameGenerator getBeanNameGenerator() {
        return this.beanNameGenerator;
    }


    public int loadBeanDefinitions(Resource[] resources) throws BeanDefinitionStoreException {
        Assert.notNull(resources, "Resource array must not be null");
        int counter = 0;
        for (Resource resource : resources) {
            counter += loadBeanDefinitions(resource);
        }
        return counter;
    }

    public int loadBeanDefinitions(String location) throws BeanDefinitionStoreException {
        return loadBeanDefinitions(location, null);
    }

    /**
     * Load bean definitions from the specified resource location.
     * <p>The location can also be a location pattern, provided that the
     * ResourceLoader of this bean definition reader is a ResourcePatternResolver.
     * @param location the resource location, to be loaded with the ResourceLoader
     * (or ResourcePatternResolver) of this bean definition reader
     * @param actualResources a Set to be filled with the actual Resource objects
     * that have been resolved during the loading process. May be <code>null</code>
     * to indicate that the caller is not interested in those Resource objects.
     * @return the number of bean definitions found
     * @throws BeanDefinitionStoreException in case of loading or parsing errors
     * @see #getResourceLoader()
     * @see #loadBeanDefinitions(com.rocket.summer.framework.core.io.Resource)
     * @see #loadBeanDefinitions(com.rocket.summer.framework.core.io.Resource[])
     */
    public int loadBeanDefinitions(String location, Set actualResources) throws BeanDefinitionStoreException {
        ResourceLoader resourceLoader = getResourceLoader();
        if (resourceLoader == null) {
            throw new BeanDefinitionStoreException(
                    "Cannot import bean definitions from location [" + location + "]: no ResourceLoader available");
        }

        if (resourceLoader instanceof ResourcePatternResolver) {
            // Resource pattern matching available.
            try {
                Resource[] resources = ((ResourcePatternResolver) resourceLoader).getResources(location);
                int loadCount = loadBeanDefinitions(resources);
                if (actualResources != null) {
                    Collections.addAll(actualResources, resources);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Loaded " + loadCount + " bean definitions from location pattern [" + location + "]");
                }
                return loadCount;
            }
            catch (IOException ex) {
                throw new BeanDefinitionStoreException(
                        "Could not resolve bean definition resource pattern [" + location + "]", ex);
            }
        }
        else {
            // Can only load single resources by absolute URL.
            Resource resource = resourceLoader.getResource(location);
            int loadCount = loadBeanDefinitions(resource);
            if (actualResources != null) {
                actualResources.add(resource);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Loaded " + loadCount + " bean definitions from location [" + location + "]");
            }
            return loadCount;
        }
    }

    public int loadBeanDefinitions(String[] locations) throws BeanDefinitionStoreException {
        Assert.notNull(locations, "Location array must not be null");
        int counter = 0;
        for (String location : locations) {
            counter += loadBeanDefinitions(location);
        }
        return counter;
    }

    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }
}
