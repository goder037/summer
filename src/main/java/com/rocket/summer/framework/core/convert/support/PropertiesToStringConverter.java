package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.converter.Converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Converts from a Properties to a String by calling {@link Properties#store(java.io.OutputStream, String)}.
 * Decodes with the UTF-8 charset before returning the String.
 *
 * @author Keith Donald
 * @since 3.0
 */
final class PropertiesToStringConverter implements Converter<Properties, String> {

    public String convert(Properties source) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            source.store(os, null);
            return os.toString("UTF-8");
        }
        catch (IOException ex) {
            // Should never happen.
            throw new IllegalArgumentException("Failed to store [" + source + "] into String", ex);
        }
    }

}

