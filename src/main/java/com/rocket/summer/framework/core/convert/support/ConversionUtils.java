package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.ConversionFailedException;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.GenericConverter;

/**
 * Internal utilities for the conversion package.
 *
 * @author Keith Donald
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
        catch (Exception ex) {
            throw new ConversionFailedException(sourceType, targetType, source, ex);
        }
    }

    public static boolean canConvertElements(TypeDescriptor sourceElementType, TypeDescriptor targetElementType, ConversionService conversionService) {
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
        else if (sourceElementType.getType().isAssignableFrom(targetElementType.getType())) {
            // maybe;
            return true;
        }
        else {
            // no;
            return false;
        }
    }

}