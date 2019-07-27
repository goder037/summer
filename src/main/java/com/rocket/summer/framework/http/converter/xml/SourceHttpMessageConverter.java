package com.rocket.summer.framework.http.converter.xml;

import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.http.converter.HttpMessageConversionException;
import com.rocket.summer.framework.http.converter.HttpMessageNotReadableException;
import com.rocket.summer.framework.http.converter.HttpMessageNotWritableException;
import org.xml.sax.InputSource;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Implementation of {@link com.rocket.summer.framework.http.converter.HttpMessageConverter} that can read and write {@link
 * Source} objects.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public class SourceHttpMessageConverter<T extends Source> extends AbstractXmlHttpMessageConverter<T> {

    @Override
    public boolean supports(Class<?> clazz) {
        return DOMSource.class.equals(clazz) || SAXSource.class.equals(clazz) || StreamSource.class.equals(clazz) ||
                Source.class.equals(clazz);
    }

    @Override
    protected T readFromSource(Class clazz, HttpHeaders headers, Source source) throws IOException {
        try {
            if (DOMSource.class.equals(clazz)) {
                DOMResult domResult = new DOMResult();
                transform(source, domResult);
                return (T) new DOMSource(domResult.getNode());
            }
            else if (SAXSource.class.equals(clazz)) {
                ByteArrayInputStream bis = transformToByteArrayInputStream(source);
                return (T) new SAXSource(new InputSource(bis));
            }
            else if (StreamSource.class.equals(clazz) || Source.class.equals(clazz)) {
                ByteArrayInputStream bis = transformToByteArrayInputStream(source);
                return (T) new StreamSource(bis);
            }
            else {
                throw new HttpMessageConversionException("Could not read class [" + clazz +
                        "]. Only DOMSource, SAXSource, and StreamSource are supported.");
            }
        }
        catch (TransformerException ex) {
            throw new HttpMessageNotReadableException("Could not transform from [" + source + "] to [" + clazz + "]",
                    ex);
        }
    }

    private ByteArrayInputStream transformToByteArrayInputStream(Source source) throws TransformerException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        transform(source, new StreamResult(bos));
        return new ByteArrayInputStream(bos.toByteArray());
    }

    @Override
    protected Long getContentLength(T t, MediaType contentType) {
        if (t instanceof DOMSource) {
            try {
                CountingOutputStream os = new CountingOutputStream();
                transform(t, new StreamResult(os));
                return os.count;
            }
            catch (TransformerException ex) {
                // ignore
            }
        }
        return null;
    }

    @Override
    protected void writeToResult(T t, HttpHeaders headers, Result result) throws IOException {
        try {
            transform(t, result);
        }
        catch (TransformerException ex) {
            throw new HttpMessageNotWritableException("Could not transform [" + t + "] to [" + result + "]", ex);
        }
    }

    private static class CountingOutputStream extends OutputStream {

        private long count = 0;

        @Override
        public void write(int b) throws IOException {
            count++;
        }

        @Override
        public void write(byte[] b) throws IOException {
            count += b.length;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            count += len;
        }
    }

}
