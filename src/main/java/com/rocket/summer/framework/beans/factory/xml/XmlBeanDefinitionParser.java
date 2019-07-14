package com.rocket.summer.framework.beans.factory.xml;

import com.rocket.summer.framework.beans.factory.BeanDefinitionStoreException;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionReader;
import com.rocket.summer.framework.core.io.Resource;
import org.w3c.dom.Document;

/**
 * Strategy interface for parsing XML bean definitions.
 * Used by XmlBeanDefinitionReader for actually parsing a DOM document.
 *
 * <p>Instantiated per document to parse: Implementations can hold
 * state in instance variables during the execution of the
 * <code>registerBeanDefinitions</code> method, for example global
 * settings that are defined for all bean definitions in the document.
 *
 * @author Juergen Hoeller
 * @since 18.12.2003
 * @deprecated as of Spring 2.0: superseded by BeanDefinitionDocumentReader
 * @see BeanDefinitionDocumentReader
 * @see XmlBeanDefinitionReader#setParserClass
 */
public interface XmlBeanDefinitionParser {

    /**
     * Parse bean definitions from the given DOM document,
     * and register them with the given bean factory.
     * @param reader the bean definition reader, containing the bean factory
     * to work on and the bean class loader to use. Can also be used to load
     * further bean definition files referenced by the given document.
     * @param doc the DOM document
     * @param resource descriptor of the original XML resource
     * (useful for displaying parse errors)
     * @return the number of bean definitions found
     * @throws BeanDefinitionStoreException in case of parsing errors
     */
    int registerBeanDefinitions(BeanDefinitionReader reader, Document doc, Resource resource)
            throws BeanDefinitionStoreException;

}
