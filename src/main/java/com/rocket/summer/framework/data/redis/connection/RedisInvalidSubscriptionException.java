package com.rocket.summer.framework.data.redis.connection;

import com.rocket.summer.framework.dao.InvalidDataAccessResourceUsageException;

/**
 * Exception thrown when subscribing to an expired/dead {@link Subscription}.
 *
 * @author Costin Leau
 */
public class RedisInvalidSubscriptionException extends InvalidDataAccessResourceUsageException {

    /**
     * Constructs a new <code>RedisInvalidSubscriptionException</code> instance.
     *
     * @param msg
     * @param cause
     */
    public RedisInvalidSubscriptionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs a new <code>RedisInvalidSubscriptionException</code> instance.
     *
     * @param msg
     */
    public RedisInvalidSubscriptionException(String msg) {
        super(msg);
    }
}

