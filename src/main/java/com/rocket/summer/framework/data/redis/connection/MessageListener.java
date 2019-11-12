package com.rocket.summer.framework.data.redis.connection;

/**
 * Listener of messages published in Redis.
 *
 * @author Costin Leau
 */
public interface MessageListener {

    /**
     * Callback for processing received objects through Redis.
     *
     * @param message message
     * @param pattern pattern matching the channel (if specified) - can be null
     */
    void onMessage(Message message, byte[] pattern);
}
