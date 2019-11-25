package com.rocket.summer.framework.data.type;

import java.util.Set;

import com.rocket.summer.framework.core.type.ClassMetadata;
import com.rocket.summer.framework.core.type.MethodMetadata;
import com.rocket.summer.framework.data.type.classreading.MethodsMetadataReader;

/**
 * Interface that defines abstract metadata of a specific class, in a form that does not require that class to be loaded
 * yet.
 *
 * @author Mark Paluch
 * @since 2.1
 * @since 1.11.11
 * @see MethodMetadata
 * @see ClassMetadata
 * @see MethodsMetadataReader#getMethodsMetadata()
 */
public interface MethodsMetadata extends ClassMetadata {

    /**
     * Return all methods.
     *
     * @return the methods declared in the class ordered as found in the class file. Order does not necessarily reflect
     *         the declaration order in the source file.
     */
    Set<MethodMetadata> getMethods();

    /**
     * Return all methods matching method {@code name}.
     *
     * @param name name of the method, must not be {@literal null} or empty.
     * @return the methods matching method {@code name } declared in the class ordered as found in the class file. Order
     *         does not necessarily reflect the declaration order in the source file.
     */
    Set<MethodMetadata> getMethods(String name);
}

