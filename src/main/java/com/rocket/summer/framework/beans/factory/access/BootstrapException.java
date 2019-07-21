package com.rocket.summer.framework.beans.factory.access;

import com.rocket.summer.framework.beans.FatalBeanException;

/**
 * Exception thrown if a bean factory could not be loaded by a bootstrap class.
 *
 * @author Rod Johnson
 * @since 02.12.2002
 */
public class BootstrapException extends FatalBeanException {

    /**
     * Create a new BootstrapException with the specified message.
     * @param msg the detail message
     */
    public BootstrapException(String msg) {
        super(msg);
    }

    /**
     * Create a new BootstrapException with the specified message
     * and root cause.
     * @param msg the detail message
     * @param cause the root cause
     */
    public BootstrapException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

