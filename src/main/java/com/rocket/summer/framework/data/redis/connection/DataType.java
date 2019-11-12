package com.rocket.summer.framework.data.redis.connection;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enumeration of the Redis data types.
 *
 * @author Costin Leau
 */
public enum DataType {

    NONE("none"), STRING("string"), LIST("list"), SET("set"), ZSET("zset"), HASH("hash");

    private static final Map<String, DataType> codeLookup = new ConcurrentHashMap<String, DataType>(6);

    static {
        for (DataType type : EnumSet.allOf(DataType.class))
            codeLookup.put(type.code, type);

    }

    private final String code;

    DataType(String name) {
        this.code = name;
    }

    /**
     * Returns the code associated with the current enum.
     *
     * @return code of this enum
     */
    public String code() {
        return code;
    }

    /**
     * Utility method for converting an enum code to an actual enum.
     *
     * @param code enum code
     * @return actual enum corresponding to the given code
     */
    public static DataType fromCode(String code) {
        DataType data = codeLookup.get(code);
        if (data == null)
            throw new IllegalArgumentException("unknown data type code");
        return data;
    }
}
