package com.rocket.summer.framework.data.keyvalue.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Converter capable of transforming a given {@link Iterable} into a collection type.
 *
 * @author Christoph Strobl
 */
public final class IterableConverter {

    private IterableConverter() {}

    /**
     * Converts a given {@link Iterable} into a {@link List}
     *
     * @param source
     * @return {@link Collections#emptyList()} when source is {@literal null}.
     */
    public static <T> List<T> toList(Iterable<T> source) {

        if (source == null) {
            return Collections.emptyList();
        }

        if (source instanceof List) {
            return (List<T>) source;
        }

        if (source instanceof Collection) {
            return new ArrayList<T>((Collection<T>) source);
        }

        List<T> result = new ArrayList<T>();
        for (T value : source) {
            result.add(value);
        }
        return result;
    }
}

