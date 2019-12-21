package com.rocket.summer.framework.core.serializer.support;

import java.io.ByteArrayOutputStream;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.core.serializer.DefaultSerializer;
import com.rocket.summer.framework.core.serializer.Serializer;
import com.rocket.summer.framework.util.Assert;

/**
 * A {@link Converter} that delegates to a
 * {@link com.rocket.summer.framework.core.serializer.Serializer}
 * to convert an object to a byte array.
 *
 * @author Gary Russell
 * @author Mark Fisher
 * @since 3.0.5
 */
public class SerializingConverter implements Converter<Object, byte[]> {

    private final Serializer<Object> serializer;


    /**
     * Create a default {@code SerializingConverter} that uses standard Java serialization.
     */
    public SerializingConverter() {
        this.serializer = new DefaultSerializer();
    }

    /**
     * Create a {@code SerializingConverter} that delegates to the provided {@link Serializer}.
     */
    public SerializingConverter(Serializer<Object> serializer) {
        Assert.notNull(serializer, "Serializer must not be null");
        this.serializer = serializer;
    }


    /**
     * Serializes the source object and returns the byte array result.
     */
    @Override
    public byte[] convert(Object source) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(1024);
        try  {
            this.serializer.serialize(source, byteStream);
            return byteStream.toByteArray();
        }
        catch (Throwable ex) {
            throw new SerializationFailedException("Failed to serialize object using " +
                    this.serializer.getClass().getSimpleName(), ex);
        }
    }

}

