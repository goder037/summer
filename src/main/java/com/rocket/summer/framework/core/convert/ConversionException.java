package com.rocket.summer.framework.core.convert;

import com.rocket.summer.framework.context.NestedRuntimeException;

/**
 * Base class for exceptions thrown by the conversion system.
 *
 * @author Keith Donald
 * @since 3.0
 */
public abstract class ConversionException extends NestedRuntimeException {

    /**
     * Construct a new conversion exception.
     * @param message the exception message
     */
    public ConversionException(String message) {
        super(message);
    }

    /**
     * Construct a new conversion exception.
     * @param message the exception message
     * @param cause the cause
     */
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

}

