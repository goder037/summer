package com.rocket.summer.framework.core.type.classreading;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.rocket.summer.framework.core.io.DefaultResourceLoader;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.util.ClassUtils;

/**
 * Simple implementation of the {@link MetadataReaderFactory} interface,
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


    /**
     * Return the ResourceLoader that this MetadataReaderFactory has been
     * constructed with.
     */
    public final ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }


    @Override
    public MetadataReader getMetadataReader(String className) throws IOException {
        try {
            String resourcePath = ResourceLoader.CLASSPATH_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(className) + ClassUtils.CLASS_FILE_SUFFIX;
            Resource resource = this.resourceLoader.getResource(resourcePath);
            return getMetadataReader(resource);
        }
        catch (FileNotFoundException ex) {
            // Maybe an inner class name using the dot name syntax? Need to use the dollar syntax here...
            // ClassUtils.forName has an equivalent check for resolution into Class references later on.
            int lastDotIndex = className.lastIndexOf('.');
            if (lastDotIndex != -1) {
                String innerClassName =
                        className.substring(0, lastDotIndex) + '$' + className.substring(lastDotIndex + 1);
                String innerClassResourcePath = ResourceLoader.CLASSPATH_URL_PREFIX +
                        ClassUtils.convertClassNameToResourcePath(innerClassName) + ClassUtils.CLASS_FILE_SUFFIX;
                Resource innerClassResource = this.resourceLoader.getResource(innerClassResourcePath);
                if (innerClassResource.exists()) {
                    return getMetadataReader(innerClassResource);
                }
            }
            throw ex;
        }
    }

    @Override
    public MetadataReader getMetadataReader(Resource resource) throws IOException {
        return new SimpleMetadataReader(resource, this.resourceLoader.getClassLoader());
    }

}
