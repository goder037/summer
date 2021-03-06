package com.rocket.summer.framework.web.method.annotation;

import com.rocket.summer.framework.core.ExceptionDepthComparator;
import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ReflectionUtils;
import com.rocket.summer.framework.web.bind.annotation.ExceptionHandler;
import com.rocket.summer.framework.web.method.HandlerMethodSelector;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Given a set of @{@link ExceptionHandler} methods at initialization, finds
 * the best matching method mapped to an exception at runtime.
 *
 * <p>Exception mappings are extracted from the method @{@link ExceptionHandler}
 * annotation or by looking for {@link Throwable} method arguments.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ExceptionHandlerMethodResolver {

    private static final Method NO_METHOD_FOUND = ClassUtils.getMethodIfAvailable(System.class, "currentTimeMillis");

    private final Map<Class<? extends Throwable>, Method> mappedMethods =
            new ConcurrentHashMap<Class<? extends Throwable>, Method>();

    private final Map<Class<? extends Throwable>, Method> exceptionLookupCache =
            new ConcurrentHashMap<Class<? extends Throwable>, Method>();

    /**
     * A constructor that finds {@link ExceptionHandler} methods in a handler.
     * @param handlerType the handler to inspect for exception handler methods.
     * @throws IllegalStateException
     * 		If an exception type is mapped to two methods.
     * @throws IllegalArgumentException
     * 		If an @{@link ExceptionHandler} method is not mapped to any exceptions.
     */
    public ExceptionHandlerMethodResolver(Class<?> handlerType) {
        init(HandlerMethodSelector.selectMethods(handlerType, EXCEPTION_HANDLER_METHODS));
    }

    private void init(Set<Method> exceptionHandlerMethods) {
        for (Method method : exceptionHandlerMethods) {
            for (Class<? extends Throwable> exceptionType : detectMappedExceptions(method)) {
                addExceptionMapping(exceptionType, method);
            }
        }
    }

    /**
     * Detect the exceptions an @{@link ExceptionHandler} method is mapped to.
     * If the method @{@link ExceptionHandler} annotation doesn't have any,
     * scan the method signature for all arguments of type {@link Throwable}.
     */
    @SuppressWarnings("unchecked")
    private List<Class<? extends Throwable>> detectMappedExceptions(Method method) {
        List<Class<? extends Throwable>> result = new ArrayList<Class<? extends Throwable>>();
        ExceptionHandler annotation = AnnotationUtils.findAnnotation(method, ExceptionHandler.class);
        if (annotation != null) {
            result.addAll(Arrays.asList(annotation.value()));
        }
        if (result.isEmpty()) {
            for (Class<?> paramType : method.getParameterTypes()) {
                if (Throwable.class.isAssignableFrom(paramType)) {
                    result.add((Class<? extends Throwable>) paramType);
                }
            }
        }
        Assert.notEmpty(result, "No exception types mapped to {" + method + "}");
        return result;
    }

    private void addExceptionMapping(Class<? extends Throwable> exceptionType, Method method) {
        Method oldMethod = this.mappedMethods.put(exceptionType, method);
        if (oldMethod != null && !oldMethod.equals(method)) {
            throw new IllegalStateException(
                    "Ambiguous @ExceptionHandler method mapped for [" + exceptionType + "]: {" +
                            oldMethod + ", " + method + "}.");
        }
    }

    /**
     * Find a method to handle the given exception. If more than one match is
     * found, the best match is selected via {@link ExceptionDepthComparator}.
     * @param exception the exception
     * @return an @{@link ExceptionHandler} method, or {@code null}
     */
    public Method resolveMethod(Exception exception) {
        Class<? extends Exception> exceptionType = exception.getClass();
        Method method = this.exceptionLookupCache.get(exceptionType);
        if (method == null) {
            method = getMappedMethod(exceptionType);
            this.exceptionLookupCache.put(exceptionType, method != null ? method : NO_METHOD_FOUND);
        }
        return method != NO_METHOD_FOUND ? method : null;
    }

    /**
     * Return the method mapped to the exception type, or {@code null}.
     */
    private Method getMappedMethod(Class<? extends Exception> exceptionType) {
        List<Class<? extends Throwable>> matches = new ArrayList<Class<? extends Throwable>>();
        for(Class<? extends Throwable> mappedException : this.mappedMethods.keySet()) {
            if (mappedException.isAssignableFrom(exceptionType)) {
                matches.add(mappedException);
            }
        }
        if (!matches.isEmpty()) {
            Collections.sort(matches, new ExceptionDepthComparator(exceptionType));
            return mappedMethods.get(matches.get(0));
        }
        else {
            return null;
        }
    }

    /**
     * A filter for selecting @{@link ExceptionHandler} methods.
     */
    public final static ReflectionUtils.MethodFilter EXCEPTION_HANDLER_METHODS = new ReflectionUtils.MethodFilter() {

        public boolean matches(Method method) {
            return AnnotationUtils.findAnnotation(method, ExceptionHandler.class) != null;
        }
    };

}
