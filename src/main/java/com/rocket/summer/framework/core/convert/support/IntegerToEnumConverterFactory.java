package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.core.convert.converter.ConverterFactory;

/**
 * Converts from a Integer to a {@link java.lang.Enum} by calling {@link Class#getEnumConstants()}.
 *
 * @author Yanming Zhou
 * @author Stephane Nicoll
 * @since 4.3
 */
final class IntegerToEnumConverterFactory implements ConverterFactory<Integer, Enum> {

    @Override
    public <T extends Enum> Converter<Integer, T> getConverter(Class<T> targetType) {
        return new IntegerToEnum(ConversionUtils.getEnumType(targetType));
    }


    private class IntegerToEnum<T extends Enum> implements Converter<Integer, T> {

        private final Class<T> enumType;

        public IntegerToEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(Integer source) {
            return this.enumType.getEnumConstants()[source];
        }
    }

}
