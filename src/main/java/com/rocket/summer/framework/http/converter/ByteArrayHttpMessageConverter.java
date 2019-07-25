package com.rocket.summer.framework.http.converter;

import com.rocket.summer.framework.http.HttpInputMessage;
import com.rocket.summer.framework.http.HttpOutputMessage;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.util.FileCopyUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Implementation of {@link HttpMessageConverter} that can read and write byte arrays.
 *
 * <p>By default, this converter supports all media types (<code>&#42;&#47;&#42;</code>), and writes with a {@code
 * Content-Type} of {@code application/octet-stream}. This can be overridden by setting the {@link
 * #setSupportedMediaTypes(java.util.List) supportedMediaTypes} property.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public class ByteArrayHttpMessageConverter extends AbstractHttpMessageConverter<byte[]> {

    /** Creates a new instance of the {@code ByteArrayHttpMessageConverter}. */
    public ByteArrayHttpMessageConverter() {
        super(new MediaType("application", "octet-stream"), MediaType.ALL);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return byte[].class.equals(clazz);
    }

    @Override
    public byte[] readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException {
        long contentLength = inputMessage.getHeaders().getContentLength();
        if (contentLength >= 0) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) contentLength);
            FileCopyUtils.copy(inputMessage.getBody(), bos);
            return bos.toByteArray();
        }
        else {
            return FileCopyUtils.copyToByteArray(inputMessage.getBody());
        }
    }

    @Override
    protected Long getContentLength(byte[] bytes, MediaType contentType) {
        return (long) bytes.length;
    }

    @Override
    protected void writeInternal(byte[] bytes, HttpOutputMessage outputMessage) throws IOException {
        FileCopyUtils.copy(bytes, outputMessage.getBody());
    }

}
