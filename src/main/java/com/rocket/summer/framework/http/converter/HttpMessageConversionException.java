package com.rocket.summer.framework.http.converter;

import com.rocket.summer.framework.context.NestedRuntimeException;

/**
 * Thrown by {@link HttpMessageConverter} implementations when the conversion fails.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public class HttpMessageConversionException extends NestedRuntimeException {

    /**
     * Create a new HttpMessageConversionException.
     *
     * @param msg the detail message
     */
    public HttpMessageConversionException(String msg) {
        super(msg);
    }

    /**
     * Create a new HttpMessageConversionException.
     *
     * @param msg the detail message
     * @param cause the root cause (if any)
     */
    public HttpMessageConversionException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
