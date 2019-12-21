package com.rocket.summer.framework.expression.spel.support;

import java.lang.reflect.Method;

/**
 * A {@link com.rocket.summer.framework.expression.PropertyAccessor} variant for data binding
 * purposes, using reflection to access properties for reading and possibly writing.
 *
 * <p>A property can be referenced through a public getter method (when being read)
 * or a public setter method (when being written), and also as a public field.
 *
 * <p>This accessor is explicitly designed for user-declared properties and does not
 * resolve technical properties on {@code java.lang.Object} or {@code java.lang.Class}.
 * For unrestricted resolution, choose {@link ReflectivePropertyAccessor} instead.
 *
 * @author Juergen Hoeller
 * @since 4.3.15
 * @see #forReadOnlyAccess()
 * @see #forReadWriteAccess()
 * @see SimpleEvaluationContext
 * @see StandardEvaluationContext
 * @see ReflectivePropertyAccessor
 */
public class DataBindingPropertyAccessor extends ReflectivePropertyAccessor {

    /**
     * Create a new property accessor for reading and possibly also writing.
     * @param allowWrite whether to also allow for write operations
     * @see #canWrite
     */
    private DataBindingPropertyAccessor(boolean allowWrite) {
        super(allowWrite);
    }

    @Override
    protected boolean isCandidateForProperty(Method method, Class<?> targetClass) {
        Class<?> clazz = method.getDeclaringClass();
        return (clazz != Object.class && clazz != Class.class && !ClassLoader.class.isAssignableFrom(targetClass));
    }


    /**
     * Create a new data-binding property accessor for read-only operations.
     */
    public static DataBindingPropertyAccessor forReadOnlyAccess() {
        return new DataBindingPropertyAccessor(false);
    }

    /**
     * Create a new data-binding property accessor for read-write operations.
     */
    public static DataBindingPropertyAccessor forReadWriteAccess() {
        return new DataBindingPropertyAccessor(true);
    }

}

