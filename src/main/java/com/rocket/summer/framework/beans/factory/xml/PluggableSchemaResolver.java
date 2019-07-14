package com.rocket.summer.framework.beans.factory.xml;

import com.rocket.summer.framework.beans.FatalBeanException;
import com.rocket.summer.framework.core.io.ClassPathResource;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.support.PropertiesLoaderUtils;
import com.rocket.summer.framework.util.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.util.Properties;

/**
 * {@link EntityResolver} implementation that attempts to resolve schema URLs into
 * local {@link ClassPathResource classpath resources} using a set of mappings files.
 *
 * <p>By default, this class will look for mapping files in the classpath using the pattern:
 * <code>META-INF/spring.schemas</code> allowing for multiple files to exist on the
 * classpath at any one time.
 *
 * The format of <code>META-INF/spring.schemas</code> is a properties
 * file where each line should be of the form <code>systemId=schema-location</code>
 * where <code>schema-location</code> should also be a schema file in the classpath.
 * Since systemId is commonly a URL, one must be careful to escape any ':' characters
 * which are treated as delimiters in properties files.
 *
 * <p>The pattern for the mapping files can be overidden using the
 * {@link #PluggableSchemaResolver(ClassLoader, String)} constructor
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public class PluggableSchemaResolver implements EntityResolver {

    /**
     * The location of the file that defines schema mappings.
     * Can be present in multiple JAR files.
     */
    public static final String DEFAULT_SCHEMA_MAPPINGS_LOCATION = "META-INF/spring.schemas";


    private static final Log logger = LogFactory.getLog(PluggableSchemaResolver.class);

    private final ClassLoader classLoader;

    private final String schemaMappingsLocation;

    /** Stores the mapping of schema URL -> local schema path */
    private Properties schemaMappings;


    /**
     * Loads the schema URL -> schema file location mappings using the default
     * mapping file pattern "META-INF/spring.schemas".
     * @param classLoader the ClassLoader to use for loading
     * (can be <code>null</code>) to use the default ClassLoader)
     * @see PropertiesLoaderUtils#loadAllProperties(String, ClassLoader)
     */
    public PluggableSchemaResolver(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.schemaMappingsLocation = DEFAULT_SCHEMA_MAPPINGS_LOCATION;
    }

    /**
     * Loads the schema URL -> schema file location mappings using the given
     * mapping file pattern.
     * @param classLoader the ClassLoader to use for loading
     * (can be <code>null</code>) to use the default ClassLoader)
     * @param schemaMappingsLocation the location of the file that defines schema mappings
     * (must not be empty)
     * @see PropertiesLoaderUtils#loadAllProperties(String, ClassLoader)
     */
    public PluggableSchemaResolver(ClassLoader classLoader, String schemaMappingsLocation) {
        Assert.hasText(schemaMappingsLocation, "'schemaMappingsLocation' must not be empty");
        this.classLoader = classLoader;
        this.schemaMappingsLocation = schemaMappingsLocation;
    }


    public InputSource resolveEntity(String publicId, String systemId) throws IOException {
        if (logger.isTraceEnabled()) {
            logger.trace("Trying to resolve XML entity with public id [" + publicId +
                    "] and system id [" + systemId + "]");
        }
        if (systemId != null) {
            String resourceLocation = getSchemaMapping(systemId);
            if (resourceLocation != null) {
                Resource resource = new ClassPathResource(resourceLocation, this.classLoader);
                InputSource source = new InputSource(resource.getInputStream());
                source.setPublicId(publicId);
                source.setSystemId(systemId);
                if (logger.isDebugEnabled()) {
                    logger.debug("Found XML schema [" + systemId + "] in classpath: " + resourceLocation);
                }
                return source;
            }
        }
        return null;
    }

    protected String getSchemaMapping(String systemId) {
        if (this.schemaMappings == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Loading schema mappings from [" + this.schemaMappingsLocation + "]");
            }
            try {
                this.schemaMappings =
                        PropertiesLoaderUtils.loadAllProperties(this.schemaMappingsLocation, this.classLoader);
                if (logger.isDebugEnabled()) {
                    logger.debug("Loaded schema mappings: " + this.schemaMappings);
                }
            }
            catch (IOException ex) {
                throw new FatalBeanException(
                        "Unable to load schema mappings from location [" + this.schemaMappingsLocation + "]", ex);
            }
        }
        return this.schemaMappings.getProperty(systemId);
    }
}
