package com.rocket.summer.framework.web.multipart;

import com.rocket.summer.framework.context.NestedRuntimeException;

/**
 * Exception thrown when multipart resolution fails.
 *
 * @author Trevor D. Cook
 * @author Juergen Hoeller
 * @since 29.09.2003
 * @see MultipartResolver#resolveMultipart
 * @see org.springframework.web.multipart.support.MultipartFilter
 */
public class MultipartException extends NestedRuntimeException {

    /**
     * Constructor for MultipartException.
     * @param msg the detail message
     */
    public MultipartException(String msg) {
        super(msg);
    }

    /**
     * Constructor for MultipartException.
     * @param msg the detail message
     * @param cause the root cause from the multipart parsing API in use
     */
    public MultipartException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
