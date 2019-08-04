package com.rocket.summer.framework.expression;

import com.rocket.summer.framework.core.convert.TypeDescriptor;

import java.util.List;

/**
 * A method resolver attempts locate a method and returns a command executor that can be
 * used to invoke that method. The command executor will be cached but if it 'goes stale'
 * the resolvers will be called again.
 *
 * @author Andy Clement
 * @since 3.0
 */
public interface MethodResolver {

    /**
     * Within the supplied context determine a suitable method on the supplied object that
     * can handle the specified arguments. Return a {@link MethodExecutor} that can be used
     * to invoke that method, or {@code null} if no method could be found.
     * @param context the current evaluation context
     * @param targetObject the object upon which the method is being called
     * @param argumentTypes the arguments that the constructor must be able to handle
     * @return a MethodExecutor that can invoke the method, or null if the method cannot be found
     */
    MethodExecutor resolve(EvaluationContext context, Object targetObject, String name,
                           List<TypeDescriptor> argumentTypes) throws AccessException;

}

