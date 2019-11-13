package com.rocket.summer.framework.data.redis.connection;

import java.util.List;

/**
 * Represents a data type returned from Redis, currently used to denote the expected return type of Redis scripting
 * commands
 *
 * @author Jennifer Hickey
 */
public enum ReturnType {

    /**
     * Returned as Boolean
     */
    BOOLEAN,

    /**
     * Returned as {@link Long}
     */
    INTEGER,

    /**
     * Returned as {@link List<Object>}
     */
    MULTI,

    /**
     * Returned as {@literal byte[]}
     */
    STATUS,

    /**
     * Returned as {@literal byte[]}
     */
    VALUE;

    public static ReturnType fromJavaType(Class<?> javaType) {
        if (javaType == null) {
            return ReturnType.STATUS;
        }
        if (javaType.isAssignableFrom(List.class)) {
            return ReturnType.MULTI;
        }
        if (javaType.isAssignableFrom(Boolean.class)) {
            return ReturnType.BOOLEAN;
        }
        if (javaType.isAssignableFrom(Long.class)) {
            return ReturnType.INTEGER;
        }
        return ReturnType.VALUE;
    }
}

