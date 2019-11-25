package com.rocket.summer.framework.data.querydsl;

import lombok.experimental.UtilityClass;

import com.rocket.summer.framework.util.StringUtils;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathType;

/**
 * Utility class for Querydsl.
 *
 * @author Oliver Gierke
 */
@UtilityClass
public class QueryDslUtils {

    public static final boolean QUERY_DSL_PRESENT = com.rocket.summer.framework.util.ClassUtils
            .isPresent("com.querydsl.core.types.Predicate", QueryDslUtils.class.getClassLoader());

    /**
     * Returns the property path for the given {@link Path}.
     *
     * @param path can be {@literal null}.
     * @return
     */
    public static String toDotPath(Path<?> path) {
        return toDotPath(path, "");
    }

    /**
     * Recursively builds up the dot path for the given {@link Path} instance by walking up the individual segments until
     * the root.
     *
     * @param path can be {@literal null}.
     * @param tail must not be {@literal null}.
     * @return
     */
    private static String toDotPath(Path<?> path, String tail) {

        if (path == null) {
            return tail;
        }

        PathMetadata metadata = path.getMetadata();
        Path<?> parent = metadata.getParent();

        if (parent == null) {
            return tail;
        }

        if (metadata.getPathType().equals(PathType.DELEGATE)) {
            return toDotPath(parent, tail);
        }

        Object element = metadata.getElement();

        if (element == null || !StringUtils.hasText(element.toString())) {
            return toDotPath(parent, tail);
        }

        return toDotPath(parent, StringUtils.hasText(tail) ? String.format("%s.%s", element, tail) : element.toString());
    }
}

