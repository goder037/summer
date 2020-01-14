package com.rocket.summer.framework.cache.interceptor;

import java.lang.reflect.Method;

/**
 * Simple key generator. Returns the parameter itself if a single non-null value
 * is given, otherwise returns a {@link SimpleKey} of the parameters.
 *
 * <p>Unlike {@link DefaultKeyGenerator}, no collisions will occur with the keys
 * generated by this class. The returned {@link SimpleKey} object can be safely
 * used with a {@link com.rocket.summer.framework.cache.concurrent.ConcurrentMapCache},
 * however, might not be suitable for all {@link com.rocket.summer.framework.cache.Cache}
 * implementations.
 *
 * @author Phillip Webb
 * @author Juergen Hoeller
 * @since 4.0
 * @see SimpleKey
 * @see DefaultKeyGenerator
 * @see com.rocket.summer.framework.cache.annotation.CachingConfigurer
 */
public class SimpleKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        return generateKey(params);
    }

    /**
     * Generate a key based on the specified parameters.
     */
    public static Object generateKey(Object... params) {
        if (params.length == 0) {
            return SimpleKey.EMPTY;
        }
        if (params.length == 1) {
            Object param = params[0];
            if (param != null && !param.getClass().isArray()) {
                return param;
            }
        }
        return new SimpleKey(params);
    }

}
