package com.rocket.summer.framework.beans.factory.xml;

import com.rocket.summer.framework.core.io.ClassPathResource;
import com.rocket.summer.framework.core.io.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.IOException;

/**
 * EntityResolver implementation for the Spring beans DTD,
 * to load the DTD from the Spring class path (or JAR file).
 *
 * <p>Fetches "spring-beans-2.0.dtd" from the class path resource
 * "/org/springframework/beans/factory/xml/spring-beans-2.0.dtd",
 * no matter whether specified as some local URL that includes "spring-beans"
 * in the DTD name or as "http://www.springframework.org/dtd/spring-beans-2.0.dtd".
 *
 * @author Juergen Hoeller
 * @author Colin Sampaleanu
 * @since 04.06.2003
 * @see ResourceEntityResolver
 */
public class BeansDtdResolver implements EntityResolver {

    private static final String DTD_EXTENSION = ".dtd";

    private static final String[] DTD_NAMES = {"spring-beans-2.0", "spring-beans"};

    private static final Log logger = LogFactory.getLog(BeansDtdResolver.class);


    public InputSource resolveEntity(String publicId, String systemId) throws IOException {
        if (logger.isTraceEnabled()) {
            logger.trace("Trying to resolve XML entity with public ID [" + publicId +
                    "] and system ID [" + systemId + "]");
        }
        if (systemId != null && systemId.endsWith(DTD_EXTENSION)) {
            int lastPathSeparator = systemId.lastIndexOf("/");
            for (int i = 0; i < DTD_NAMES.length; ++i) {
                int dtdNameStart = systemId.indexOf(DTD_NAMES[i]);
                if (dtdNameStart > lastPathSeparator) {
                    String dtdFile = systemId.substring(dtdNameStart);
                    if (logger.isTraceEnabled()) {
                        logger.trace("Trying to locate [" + dtdFile + "] in Spring jar");
                    }
                    try {
                        Resource resource = new ClassPathResource(dtdFile, getClass());
                        InputSource source = new InputSource(resource.getInputStream());
                        source.setPublicId(publicId);
                        source.setSystemId(systemId);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Found beans DTD [" + systemId + "] in classpath: " + dtdFile);
                        }
                        return source;
                    }
                    catch (IOException ex) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Could not resolve beans DTD [" + systemId + "]: not found in class path", ex);
                        }
                    }

                }
            }
        }

        // Use the default behavior -> download from website or wherever.
        return null;
    }
}
