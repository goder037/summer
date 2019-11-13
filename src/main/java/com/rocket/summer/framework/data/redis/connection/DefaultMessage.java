package com.rocket.summer.framework.data.redis.connection;

/**
 * Default message implementation.
 *
 * @author Costin Leau
 */
public class DefaultMessage implements Message {

    private final byte[] channel;
    private final byte[] body;
    private String toString;

    public DefaultMessage(byte[] channel, byte[] body) {
        this.body = body;
        this.channel = channel;
    }

    public byte[] getChannel() {
        return (channel != null ? channel.clone() : null);
    }

    public byte[] getBody() {
        return (body != null ? body.clone() : null);
    }

    public String toString() {
        if (toString == null) {
            toString = new String(body);
        }
        return toString;
    }
}

