package com.rocket.summer.framework.data.repository.query.spi;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.TypeUtils;

/**
 * Value object to represent a function. Can either be backed by a static {@link Method} invocation (see
 * {@link #Function(Method)}) or a method invocation on an instance (see {@link #Function(Method, Object)}.
 *
 * @author Thomas Darimont
 * @author Oliver Gierke
 * @since 1.9
 */
public class Function {

    private final Method method;
    private final Object target;

    /**
     * Creates a new {@link Function} to statically invoke the given {@link Method}.
     *
     * @param method
     */
    public Function(Method method) {

        this(method, null);

        Assert.isTrue(Modifier.isStatic(method.getModifiers()), "Method must be static!");
    }

    /**
     * Creates a new {@link Function} for the given method on the given target instance.
     *
     * @param method must not be {@literal null}.
     * @param target can be {@literal null}, if so, the method
     */
    public Function(Method method, Object target) {

        Assert.notNull(method, "Method must not be null!");
        Assert.isTrue(target != null || Modifier.isStatic(method.getModifiers()),
                "Method must either be static or a non-static one with a target object!");

        this.method = method;
        this.target = target;
    }

    /**
     * Invokes the function with the given arguments.
     *
     * @param arguments must not be {@literal null}.
     * @return
     * @throws Exception
     */
    public Object invoke(Object[] arguments) throws Exception {
        return method.invoke(target, arguments);
    }

    /**
     * Returns the name of the function.
     *
     * @return
     */
    public String getName() {
        return method.getName();
    }

    /**
     * Returns the type declaring the {@link Function}.
     *
     * @return
     */
    public Class<?> getDeclaringClass() {
        return method.getDeclaringClass();
    }

    /**
     * Returns {@literal true} if the function can be called with the given {@code argumentTypes}.
     *
     * @param argumentTypes
     * @return
     */
    public boolean supports(List<TypeDescriptor> argumentTypes) {

        Class<?>[] parameterTypes = method.getParameterTypes();

        if (parameterTypes.length != argumentTypes.size()) {
            return false;
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            if (!TypeUtils.isAssignable(parameterTypes[i], argumentTypes.get(i).getType())) {
                return false;
            }
        }

        return true;
    }
}

