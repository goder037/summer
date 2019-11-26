package com.rocket.summer.framework.data.convert;

/**
 * Interface to abstract implementations of how to access a type alias from a given source or sink.
 *
 * @author Oliver Gierke
 */
public interface TypeAliasAccessor<S> {

    /**
     * Reads the type alias to be used from the given source.
     *
     * @param source
     * @return can be {@literal null} in case no alias was found.
     */
    Object readAliasFrom(S source);

    /**
     * Writes the given type alias to the given sink.
     *
     * @param sink
     * @param alias
     */
    void writeTypeTo(S sink, Object alias);
}

