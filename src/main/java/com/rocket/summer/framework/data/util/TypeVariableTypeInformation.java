package com.rocket.summer.framework.data.util;

import static com.rocket.summer.framework.util.ObjectUtils.*;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import com.rocket.summer.framework.util.Assert;

/**
 * Special {@link TypeDiscoverer} to determine the actual type for a {@link TypeVariable}. Will consider the context the
 * {@link TypeVariable} is being used in.
 *
 * @author Oliver Gierke
 */
class TypeVariableTypeInformation<T> extends ParentTypeAwareTypeInformation<T> {

    private final TypeVariable<?> variable;

    /**
     * Creates a new {@link TypeVariableTypeInformation} for the given {@link TypeVariable} owning {@link Type} and parent
     * {@link TypeDiscoverer}.
     *
     * @param variable must not be {@literal null}
     * @param owningType must not be {@literal null}
     * @param parent must not be {@literal null}.
     */
    public TypeVariableTypeInformation(TypeVariable<?> variable, TypeDiscoverer<?> parent) {

        super(variable, parent);

        Assert.notNull(variable, "TypeVariable must not be null!");

        this.variable = variable;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.util.ParentTypeAwareTypeInformation#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof TypeVariableTypeInformation)) {
            return false;
        }

        TypeVariableTypeInformation<?> that = (TypeVariableTypeInformation<?>) obj;

        return getType().equals(that.getType());
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.util.ParentTypeAwareTypeInformation#hashCode()
     */
    @Override
    public int hashCode() {

        int result = 17;

        result += 31 * nullSafeHashCode(getType());

        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return variable.getName();
    }
}

