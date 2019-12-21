package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.CollectionFactory;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.ConditionalGenericConverter;
import com.rocket.summer.framework.core.convert.converter.GenericConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Converts from a Collection to another Collection.
 *
 * <p>First, creates a new Collection of the requested targetType with a size equal to the
 * size of the source Collection. Then copies each element in the source collection to the
 * target collection. Will perform an element conversion from the source collection's
 * parameterized type to the target collection's parameterized type if necessary.
 *
 * @author Keith Donald
 * @since 3.0
 */
final class CollectionToCollectionConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;

    public CollectionToCollectionConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Collection.class, Collection.class));
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(
                sourceType.getElementTypeDescriptor(), targetType.getElementTypeDescriptor(), this.conversionService);
    }

    @SuppressWarnings("unchecked")
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        boolean copyRequired = !targetType.getType().isInstance(source);
        Collection<?> sourceCollection = (Collection<?>) source;
        if (!copyRequired && sourceCollection.isEmpty()) {
            return sourceCollection;
        }
        Collection<Object> target = CollectionFactory.createCollection(targetType.getType(), sourceCollection.size());
        if (targetType.getElementTypeDescriptor() == null) {
            for (Object element : sourceCollection) {
                target.add(element);
            }
        }
        else {
            for (Object sourceElement : sourceCollection) {
                Object targetElement = this.conversionService.convert(sourceElement,
                        sourceType.elementTypeDescriptor(sourceElement), targetType.getElementTypeDescriptor());
                target.add(targetElement);
                if (sourceElement != targetElement) {
                    copyRequired = true;
                }
            }
        }
        return (copyRequired ? target : source);
    }

}
