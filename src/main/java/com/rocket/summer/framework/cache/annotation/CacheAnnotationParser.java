package com.rocket.summer.framework.cache.annotation;

import java.lang.reflect.Method;
import java.util.Collection;

import com.rocket.summer.framework.cache.interceptor.CacheOperation;

/**
 * Strategy interface for parsing known caching annotation types.
 * {@link AnnotationCacheOperationSource} delegates to such parsers
 * for supporting specific annotation types such as Spring's own
 * {@link Cacheable}, {@link CachePut} and{@link CacheEvict}.
 *
 * @author Costin Leau
 * @author Stephane Nicoll
 * @since 3.1
 * @see AnnotationCacheOperationSource
 * @see SpringCacheAnnotationParser
 */
public interface CacheAnnotationParser {

    /**
     * Parse the cache definition for the given class,
     * based on an annotation type understood by this parser.
     * <p>This essentially parses a known cache annotation into Spring's metadata
     * attribute class. Returns {@code null} if the class is not cacheable.
     * @param type the annotated class
     * @return the configured caching operation, or {@code null} if none found
     * @see AnnotationCacheOperationSource#findCacheOperations(Class)
     */
    Collection<CacheOperation> parseCacheAnnotations(Class<?> type);

    /**
     * Parse the cache definition for the given method,
     * based on an annotation type understood by this parser.
     * <p>This essentially parses a known cache annotation into Spring's metadata
     * attribute class. Returns {@code null} if the method is not cacheable.
     * @param method the annotated method
     * @return the configured caching operation, or {@code null} if none found
     * @see AnnotationCacheOperationSource#findCacheOperations(Method)
     */
    Collection<CacheOperation> parseCacheAnnotations(Method method);

}

