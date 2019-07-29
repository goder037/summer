package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.ConditionalGenericConverter;
import com.rocket.summer.framework.core.convert.converter.GenericConverter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converts a {@link Stream} to and from a collection or array, converting the
 * element type if necessary.
 *
 * @author Stephane Nicoll
 * @since 4.2
 */
class StreamConverter implements ConditionalGenericConverter {

    private static final TypeDescriptor STREAM_TYPE = TypeDescriptor.valueOf(Stream.class);

    private static final Set<GenericConverter.ConvertiblePair> CONVERTIBLE_TYPES = createConvertibleTypes();

    private final ConversionService conversionService;


    public StreamConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }


    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return CONVERTIBLE_TYPES;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType.isAssignableTo(STREAM_TYPE)) {
            return matchesFromStream(sourceType.getElementTypeDescriptor(), targetType);
        }
        if (targetType.isAssignableTo(STREAM_TYPE)) {
            return matchesToStream(targetType.getElementTypeDescriptor(), sourceType);
        }
        return false;
    }

    /**
     * Validate that a {@link Collection} of the elements held within the stream can be
     * converted to the specified {@code targetType}.
     * @param elementType the type of the stream elements
     * @param targetType the type to convert to
     */
    public boolean matchesFromStream(TypeDescriptor elementType, TypeDescriptor targetType) {
        TypeDescriptor collectionOfElement = TypeDescriptor.collection(Collection.class, elementType);
        return this.conversionService.canConvert(collectionOfElement, targetType);
    }

    /**
     * Validate that the specified {@code sourceType} can be converted to a {@link Collection} of
     * the type of the stream elements.
     * @param elementType the type of the stream elements
     * @param sourceType the type to convert from
     */
    public boolean matchesToStream(TypeDescriptor elementType, TypeDescriptor sourceType) {
        TypeDescriptor collectionOfElement = TypeDescriptor.collection(Collection.class, elementType);
        return this.conversionService.canConvert(sourceType, collectionOfElement);
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType.isAssignableTo(STREAM_TYPE)) {
            return convertFromStream((Stream<?>) source, sourceType, targetType);
        }
        if (targetType.isAssignableTo(STREAM_TYPE)) {
            return convertToStream(source, sourceType, targetType);
        }
        // Should not happen
        throw new IllegalStateException("Unexpected source/target types");
    }

    private Object convertFromStream(Stream<?> source, TypeDescriptor streamType, TypeDescriptor targetType) {
        List<Object> content = source.collect(Collectors.<Object>toList());
        TypeDescriptor listType = TypeDescriptor.collection(List.class, streamType.getElementTypeDescriptor());
        return this.conversionService.convert(content, listType, targetType);
    }

    private Object convertToStream(Object source, TypeDescriptor sourceType, TypeDescriptor streamType) {
        TypeDescriptor targetCollection = TypeDescriptor.collection(List.class, streamType.getElementTypeDescriptor());
        List<?> target = (List<?>) this.conversionService.convert(source, sourceType, targetCollection);
        return target.stream();
    }


    private static Set<GenericConverter.ConvertiblePair> createConvertibleTypes() {
        Set<GenericConverter.ConvertiblePair> convertiblePairs = new HashSet<GenericConverter.ConvertiblePair>();
        convertiblePairs.add(new GenericConverter.ConvertiblePair(Stream.class, Collection.class));
        convertiblePairs.add(new GenericConverter.ConvertiblePair(Stream.class, Object[].class));
        convertiblePairs.add(new GenericConverter.ConvertiblePair(Collection.class, Stream.class));
        convertiblePairs.add(new GenericConverter.ConvertiblePair(Object[].class, Stream.class));
        return convertiblePairs;
    }

}