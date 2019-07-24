package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.converter.Converter;

/**
 * Simply calls {@link Enum#name()} to convert a source Enum to a String.
 * @author Keith Donald
 * @since 3.0
 */
final class EnumToStringConverter implements Converter<Enum<?>, String> {

    public String convert(Enum<?> source) {
        return source.name();
    }

}
