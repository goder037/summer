package com.rocket.summer.framework.expression.spel.support;

import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.expression.AccessException;
import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.expression.MethodExecutor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * A {@link com.rocket.summer.framework.expression.MethodResolver} variant for data binding
 * purposes, using reflection to access instance methods on a given target object.
 *
 * <p>This accessor does not resolve static methods and also no technical methods
 * on {@code java.lang.Object} or {@code java.lang.Class}.
 * For unrestricted resolution, choose {@link ReflectiveMethodResolver} instead.
 *
 * @author Juergen Hoeller
 * @since 4.3.15
 * @see #forInstanceMethodInvocation()
 * @see DataBindingPropertyAccessor
 */
public class DataBindingMethodResolver extends ReflectiveMethodResolver {

    private DataBindingMethodResolver() {
        super();
    }

    @Override
    public MethodExecutor resolve(EvaluationContext context, Object targetObject, String name,
                                  List<TypeDescriptor> argumentTypes) throws AccessException {

        if (targetObject instanceof Class) {
            throw new IllegalArgumentException("DataBindingMethodResolver does not support Class targets");
        }
        return super.resolve(context, targetObject, name, argumentTypes);
    }

    @Override
    protected boolean isCandidateForInvocation(Method method, Class<?> targetClass) {
        if (Modifier.isStatic(method.getModifiers())) {
            return false;
        }
        Class<?> clazz = method.getDeclaringClass();
        return (clazz != Object.class && clazz != Class.class && !ClassLoader.class.isAssignableFrom(targetClass));
    }


    /**
     * Create a new data-binding method resolver for instance method resolution.
     */
    public static DataBindingMethodResolver forInstanceMethodInvocation() {
        return new DataBindingMethodResolver();
    }

}

