package com.rocket.summer.framework.web;

import com.rocket.summer.framework.http.MediaType;

import javax.servlet.ServletException;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base for exceptions related to media types. Adds a list of supported {@link MediaType MediaTypes}.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public abstract class HttpMediaTypeException extends ServletException {

    private final List<MediaType> supportedMediaTypes;

    /**
     * Create a new MediaTypeException.
     * @param message the exception message
     */
    protected HttpMediaTypeException(String message) {
        super(message);
        this.supportedMediaTypes = Collections.emptyList();
    }

    /**
     * Create a new HttpMediaTypeNotSupportedException.
     * @param supportedMediaTypes the list of supported media types
     */
    protected HttpMediaTypeException(String message, List<MediaType> supportedMediaTypes) {
        super(message);
        this.supportedMediaTypes = supportedMediaTypes;
    }

    /**
     * Return the list of supported media types.
     */
    public List<MediaType> getSupportedMediaTypes() {
        return supportedMediaTypes;
    }
}

