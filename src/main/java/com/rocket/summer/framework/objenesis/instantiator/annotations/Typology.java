package com.rocket.summer.framework.objenesis.instantiator.annotations;

/**
 * Possible types of instantiator
 * @author Henri Tremblay
 */
public enum Typology {
    /**
     * Mark an instantiator used for standard instantiation (not calling a constructor).
     */
    STANDARD,

    /**
     * Mark an instantiator used for serialization.
     */
    SERIALIZATION,

    /**
     * Mark an instantiator that doesn't behave like a {@link #STANDARD} nor a {@link #SERIALIZATION} (e.g. calls a constructor, fails
     * all the time, etc.)
     */
    NOT_COMPLIANT,

    /**
     * No type specified on the instantiator class
     */
    UNKNOWN
}
