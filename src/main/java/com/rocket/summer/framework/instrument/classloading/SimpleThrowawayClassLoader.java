package com.rocket.summer.framework.instrument.classloading;

import com.rocket.summer.framework.core.OverridingClassLoader;

/**
 * ClassLoader that can be used to load classes without bringing them
 * into the parent loader. Intended to support JPA "temp class loader"
 * requirement, but not JPA-specific.
 *
 * @author Rod Johnson
 * @since 2.0
 */
public class SimpleThrowawayClassLoader extends OverridingClassLoader {

    static {
        if (parallelCapableClassLoaderAvailable) {
            ClassLoader.registerAsParallelCapable();
        }
    }


    /**
     * Create a new SimpleThrowawayClassLoader for the given ClassLoader.
     * @param parent the ClassLoader to build a throwaway ClassLoader for
     */
    public SimpleThrowawayClassLoader(ClassLoader parent) {
        super(parent);
    }

}

