package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.ConditionalGenericConverter;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Set;

/**
 * Simply calls {@link Object#toString()} to convert any supported Object to a String.
 * Supports CharSequence, StringWriter, and any class with a String constructor or <code>valueOf(String)</code> method.
 *
 * <p>Used by the default ConversionService as a fallback if there are no other explicit
 * to-String converters registered.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 */
final class FallbackObjectToStringConverter implements ConditionalGenericConverter {

    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Object.class, String.class));
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        Class<?> sourceClass = sourceType.getObjectType();
        return CharSequence.class.isAssignableFrom(sourceClass) || StringWriter.class.isAssignableFrom(sourceClass) ||
                ObjectToObjectConverter.hasValueOfMethodOrConstructor(sourceClass, String.class);
    }

    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return (source != null ? source.toString() : null);
    }

}
