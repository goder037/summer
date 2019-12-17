package com.rocket.summer.framework.objenesis.instantiator.sun;

import com.rocket.summer.framework.objenesis.ObjenesisException;
import com.rocket.summer.framework.objenesis.instantiator.ObjectInstantiator;
import com.rocket.summer.framework.objenesis.instantiator.annotations.Instantiator;
import com.rocket.summer.framework.objenesis.instantiator.annotations.Typology;
import com.rocket.summer.framework.objenesis.instantiator.util.UnsafeUtils;
import sun.misc.Unsafe;

@Instantiator(Typology.STANDARD)
public class UnsafeFactoryInstantiator<T> implements ObjectInstantiator<T> {
    private final Unsafe unsafe = UnsafeUtils.getUnsafe();
    private final Class<T> type;

    public UnsafeFactoryInstantiator(Class<T> type) {
        this.type = type;
    }

    public T newInstance() {
        try {
            return this.type.cast(this.unsafe.allocateInstance(this.type));
        } catch (InstantiationException var2) {
            throw new ObjenesisException(var2);
        }
    }
}
