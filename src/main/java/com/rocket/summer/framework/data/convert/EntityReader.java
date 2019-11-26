package com.rocket.summer.framework.data.convert;

/**
 * Interface to read object from store specific sources.
 *
 * @author Oliver Gierke
 */
public interface EntityReader<T, S> {

    /**
     * Reads the given source into the given type.
     *
     * @param type they type to convert the given source to.
     * @param source the source to create an object of the given type from.
     * @return
     */
    <R extends T> R read(Class<R> type, S source);
}

