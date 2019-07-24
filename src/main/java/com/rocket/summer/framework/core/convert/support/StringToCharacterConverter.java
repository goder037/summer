package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.converter.Converter;

/**
 * Converts a String to a Character.
 *
 * @author Keith Donald
 * @since 3.0
 */
final class StringToCharacterConverter implements Converter<String, Character> {

    public Character convert(String source) {
        if (source.length() == 0) {
            return null;
        }
        if (source.length() > 1) {
            throw new IllegalArgumentException(
                    "Can only convert a [String] with length of 1 to a [Character]; string value '" + source + "'  has length of " + source.length());
        }
        return source.charAt(0);
    }

}
