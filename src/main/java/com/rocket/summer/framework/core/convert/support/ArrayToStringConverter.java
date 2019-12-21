package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.ConditionalGenericConverter;
import com.rocket.summer.framework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * Converts an Array to a comma-delimited String.
 * This implementation first adapts the source Array to a List, then delegates to {@link CollectionToStringConverter} to perform the target String conversion.
 *
 * @author Keith Donald
 * @since 3.0
 */
final class ArrayToStringConverter implements ConditionalGenericConverter {

    private final CollectionToStringConverter helperConverter;

    public ArrayToStringConverter(ConversionService conversionService) {
        this.helperConverter = new CollectionToStringConverter(conversionService);
    }

    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Object[].class, String.class));
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.helperConverter.matches(sourceType, targetType);
    }

    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.helperConverter.convert(Arrays.asList(ObjectUtils.toObjectArray(source)), sourceType, targetType);
    }

}
