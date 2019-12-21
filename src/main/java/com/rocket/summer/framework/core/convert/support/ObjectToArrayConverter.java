package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.ConditionalGenericConverter;
import com.rocket.summer.framework.core.convert.converter.GenericConverter;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

/**
 * Converts an Object to a single-element Array containing the Object.
 * Will convert the Object to the target Array's component type if necessary.
 *
 * @author Keith Donald
 * @since 3.0
 */
final class ObjectToArrayConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;

    public ObjectToArrayConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object.class, Object[].class));
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(sourceType, targetType.getElementTypeDescriptor(), this.conversionService);
    }

    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        Object target = Array.newInstance(targetType.getElementTypeDescriptor().getType(), 1);
        Object targetElement = this.conversionService.convert(source, sourceType, targetType.getElementTypeDescriptor());
        Array.set(target, 0, targetElement);
        return target;
    }

}
