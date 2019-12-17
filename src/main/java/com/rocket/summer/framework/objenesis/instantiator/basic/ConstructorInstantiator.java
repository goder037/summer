package com.rocket.summer.framework.objenesis.instantiator.basic;

import com.rocket.summer.framework.objenesis.ObjenesisException;
import com.rocket.summer.framework.objenesis.instantiator.ObjectInstantiator;
import com.rocket.summer.framework.objenesis.instantiator.annotations.Instantiator;
import com.rocket.summer.framework.objenesis.instantiator.annotations.Typology;

import java.lang.reflect.Constructor;

/**
 * Instantiates a class by grabbing the no args constructor and calling Constructor.newInstance().
 * This can deal with default public constructors, but that's about it.
 *
 * @author Joe Walnes
 * @param <T> Type instantiated
 * @see ObjectInstantiator
 */
@Instantiator(Typology.NOT_COMPLIANT)
public class ConstructorInstantiator<T> implements ObjectInstantiator<T> {

    protected Constructor<T> constructor;

    public ConstructorInstantiator(Class<T> type) {
        try {
            constructor = type.getDeclaredConstructor((Class[]) null);
        }
        catch(Exception e) {
            throw new ObjenesisException(e);
        }
    }

    public T newInstance() {
        try {
            return constructor.newInstance((Object[]) null);
        }
        catch(Exception e) {
            throw new ObjenesisException(e);
        }
    }

}

