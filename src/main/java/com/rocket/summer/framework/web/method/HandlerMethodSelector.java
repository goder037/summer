package com.rocket.summer.framework.web.method;

import com.rocket.summer.framework.core.BridgeMethodResolver;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Defines the algorithm for searching handler methods exhaustively including interfaces and parent
 * classes while also dealing with parameterized methods as well as interface and class-based proxies.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public abstract class HandlerMethodSelector {

    /**
     * Selects handler methods for the given handler type. Callers of this method define handler methods
     * of interest through the {@link ReflectionUtils.MethodFilter} parameter.
     *
     * @param handlerType the handler type to search handler methods on
     * @param handlerMethodFilter a {@link ReflectionUtils.MethodFilter} to help recognize handler methods of interest
     * @return the selected methods, or an empty set
     */
    public static Set<Method> selectMethods(final Class<?> handlerType, final ReflectionUtils.MethodFilter handlerMethodFilter) {
        final Set<Method> handlerMethods = new LinkedHashSet<Method>();
        Set<Class<?>> handlerTypes = new LinkedHashSet<Class<?>>();
        Class<?> specificHandlerType = null;
        if (!Proxy.isProxyClass(handlerType)) {
            handlerTypes.add(handlerType);
            specificHandlerType = handlerType;
        }
        handlerTypes.addAll(Arrays.asList(handlerType.getInterfaces()));
        for (Class<?> currentHandlerType : handlerTypes) {
            final Class<?> targetClass = (specificHandlerType != null ? specificHandlerType : currentHandlerType);
            ReflectionUtils.doWithMethods(currentHandlerType, new ReflectionUtils.MethodCallback() {
                public void doWith(Method method) {
                    Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
                    Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
                    if (handlerMethodFilter.matches(specificMethod) &&
                            (bridgedMethod == specificMethod || !handlerMethodFilter.matches(bridgedMethod))) {
                        handlerMethods.add(specificMethod);
                    }
                }
            }, ReflectionUtils.USER_DECLARED_METHODS);
        }
        return handlerMethods;
    }

}