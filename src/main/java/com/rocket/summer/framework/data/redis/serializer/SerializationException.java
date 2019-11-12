package com.rocket.summer.framework.data.redis.serializer;

import com.rocket.summer.framework.core.NestedRuntimeException;

/**
 * Generic exception indicating a serialization/deserialization error.
 *
 * @author Costin Leau
 */
public class SerializationException extends NestedRuntimeException {

    /**
     * Constructs a new <code>SerializationException</code> instance.
     *
     * @param msg
     * @param cause
     */
    public SerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs a new <code>SerializationException</code> instance.
     *
     * @param msg
     */
    public SerializationException(String msg) {
        super(msg);
    }
}
