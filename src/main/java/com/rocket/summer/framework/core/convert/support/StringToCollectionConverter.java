package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.CollectionFactory;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.ConditionalGenericConverter;
import com.rocket.summer.framework.core.convert.converter.GenericConverter;
import com.rocket.summer.framework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Converts a comma-delimited String to a Collection.
 * If the target collection element type is declared, only matches if String.class can be converted to it.
 *
 * @author Keith Donald
 * @since 3.0
 */
final class StringToCollectionConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;

    public StringToCollectionConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, Collection.class));
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (targetType.getElementTypeDescriptor() != null) {
            return this.conversionService.canConvert(sourceType, targetType.getElementTypeDescriptor());
        } else {
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        String string = (String) source;
        String[] fields = StringUtils.commaDelimitedListToStringArray(string);
        Collection<Object> target = CollectionFactory.createCollection(targetType.getType(), fields.length);
        if (targetType.getElementTypeDescriptor() == null) {
            for (String field : fields) {
                target.add(field.trim());
            }
        } else {
            for (String field : fields) {
                Object targetElement = this.conversionService.convert(field.trim(), sourceType, targetType.getElementTypeDescriptor());
                target.add(targetElement);
            }
        }
        return target;
    }

}
