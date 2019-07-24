package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.converter.Converter;

/**
 * Converts from any JDK-standard Number implementation to a Character.
 *
 * @author Keith Donald
 * @since 3.0
 * @see java.lang.Character
 * @see java.lang.Short
 * @see java.lang.Integer
 * @see java.lang.Long
 * @see java.math.BigInteger
 * @see java.lang.Float
 * @see java.lang.Double
 * @see java.math.BigDecimal
 */
final class NumberToCharacterConverter implements Converter<Number, Character> {

    public Character convert(Number source) {
        return (char) source.shortValue();
    }

}
