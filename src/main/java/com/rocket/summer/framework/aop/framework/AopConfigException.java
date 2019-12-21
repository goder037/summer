package com.rocket.summer.framework.aop.framework;

import com.rocket.summer.framework.context.NestedRuntimeException;

/**
 * Exception that gets thrown on illegal AOP configuration arguments.
 *
 * @author Rod Johnson
 * @since 13.03.2003
 */
public class AopConfigException extends NestedRuntimeException {

    /**
     * Constructor for AopConfigException.
     * @param msg the detail message
     */
    public AopConfigException(String msg) {
        super(msg);
    }

    /**
     * Constructor for AopConfigException.
     * @param msg the detail message
     * @param cause the root cause
     */
    public AopConfigException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

