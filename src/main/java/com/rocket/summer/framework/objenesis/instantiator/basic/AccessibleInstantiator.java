package com.rocket.summer.framework.objenesis.instantiator.basic;

import com.rocket.summer.framework.objenesis.instantiator.annotations.Instantiator;
import com.rocket.summer.framework.objenesis.instantiator.annotations.Typology;

/**
 * Instantiates a class by grabbing the no-args constructor, making it accessible and then calling
 * Constructor.newInstance(). Although this still requires no-arg constructors, it can call
 * non-public constructors (if the security manager allows it).
 *
 * @author Joe Walnes
 * @see org.objenesis.instantiator.ObjectInstantiator
 */
@Instantiator(Typology.NOT_COMPLIANT)
public class AccessibleInstantiator<T> extends ConstructorInstantiator<T> {

    public AccessibleInstantiator(Class<T> type) {
        super(type);
        if(constructor != null) {
            constructor.setAccessible(true);
        }
    }
}

