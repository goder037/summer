package com.rocket.summer.framework.core.serializer;

import com.rocket.summer.framework.core.ConfigurableObjectInputStream;
import com.rocket.summer.framework.core.NestedIOException;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * A default {@link Deserializer} implementation that reads an input stream
 * using Java serialization.
 *
 * @author Gary Russell
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 3.0.5
 * @see ObjectInputStream
 */
public class DefaultDeserializer implements Deserializer<Object> {

    private final ClassLoader classLoader;


    /**
     * Create a {@code DefaultDeserializer} with default {@link ObjectInputStream}
     * configuration, using the "latest user-defined ClassLoader".
     */
    public DefaultDeserializer() {
        this.classLoader = null;
    }

    /**
     * Create a {@code DefaultDeserializer} for using an {@link ObjectInputStream}
     * with the given {@code ClassLoader}.
     * @since 4.2.1
     * @see ConfigurableObjectInputStream#ConfigurableObjectInputStream(InputStream, ClassLoader)
     */
    public DefaultDeserializer(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }


    /**
     * Read from the supplied {@code InputStream} and deserialize the contents
     * into an object.
     * @see ObjectInputStream#readObject()
     */
    @Override
    @SuppressWarnings("resource")
    public Object deserialize(InputStream inputStream) throws IOException {
        ObjectInputStream objectInputStream = new ConfigurableObjectInputStream(inputStream, this.classLoader);
        try {
            return objectInputStream.readObject();
        }
        catch (ClassNotFoundException ex) {
            throw new NestedIOException("Failed to deserialize object type", ex);
        }
    }

}
