package com.rocket.summer.framework.core.type.classreading;

import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Caching implementation of the {@link MetadataReaderFactory} interface,
 * caching an ASM {@link com.rocket.summer.framework.asm.ClassReader} per Spring Resource handle
 * (i.e. per ".class" file).
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
public class CachingMetadataReaderFactory extends SimpleMetadataReaderFactory {

    /** Default maximum number of entries for the MetadataReader cache: 256 */
    public static final int DEFAULT_CACHE_LIMIT = 256;


    private volatile int cacheLimit = DEFAULT_CACHE_LIMIT;

    private final Map<Resource, MetadataReader> metadataReaderCache =
            new LinkedHashMap<Resource, MetadataReader>(DEFAULT_CACHE_LIMIT, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Resource, MetadataReader> eldest) {
                    return size() > getCacheLimit();
                }
            };

    /**
     * Create a new CachingMetadataReaderFactory for the default class loader.
     */
    public CachingMetadataReaderFactory() {
        super();
    }

    /**
     * Create a new CachingMetadataReaderFactory for the given resource loader.
     * @param resourceLoader the Spring ResourceLoader to use
     * (also determines the ClassLoader to use)
     */
    public CachingMetadataReaderFactory(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    /**
     * Create a new CachingMetadataReaderFactory for the given class loader.
     * @param classLoader the ClassLoader to use
     */
    public CachingMetadataReaderFactory(ClassLoader classLoader) {
        super(classLoader);
    }

    /**
     * Return the maximum number of entries for the MetadataReader cache.
     */
    public int getCacheLimit() {
        return this.cacheLimit;
    }

    @Override
    public MetadataReader getMetadataReader(Resource resource) throws IOException {
        if (getCacheLimit() <= 0) {
            return super.getMetadataReader(resource);
        }
        synchronized (this.metadataReaderCache) {
            MetadataReader metadataReader = this.metadataReaderCache.get(resource);
            if (metadataReader == null) {
                metadataReader = super.getMetadataReader(resource);
                this.metadataReaderCache.put(resource, metadataReader);
            }
            return metadataReader;
        }
    }

    /**
     * Clear the entire MetadataReader cache, removing all cached class metadata.
     */
    public void clearCache() {
        synchronized (this.metadataReaderCache) {
            this.metadataReaderCache.clear();
        }
    }

}
