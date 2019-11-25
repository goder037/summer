package com.rocket.summer.framework.data.projection;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import com.rocket.summer.framework.util.ReflectionUtils;

/**
 * {@link MethodInterceptor} to support accessor methods to store and retrieve values from a {@link Map}.
 *
 * @author Oliver Gierke
 * @since 1.10
 */
@RequiredArgsConstructor
class MapAccessingMethodInterceptor implements MethodInterceptor {

    private final @NonNull Map<String, Object> map;

    /*
     * (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Method method = invocation.getMethod();

        if (ReflectionUtils.isObjectMethod(method)) {
            return invocation.proceed();
        }

        Accessor accessor = new Accessor(method);

        if (accessor.isGetter()) {
            return map.get(accessor.getPropertyName());
        } else if (accessor.isSetter()) {
            map.put(accessor.getPropertyName(), invocation.getArguments()[0]);
            return null;
        }

        throw new IllegalStateException("Should never get here!");
    }
}

