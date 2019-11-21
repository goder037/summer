package com.rocket.summer.framework.data.util;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * Special {@link TypeDiscoverer} handling {@link GenericArrayType}s.
 *
 * @author Oliver Gierke
 */
class GenericArrayTypeInformation<S> extends ParentTypeAwareTypeInformation<S> {

    private final GenericArrayType type;

    /**
     * Creates a new {@link GenericArrayTypeInformation} for the given {@link GenericArrayTypeInformation} and
     * {@link TypeDiscoverer}.
     *
     * @param type must not be {@literal null}.
     * @param parent must not be {@literal null}.
     */
    protected GenericArrayTypeInformation(GenericArrayType type, TypeDiscoverer<?> parent) {

        super(type, parent);
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.util.TypeDiscoverer#getType()
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class<S> getType() {
        return (Class<S>) Array.newInstance(resolveType(type.getGenericComponentType()), 0).getClass();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.util.TypeDiscoverer#doGetComponentType()
     */
    @Override
    protected TypeInformation<?> doGetComponentType() {

        Type componentType = type.getGenericComponentType();
        return createInfo(componentType);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return type.toString();
    }
}

