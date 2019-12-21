package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.util.Assert;

/**
 * Tag collection class used to hold managed array elements, which may
 * include runtime bean references (to be resolved into bean objects).
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
public class ManagedArray extends ManagedList<Object> {

    /** Resolved element type for runtime creation of the target array */
    volatile Class<?> resolvedElementType;


    /**
     * Create a new managed array placeholder.
     * @param elementTypeName the target element type as a class name
     * @param size the size of the array
     */
    public ManagedArray(String elementTypeName, int size) {
        super(size);
        Assert.notNull(elementTypeName, "elementTypeName must not be null");
        setElementTypeName(elementTypeName);
    }

}

