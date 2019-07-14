package com.rocket.summer.framework.beans.factory.xml;

import com.rocket.summer.framework.util.Assert;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * {@link EntityResolver} implementation that delegates to a {@link BeansDtdResolver}
 * and a {@link PluggableSchemaResolver} for DTDs and XML schemas, respectively.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rick Evans
 * @since 2.0
 * @see BeansDtdResolver
 * @see PluggableSchemaResolver
 */
public class DelegatingEntityResolver implements EntityResolver {

    /** Suffix for DTD files */
    public static final String DTD_SUFFIX = ".dtd";

    /** Suffix for schema definition files */
    public static final String XSD_SUFFIX = ".xsd";


    private final EntityResolver dtdResolver;

    private final EntityResolver schemaResolver;

    /**
     * Create a new DelegatingEntityResolver that delegates to
     * a default {@link BeansDtdResolver} and a default {@link PluggableSchemaResolver}.
     * <p>Configures the {@link PluggableSchemaResolver} with the supplied
     * {@link ClassLoader}.
     * @param classLoader the ClassLoader to use for loading
     * (can be <code>null</code>) to use the default ClassLoader)
     */
    public DelegatingEntityResolver(ClassLoader classLoader) {
        this.dtdResolver = new BeansDtdResolver();
        this.schemaResolver = new PluggableSchemaResolver(classLoader);
    }

    /**
     * Create a new DelegatingEntityResolver that delegates to
     * the given {@link EntityResolver EntityResolvers}.
     * @param dtdResolver the EntityResolver to resolve DTDs with
     * @param schemaResolver the EntityResolver to resolve XML schemas with
     */
    public DelegatingEntityResolver(EntityResolver dtdResolver, EntityResolver schemaResolver) {
        Assert.notNull(dtdResolver, "'dtdResolver' is required");
        Assert.notNull(schemaResolver, "'schemaResolver' is required");
        this.dtdResolver = dtdResolver;
        this.schemaResolver = schemaResolver;
    }


    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        if (systemId != null) {
            if (systemId.endsWith(DTD_SUFFIX)) {
                return this.dtdResolver.resolveEntity(publicId, systemId);
            }
            else if (systemId.endsWith(XSD_SUFFIX)) {
                return this.schemaResolver.resolveEntity(publicId, systemId);
            }
        }
        return null;
    }
}
