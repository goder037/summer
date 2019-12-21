package com.rocket.summer.framework.core.serializer.support;

import com.rocket.summer.framework.core.serializer.DefaultDeserializer;
import com.rocket.summer.framework.core.serializer.DefaultSerializer;
import com.rocket.summer.framework.core.serializer.Deserializer;
import com.rocket.summer.framework.core.serializer.Serializer;
import com.rocket.summer.framework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A convenient delegate with pre-arranged configuration state for common
 * serialization needs. Implements {@link Serializer} and {@link Deserializer}
 * itself, so can also be passed into such more specific callback methods.
 *
 * @author Juergen Hoeller
 * @since 4.3
 */
public class SerializationDelegate implements Serializer<Object>, Deserializer<Object> {

    private final Serializer<Object> serializer;

    private final Deserializer<Object> deserializer;


    /**
     * Create a {@code SerializationDelegate} with a default serializer/deserializer
     * for the given {@code ClassLoader}.
     * @see DefaultDeserializer
     * @see DefaultDeserializer#DefaultDeserializer(ClassLoader)
     */
    public SerializationDelegate(ClassLoader classLoader) {
        this.serializer = new DefaultSerializer();
        this.deserializer = new DefaultDeserializer(classLoader);
    }

    /**
     * Create a {@code SerializationDelegate} with the given serializer/deserializer.
     * @param serializer the {@link Serializer} to use (never {@code null)}
     * @param deserializer the {@link Deserializer} to use (never {@code null)}
     */
    public SerializationDelegate(Serializer<Object> serializer, Deserializer<Object> deserializer) {
        Assert.notNull(serializer, "Serializer must not be null");
        Assert.notNull(deserializer, "Deserializer must not be null");
        this.serializer = serializer;
        this.deserializer = deserializer;
    }


    @Override
    public void serialize(Object object, OutputStream outputStream) throws IOException {
        this.serializer.serialize(object, outputStream);
    }

    @Override
    public Object deserialize(InputStream inputStream) throws IOException {
        return this.deserializer.deserialize(inputStream);
    }

}

