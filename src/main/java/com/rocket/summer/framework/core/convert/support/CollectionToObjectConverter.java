package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.ConditionalGenericConverter;
import com.rocket.summer.framework.core.convert.converter.GenericConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Converts a Collection to an Object by returning the first collection element after converting it to the desired targetType.
 *
 * @author Keith Donald
 * @since 3.0
 */
final class CollectionToObjectConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;

    public CollectionToObjectConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Collection.class, Object.class));
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(sourceType.getElementTypeDescriptor(), targetType, this.conversionService);
    }

    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        if (sourceType.isAssignableTo(targetType)) {
            return source;
        }
        Collection<?> sourceCollection = (Collection<?>) source;
        if (sourceCollection.size() == 0) {
            return null;
        }
        Object firstElement = sourceCollection.iterator().next();
        return this.conversionService.convert(firstElement, sourceType.elementTypeDescriptor(firstElement), targetType);
    }

}
