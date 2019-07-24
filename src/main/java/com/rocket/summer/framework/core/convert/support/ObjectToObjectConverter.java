package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.ConversionFailedException;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.ConditionalGenericConverter;
import com.rocket.summer.framework.core.convert.converter.GenericConverter;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

/**
 * Generic Converter that attempts to convert a source Object to a target type
 * by delegating to methods on the target type.
 *
 * <p>Calls the static <code>valueOf(sourceType)</code> method on the target type
 * to perform the conversion, if such a method exists. Else calls the target type's
 * Constructor that accepts a single sourceType argument, if such a Constructor exists.
 * Else throws a ConversionFailedException.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 */
final class ObjectToObjectConverter implements ConditionalGenericConverter {

    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object.class, Object.class));
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return !sourceType.equals(targetType) && hasValueOfMethodOrConstructor(targetType.getType(), sourceType.getType());
    }

    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        Class<?> sourceClass = sourceType.getType();
        Class<?> targetClass = targetType.getType();
        Method method = getValueOfMethodOn(targetClass, sourceClass);
        try {
            if (method != null) {
                ReflectionUtils.makeAccessible(method);
                return method.invoke(null, source);
            }
            else {
                Constructor<?> constructor = getConstructor(targetClass, sourceClass);
                if (constructor != null) {
                    return constructor.newInstance(source);
                }
            }
        }
        catch (InvocationTargetException ex) {
            throw new ConversionFailedException(sourceType, targetType, source, ex.getTargetException());
        }
        catch (Throwable ex) {
            throw new ConversionFailedException(sourceType, targetType, source, ex);
        }
        throw new IllegalStateException("No static valueOf(" + sourceClass.getName() +
                ") method or Constructor(" + sourceClass.getName() + ") exists on " + targetClass.getName());
    }

    static boolean hasValueOfMethodOrConstructor(Class<?> clazz, Class<?> sourceParameterType) {
        return getValueOfMethodOn(clazz, sourceParameterType) != null || getConstructor(clazz, sourceParameterType) != null;
    }

    private static Method getValueOfMethodOn(Class<?> clazz, Class<?> sourceParameterType) {
        return ClassUtils.getStaticMethod(clazz, "valueOf", sourceParameterType);
    }

    private static Constructor<?> getConstructor(Class<?> clazz, Class<?> sourceParameterType) {
        return ClassUtils.getConstructorIfAvailable(clazz, sourceParameterType);
    }

}
