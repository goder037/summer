package com.rocket.summer.framework.data.redis.core;

import java.nio.charset.Charset;

import com.rocket.summer.framework.context.ApplicationEvent;
import com.rocket.summer.framework.data.redis.core.convert.MappingRedisConverter.BinaryKeyspaceIdentifier;

/**
 * {@link RedisKeyExpiredEvent} is Redis specific {@link ApplicationEvent} published when a specific key in Redis
 * expires. It might but must not hold the expired value itself next to the key.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 1.7
 */
public class RedisKeyExpiredEvent<T> extends RedisKeyspaceEvent {

    /**
     * Use {@literal UTF-8} as default charset.
     */
    static final Charset CHARSET = Charset.forName("UTF-8");

    private final BinaryKeyspaceIdentifier objectId;
    private final Object value;

    /**
     * Creates new {@link RedisKeyExpiredEvent}.
     *
     * @param key
     */
    public RedisKeyExpiredEvent(byte[] key) {
        this(key, null);
    }

    /**
     * Creates new {@link RedisKeyExpiredEvent}
     *
     * @param key
     * @param value
     */
    public RedisKeyExpiredEvent(byte[] key, Object value) {
        this(null, key, value);
    }

    /**
     * Creates new {@link RedisKeyExpiredEvent}
     *
     * @pamam channel
     * @param key
     * @param value
     * @since 1.8
     */
    public RedisKeyExpiredEvent(String channel, byte[] key, Object value) {
        super(channel, key);

        if (BinaryKeyspaceIdentifier.isValid(key)) {
            this.objectId = BinaryKeyspaceIdentifier.of(key);
        } else {
            this.objectId = null;
        }

        this.value = value;
    }

    /**
     * Gets the keyspace in which the expiration occured.
     *
     * @return {@literal null} if it could not be determined.
     */
    public String getKeyspace() {
        return objectId != null ? new String(objectId.getKeyspace(), CHARSET) : null;
    }

    /**
     * Get the expired objects id;
     *
     * @return
     */
    public byte[] getId() {
        return objectId != null ? objectId.getId() : getSource();
    }

    /**
     * Get the expired Object
     *
     * @return {@literal null} if not present.
     */
    public Object getValue() {
        return value;
    }

    /*
     * (non-Javadoc)
     * @see java.util.EventObject#toString()
     */
    @Override
    public String toString() {
        return "RedisKeyExpiredEvent [keyspace=" + getKeyspace() + ", id=" + getId() + "]";
    }

}

