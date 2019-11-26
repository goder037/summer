package com.rocket.summer.framework.data.redis.core;

import com.rocket.summer.framework.context.event.ApplicationEvent;

/**
 * Redis specific {@link ApplicationEvent} published when a key expires in Redis.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public class RedisKeyspaceEvent extends ApplicationEvent {

    private final String channel;

    /**
     * Creates new {@link RedisKeyspaceEvent}.
     *
     * @param key The key that expired. Must not be {@literal null}.
     */
    public RedisKeyspaceEvent(byte[] key) {
        this(null, key);
    }

    /**
     * Creates new {@link RedisKeyspaceEvent}.
     *
     * @param channel The source channel aka subscription topic. Can be {@literal null}.
     * @param key The key that expired. Must not be {@literal null}.
     * @since 1.8
     */
    public RedisKeyspaceEvent(String channel, byte[] key) {

        super(key);
        this.channel = channel;
    }

    /*
     * (non-Javadoc)
     * @see java.util.EventObject#getSource()
     */
    public byte[] getSource() {
        return (byte[]) super.getSource();
    }

    /**
     *
     * @return can be {@literal null}.
     * @since 1.8
     */
    public String getChannel() {
        return this.channel;
    }

}

