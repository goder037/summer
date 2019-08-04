package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.TypeDescriptor;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.ConditionalGenericConverter;

/**
 * Convert an Object to {@code java.util.Optional<T>} if necessary using the
 * {@code ConversionService} to convert the source Object to the generic type
 * of Optional when known.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 4.1
 */
final class ObjectToOptionalConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;


    public ObjectToOptionalConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }


    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        Set<ConvertiblePair> convertibleTypes = new LinkedHashSet<ConvertiblePair>(4);
        convertibleTypes.add(new ConvertiblePair(Collection.class, Optional.class));
        convertibleTypes.add(new ConvertiblePair(Object[].class, Optional.class));
        convertibleTypes.add(new ConvertiblePair(Object.class, Optional.class));
        return convertibleTypes;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (targetType.getResolvableType() != null) {
            return this.conversionService.canConvert(sourceType, new GenericTypeDescriptor(targetType));
        }
        else {
            return true;
        }
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return Optional.empty();
        }
        else if (source instanceof Optional) {
            return source;
        }
        else if (targetType.getResolvableType() != null) {
            Object target = this.conversionService.convert(source, sourceType, new GenericTypeDescriptor(targetType));
            if (target == null || (target.getClass().isArray() && Array.getLength(target) == 0) ||
                    (target instanceof Collection && ((Collection) target).isEmpty())) {
                return Optional.empty();
            }
            return Optional.of(target);
        }
        else {
            return Optional.of(source);
        }
    }


    @SuppressWarnings("serial")
    private static class GenericTypeDescriptor extends TypeDescriptor {

        public GenericTypeDescriptor(TypeDescriptor typeDescriptor) {
            super(typeDescriptor.getResolvableType().getGeneric(), null, typeDescriptor.getAnnotations());
        }
    }

}

