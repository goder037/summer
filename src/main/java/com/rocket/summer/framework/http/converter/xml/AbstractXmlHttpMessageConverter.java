package com.rocket.summer.framework.http.converter.xml;

import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.http.HttpInputMessage;
import com.rocket.summer.framework.http.HttpOutputMessage;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.http.converter.AbstractHttpMessageConverter;
import com.rocket.summer.framework.http.converter.HttpMessageConversionException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

/**
 * Abstract base class for {@link com.rocket.summer.framework.http.converter.HttpMessageConverter HttpMessageConverters}
 * that convert from/to XML.
 *
 * <p>By default, subclasses of this converter support {@code text/xml}, {@code application/xml}, and {@code
 * application/*-xml}. This can be overridden by setting the {@link #setSupportedMediaTypes(java.util.List)
 * supportedMediaTypes} property.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public abstract class AbstractXmlHttpMessageConverter<T> extends AbstractHttpMessageConverter<T> {

    private final TransformerFactory transformerFactory = TransformerFactory.newInstance();


    /**
     * Protected constructor that sets the {@link #setSupportedMediaTypes(java.util.List) supportedMediaTypes}
     * to {@code text/xml} and {@code application/xml}, and {@code application/*-xml}.
     */
    protected AbstractXmlHttpMessageConverter() {
        super(MediaType.APPLICATION_XML, MediaType.TEXT_XML, new MediaType("application", "*+xml"));
    }


    @Override
    public final T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException {
        return readFromSource(clazz, inputMessage.getHeaders(), new StreamSource(inputMessage.getBody()));
    }

    @Override
    protected final void writeInternal(T t, HttpOutputMessage outputMessage) throws IOException {
        writeToResult(t, outputMessage.getHeaders(), new StreamResult(outputMessage.getBody()));
    }

    /**
     * Transforms the given {@code Source} to the {@code Result}.
     * @param source the source to transform from
     * @param result the result to transform to
     * @throws TransformerException in case of transformation errors
     */
    protected void transform(Source source, Result result) throws TransformerException {
        this.transformerFactory.newTransformer().transform(source, result);
    }


    /**
     * Abstract template method called from {@link #read(Class, HttpInputMessage)}.
     * @param clazz the type of object to return
     * @param headers the HTTP input headers
     * @param source the HTTP input body
     * @return the converted object
     * @throws IOException in case of I/O errors
     * @throws com.rocket.summer.framework.http.converter.HttpMessageConversionException in case of conversion errors
     */
    protected abstract T readFromSource(Class<? extends T> clazz, HttpHeaders headers, Source source)
            throws IOException;

    /**
     * Abstract template method called from {@link #writeInternal(Object, HttpOutputMessage)}.
     * @param t the object to write to the output message
     * @param headers the HTTP output headers
     * @param result the HTTP output body
     * @throws IOException in case of I/O errors
     * @throws HttpMessageConversionException in case of conversion errors
     */
    protected abstract void writeToResult(T t, HttpHeaders headers, Result result)
            throws IOException;

}
