package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.ConditionalGenericConverter;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Set;

/**
 * Converts an entity identifier to a entity reference by calling a static finder method
 * on the target entity type.
 *
 * <p>For this converter to match, the finder method must be public, static, have the signature
 * <code>find[EntityName]([IdType])</code>, and return an instance of the desired entity type.
 *
 * @author Keith Donald
 * @since 3.0
 */
final class IdToEntityConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;

    public IdToEntityConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Object.class, Object.class));
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        Method finder = getFinder(targetType.getType());
        return finder != null && this.conversionService.canConvert(sourceType, TypeDescriptor.valueOf(finder.getParameterTypes()[0]));
    }

    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        Method finder = getFinder(targetType.getType());
        Object id = this.conversionService.convert(source, sourceType, TypeDescriptor.valueOf(finder.getParameterTypes()[0]));
        return ReflectionUtils.invokeMethod(finder, source, id);
    }

    private Method getFinder(Class<?> entityClass) {
        String finderMethod = "find" + getEntityName(entityClass);
        Method[] methods = entityClass.getDeclaredMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers()) && method.getParameterTypes().length == 1 && method.getReturnType().equals(entityClass)) {
                if (method.getName().equals(finderMethod)) {
                    return method;
                }
            }
        }
        return null;
    }

    private String getEntityName(Class<?> entityClass) {
        String shortName = ClassUtils.getShortName(entityClass);
        int lastDot = shortName.lastIndexOf('.');
        if (lastDot != -1) {
            return shortName.substring(lastDot + 1);
        }
        else {
            return shortName;
        }
    }

}