package com.rocket.summer.framework.context.support;

import com.rocket.summer.framework.beans.factory.support.DefaultListableBeanFactory;
import com.rocket.summer.framework.beans.factory.xml.ResourceEntityResolver;
import com.rocket.summer.framework.beans.factory.xml.XmlBeanDefinitionReader;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.core.io.Resource;

import java.io.IOException;

public abstract class AbstractXmlApplicationContext extends AbstractRefreshableConfigApplicationContext {

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
     * Return an array of Resource objects, referring to the XML bean definition
     * files that this context should be built with.
     * <p>The default implementation returns <code>null</code>. Subclasses can override
     * this to provide pre-built Resource objects rather than location Strings.
     * @return an array of Resource objects, or <code>null</code> if none
     * @see #getConfigLocations()
     */
    protected Resource[] getConfigResources() {
        return null;
    }

    /**
     * Loads the bean definitions via an XmlBeanDefinitionReader.
     * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
     * @see #initBeanDefinitionReader
     * @see #loadBeanDefinitions
     */
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws IOException {
        // Create a new XmlBeanDefinitionReader for the given BeanFactory.
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        // Configure the bean definition reader with this context's
        // resource loading environment.
        beanDefinitionReader.setResourceLoader(this);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

        // Allow a subclass to provide custom initialization of the reader,
        // then proceed with actually loading the bean definitions.
        initBeanDefinitionReader(beanDefinitionReader);
        loadBeanDefinitions(beanDefinitionReader);
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
     * Initialize the bean definition reader used for loading the bean
     * definitions of this context. Default implementation is empty.
     * <p>Can be overridden in subclasses, e.g. for turning off XML validation
     * or using a different XmlBeanDefinitionParser implementation.
     * @param beanDefinitionReader the bean definition reader used by this context
     * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader#setDocumentReaderClass
     */
    protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {
    }
}
