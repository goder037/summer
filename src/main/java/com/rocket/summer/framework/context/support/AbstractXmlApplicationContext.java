package com.rocket.summer.framework.context.support;


import com.rocket.summer.framework.beans.factory.support.DefaultListableBeanFactory;
import com.rocket.summer.framework.beans.factory.xml.ResourceEntityResolver;
import com.rocket.summer.framework.beans.factory.xml.XmlBeanDefinitionReader;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.core.io.Resource;

import java.io.IOException;

/**
* Convenient base class for {@link com.rocket.summer.framework.context.ApplicationContext}
* implementations, drawing configuration from XML documents containing bean definitions
* understood by an {@link com.rocket.summer.framework.beans.factory.xml.XmlBeanDefinitionReader}.
*
* <p>Subclasses just have to implement the {@link #getConfigResources} and/or
* the {@link #getConfigLocations} method. Furthermore, they might override
* the {@link #getResourceByPath} hook to interpret relative paths in an
* environment-specific fashion, and/or {@link #getResourcePatternResolver}
* for extended pattern resolution.
*
* @author Rod Johnson
* @author Juergen Hoeller
* @see #getConfigResources
* @see #getConfigLocations
* @see com.rocket.summer.framework.beans.factory.xml.XmlBeanDefinitionReader
*/
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableConfigApplicationContext {

    private boolean validating = true;


    /**
     * Create a new AbstractXmlApplicationContext with no parent.
     */
    public AbstractXmlApplicationContext() {
    }

    /**
     * Create a new AbstractXmlApplicationContext with the given parent context.
     * @param parent the parent context
     */
    public AbstractXmlApplicationContext(ApplicationContext parent) {
        super(parent);
    }


    /**
     * Set whether to use XML validation. Default is {@code true}.
     */
    public void setValidating(boolean validating) {
        this.validating = validating;
    }


    /**
     * Loads the bean definitions via an XmlBeanDefinitionReader.
     * @see com.rocket.summer.framework.beans.factory.xml.XmlBeanDefinitionReader
     * @see #initBeanDefinitionReader
     * @see #loadBeanDefinitions
     */
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
        // Create a new XmlBeanDefinitionReader for the given BeanFactory.
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        // Configure the bean definition reader with this context's
        // resource loading environment.
        beanDefinitionReader.setEnvironment(this.getEnvironment());
        beanDefinitionReader.setResourceLoader(this);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

        // Allow a subclass to provide custom initialization of the reader,
        // then proceed with actually loading the bean definitions.
        initBeanDefinitionReader(beanDefinitionReader);
        loadBeanDefinitions(beanDefinitionReader);
    }

    /**
     * Initialize the bean definition reader used for loading the bean
     * definitions of this context. Default implementation is empty.
     * <p>Can be overridden in subclasses, e.g. for turning off XML validation
     * or using a different XmlBeanDefinitionParser implementation.
     * @param reader the bean definition reader used by this context
     * @see com.rocket.summer.framework.beans.factory.xml.XmlBeanDefinitionReader#setDocumentReaderClass
     */
    protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
        reader.setValidating(this.validating);
    }

    /**
     * Load the bean definitions with the given XmlBeanDefinitionReader.
     * <p>The lifecycle of the bean factory is handled by the {@link #refreshBeanFactory}
     * method; hence this method is just supposed to load and/or register bean definitions.
     * @param reader the XmlBeanDefinitionReader to use
     * @throws BeansException in case of bean registration errors
     * @throws IOException if the required XML document isn't found
     * @see #refreshBeanFactory
     * @see #getConfigLocations
     * @see #getResources
     * @see #getResourcePatternResolver
     */
    protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
        Resource[] configResources = getConfigResources();
        if (configResources != null) {
            reader.loadBeanDefinitions(configResources);
        }
        String[] configLocations = getConfigLocations();
        if (configLocations != null) {
            reader.loadBeanDefinitions(configLocations);
        }
    }

    /**
     * Return an array of Resource objects, referring to the XML bean definition
     * files that this context should be built with.
     * <p>The default implementation returns {@code null}. Subclasses can override
     * this to provide pre-built Resource objects rather than location Strings.
     * @return an array of Resource objects, or {@code null} if none
     * @see #getConfigLocations()
     */
    protected Resource[] getConfigResources() {
        return null;
    }

}

