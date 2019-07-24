package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.ConditionalGenericConverter;
import com.rocket.summer.framework.core.convert.converter.GenericConverter;
import com.rocket.summer.framework.util.StringUtils;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

/**
 * Converts a comma-delimited String to an Array.
 * Only matches if String.class can be converted to the target array element type.
 *
 * @author Keith Donald
 * @since 3.0
 */
final class StringToArrayConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;

    public StringToArrayConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, Object[].class));
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.conversionService.canConvert(sourceType, targetType.getElementTypeDescriptor());
    }

    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        String string = (String) source;
        String[] fields = StringUtils.commaDelimitedListToStringArray(string);
        Object target = Array.newInstance(targetType.getElementTypeDescriptor().getType(), fields.length);
        for (int i = 0; i < fields.length; i++) {
            String sourceElement = fields[i];
            Object targetElement = this.conversionService.convert(sourceElement.trim(), sourceType, targetType.getElementTypeDescriptor());
            Array.set(target, i, targetElement);
        }
        return target;
    }


}
