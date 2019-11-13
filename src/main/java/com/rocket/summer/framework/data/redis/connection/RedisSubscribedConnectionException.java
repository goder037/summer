package com.rocket.summer.framework.data.redis.connection;

import com.rocket.summer.framework.dao.InvalidDataAccessApiUsageException;

/**
 * Exception thrown when issuing commands on a connection that is subscribed and waiting for events.
 *
 * @author Costin Leau
 * @see com.rocket.summer.framework.data.redis.connection.RedisPubSubCommands
 */
public class RedisSubscribedConnectionException extends InvalidDataAccessApiUsageException {

    /**
     * Constructs a new <code>RedisSubscribedConnectionException</code> instance.
     *
     * @param msg
     * @param cause
     */
    public RedisSubscribedConnectionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs a new <code>RedisSubscribedConnectionException</code> instance.
     *
     * @param msg
     */
    public RedisSubscribedConnectionException(String msg) {
        super(msg);
    }
}

