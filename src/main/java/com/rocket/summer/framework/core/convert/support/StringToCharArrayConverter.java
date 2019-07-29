package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.converter.Converter;

/**
 * Converts a String to a Char Array.
 *
 * @author Phillip Webb
 */
public class StringToCharArrayConverter implements Converter<String, char[]> {

    @Override
    public char[] convert(String source) {
        return source.toCharArray();
    }

}