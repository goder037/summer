package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.converter.Converter;

import java.nio.charset.Charset;

/**
 * Convert a String to a {@link Charset}.
 *
 * @author Stephane Nicoll
 * @since 4.2
 */
class StringToCharsetConverter implements Converter<String, Charset> {

    @Override
    public Charset convert(String source) {
        return Charset.forName(source);
    }

}