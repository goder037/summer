package com.rocket.summer.framework.expression;

/**
 * An AccessException is thrown by an accessor if it has an unexpected problem.
 *
 * @author Andy Clement
 * @since 3.0
 */
public class AccessException extends Exception {

    /**
     * Create an AccessException with a specific message.
     * @param message the message
     */
    public AccessException(String message) {
        super(message);
    }

    /**
     * Create an AccessException with a specific message and cause.
     * @param message the message
     * @param cause the cause
     */
    public AccessException(String message, Exception cause) {
        super(message, cause);
    }

}
