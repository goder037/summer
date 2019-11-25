package com.rocket.summer.framework.data.projection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import com.rocket.summer.framework.aop.ProxyMethodInvocation;
import com.rocket.summer.framework.util.ConcurrentReferenceHashMap;
import com.rocket.summer.framework.util.ConcurrentReferenceHashMap.ReferenceType;
import com.rocket.summer.framework.util.ReflectionUtils;

/**
 * Method interceptor to invoke default methods on the repository proxy.
 *
 * @author Oliver Gierke
 * @author Jens Schauder
 * @author Mark Paluch
 */
public class DefaultMethodInvokingMethodInterceptor implements MethodInterceptor {

    private final MethodHandleLookup methodHandleLookup = MethodHandleLookup.getMethodHandleLookup();
    private final Map<Method, MethodHandle> methodHandleCache = new ConcurrentReferenceHashMap<Method, MethodHandle>(10,
            ReferenceType.WEAK);

    /*
     * (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Method method = invocation.getMethod();

        if (!com.rocket.summer.framework.data.util.ReflectionUtils.isDefaultMethod(method)) {
            return invocation.proceed();
        }

        Object[] arguments = invocation.getArguments();
        Object proxy = ((ProxyMethodInvocation) invocation).getProxy();

        return getMethodHandle(method).bindTo(proxy).invokeWithArguments(arguments);
    }

    private MethodHandle getMethodHandle(Method method) throws Exception {

        MethodHandle handle = methodHandleCache.get(method);

        if (handle == null) {

            handle = methodHandleLookup.lookup(method);
            methodHandleCache.put(method, handle);
        }

        return handle;
    }

    /**
     * Strategies for {@link MethodHandle} lookup.
     *
     * @since 2.0
     */
    enum MethodHandleLookup {

        /**
         * Open (via reflection construction of {@link MethodHandles.Lookup}) method handle lookup. Works with Java 8 and
         * with Java 9 permitting illegal access.
         */
        OPEN {

            private final Constructor<Lookup> constructor = getLookupConstructor();

            /*
             * (non-Javadoc)
             * @see com.rocket.summer.framework.data.projection.DefaultMethodInvokingMethodInterceptor.MethodHandleLookup#lookup(java.lang.reflect.Method)
             */
            @Override
            MethodHandle lookup(Method method) throws ReflectiveOperationException {

                if (constructor == null) {
                    throw new IllegalStateException("Could not obtain MethodHandles.lookup constructor");
                }

                return constructor.newInstance(method.getDeclaringClass()).unreflectSpecial(method, method.getDeclaringClass());
            }

            /*
             * (non-Javadoc)
             * @see com.rocket.summer.framework.data.projection.DefaultMethodInvokingMethodInterceptor.MethodHandleLookup#isAvailable()
             */
            @Override
            boolean isAvailable() {
                return constructor != null;
            }
        },

        /**
         * Encapsulated {@link MethodHandle} lookup working on Java 9.
         */
        ENCAPSULATED {

            /*
             * (non-Javadoc)
             * @see com.rocket.summer.framework.data.projection.DefaultMethodInvokingMethodInterceptor.MethodHandleLookup#lookup(java.lang.reflect.Method)
             */
            @Override
            MethodHandle lookup(Method method) throws ReflectiveOperationException {

                MethodType methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());

                return MethodHandles.lookup().findSpecial(method.getDeclaringClass(), method.getName(), methodType,
                        method.getDeclaringClass());
            }

            /*
             * (non-Javadoc)
             * @see com.rocket.summer.framework.data.projection.DefaultMethodInvokingMethodInterceptor.MethodHandleLookup#isAvailable()
             */
            @Override
            boolean isAvailable() {
                return true;
            }
        };

        /**
         * Lookup a {@link MethodHandle} given {@link Method} to look up.
         *
         * @param method must not be {@literal null}.
         * @return the method handle.
         * @throws ReflectiveOperationException
         */
        abstract MethodHandle lookup(Method method) throws ReflectiveOperationException;

        /**
         * @return {@literal true} if the lookup is available.
         */
        abstract boolean isAvailable();

        /**
         * Obtain the first available {@link MethodHandleLookup}.
         *
         * @return the {@link MethodHandleLookup}
         * @throws IllegalStateException if no {@link MethodHandleLookup} is available.
         */
        public static MethodHandleLookup getMethodHandleLookup() {

            for (MethodHandleLookup lookup : MethodHandleLookup.values()) {
                if (lookup.isAvailable()) {
                    return lookup;
                }
            }

            throw new IllegalStateException("No MethodHandleLookup available!");
        }

        private static Constructor<Lookup> getLookupConstructor() {

            try {

                Constructor<Lookup> constructor = Lookup.class.getDeclaredConstructor(Class.class);
                ReflectionUtils.makeAccessible(constructor);

                return constructor;

            } catch (Exception ex) {

                // this is the signal that we are on Java 9 (encapsulated) and can't use the accessible constructor approach.
                if (ex.getClass().getName().equals("java.lang.reflect.InaccessibleObjectException")) {
                    return null;
                }

                throw new IllegalStateException(ex);
            }
        }
    }
}

