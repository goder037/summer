package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.ConversionFailedException;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.GenericConverter;
import com.rocket.summer.framework.util.ClassUtils;

/**
 * Internal utilities for the conversion package.
 *
 * @author Keith Donald
 * @author Stephane Nicoll
 * @since 3.0
 */
abstract class ConversionUtils {

    public static Object invokeConverter(GenericConverter converter, Object source, TypeDescriptor sourceType,
                                         TypeDescriptor targetType) {

        try {
            return converter.convert(source, sourceType, targetType);
        }
        catch (ConversionFailedException ex) {
            throw ex;
        }
        catch (Throwable ex) {
            throw new ConversionFailedException(sourceType, targetType, source, ex);
        }
    }

    public static boolean canConvertElements(TypeDescriptor sourceElementType, TypeDescriptor targetElementType,
                                             ConversionService conversionService) {

        if (targetElementType == null) {
            // yes
            return true;
        }
        if (sourceElementType == null) {
            // maybe
            return true;
        }
        if (conversionService.canConvert(sourceElementType, targetElementType)) {
            // yes
            return true;
        }
        if (ClassUtils.isAssignable(sourceElementType.getType(), targetElementType.getType())) {
            // maybe
            return true;
        }
        // no
        return false;
    }

    public static Class<?> getEnumType(Class<?> targetType) {
        Class<?> enumType = targetType;
        while (enumType != null && !enumType.isEnum()) {
            enumType = enumType.getSuperclass();
        }
        if (enumType == null) {
            throw new IllegalArgumentException(
                    "The target type " + targetType.getName() + " does not refer to an enum");
        }
        return enumType;
    }

}