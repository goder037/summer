package com.rocket.summer.framework.objenesis.strategy;

import com.rocket.summer.framework.objenesis.instantiator.ObjectInstantiator;
import com.rocket.summer.framework.objenesis.instantiator.basic.AccessibleInstantiator;
import com.rocket.summer.framework.objenesis.instantiator.basic.ObjectInputStreamInstantiator;
import com.rocket.summer.framework.objenesis.instantiator.sun.SunReflectionFactoryInstantiator;
import com.rocket.summer.framework.objenesis.instantiator.sun.UnsafeFactoryInstantiator;

import java.io.Serializable;

import static org.objenesis.strategy.PlatformDescription.*;

/**
 * Guess the best instantiator for a given class. The instantiator will instantiate the class
 * without calling any constructor. Currently, the selection doesn't depend on the class. It relies
 * on the
 * <ul>
 * <li>JVM version</li>
 * <li>JVM vendor</li>
 * <li>JVM vendor version</li>
 * </ul>
 * However, instantiators are stateful and so dedicated to their class.
 *
 * @author Henri Tremblay
 * @see ObjectInstantiator
 */
public class StdInstantiatorStrategy extends BaseInstantiatorStrategy {

    /**
     * Return an {@link ObjectInstantiator} allowing to create instance without any constructor being
     * called.
     *
     * @param type Class to instantiate
     * @return The ObjectInstantiator for the class
     */
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {

        if(PlatformDescription.isThisJVM(HOTSPOT) || PlatformDescription.isThisJVM(OPENJDK)) {
            // Java 7 GAE was under a security manager so we use a degraded system
            if(PlatformDescription.isGoogleAppEngine() && PlatformDescription.SPECIFICATION_VERSION.equals("1.7")) {
                if(Serializable.class.isAssignableFrom(type)) {
                    return new ObjectInputStreamInstantiator<>(type);
                }
                return new AccessibleInstantiator<>(type);
            }
            // The UnsafeFactoryInstantiator would also work. But according to benchmarks, it is 2.5
            // times slower. So I prefer to use this one
            return new SunReflectionFactoryInstantiator<>(type);
        }

        // Fallback instantiator, should work with most modern JVM
        return new UnsafeFactoryInstantiator<>(type);

    }
}