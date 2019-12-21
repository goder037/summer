package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.ConditionalGenericConverter;
import com.rocket.summer.framework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * Converts an Array to another Array.
 * First adapts the source array to a List, then delegates to {@link CollectionToArrayConverter} to perform the target array conversion.
 *
 * @author Keith Donald
 * @since 3.0
 */
final class ArrayToArrayConverter implements ConditionalGenericConverter {

    private final CollectionToArrayConverter helperConverter;

    public ArrayToArrayConverter(ConversionService conversionService) {
        this.helperConverter = new CollectionToArrayConverter(conversionService);
    }

    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Object[].class, Object[].class));
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.helperConverter.matches(sourceType, targetType);
    }

    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.helperConverter.convert(Arrays.asList(ObjectUtils.toObjectArray(source)), sourceType, targetType);
    }

}

