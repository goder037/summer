package com.rocket.summer.framework.data.redis.core.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rocket.summer.framework.data.redis.connection.DefaultSortParameters;
import com.rocket.summer.framework.data.redis.connection.SortParameters;
import com.rocket.summer.framework.data.redis.serializer.RedisSerializer;

/**
 * Utilities for {@link SortQuery} implementations.
 *
 * @author Costin Leau
 */
public abstract class QueryUtils {

    public static <K> SortParameters convertQuery(SortQuery<K> query, RedisSerializer<String> stringSerializer) {

        return new DefaultSortParameters(stringSerializer.serialize(query.getBy()), query.getLimit(), serialize(
                query.getGetPattern(), stringSerializer), query.getOrder(), query.isAlphabetic());
    }

    private static byte[][] serialize(List<String> strings, RedisSerializer<String> stringSerializer) {
        List<byte[]> raw = null;

        if (strings == null) {
            raw = Collections.emptyList();
        } else {
            raw = new ArrayList<byte[]>(strings.size());
            for (String key : strings) {
                raw.add(stringSerializer.serialize(key));
            }
        }
        return raw.toArray(new byte[raw.size()][]);
    }
}

