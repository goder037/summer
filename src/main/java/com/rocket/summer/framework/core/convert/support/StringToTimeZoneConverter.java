package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.util.StringUtils;

import java.util.TimeZone;

/**
 * Convert a String to a {@link TimeZone}.
 *
 * @author Stephane Nicoll
 * @since 4.2
 */
class StringToTimeZoneConverter implements Converter<String, TimeZone> {

    @Override
    public TimeZone convert(String source) {
        return StringUtils.parseTimeZoneString(source);
    }

}
