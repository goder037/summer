package com.rocket.summer.framework.data.redis.connection.util;

import java.util.Arrays;

/**
 * Simple wrapper class used for wrapping arrays so they can be used as keys inside maps.
 *
 * @author Costin Leau
 */
public class ByteArrayWrapper {

    private final byte[] array;
    private final int hashCode;

    public ByteArrayWrapper(byte[] array) {
        this.array = array;
        this.hashCode = Arrays.hashCode(array);
    }

    public boolean equals(Object obj) {
        if (obj instanceof ByteArrayWrapper) {
            return Arrays.equals(array, ((ByteArrayWrapper) obj).array);
        }

        return false;
    }

    public int hashCode() {
        return hashCode;
    }

    /**
     * Returns the array.
     *
     * @return Returns the array
     */
    public byte[] getArray() {
        return array;
    }
}

