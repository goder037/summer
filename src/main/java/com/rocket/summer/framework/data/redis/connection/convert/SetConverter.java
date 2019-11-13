package com.rocket.summer.framework.data.redis.connection.convert;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.rocket.summer.framework.core.convert.converter.Converter;

/**
 * Converts a Set of values of one type to a Set of values of another type
 *
 * @author Jennifer Hickey
 * @param <S> The type of elements in the Set to convert
 * @param <T> The type of elements in the converted Set
 */
public class SetConverter<S, T> implements Converter<Set<S>, Set<T>> {

    private Converter<S, T> itemConverter;

    /**
     * @param itemConverter The {@link Converter} to use for converting individual Set items
     */
    public SetConverter(Converter<S, T> itemConverter) {
        this.itemConverter = itemConverter;
    }

    public Set<T> convert(Set<S> source) {
        if (source == null) {
            return null;
        }
        Set<T> results;
        if (source instanceof LinkedHashSet) {
            results = new LinkedHashSet<T>();
        } else {
            results = new HashSet<T>();
        }
        for (S result : source) {
            results.add(itemConverter.convert(result));
        }
        return results;
    }

}

