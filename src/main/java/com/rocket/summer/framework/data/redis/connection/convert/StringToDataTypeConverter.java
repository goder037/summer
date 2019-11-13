package com.rocket.summer.framework.data.redis.connection.convert;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.data.redis.connection.DataType;

/**
 * Converts Strings to {@link DataType}s
 *
 * @author Jennifer Hickey
 */
public class StringToDataTypeConverter implements Converter<String, DataType> {

    public DataType convert(String source) {
        if (source == null) {
            return null;
        }
        return DataType.fromCode(source);
    }

}

