package com.rocket.summer.framework.beans.factory.xml;

import com.rocket.summer.framework.beans.factory.BeanDefinitionStoreException;
import org.w3c.dom.Document;

/**
 * SPI for parsing an XML document that contains Spring bean definitions.
 * Used by XmlBeanDefinitionReader for actually parsing a DOM document.
 *
 * <p>Instantiated per document to parse: Implementations can hold
 * state in instance variables during the execution of the
 * <code>registerBeanDefinitions</code> method, for example global
 * settings that are defined for all bean definitions in the document.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 18.12.2003
 * @see XmlBeanDefinitionReader#setDocumentReaderClass
 */
public interface BeanDefinitionDocumentReader {

    /**
     * Read bean definitions from the given DOM document,
     * and register them with the given bean factory.
     * @param doc the DOM document
     * @param readerContext the current context of the reader. Includes the resource being parsed
     * @throws BeanDefinitionStoreException in case of parsing errors
     */
    void registerBeanDefinitions(Document doc, XmlReaderContext readerContext)
            throws BeanDefinitionStoreException;

}
