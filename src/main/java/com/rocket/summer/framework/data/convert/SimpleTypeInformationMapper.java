package com.rocket.summer.framework.data.convert;

import lombok.Value;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rocket.summer.framework.data.util.ClassTypeInformation;
import com.rocket.summer.framework.data.util.TypeInformation;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Basic {@link TypeInformationMapper} implementation that interprets the alias handles as fully qualified class name
 * and tries to load a class with the given name to build {@link TypeInformation}. Returns the fully qualified class
 * name for alias creation.
 *
 * @author Oliver Gierke
 */
public class SimpleTypeInformationMapper implements TypeInformationMapper {

    private final Map<String, CachedTypeInformation> CACHE = new ConcurrentHashMap<String, CachedTypeInformation>();

    /**
     * Returns the {@link TypeInformation} that shall be used when the given {@link String} value is found as type hint.
     * The implementation will simply interpret the given value as fully-qualified class name and try to load the class.
     * Will return {@literal null} in case the given {@link String} is empty.
     *
     * @param alias the type to load, must not be {@literal null}.
     * @return the type to be used for the given {@link String} representation or {@literal null} if nothing found or the
     *         class cannot be loaded.
     */
    public ClassTypeInformation<?> resolveTypeFrom(Object alias) {

        if (!(alias instanceof String)) {
            return null;
        }

        String value = (String) alias;

        if (!StringUtils.hasText(value)) {
            return null;
        }

        CachedTypeInformation cachedValue = CACHE.get(value);

        if (cachedValue != null) {
            return cachedValue.getType();
        }

        try {
            return cacheAndReturn(value, ClassTypeInformation.from(ClassUtils.forName(value, null)));
        } catch (ClassNotFoundException e) {
            return cacheAndReturn(value, null);
        }
    }

    private ClassTypeInformation<?> cacheAndReturn(String value, ClassTypeInformation<?> type) {

        CACHE.put(value, CachedTypeInformation.of(type));

        return type;
    }

    /**
     * Turn the given type information into the String representation that shall be stored. Default implementation simply
     * returns the fully-qualified class name.
     *
     * @param type must not be {@literal null}.
     * @return the String representation to be stored or {@literal null} if no type information shall be stored.
     */
    public String createAliasFor(TypeInformation<?> type) {
        return type.getType().getName();
    }

    @Value(staticConstructor = "of")
    static class CachedTypeInformation {
        ClassTypeInformation<?> type;
    }
}

