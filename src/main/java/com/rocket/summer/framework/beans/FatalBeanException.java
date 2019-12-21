package com.rocket.summer.framework.beans;

import com.rocket.summer.framework.context.BeansException;

/**
 * Thrown on an unrecoverable problem encountered in the
 * beans packages or sub-packages, e.g. bad class or field.
 *
 * @author Rod Johnson
 */
public class FatalBeanException extends BeansException {

    /**
     * Create a new FatalBeanException with the specified message.
     * @param msg the detail message
     */
    public FatalBeanException(String msg) {
        super(msg);
    }

    /**
     * Create a new FatalBeanException with the specified message
     * and root cause.
     * @param msg the detail message
     * @param cause the root cause
     */
    public FatalBeanException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

