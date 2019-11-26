package com.rocket.summer.framework.data.convert;

/**
 * Interface to write objects into store specific sinks.
 *
 * @param <T> the entity type the converter can handle
 * @param <S> the store specific sink the converter is able to write to
 * @author Oliver Gierke
 */
public interface EntityWriter<T, S> {

    void write(T source, S sink);
}
