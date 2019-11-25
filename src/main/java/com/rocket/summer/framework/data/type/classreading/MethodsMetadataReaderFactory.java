package com.rocket.summer.framework.data.type.classreading;

import java.io.IOException;

import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.core.type.classreading.SimpleMetadataReaderFactory;
import com.rocket.summer.framework.data.type.MethodsMetadata;

/**
 * Extension of {@link SimpleMetadataReaderFactory} that reads {@link MethodsMetadata}, creating a new ASM
 * {@link MethodsMetadataReader} for every request.
 *
 * @author Mark Paluch
 * @since 2.1
 * @since 1.11.11
 */
public class MethodsMetadataReaderFactory extends SimpleMetadataReaderFactory {

    /**
     * Create a new {@link MethodsMetadataReaderFactory} for the default class loader.
     */
    public MethodsMetadataReaderFactory() {}

    /**
     * Create a new {@link MethodsMetadataReaderFactory} for the given {@link ResourceLoader}.
     *
     * @param resourceLoader the Spring {@link ResourceLoader} to use (also determines the {@link ClassLoader} to use).
     */
    public MethodsMetadataReaderFactory(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    /**
     * Create a new {@link MethodsMetadataReaderFactory} for the given {@link ClassLoader}.
     *
     * @param classLoader the class loader to use.
     */
    public MethodsMetadataReaderFactory(ClassLoader classLoader) {
        super(classLoader);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.core.type.classreading.SimpleMetadataReaderFactory#getMetadataReader(java.lang.String)
     */
    @Override
    public MethodsMetadataReader getMetadataReader(String className) throws IOException {
        return (MethodsMetadataReader) super.getMetadataReader(className);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.core.type.classreading.SimpleMetadataReaderFactory#getMetadataReader(com.rocket.summer.framework.core.io.Resource)
     */
    @Override
    public MethodsMetadataReader getMetadataReader(Resource resource) throws IOException {
        return new DefaultMethodsMetadataReader(resource, getResourceLoader().getClassLoader());
    }
}

