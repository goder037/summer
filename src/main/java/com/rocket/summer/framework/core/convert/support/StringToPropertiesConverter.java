package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.converter.Converter;

import java.io.ByteArrayInputStream;
import java.util.Properties;

/**
 * Converts a String to a Properties by calling Properties#load(java.io.InputStream).
 * Uses UTF-8 encoding required by Properties.
 *
 * @author Keith Donald
 * @since 3.0
 */
final class StringToPropertiesConverter implements Converter<String, Properties> {

    public Properties convert(String source) {
        try {
            Properties props = new Properties();
            // Must use the ISO-8859-1 encoding because Properties.load(stream) expects it.
            props.load(new ByteArrayInputStream(source.getBytes("UTF-8")));
            return props;
        }
        catch (Exception ex) {
            // Should never happen.
            throw new IllegalArgumentException("Failed to parse [" + source + "] into Properties", ex);
        }
    }

}

