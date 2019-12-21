package com.rocket.summer.framework.http.converter.xml;

import com.rocket.summer.framework.http.converter.HttpMessageConversionException;
import com.rocket.summer.framework.util.Assert;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Abstract base class for {@link com.rocket.summer.framework.http.converter.HttpMessageConverter HttpMessageConverters} that
 * use JAXB2. Creates {@link JAXBContext} object lazily.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public abstract class AbstractJaxb2HttpMessageConverter<T> extends AbstractXmlHttpMessageConverter<T> {

    private final ConcurrentMap<Class, JAXBContext> jaxbContexts = new ConcurrentHashMap<Class, JAXBContext>();

    /**
     * Creates a new {@link Marshaller} for the given class.
     *
     * @param clazz the class to create the marshaller for
     * @return the {@code Marshaller}
     * @throws HttpMessageConversionException in case of JAXB errors
     */
    protected final Marshaller createMarshaller(Class clazz) {
        try {
            JAXBContext jaxbContext = getJaxbContext(clazz);
            return jaxbContext.createMarshaller();
        }
        catch (JAXBException ex) {
            throw new HttpMessageConversionException(
                    "Could not create Marshaller for class [" + clazz + "]: " + ex.getMessage(), ex);
        }
    }

    /**
     * Creates a new {@link Unmarshaller} for the given class.
     *
     * @param clazz the class to create the unmarshaller for
     * @return the {@code Unmarshaller}
     * @throws HttpMessageConversionException in case of JAXB errors
     */
    protected final Unmarshaller createUnmarshaller(Class clazz) throws JAXBException {
        try {
            JAXBContext jaxbContext = getJaxbContext(clazz);
            return jaxbContext.createUnmarshaller();
        }
        catch (JAXBException ex) {
            throw new HttpMessageConversionException(
                    "Could not create Unmarshaller for class [" + clazz + "]: " + ex.getMessage(), ex);
        }
    }

    /**
     * Returns a {@link JAXBContext} for the given class.
     *
     * @param clazz the class to return the context for
     * @return the {@code JAXBContext}
     * @throws HttpMessageConversionException in case of JAXB errors
     */
    protected final JAXBContext getJaxbContext(Class clazz) {
        Assert.notNull(clazz, "'clazz' must not be null");
        JAXBContext jaxbContext = jaxbContexts.get(clazz);
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(clazz);
                jaxbContexts.putIfAbsent(clazz, jaxbContext);
            }
            catch (JAXBException ex) {
                throw new HttpMessageConversionException(
                        "Could not instantiate JAXBContext for class [" + clazz + "]: " + ex.getMessage(), ex);
            }
        }
        return jaxbContext;
    }

}

