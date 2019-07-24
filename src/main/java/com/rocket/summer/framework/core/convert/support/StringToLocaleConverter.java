package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.util.StringUtils;

import java.util.Locale;

/**
 * Converts a String to a Locale.
 *
 * @author Keith Donald
 * @since 3.0
 */
final class StringToLocaleConverter implements Converter<String, Locale> {

    public Locale convert(String source) {
        return StringUtils.parseLocaleString(source);
    }

}
