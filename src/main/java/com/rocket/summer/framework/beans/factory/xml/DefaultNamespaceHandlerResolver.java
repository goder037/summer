package com.rocket.summer.framework.beans.factory.xml;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.beans.FatalBeanException;
import com.rocket.summer.framework.core.io.support.PropertiesLoaderUtils;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Default implementation of the {@link NamespaceHandlerResolver} interface.
 * Resolves namespace URIs to implementation classes based on the mappings
 * contained in mapping file.
 *
 * <p>By default, this implementation looks for the mapping file at
 * <code>META-INF/spring.handlers</code>, but this can be changed using the
 * {@link #DefaultNamespaceHandlerResolver(ClassLoader, String)} constructor.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see NamespaceHandler
 * @see DefaultBeanDefinitionDocumentReader
 */
public class DefaultNamespaceHandlerResolver implements NamespaceHandlerResolver {

    /**
     * The location to look for the mapping files. Can be present in multiple JAR files.
     */
    public static final String DEFAULT_HANDLER_MAPPINGS_LOCATION = "META-INF/spring.handlers";


    /** Logger available to subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    /** ClassLoader to use for NamespaceHandler classes */
    private final ClassLoader classLoader;

    /** Resource location to search for */
    private final String handlerMappingsLocation;

    /** Stores the mappings from namespace URI to NamespaceHandler class name / instance */
    private Map handlerMappings;


    /**
     * Create a new <code>DefaultNamespaceHandlerResolver</code> using the
     * default mapping file location.
     * <p>This constructor will result in the thread context ClassLoader being used
     * to load resources.
     * @see #DEFAULT_HANDLER_MAPPINGS_LOCATION
     */
    public DefaultNamespaceHandlerResolver() {
        this(null, DEFAULT_HANDLER_MAPPINGS_LOCATION);
    }

    /**
     * Create a new <code>DefaultNamespaceHandlerResolver</code> using the
     * default mapping file location.
     * @param classLoader the {@link ClassLoader} instance used to load mapping resources
     * (may be <code>null</code>, in which case the thread context ClassLoader will be used)
     * @see #DEFAULT_HANDLER_MAPPINGS_LOCATION
     */
    public DefaultNamespaceHandlerResolver(ClassLoader classLoader) {
        this(classLoader, DEFAULT_HANDLER_MAPPINGS_LOCATION);
    }

    /**
     * Create a new <code>DefaultNamespaceHandlerResolver</code> using the
     * supplied mapping file location.
     * @param classLoader the {@link ClassLoader} instance used to load mapping resources
     * may be <code>null</code>, in which case the thread context ClassLoader will be used)
     * @param handlerMappingsLocation the mapping file location
     */
    public DefaultNamespaceHandlerResolver(ClassLoader classLoader, String handlerMappingsLocation) {
        Assert.notNull(handlerMappingsLocation, "Handler mappings location must not be null");
        this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
        this.handlerMappingsLocation = handlerMappingsLocation;
    }


    /**
     * Locate the {@link NamespaceHandler} for the supplied namespace URI
     * from the configured mappings.
     * @param namespaceUri the relevant namespace URI
     * @return the located {@link NamespaceHandler}, or <code>null</code> if none found
     */
    public NamespaceHandler resolve(String namespaceUri) {
        Map handlerMappings = getHandlerMappings();
        Object handlerOrClassName = handlerMappings.get(namespaceUri);
        if (handlerOrClassName == null) {
            return null;
        }
        else if (handlerOrClassName instanceof NamespaceHandler) {
            return (NamespaceHandler) handlerOrClassName;
        }
        else {
            String className = (String) handlerOrClassName;
            try {
                Class handlerClass = ClassUtils.forName(className, this.classLoader);
                if (!NamespaceHandler.class.isAssignableFrom(handlerClass)) {
                    throw new FatalBeanException("Class [" + className + "] for namespace [" + namespaceUri +
                            "] does not implement the [" + NamespaceHandler.class.getName() + "] interface");
                }
                NamespaceHandler namespaceHandler = (NamespaceHandler) BeanUtils.instantiateClass(handlerClass);
                namespaceHandler.init();
                handlerMappings.put(namespaceUri, namespaceHandler);
                return namespaceHandler;
            }
            catch (ClassNotFoundException ex) {
                throw new FatalBeanException("NamespaceHandler class [" + className + "] for namespace [" +
                        namespaceUri + "] not found", ex);
            }
            catch (LinkageError err) {
                throw new FatalBeanException("Invalid NamespaceHandler class [" + className + "] for namespace [" +
                        namespaceUri + "]: problem with handler class file or dependent class", err);
            }
        }
    }

    /**
     * Load the specified NamespaceHandler mappings lazily.
     */
    private Map getHandlerMappings() {
        if (this.handlerMappings == null) {
            try {
                Properties mappings =
                        PropertiesLoaderUtils.loadAllProperties(this.handlerMappingsLocation, this.classLoader);
                if (logger.isDebugEnabled()) {
                    logger.debug("Loaded mappings [" + mappings + "]");
                }
                this.handlerMappings = new HashMap(mappings);
            }
            catch (IOException ex) {
                IllegalStateException ise = new IllegalStateException(
                        "Unable to load NamespaceHandler mappings from location [" + this.handlerMappingsLocation + "]");
                ise.initCause(ex);
                throw ise;
            }
        }
        return this.handlerMappings;
    }

}
