package com.rocket.summer.framework.context;

import com.rocket.summer.framework.beans.FatalBeanException;

/**
 * Exception thrown during application context initialization.
 *
 * @author Rod Johnson
 */
public class ApplicationContextException extends FatalBeanException {

    /**
     * Create a new <code>ApplicationContextException</code>
     * with the specified detail message and no root cause.
     * @param msg the detail message
     */
    public ApplicationContextException(String msg) {
        super(msg);
    }

    /**
     * Create a new <code>ApplicationContextException</code>
     * with the specified detail message and the given root cause.
     * @param msg the detail message
     * @param cause the root cause
     */
    public ApplicationContextException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

