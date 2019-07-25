package com.rocket.summer.framework.http.converter;

/**
 * Thrown by {@link HttpMessageConverter} implementations when the
 * {@link HttpMessageConverter#read(Class, org.springframework.http.HttpInputMessage) read} method fails.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public class HttpMessageNotReadableException extends HttpMessageConversionException {

    /**
     * Create a new HttpMessageNotReadableException.
     *
     * @param msg the detail message
     */
    public HttpMessageNotReadableException(String msg) {
        super(msg);
    }

    /**
     * Create a new HttpMessageNotReadableException.
     *
     * @param msg the detail message
     * @param cause the root cause (if any)
     */
    public HttpMessageNotReadableException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
