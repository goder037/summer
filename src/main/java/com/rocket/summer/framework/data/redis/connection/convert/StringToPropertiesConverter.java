package com.rocket.summer.framework.data.redis.connection.convert;

import java.io.StringReader;
import java.util.Properties;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.data.redis.RedisSystemException;

/**
 * Converts Strings to {@link Properties}
 *
 * @author Jennifer Hickey
 */
public class StringToPropertiesConverter implements Converter<String, Properties> {

    public Properties convert(String source) {
        if (source == null) {
            return null;
        }
        Properties info = new Properties();
        StringReader stringReader = new StringReader(source);
        try {
            info.load(stringReader);
        } catch (Exception ex) {
            throw new RedisSystemException("Cannot read Redis info", ex);
        } finally {
            stringReader.close();
        }
        return info;
    }
}

