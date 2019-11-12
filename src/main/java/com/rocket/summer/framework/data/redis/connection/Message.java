package com.rocket.summer.framework.data.redis.connection;

import java.io.Serializable;

/**
 * Class encapsulating a Redis message body and its properties.
 *
 * @author Costin Leau
 */
public interface Message extends Serializable {

    /**
     * Returns the body (or the payload) of the message.
     *
     * @return message body
     */
    byte[] getBody();

    /**
     * Returns the channel associated with the message.
     *
     * @return message channel.
     */
    byte[] getChannel();
}
