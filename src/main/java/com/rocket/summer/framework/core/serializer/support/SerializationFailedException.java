package com.rocket.summer.framework.core.serializer.support;

import com.rocket.summer.framework.core.NestedRuntimeException;

/**
 * Wrapper for the native IOException (or similar) when a
 * {@link com.rocket.summer.framework.core.serializer.Serializer} or
 * {@link com.rocket.summer.framework.core.serializer.Deserializer} failed.
 * Thrown by {@link SerializingConverter} and {@link DeserializingConverter}.
 *
 * @author Gary Russell
 * @author Juergen Hoeller
 * @since 3.0.5
 */
@SuppressWarnings("serial")
public class SerializationFailedException extends NestedRuntimeException {

    /**
     * Construct a {@code SerializationException} with the specified detail message.
     * @param message the detail message
     */
    public SerializationFailedException(String message) {
        super(message);
    }

    /**
     * Construct a {@code SerializationException} with the specified detail message
     * and nested exception.
     * @param message the detail message
     * @param cause the nested exception
     */
    public SerializationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}

