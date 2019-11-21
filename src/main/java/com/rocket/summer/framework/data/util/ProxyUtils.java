package com.rocket.summer.framework.data.util;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;

import com.rocket.summer.framework.core.io.support.SpringFactoriesLoader;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ConcurrentReferenceHashMap;

/**
 * Proxy type detection utilities, extensible via {@link ProxyDetector} registered via Spring factories.
 *
 * @author Oliver Gierke
 * @soundtrack Victor Wooten - Cruising Altitude (Trypnotix)
 */
@UtilityClass
public class ProxyUtils {

    private static Map<Class<?>, Class<?>> USER_TYPES = new ConcurrentReferenceHashMap<Class<?>, Class<?>>();

    private static final List<ProxyDetector> DETECTORS = SpringFactoriesLoader.loadFactories(ProxyDetector.class,
            ProxyUtils.class.getClassLoader());

    static {

        DETECTORS.add(new ProxyDetector() {

            @Override
            public Class<?> getUserType(Class<?> type) {
                return ClassUtils.getUserClass(type);
            }
        });
    }

    /**
     * Returns the user class for the given type.
     *
     * @param type must not be {@literal null}.
     * @return
     */
    public static Class<?> getUserClass(Class<?> type) {

        Assert.notNull(type, "Type must not be null!");

        Class<?> result = USER_TYPES.get(type);

        if (result != null) {
            return result;
        }

        result = type;

        for (ProxyDetector proxyDetector : DETECTORS) {
            result = proxyDetector.getUserType(result);
        }

        USER_TYPES.put(type, result);

        return result;
    }

    /**
     * Returns the user class for the given source object.
     *
     * @param source must not be {@literal null}.
     * @return
     */
    public static Class<?> getUserClass(Object source) {

        Assert.notNull(source, "Source object must not be null!");

        return getUserClass(source.getClass());
    }

    /**
     * SPI to extend Spring's default proxy detection capabilities.
     *
     * @author Oliver Gierke
     */
    public interface ProxyDetector {

        /**
         * Returns the user class for the given type.
         *
         * @param type will never be {@literal null}.
         * @return
         */
        Class<?> getUserType(Class<?> type);
    }
}

