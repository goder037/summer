package com.rocket.summer.framework.data.redis.serializer;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.core.serializer.DefaultDeserializer;
import com.rocket.summer.framework.core.serializer.DefaultSerializer;
import com.rocket.summer.framework.core.serializer.support.DeserializingConverter;
import com.rocket.summer.framework.core.serializer.support.SerializingConverter;
import com.rocket.summer.framework.util.Assert;

/**
 * Java Serialization Redis serializer. Delegates to the default (Java based) {@link DefaultSerializer serializer} and
 * {@link DefaultDeserializer}. This {@link RedisSerializer serializer} can be constructed with either custom
 * {@link ClassLoader} or own {@link Converter converters}.
 *
 * @author Mark Pollack
 * @author Costin Leau
 * @author Mark Paluch
 * @author Christoph Strobl
 */
public class JdkSerializationRedisSerializer implements RedisSerializer<Object> {

    private final Converter<Object, byte[]> serializer;
    private final Converter<byte[], Object> deserializer;

    /**
     * Creates a new {@link JdkSerializationRedisSerializer} using the default class loader.
     */
    public JdkSerializationRedisSerializer() {
        this(new SerializingConverter(), new DeserializingConverter());
    }

    /**
     * Creates a new {@link JdkSerializationRedisSerializer} using a {@link ClassLoader}.
     *
     * @param classLoader
     * @since 1.7
     */
    public JdkSerializationRedisSerializer(ClassLoader classLoader) {
        this(new SerializingConverter(), new DeserializingConverter(classLoader));
    }

    /**
     * Creates a new {@link JdkSerializationRedisSerializer} using a {@link Converter converters} to serialize and
     * deserialize objects.
     *
     * @param serializer must not be {@literal null}
     * @param deserializer must not be {@literal null}
     * @since 1.7
     */
    public JdkSerializationRedisSerializer(Converter<Object, byte[]> serializer, Converter<byte[], Object> deserializer) {

        Assert.notNull(serializer, "Serializer must not be null!");
        Assert.notNull(deserializer, "Deserializer must not be null!");

        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    public Object deserialize(byte[] bytes) {
        if (SerializationUtils.isEmpty(bytes)) {
            return null;
        }

        try {
            return deserializer.convert(bytes);
        } catch (Exception ex) {
            throw new SerializationException("Cannot deserialize", ex);
        }
    }

    public byte[] serialize(Object object) {
        if (object == null) {
            return SerializationUtils.EMPTY_ARRAY;
        }
        try {
            return serializer.convert(object);
        } catch (Exception ex) {
            throw new SerializationException("Cannot serialize", ex);
        }
    }
}

