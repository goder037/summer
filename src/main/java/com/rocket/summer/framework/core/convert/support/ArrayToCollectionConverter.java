package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.CollectionFactory;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.ConditionalGenericConverter;
import com.rocket.summer.framework.core.convert.converter.GenericConverter;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Converts an Array to a Collection.
 *
 * <p>First, creates a new Collection of the requested targetType.
 * Then adds each array element to the target collection.
 * Will perform an element conversion from the source component type to the collection's parameterized type if necessary.
 *
 * @author Keith Donald
 * @since 3.0
 */
final class ArrayToCollectionConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;

    public ArrayToCollectionConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object[].class, Collection.class));
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(
                sourceType.getElementTypeDescriptor(), targetType.getElementTypeDescriptor(), this.conversionService);
    }

    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        int length = Array.getLength(source);
        Collection<Object> target = CollectionFactory.createCollection(targetType.getType(), length);
        if (targetType.getElementTypeDescriptor() == null) {
            for (int i = 0; i < length; i++) {
                Object sourceElement = Array.get(source, i);
                target.add(sourceElement);
            }
        }
        else {
            for (int i = 0; i < length; i++) {
                Object sourceElement = Array.get(source, i);
                Object targetElement = this.conversionService.convert(sourceElement,
                        sourceType.elementTypeDescriptor(sourceElement), targetType.getElementTypeDescriptor());
                target.add(targetElement);
            }
        }
        return target;
    }

}
