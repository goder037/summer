package com.rocket.summer.framework.boot.type.classreading;

import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.core.type.classreading.CachingMetadataReaderFactory;
import com.rocket.summer.framework.core.type.classreading.MetadataReader;
import com.rocket.summer.framework.core.type.classreading.MetadataReaderFactory;
import com.rocket.summer.framework.core.type.classreading.SimpleMetadataReaderFactory;
import com.rocket.summer.framework.util.ConcurrentReferenceHashMap;

import java.io.IOException;
import java.util.Map;

/**
 * Caching implementation of the {@link MetadataReaderFactory} interface backed by a
 * {@link ConcurrentReferenceHashMap}, caching {@link MetadataReader} per Spring
 * {@link Resource} handle (i.e. per ".class" file).
 *
 * @author Phillip Webb
 * @since 1.4.0
 * @see CachingMetadataReaderFactory
 */
public class ConcurrentReferenceCachingMetadataReaderFactory
        extends SimpleMetadataReaderFactory {

    private final Map<Resource, MetadataReader> cache = new ConcurrentReferenceHashMap<Resource, MetadataReader>();

    /**
     * Create a new {@link ConcurrentReferenceCachingMetadataReaderFactory} instance for
     * the default class loader.
     */
    public ConcurrentReferenceCachingMetadataReaderFactory() {
        super();
    }

    /**
     * Create a new {@link ConcurrentReferenceCachingMetadataReaderFactory} instance for
     * the given resource loader.
     * @param resourceLoader the Spring ResourceLoader to use (also determines the
     * ClassLoader to use)
     */
    public ConcurrentReferenceCachingMetadataReaderFactory(
            ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    /**
     * Create a new {@link ConcurrentReferenceCachingMetadataReaderFactory} instance for
     * the given class loader.
     * @param classLoader the ClassLoader to use
     */
    public ConcurrentReferenceCachingMetadataReaderFactory(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    public MetadataReader getMetadataReader(Resource resource) throws IOException {
        MetadataReader metadataReader = this.cache.get(resource);
        if (metadataReader == null) {
            metadataReader = createMetadataReader(resource);
            this.cache.put(resource, metadataReader);
        }
        return metadataReader;
    }

    /**
     * Create the meta-data reader.
     * @param resource the source resource.
     * @return the meta-data reader
     * @throws IOException on error
     */
    protected MetadataReader createMetadataReader(Resource resource) throws IOException {
        return super.getMetadataReader(resource);
    }

    /**
     * Clear the entire MetadataReader cache, removing all cached class metadata.
     */
    public void clearCache() {
        this.cache.clear();
    }

}

