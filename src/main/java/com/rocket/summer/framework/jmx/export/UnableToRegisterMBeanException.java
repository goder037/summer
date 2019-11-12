package com.rocket.summer.framework.jmx.export;

/**
 * Exception thrown when we are unable to register an MBean,
 * for example because of a naming conflict.
 *
 * @author Rob Harrop
 * @since 2.0
 */
@SuppressWarnings("serial")
public class UnableToRegisterMBeanException extends MBeanExportException {

    /**
     * Create a new {@code UnableToRegisterMBeanException} with the
     * specified error message.
     * @param msg the detail message
     */
    public UnableToRegisterMBeanException(String msg) {
        super(msg);
    }

    /**
     * Create a new {@code UnableToRegisterMBeanException} with the
     * specified error message and root cause.
     * @param msg the detail message
     * @param cause the root caus
     */
    public UnableToRegisterMBeanException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

