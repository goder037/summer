package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.core.convert.converter.ConverterFactory;

/**
 * Converts from a String to a java.lang.Enum by calling {@link Enum#valueOf(Class, String)}.
 *
 * @author Keith Donald
 * @since 3.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
final class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnum(targetType);
    }

    private class StringToEnum<T extends Enum> implements Converter<String, T> {

        private final Class<T> enumType;

        public StringToEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        public T convert(String source) {
            if (source.length() == 0) {
                // It's an empty enum identifier: reset the enum value to null.
                return null;
            }
            return (T) Enum.valueOf(this.enumType, source.trim());
        }
    }

}
