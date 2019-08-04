package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.converter.Converter;

import java.util.Currency;

/**
 * Convert a String to a {@link Currency}.
 *
 * @author Stephane Nicoll
 * @since 4.2
 */
class StringToCurrencyConverter implements Converter<String, Currency> {

    @Override
    public Currency convert(String source) {
        return Currency.getInstance(source);
    }

}
