package com.rocket.summer.framework.core.io.support;

import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.DefaultPropertiesPersister;
import com.rocket.summer.framework.util.PropertiesPersister;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Convenient utility methods for loading of <code>java.util.Properties</code>,
 * performing standard handling of input streams.
 *
 * <p>For more configurable properties loading, including the option of a
 * customized encoding, consider using the PropertiesLoaderSupport class.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2.0
 * @see PropertiesLoaderSupport
 */
public abstract class PropertiesLoaderUtils {

    private static final String XML_FILE_EXTENSION = ".xml";

    /**
     * Load properties from the given resource.
     * @param resource the resource to load from
     * @return the populated Properties instance
     * @throws IOException if loading failed
     */
    public static Properties loadProperties(Resource resource) throws IOException {
        Properties props = new Properties();
        fillProperties(props, resource);
        return props;
    }

    /**
     * Load properties from the given EncodedResource,
     * potentially defining a specific encoding for the properties file.
     * @see #fillProperties(java.util.Properties, EncodedResource)
     */
    public static Properties loadProperties(EncodedResource resource) throws IOException {
        Properties props = new Properties();
        fillProperties(props, resource);
        return props;
    }

    /**
     * Fill the given properties from the given EncodedResource,
     * potentially defining a specific encoding for the properties file.
     * @param props the Properties instance to load into
     * @param resource the resource to load from
     * @throws IOException in case of I/O errors
     */
    public static void fillProperties(Properties props, EncodedResource resource)
            throws IOException {

        fillProperties(props, resource, new DefaultPropertiesPersister());
    }

    /**
     * Actually load properties from the given EncodedResource into the given Properties instance.
     * @param props the Properties instance to load into
     * @param resource the resource to load from
     * @param persister the PropertiesPersister to use
     * @throws IOException in case of I/O errors
     */
    static void fillProperties(Properties props, EncodedResource resource, PropertiesPersister persister)
            throws IOException {

        InputStream stream = null;
        Reader reader = null;
        try {
            String filename = resource.getResource().getFilename();
            if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
                stream = resource.getInputStream();
                persister.loadFromXml(props, stream);
            }
            else if (resource.requiresReader()) {
                reader = resource.getReader();
                persister.load(props, reader);
            }
            else {
                stream = resource.getInputStream();
                persister.load(props, stream);
            }
        }
        finally {
            if (stream != null) {
                stream.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Fill the given properties from the given resource.
     * @param props the Properties instance to fill
     * @param resource the resource to load from
     * @throws IOException if loading failed
     */
    public static void fillProperties(Properties props, Resource resource) throws IOException {
        InputStream is = resource.getInputStream();
        try {
            props.load(is);
        }
        finally {
            is.close();
        }
    }

    /**
     * Load all properties from the given class path resource,
     * using the default class loader.
     * <p>Merges properties if more than one resource of the same name
     * found in the class path.
     * @param resourceName the name of the class path resource
     * @return the populated Properties instance
     * @throws IOException if loading failed
     */
    public static Properties loadAllProperties(String resourceName) throws IOException {
        return loadAllProperties(resourceName, null);
    }

    /**
     * Load all properties from the given class path resource,
     * using the given class loader.
     * <p>Merges properties if more than one resource of the same name
     * found in the class path.
     * @param resourceName the name of the class path resource
     * @param classLoader the ClassLoader to use for loading
     * (or <code>null</code> to use the default class loader)
     * @return the populated Properties instance
     * @throws IOException if loading failed
     */
    public static Properties loadAllProperties(String resourceName, ClassLoader classLoader) throws IOException {
        Assert.notNull(resourceName, "Resource name must not be null");
        ClassLoader clToUse = classLoader;
        if (clToUse == null) {
            clToUse = ClassUtils.getDefaultClassLoader();
        }
        Properties properties = new Properties();
        Enumeration urls = clToUse.getResources(resourceName);
        while (urls.hasMoreElements()) {
            URL url = (URL) urls.nextElement();
            InputStream is = null;
            try {
                URLConnection con = url.openConnection();
                con.setUseCaches(false);
                is = con.getInputStream();
                properties.load(is);
            }
            finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        return properties;
    }

}

