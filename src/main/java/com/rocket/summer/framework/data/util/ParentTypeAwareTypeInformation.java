package com.rocket.summer.framework.data.util;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

/**
 * Base class for {@link TypeInformation} implementations that need parent type awareness.
 *
 * @author Oliver Gierke
 */
public abstract class ParentTypeAwareTypeInformation<S> extends TypeDiscoverer<S> {

    private final TypeDiscoverer<?> parent;
    private int hashCode;

    /**
     * Creates a new {@link ParentTypeAwareTypeInformation}.
     *
     * @param type must not be {@literal null}.
     * @param parent must not be {@literal null}.
     */
    protected ParentTypeAwareTypeInformation(Type type, TypeDiscoverer<?> parent) {
        this(type, parent, parent.getTypeVariableMap());
    }

    /**
     * Creates a new {@link ParentTypeAwareTypeInformation} with the given type variables.
     *
     * @param type must not be {@literal null}.
     * @param parent must not be {@literal null}.
     * @param typeVariables must not be {@literal null}.
     */
    protected ParentTypeAwareTypeInformation(Type type, TypeDiscoverer<?> parent,
                                             Map<TypeVariable<?>, Type> typeVariables) {

        super(type, typeVariables);
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.util.TypeDiscoverer#createInfo(java.lang.reflect.Type)
     */
    @Override
    protected TypeInformation<?> createInfo(Type fieldType) {

        if (parent.getType().equals(fieldType)) {
            return parent;
        }

        return super.createInfo(fieldType);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.util.TypeDiscoverer#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (!super.equals(obj)) {
            return false;
        }

        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }

        ParentTypeAwareTypeInformation<?> that = (ParentTypeAwareTypeInformation<?>) obj;
        return this.parent == null ? that.parent == null : this.parent.equals(that.parent);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.util.TypeDiscoverer#hashCode()
     */
    @Override
    public int hashCode() {

        if (this.hashCode == 0) {
            this.hashCode = super.hashCode() + 31 * parent.hashCode();
        }

        return this.hashCode;
    }
}

