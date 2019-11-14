package com.rocket.summer.framework.data.redis.cache;

import static com.rocket.summer.framework.util.Assert.*;

import java.util.Arrays;

import com.rocket.summer.framework.data.redis.serializer.RedisSerializer;

/**
 * @author Christoph Strobl
 * @since 1.5
 */
public class RedisCacheKey {

    private final Object keyElement;
    private byte[] prefix;
    @SuppressWarnings("rawtypes")//
    private RedisSerializer serializer;

    /**
     * @param keyElement must not be {@literal null}.
     */
    public RedisCacheKey(Object keyElement) {

        notNull(keyElement, "KeyElement must not be null!");
        this.keyElement = keyElement;
    }

    /**
     * Get the {@link Byte} representation of the given key element using prefix if available.
     */
    public byte[] getKeyBytes() {

        byte[] rawKey = serializeKeyElement();
        if (!hasPrefix()) {
            return rawKey;
        }

        byte[] prefixedKey = Arrays.copyOf(prefix, prefix.length + rawKey.length);
        System.arraycopy(rawKey, 0, prefixedKey, prefix.length, rawKey.length);

        return prefixedKey;
    }

    /**
     * @return
     */
    public Object getKeyElement() {
        return keyElement;
    }

    @SuppressWarnings("unchecked")
    private byte[] serializeKeyElement() {

        if (serializer == null && keyElement instanceof byte[]) {
            return (byte[]) keyElement;
        }

        return serializer.serialize(keyElement);
    }

    /**
     * Set the {@link RedisSerializer} used for converting the key into its {@link Byte} representation.
     *
     * @param serializer can be {@literal null}.
     */
    public void setSerializer(RedisSerializer<?> serializer) {
        this.serializer = serializer;
    }

    /**
     * @return true if prefix is not empty.
     */
    public boolean hasPrefix() {
        return (prefix != null && prefix.length > 0);
    }

    /**
     * Use the given prefix when generating key.
     *
     * @param prefix can be {@literal null}.
     * @return
     */
    public RedisCacheKey usePrefix(byte[] prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * Use {@link RedisSerializer} for converting the key into its {@link Byte} representation.
     *
     * @param serializer can be {@literal null}.
     * @return
     */
    public RedisCacheKey withKeySerializer(RedisSerializer serializer) {

        this.serializer = serializer;
        return this;
    }

}

