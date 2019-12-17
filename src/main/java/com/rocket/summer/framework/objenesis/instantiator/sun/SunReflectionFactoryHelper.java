package com.rocket.summer.framework.objenesis.instantiator.sun;

import com.rocket.summer.framework.objenesis.ObjenesisException;
import com.rocket.summer.framework.objenesis.instantiator.ObjectInstantiator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Helper methods providing access to {@link sun.reflect.ReflectionFactory} via reflection, for use
 * by the {@link ObjectInstantiator}s that use it.
 *
 * @author Henri Tremblay
 */
class SunReflectionFactoryHelper {

    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> newConstructorForSerialization(Class<T> type,
                                                                    Constructor<?> constructor) {
        Class<?> reflectionFactoryClass = getReflectionFactoryClass();
        Object reflectionFactory = createReflectionFactory(reflectionFactoryClass);

        Method newConstructorForSerializationMethod = getNewConstructorForSerializationMethod(
                reflectionFactoryClass);

        try {
            return (Constructor<T>) newConstructorForSerializationMethod.invoke(
                    reflectionFactory, type, constructor);
        }
        catch(IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }

    private static Class<?> getReflectionFactoryClass() {
        try {
            return Class.forName("sun.reflect.ReflectionFactory");
        }
        catch(ClassNotFoundException e) {
            throw new ObjenesisException(e);
        }
    }

    private static Object createReflectionFactory(Class<?> reflectionFactoryClass) {
        try {
            Method method = reflectionFactoryClass.getDeclaredMethod(
                    "getReflectionFactory");
            return method.invoke(null);
        }
        catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            throw new ObjenesisException(e);
        }
    }

    private static Method getNewConstructorForSerializationMethod(Class<?> reflectionFactoryClass) {
        try {
            return reflectionFactoryClass.getDeclaredMethod(
                    "newConstructorForSerialization", Class.class, Constructor.class);
        }
        catch(NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }
}
