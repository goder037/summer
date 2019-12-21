package com.rocket.summer.framework.core.io.support;

import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ResourceUtils;

/**
 * Utility class for determining whether a given URL is a resource
 * location that can be loaded via a ResourcePatternResolver.
 *
 * <p>Callers will usually assume that a location is a relative path
 * if the {@link #isUrl(String)} method returns <code>false</code>.
 *
 * @author Juergen Hoeller
 * @since 1.2.3
 */
public abstract class ResourcePatternUtils {

    /**
     * Return whether the given resource location is a URL: either a
     * special "classpath" or "classpath*" pseudo URL or a standard URL.
     * @param resourceLocation the location String to check
     * @return whether the location qualifies as a URL
     * @see ResourcePatternResolver#CLASSPATH_ALL_URL_PREFIX
     * @see com.rocket.summer.framework.util.ResourceUtils#CLASSPATH_URL_PREFIX
     * @see com.rocket.summer.framework.util.ResourceUtils#isUrl(String)
     * @see java.net.URL
     */
    public static boolean isUrl(String resourceLocation) {
        return (resourceLocation != null &&
                (resourceLocation.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX) ||
                        ResourceUtils.isUrl(resourceLocation)));
    }

    /**
     * Return a default ResourcePatternResolver for the given ResourceLoader.
     * <p>This might be the ResourceLoader itself, if it implements the
     * ResourcePatternResolver extension, or a PathMatchingResourcePatternResolver
     * built on the given ResourceLoader.
     * @param resourceLoader the ResourceLoader to build a pattern resolver for
     * (not <code>null</code>)
     * @return the ResourcePatternResolver
     * @see PathMatchingResourcePatternResolver
     */
    public static ResourcePatternResolver getResourcePatternResolver(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        if (resourceLoader instanceof ResourcePatternResolver) {
            return (ResourcePatternResolver) resourceLoader;
        }
        else {
            return new PathMatchingResourcePatternResolver(resourceLoader);
        }
    }

}

