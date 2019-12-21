package com.rocket.summer.framework.core;

import com.rocket.summer.framework.util.ClassUtils;

/**
 * Default implementation of the {@link ParameterNameDiscoverer} strategy interface,
 * using the Java 8 standard reflection mechanism (if available), and falling back
 * to the ASM-based {@link LocalVariableTableParameterNameDiscoverer} for checking
 * debug information in the class file.
 *
 * <p>Further discoverers may be added through {@link #addDiscoverer(ParameterNameDiscoverer)}.
 *
 * @author Juergen Hoeller
 * @since 4.0
 * @see StandardReflectionParameterNameDiscoverer
 * @see LocalVariableTableParameterNameDiscoverer
 */
public class DefaultParameterNameDiscoverer extends PrioritizedParameterNameDiscoverer {

    private static final boolean standardReflectionAvailable = ClassUtils.isPresent(
            "java.lang.reflect.Executable", DefaultParameterNameDiscoverer.class.getClassLoader());


    public DefaultParameterNameDiscoverer() {
        if (standardReflectionAvailable) {
            addDiscoverer(new StandardReflectionParameterNameDiscoverer());
        }
        addDiscoverer(new LocalVariableTableParameterNameDiscoverer());
    }

}

