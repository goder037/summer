package com.rocket.summer.framework.objenesis.instantiator.sun;

import com.rocket.summer.framework.objenesis.ObjenesisException;
import com.rocket.summer.framework.objenesis.instantiator.ObjectInstantiator;
import com.rocket.summer.framework.objenesis.instantiator.annotations.Instantiator;
import com.rocket.summer.framework.objenesis.instantiator.annotations.Typology;

import java.lang.reflect.Constructor;

/**
 * Instantiates an object, WITHOUT calling it's constructor, using internal
 * sun.reflect.ReflectionFactory - a class only available on JDK's that use Sun's 1.4 (or later)
 * Java implementation. This is the best way to instantiate an object without any side effects
 * caused by the constructor - however it is not available on every platform.
 *
 * @author Joe Walnes
 * @see ObjectInstantiator
 */
@Instantiator(Typology.STANDARD)
public class SunReflectionFactoryInstantiator<T> implements ObjectInstantiator<T> {

    private final Constructor<T> mungedConstructor;

    public SunReflectionFactoryInstantiator(Class<T> type) {
        Constructor<Object> javaLangObjectConstructor = getJavaLangObjectConstructor();
        mungedConstructor = SunReflectionFactoryHelper.newConstructorForSerialization(
                type, javaLangObjectConstructor);
        mungedConstructor.setAccessible(true);
    }

    public T newInstance() {
        try {
            return mungedConstructor.newInstance((Object[]) null);
        }
        catch(Exception e) {
            throw new ObjenesisException(e);
        }
    }

    private static Constructor<Object> getJavaLangObjectConstructor() {
        try {
            return Object.class.getConstructor((Class[]) null);
        }
        catch(NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }
}


