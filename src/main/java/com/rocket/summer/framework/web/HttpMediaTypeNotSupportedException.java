package com.rocket.summer.framework.web;

import com.rocket.summer.framework.http.MediaType;

import java.util.List;

/**
 * Exception thrown when a client POSTs or PUTs content
 * not supported by request handler.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public class HttpMediaTypeNotSupportedException extends HttpMediaTypeException {

    private final MediaType contentType;

    /**
     * Create a new HttpMediaTypeNotSupportedException.
     * @param message the exception message
     */
    public HttpMediaTypeNotSupportedException(String message) {
        super(message);
        this.contentType = null;
    }

    /**
     * Create a new HttpMediaTypeNotSupportedException.
     * @param contentType the unsupported content type
     * @param supportedMediaTypes the list of supported media types
     */
    public HttpMediaTypeNotSupportedException(MediaType contentType, List<MediaType> supportedMediaTypes) {
        this(contentType, supportedMediaTypes, "Content type '" + contentType + "' not supported");
    }

    /**
     * Create a new HttpMediaTypeNotSupportedException.
     * @param contentType the unsupported content type
     * @param supportedMediaTypes the list of supported media types
     * @param msg the detail message
     */
    public HttpMediaTypeNotSupportedException(MediaType contentType, List<MediaType> supportedMediaTypes, String msg) {
        super(msg, supportedMediaTypes);
        this.contentType = contentType;
    }

    /**
     * Return the HTTP request content type method that caused the failure.
     */
    public MediaType getContentType() {
        return contentType;
    }

}

