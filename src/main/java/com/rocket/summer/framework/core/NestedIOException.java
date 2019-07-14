package com.rocket.summer.framework.core;

import java.io.IOException;

/**
 * Subclass of IOException that properly handles a root cause,
 * exposing the root cause just like NestedChecked/RuntimeException does.
 *
 * <p>The similarity between this class and the NestedChecked/RuntimeException
 * class is unavoidable, as this class needs to derive from IOException
 * and cannot derive from NestedCheckedException.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see #getMessage
 * @see #printStackTrace
 * @see org.springframework.core.NestedCheckedException
 * @see org.springframework.core.NestedRuntimeException
 */
public class NestedIOException extends IOException {

    /**
     * Construct a <code>NestedIOException</code> with the specified detail message.
     * @param msg the detail message
     */
    public NestedIOException(String msg) {
        super(msg);
    }

    /**
     * Construct a <code>NestedIOException</code> with the specified detail message
     * and nested exception.
     * @param msg the detail message
     * @param cause the nested exception
     */
    public NestedIOException(String msg, Throwable cause) {
        super(msg);
        initCause(cause);
    }


    /**
     * Return the detail message, including the message from the nested exception
     * if there is one.
     */
    public String getMessage() {
        return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
    }

}
