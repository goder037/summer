package com.rocket.summer.framework.core.type.classreading;

import com.rocket.summer.framework.core.io.Resource;

import java.io.IOException;

/**
 * Factory interface for {@link MetadataReader} instances.
 * Allows for caching a MetadataReader per original resource.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see SimpleMetadataReaderFactory
 * @see CachingMetadataReaderFactory
 */
public interface MetadataReaderFactory {

    /**
     * Obtain a MetadataReader for the given class name.
     * @param className the class name (to be resolved to a ".class" file)
     * @return a holder for the ClassReader instance (never <code>null</code>)
     * @throws IOException in case of I/O failure
     */
    MetadataReader getMetadataReader(String className) throws IOException;

    /**
     * Obtain a MetadataReader for the given resource.
     * @param resource the resource (pointing to a ".class" file)
     * @return a holder for the ClassReader instance (never <code>null</code>)
     * @throws IOException in case of I/O failure
     */
    MetadataReader getMetadataReader(Resource resource) throws IOException;

}

