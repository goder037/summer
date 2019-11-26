package com.rocket.summer.framework.data.convert;

import com.rocket.summer.framework.data.util.TypeInformation;

/**
 * Interface to abstract the mapping from a type alias to the actual type.
 *
 * @author Oliver Gierke
 */
public interface TypeInformationMapper {

    /**
     * Returns the actual {@link TypeInformation} to be used for the given alias.
     *
     * @param alias can be {@literal null}.
     * @return
     */
    TypeInformation<?> resolveTypeFrom(Object alias);

    /**
     * Returns the alias to be used for the given {@link TypeInformation}.
     *
     * @param type
     * @return
     */
    Object createAliasFor(TypeInformation<?> type);
}

