package com.rocket.summer.framework.data.keyvalue.core;

import com.rocket.summer.framework.data.util.TypeInformation;

/**
 * API for components generating identifiers.
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 */
public interface IdentifierGenerator {

    /**
     * Creates an identifier of the given type.
     *
     * @param type must not be {@literal null}.
     * @return an identifier of the given type.
     */
    <T> T generateIdentifierOfType(TypeInformation<T> type);
}

