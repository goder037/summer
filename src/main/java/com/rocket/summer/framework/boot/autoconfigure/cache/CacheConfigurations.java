package com.rocket.summer.framework.boot.autoconfigure.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.rocket.summer.framework.util.Assert;

/**
 * Mappings between {@link CacheType} and {@code @Configuration}.
 *
 * @author Phillip Webb
 * @author Eddú Meléndez
 */
final class CacheConfigurations {

    private static final Map<CacheType, Class<?>> MAPPINGS;

    static {
        Map<CacheType, Class<?>> mappings = new HashMap<CacheType, Class<?>>();
        mappings.put(CacheType.GENERIC, GenericCacheConfiguration.class);
        mappings.put(CacheType.REDIS, RedisCacheConfiguration.class);
        mappings.put(CacheType.SIMPLE, SimpleCacheConfiguration.class);
        mappings.put(CacheType.NONE, NoOpCacheConfiguration.class);
        MAPPINGS = Collections.unmodifiableMap(mappings);
    }



    private CacheConfigurations() {
    }

    public static String getConfigurationClass(CacheType cacheType) {
        Class<?> configurationClass = MAPPINGS.get(cacheType);
        Assert.state(configurationClass != null, "Unknown cache type " + cacheType);
        return configurationClass.getName();
    }

    public static CacheType getType(String configurationClassName) {
        for (Map.Entry<CacheType, Class<?>> entry : MAPPINGS.entrySet()) {
            if (entry.getValue().getName().equals(configurationClassName)) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException(
                "Unknown configuration class " + configurationClassName);
    }

}

