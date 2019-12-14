package com.rocket.summer.framework.boot.context.embedded;

import com.rocket.summer.framework.beans.factory.xml.XmlBeanDefinitionReader;
import com.rocket.summer.framework.core.env.ConfigurableEnvironment;
import com.rocket.summer.framework.core.io.ClassPathResource;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.web.context.support.XmlWebApplicationContext;

/**
 * {@link EmbeddedWebApplicationContext} which takes its configuration from XML documents,
 * understood by an {@link com.rocket.summer.framework.beans.factory.xml.XmlBeanDefinitionReader}.
 * <p>
 * Note: In case of multiple config locations, later bean definitions will override ones
 * defined in earlier loaded files. This can be leveraged to deliberately override certain
 * bean definitions via an extra XML file.
 *
 * @author Phillip Webb
 * @see #setNamespace
 * @see #setConfigLocations
 * @see EmbeddedWebApplicationContext
 * @see XmlWebApplicationContext
 */
public class XmlEmbeddedWebApplicationContext extends EmbeddedWebApplicationContext {

    private final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);

    /**
     * Create a new {@link XmlEmbeddedWebApplicationContext} that needs to be
     * {@linkplain #load loaded} and then manually {@link #refresh refreshed}.
     */
    public XmlEmbeddedWebApplicationContext() {
        this.reader.setEnvironment(this.getEnvironment());
    }

    /**
     * Create a new {@link XmlEmbeddedWebApplicationContext}, loading bean definitions
     * from the given resources and automatically refreshing the context.
     * @param resources the resources to load from
     */
    public XmlEmbeddedWebApplicationContext(Resource... resources) {
        load(resources);
        refresh();
    }

    /**
     * Create a new {@link XmlEmbeddedWebApplicationContext}, loading bean definitions
     * from the given resource locations and automatically refreshing the context.
     * @param resourceLocations the resources to load from
     */
    public XmlEmbeddedWebApplicationContext(String... resourceLocations) {
        load(resourceLocations);
        refresh();
    }

    /**
     * Create a new {@link XmlEmbeddedWebApplicationContext}, loading bean definitions
     * from the given resource locations and automatically refreshing the context.
     * @param relativeClass class whose package will be used as a prefix when loading each
     * specified resource name
     * @param resourceNames relatively-qualified names of resources to load
     */
    public XmlEmbeddedWebApplicationContext(Class<?> relativeClass,
                                            String... resourceNames) {
        load(relativeClass, resourceNames);
        refresh();
    }

    /**
     * Set whether to use XML validation. Default is {@code true}.
     * @param validating if validating the XML
     */
    public void setValidating(boolean validating) {
        this.reader.setValidating(validating);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates the given environment to underlying {@link XmlBeanDefinitionReader}.
     * Should be called before any call to {@link #load}.
     */
    @Override
    public void setEnvironment(ConfigurableEnvironment environment) {
        super.setEnvironment(environment);
        this.reader.setEnvironment(this.getEnvironment());
    }

    /**
     * Load bean definitions from the given XML resources.
     * @param resources one or more resources to load from
     */
    public final void load(Resource... resources) {
        this.reader.loadBeanDefinitions(resources);
    }

    /**
     * Load bean definitions from the given XML resources.
     * @param resourceLocations one or more resource locations to load from
     */
    public final void load(String... resourceLocations) {
        this.reader.loadBeanDefinitions(resourceLocations);
    }

    /**
     * Load bean definitions from the given XML resources.
     * @param relativeClass class whose package will be used as a prefix when loading each
     * specified resource name
     * @param resourceNames relatively-qualified names of resources to load
     */
    public final void load(Class<?> relativeClass, String... resourceNames) {
        Resource[] resources = new Resource[resourceNames.length];
        for (int i = 0; i < resourceNames.length; i++) {
            resources[i] = new ClassPathResource(resourceNames[i], relativeClass);
        }
        this.reader.loadBeanDefinitions(resources);
    }

}
