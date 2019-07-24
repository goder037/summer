package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.ConditionalGenericConverter;
import com.rocket.summer.framework.core.convert.converter.GenericConverter;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Converts a Collection to an array.
 *
 * <p>First, creates a new array of the requested targetType with a length equal to the
 * size of the source Collection. Then sets each collection element into the array.
 * Will perform an element conversion from the collection's parameterized type to the
 * array's component type if necessary.
 *
 * @author Keith Donald
 * @since 3.0
 */
final class CollectionToArrayConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;

    public CollectionToArrayConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Collection.class, Object[].class));
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(sourceType.getElementTypeDescriptor(), targetType.getElementTypeDescriptor(), this.conversionService);
    }

    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        Collection<?> sourceCollection = (Collection<?>) source;
        Object array = Array.newInstance(targetType.getElementTypeDescriptor().getType(), sourceCollection.size());
        int i = 0;
        for (Object sourceElement : sourceCollection) {
            Object targetElement = this.conversionService.convert(sourceElement, sourceType.elementTypeDescriptor(sourceElement), targetType.getElementTypeDescriptor());
            Array.set(array, i++, targetElement);
        }
        return array;
    }

}
