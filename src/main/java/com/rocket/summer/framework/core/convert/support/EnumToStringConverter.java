package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.converter.Converter;


/**
 * Calls {@link Enum#name()} to convert a source Enum to a String.
 * This converter will not match enums with interfaces that can be converted.
 *
 * @author Keith Donald
 * @author Phillip Webb
 * @since 3.0
 */
final class EnumToStringConverter extends AbstractConditionalEnumConverter implements Converter<Enum<?>, String> {

    public EnumToStringConverter(ConversionService conversionService) {
        super(conversionService);
    }

    @Override
    public String convert(Enum<?> source) {
        return source.name();
    }

}