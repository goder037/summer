package com.rocket.summer.framework.data.redis.connection.convert;

import java.util.Map;
import java.util.Properties;

import com.rocket.summer.framework.core.convert.converter.Converter;

/**
 * @author Christoph Strobl
 * @since 1.4
 */
public enum MapToPropertiesConverter implements Converter<Map<?, ?>, Properties> {
    INSTANCE;

    @Override
    public Properties convert(Map<?, ?> source) {

        Properties p = new Properties();
        if (source != null) {
            p.putAll(source);
        }
        return p;
    }

}
