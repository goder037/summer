package com.rocket.summer.framework.http.converter;

/**
 * Thrown by {@link com.rocket.summer.framework.http.converter.HttpMessageConverter} implementations when the
 * {@link com.rocket.summer.framework.http.converter.HttpMessageConverter#write(Object, com.rocket.summer.framework.http.HttpOutputMessage) write} method fails.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public class HttpMessageNotWritableException extends HttpMessageConversionException {

    /**
     * Create a new HttpMessageNotWritableException.
     *
     * @param msg the detail message
     */
    public HttpMessageNotWritableException(String msg) {
        super(msg);
    }

    /**
     * Create a new HttpMessageNotWritableException.
     *
     * @param msg the detail message
     * @param cause the root cause (if any)
     */
    public HttpMessageNotWritableException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
