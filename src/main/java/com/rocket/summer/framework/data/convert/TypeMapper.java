package com.rocket.summer.framework.data.convert;

import com.rocket.summer.framework.data.util.TypeInformation;

/**
 * Interface to define strategies how to store type information in a store specific sink or source.
 *
 * @author Oliver Gierke
 */
public interface TypeMapper<S> {

    /**
     * Reads the {@link TypeInformation} from the given source.
     *
     * @param source must not be {@literal null}.
     * @return
     */
    TypeInformation<?> readType(S source);

    /**
     * Returns the {@link TypeInformation} from the given source if it is a more concrete type than the given default one.
     *
     * @param source must not be {@literal null}.
     * @param defaultType
     * @return
     */
    <T> TypeInformation<? extends T> readType(S source, TypeInformation<T> defaultType);

    /**
     * Writes type information for the given type into the given sink.
     *
     * @param type must not be {@literal null}.
     * @param dbObject must not be {@literal null}.
     */
    void writeType(Class<?> type, S dbObject);

    /**
     * Writes type information for the given {@link TypeInformation} into the given sink.
     *
     * @param type must not be {@literal null}.
     * @param dbObject must not be {@literal null}.
     */
    void writeType(TypeInformation<?> type, S dbObject);
}

