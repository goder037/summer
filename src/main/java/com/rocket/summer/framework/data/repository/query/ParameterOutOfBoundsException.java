package com.rocket.summer.framework.data.repository.query;

/**
 * Exception to be thrown when trying to access a {@link Parameter} with an invalid index inside a {@link Parameters}
 * instance.
 *
 * @author Oliver Gierke
 */
public class ParameterOutOfBoundsException extends RuntimeException {

    private static final long serialVersionUID = 8433209953653278886L;

    /**
     * Creates a new {@link ParameterOutOfBoundsException} with the given exception as cause.
     *
     * @param message
     * @param cause
     */
    public ParameterOutOfBoundsException(String message, Throwable cause) {
        super(message, cause);
    }
}
