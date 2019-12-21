package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.CollectionFactory;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.ConditionalGenericConverter;

import java.util.*;

/**
 * Converts a Map to another Map.
 *
 * <p>First, creates a new Map of the requested targetType with a size equal to the
 * size of the source Map. Then copies each element in the source map to the target map.
 * Will perform a conversion from the source maps's parameterized K,V types to the target
 * map's parameterized types K,V if necessary.
 *
 * @author Keith Donald
 * @since 3.0
 */
final class MapToMapConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;

    public MapToMapConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Map.class, Map.class));
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return canConvertKey(sourceType, targetType) && canConvertValue(sourceType, targetType);
    }

    @SuppressWarnings("unchecked")
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        boolean copyRequired = !targetType.getType().isInstance(source);
        Map<Object, Object> sourceMap = (Map<Object, Object>) source;
        if (!copyRequired && sourceMap.isEmpty()) {
            return sourceMap;
        }
        List<MapEntry> targetEntries = new ArrayList<MapEntry>(sourceMap.size());
        for (Map.Entry<Object, Object> entry : sourceMap.entrySet()) {
            Object sourceKey = entry.getKey();
            Object sourceValue = entry.getValue();
            Object targetKey = convertKey(sourceKey, sourceType, targetType.getMapKeyTypeDescriptor());
            Object targetValue = convertValue(sourceValue, sourceType, targetType.getMapValueTypeDescriptor());
            targetEntries.add(new MapEntry(targetKey, targetValue));
            if (sourceKey != targetKey || sourceValue != targetValue) {
                copyRequired = true;
            }
        }
        if(!copyRequired) {
            return sourceMap;
        }
        Map<Object, Object> targetMap = CollectionFactory.createMap(targetType.getType(), sourceMap.size());
        for (MapEntry entry : targetEntries) {
            entry.addToMap(targetMap);
        }
        return targetMap;
    }

    // internal helpers

    private boolean canConvertKey(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(sourceType.getMapKeyTypeDescriptor(),
                targetType.getMapKeyTypeDescriptor(), this.conversionService);
    }

    private boolean canConvertValue(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(sourceType.getMapValueTypeDescriptor(),
                targetType.getMapValueTypeDescriptor(), this.conversionService);
    }

    private Object convertKey(Object sourceKey, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (targetType == null) {
            return sourceKey;
        }
        return this.conversionService.convert(sourceKey, sourceType.getMapKeyTypeDescriptor(sourceKey), targetType);
    }

    private Object convertValue(Object sourceValue, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (targetType == null) {
            return sourceValue;
        }
        return this.conversionService.convert(sourceValue, sourceType.getMapValueTypeDescriptor(sourceValue), targetType);
    }

    private static class MapEntry {

        private Object key;
        private Object value;

        public MapEntry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public void addToMap(Map<Object, Object> map) {
            map.put(key, value);
        }
    }

}
