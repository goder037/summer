package com.rocket.summer.framework.objenesis;

/**
 * Exception thrown by Objenesis. It wraps any instantiation exceptions. Note that this exception is
 * runtime to prevent having to catch it.
 *
 * @author Henri Tremblay
 */
public class ObjenesisException extends RuntimeException {

    private static final long serialVersionUID = -2677230016262426968L;

    /**
     * @param msg Error message
     */
    public ObjenesisException(String msg) {
        super(msg);
    }

    /**
     * @param cause Wrapped exception. The message will be the one of the cause.
     */
    public ObjenesisException(Throwable cause) {
        super(cause);
    }

    /**
     * @param msg Error message
     * @param cause Wrapped exception
     */
    public ObjenesisException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
