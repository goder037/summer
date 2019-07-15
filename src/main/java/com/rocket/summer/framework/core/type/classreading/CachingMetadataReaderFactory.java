package com.rocket.summer.framework.core.type.classreading;

import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Caching implementation of the {@link MetadataReaderFactory} interface,
 * caching an ASM {@link org.objectweb.asm.ClassReader} per Spring Resource handle
 * (i.e. per ".class" file).
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
public class CachingMetadataReaderFactory extends SimpleMetadataReaderFactory {

    private final Map<Resource, MetadataReader> classReaderCache = new HashMap<Resource, MetadataReader>();


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


    public MetadataReader getMetadataReader(Resource resource) throws IOException {
        synchronized (this.classReaderCache) {
            MetadataReader metadataReader = this.classReaderCache.get(resource);
            if (metadataReader == null) {
                metadataReader = super.getMetadataReader(resource);
                this.classReaderCache.put(resource, metadataReader);
            }
            return metadataReader;
        }
    }

}
