package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.converter.Converter;

/**
 * Simply calls {@link Object#toString()} to convert a source Object to a String.
 * @author Keith Donald
 * @since 3.0
 */
final class ObjectToStringConverter implements Converter<Object, String> {

    public String convert(Object source) {
        return source.toString();
    }

}
