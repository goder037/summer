package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.core.convert.converter.ConverterFactory;
import com.rocket.summer.framework.util.NumberUtils;

/**
 * Converts from any JDK-standard Number implementation to any other JDK-standard Number implementation.
 *
 * <p>Support Number classes including Byte, Short, Integer, Float, Double, Long, BigInteger, BigDecimal. This class
 * delegates to {@link NumberUtils#convertNumberToTargetClass(Number, Class)} to perform the conversion.
 *
 * @author Keith Donald
 * @since 3.0
 * @see java.lang.Byte
 * @see java.lang.Short
 * @see java.lang.Integer
 * @see java.lang.Long
 * @see java.math.BigInteger
 * @see java.lang.Float
 * @see java.lang.Double
 * @see java.math.BigDecimal
 * @see NumberUtils
 */
final class NumberToNumberConverterFactory implements ConverterFactory<Number, Number> {

    public <T extends Number> Converter<Number, T> getConverter(Class<T> targetType) {
        return new NumberToNumber<T>(targetType);
    }

    private final static class NumberToNumber<T extends Number> implements Converter<Number, T> {

        private final Class<T> targetType;

        public NumberToNumber(Class<T> targetType) {
            this.targetType = targetType;
        }

        public T convert(Number source) {
            return NumberUtils.convertNumberToTargetClass(source, this.targetType);
        }
    }

}
