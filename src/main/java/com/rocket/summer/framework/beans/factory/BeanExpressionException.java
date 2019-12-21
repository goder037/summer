package com.rocket.summer.framework.beans.factory;

import com.rocket.summer.framework.beans.FatalBeanException;

/**
 * Exception that indicates an expression evaluation attempt having failed.
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
public class BeanExpressionException extends FatalBeanException {

    /**
     * Create a new BeanExpressionException with the specified message.
     * @param msg the detail message
     */
    public BeanExpressionException(String msg) {
        super(msg);
    }

    /**
     * Create a new BeanExpressionException with the specified message
     * and root cause.
     * @param msg the detail message
     * @param cause the root cause
     */
    public BeanExpressionException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
