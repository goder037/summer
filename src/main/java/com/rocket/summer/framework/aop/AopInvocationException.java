package com.rocket.summer.framework.aop;

import com.rocket.summer.framework.context.NestedRuntimeException;

/**
 * Exception that gets thrown when an AOP invocation failed
 * because of misconfiguration or unexpected runtime issues.
 *
 * @author Juergen Hoeller
 * @since 2.0
 */
public class AopInvocationException extends NestedRuntimeException {

    /**
     * Constructor for AopInvocationException.
     * @param msg the detail message
     */
    public AopInvocationException(String msg) {
        super(msg);
    }

    /**
     * Constructor for AopInvocationException.
     * @param msg the detail message
     * @param cause the root cause
     */
    public AopInvocationException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

