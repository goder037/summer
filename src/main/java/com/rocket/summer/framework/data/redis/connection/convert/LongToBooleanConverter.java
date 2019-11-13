package com.rocket.summer.framework.data.redis.connection.convert;

import com.rocket.summer.framework.core.convert.converter.Converter;

/**
 * Converts Longs to Booleans
 *
 * @author Jennifer Hickey
 */
public class LongToBooleanConverter implements Converter<Long, Boolean> {

    public Boolean convert(Long result) {
        return result != null ? result == 1 : null;
    }

}
