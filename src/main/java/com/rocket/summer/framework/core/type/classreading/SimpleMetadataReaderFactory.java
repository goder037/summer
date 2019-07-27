package com.rocket.summer.framework.core.type.classreading;

import com.rocket.summer.framework.core.io.DefaultResourceLoader;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.util.ClassUtils;

import java.io.IOException;

/**
 * Simple implementation of the {@link MetadataReaderFactory} interface,
 * creating a new ASM {@link org.objectweb.asm.ClassReader} for every request.
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
public class SimpleMetadataReaderFactory implements MetadataReaderFactory {

    private final ResourceLoader resourceLoader;


    /**
     * Create a new SimpleMetadataReaderFactory for the default class loader.
     */
    public SimpleMetadataReaderFactory() {
        this.resourceLoader = new DefaultResourceLoader();
    }

    /**
     * Create a new SimpleMetadataReaderFactory for the given resource loader.
     * @param resourceLoader the Spring ResourceLoader to use
     * (also determines the ClassLoader to use)
     */
    public SimpleMetadataReaderFactory(ResourceLoader resourceLoader) {
        this.resourceLoader = (resourceLoader != null ? resourceLoader : new DefaultResourceLoader());
    }

    /**
     * Create a new SimpleMetadataReaderFactory for the given class loader.
     * @param classLoader the ClassLoader to use
     */
    public SimpleMetadataReaderFactory(ClassLoader classLoader) {
        this.resourceLoader =
                (classLoader != null ? new DefaultResourceLoader(classLoader) : new DefaultResourceLoader());
    }


    public MetadataReader getMetadataReader(String className) throws IOException {
        String resourcePath = ResourceLoader.CLASSPATH_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(className) + ClassUtils.CLASS_FILE_SUFFIX;
        return getMetadataReader(this.resourceLoader.getResource(resourcePath));
    }

    @Override
    public MetadataReader getMetadataReader(Resource resource) throws IOException {
        return new SimpleMetadataReader(resource, this.resourceLoader.getClassLoader());
    }

}
