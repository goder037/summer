package com.rocket.summer.framework.cache.annotation;

import com.rocket.summer.framework.cache.CacheManager;
import com.rocket.summer.framework.cache.interceptor.CacheErrorHandler;
import com.rocket.summer.framework.cache.interceptor.CacheResolver;
import com.rocket.summer.framework.cache.interceptor.KeyGenerator;

/**
 * Interface to be implemented by @{@link com.rocket.summer.framework.context.annotation.Configuration
 * Configuration} classes annotated with @{@link EnableCaching} that wish or need to
 * specify explicitly how caches are resolved and how keys are generated for annotation-driven
 * cache management. Consider extending {@link CachingConfigurerSupport}, which provides a
 * stub implementation of all interface methods.
 *
 * <p>See @{@link EnableCaching} for general examples and context; see
 * {@link #cacheManager()}, {@link #cacheResolver()} and {@link #keyGenerator()}
 * for detailed instructions.
 *
 * @author Chris Beams
 * @author Stephane Nicoll
 * @since 3.1
 * @see EnableCaching
 * @see CachingConfigurerSupport
 */
public interface CachingConfigurer {

    /**
     * Return the cache manager bean to use for annotation-driven cache
     * management. A default {@link CacheResolver} will be initialized
     * behind the scenes with this cache manager. For more fine-grained
     * management of the cache resolution, consider setting the
     * {@link CacheResolver} directly.
     * <p>Implementations must explicitly declare
     * {@link com.rocket.summer.framework.context.annotation.Bean @Bean}, e.g.
     * <pre class="code">
     * &#064;Configuration
     * &#064;EnableCaching
     * public class AppConfig extends CachingConfigurerSupport {
     *     &#064;Bean // important!
     *     &#064;Override
     *     public CacheManager cacheManager() {
     *         // configure and return CacheManager instance
     *     }
     *     // ...
     * }
     * </pre>
     * See @{@link EnableCaching} for more complete examples.
     */
    CacheManager cacheManager();

    /**
     * Return the {@link CacheResolver} bean to use to resolve regular caches for
     * annotation-driven cache management. This is an alternative and more powerful
     * option of specifying the {@link CacheManager} to use.
     * <p>If both a {@link #cacheManager()} and {@code #cacheResolver()} are set,
     * the cache manager is ignored.
     * <p>Implementations must explicitly declare
     * {@link com.rocket.summer.framework.context.annotation.Bean @Bean}, e.g.
     * <pre class="code">
     * &#064;Configuration
     * &#064;EnableCaching
     * public class AppConfig extends CachingConfigurerSupport {
     *     &#064;Bean // important!
     *     &#064;Override
     *     public CacheResolver cacheResolver() {
     *         // configure and return CacheResolver instance
     *     }
     *     // ...
     * }
     * </pre>
     * See {@link EnableCaching} for more complete examples.
     */
    CacheResolver cacheResolver();

    /**
     * Return the key generator bean to use for annotation-driven cache management.
     * Implementations must explicitly declare
     * {@link com.rocket.summer.framework.context.annotation.Bean @Bean}, e.g.
     * <pre class="code">
     * &#064;Configuration
     * &#064;EnableCaching
     * public class AppConfig extends CachingConfigurerSupport {
     *     &#064;Bean // important!
     *     &#064;Override
     *     public KeyGenerator keyGenerator() {
     *         // configure and return KeyGenerator instance
     *     }
     *     // ...
     * }
     * </pre>
     * See @{@link EnableCaching} for more complete examples.
     */
    KeyGenerator keyGenerator();

    /**
     * Return the {@link CacheErrorHandler} to use to handle cache-related errors.
     * <p>By default,{@link com.rocket.summer.framework.cache.interceptor.SimpleCacheErrorHandler}
     * is used and simply throws the exception back at the client.
     * <p>Implementations must explicitly declare
     * {@link com.rocket.summer.framework.context.annotation.Bean @Bean}, e.g.
     * <pre class="code">
     * &#064;Configuration
     * &#064;EnableCaching
     * public class AppConfig extends CachingConfigurerSupport {
     *     &#064;Bean // important!
     *     &#064;Override
     *     public CacheErrorHandler errorHandler() {
     *         // configure and return CacheErrorHandler instance
     *     }
     *     // ...
     * }
     * </pre>
     * See @{@link EnableCaching} for more complete examples.
     */
    CacheErrorHandler errorHandler();

}

