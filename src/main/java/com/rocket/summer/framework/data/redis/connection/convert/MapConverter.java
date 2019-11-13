package com.rocket.summer.framework.data.redis.connection.convert;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.rocket.summer.framework.core.convert.converter.Converter;

/**
 * Converts a Map of values of one key/value type to a Map of values of another type
 *
 * @author Jennifer Hickey
 * @param <S> The type of keys and values in the Map to convert
 * @param <T> The type of keys and values in the converted Map
 */
public class MapConverter<S, T> implements Converter<Map<S, S>, Map<T, T>> {

    private Converter<S, T> itemConverter;

    /**
     * @param itemConverter The {@link Converter} to use for converting individual Map keys and values
     */
    public MapConverter(Converter<S, T> itemConverter) {
        this.itemConverter = itemConverter;
    }

    public Map<T, T> convert(Map<S, S> source) {
        if (source == null) {
            return null;
        }
        Map<T, T> results;
        if (source instanceof LinkedHashMap) {
            results = new LinkedHashMap<T, T>();
        } else {
            results = new HashMap<T, T>();
        }
        for (Map.Entry<S, S> result : source.entrySet()) {
            results.put(itemConverter.convert(result.getKey()), itemConverter.convert(result.getValue()));
        }
        return results;
    }

}

