package com.rocket.summer.framework.jmx.export.naming;

import java.io.IOException;
import java.util.Properties;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rocket.summer.framework.beans.factory.InitializingBean;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.support.PropertiesLoaderUtils;
import com.rocket.summer.framework.jmx.support.ObjectNameManager;
import com.rocket.summer.framework.util.CollectionUtils;

/**
 * {@code ObjectNamingStrategy} implementation that builds
 * {@code ObjectName} instances from the key used in the
 * "beans" map passed to {@code MBeanExporter}.
 *
 * <p>Can also check object name mappings, given as {@code Properties}
 * or as {@code mappingLocations} of properties files. The key used
 * to look up is the key used in {@code MBeanExporter}'s "beans" map.
 * If no mapping is found for a given key, the key itself is used to
 * build an {@code ObjectName}.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.2
 * @see #setMappings
 * @see #setMappingLocation
 * @see #setMappingLocations
 * @see com.rocket.summer.framework.jmx.export.MBeanExporter#setBeans
 */
public class KeyNamingStrategy implements ObjectNamingStrategy, InitializingBean {

    /**
     * {@code Log} instance for this class.
     */
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Stores the mappings of bean key to {@code ObjectName}.
     */
    private Properties mappings;

    /**
     * Stores the {@code Resource}s containing properties that should be loaded
     * into the final merged set of {@code Properties} used for {@code ObjectName}
     * resolution.
     */
    private Resource[] mappingLocations;

    /**
     * Stores the result of merging the {@code mappings} {@code Properties}
     * with the properties stored in the resources defined by {@code mappingLocations}.
     */
    private Properties mergedMappings;


    /**
     * Set local properties, containing object name mappings, e.g. via
     * the "props" tag in XML bean definitions. These can be considered
     * defaults, to be overridden by properties loaded from files.
     */
    public void setMappings(Properties mappings) {
        this.mappings = mappings;
    }

    /**
     * Set a location of a properties file to be loaded,
     * containing object name mappings.
     */
    public void setMappingLocation(Resource location) {
        this.mappingLocations = new Resource[] {location};
    }

    /**
     * Set location of properties files to be loaded,
     * containing object name mappings.
     */
    public void setMappingLocations(Resource... mappingLocations) {
        this.mappingLocations = mappingLocations;
    }


    /**
     * Merges the {@code Properties} configured in the {@code mappings} and
     * {@code mappingLocations} into the final {@code Properties} instance
     * used for {@code ObjectName} resolution.
     */
    @Override
    public void afterPropertiesSet() throws IOException {
        this.mergedMappings = new Properties();
        CollectionUtils.mergePropertiesIntoMap(this.mappings, this.mergedMappings);

        if (this.mappingLocations != null) {
            for (Resource location : this.mappingLocations) {
                if (logger.isInfoEnabled()) {
                    logger.info("Loading JMX object name mappings file from " + location);
                }
                PropertiesLoaderUtils.fillProperties(this.mergedMappings, location);
            }
        }
    }


    /**
     * Attempts to retrieve the {@code ObjectName} via the given key, trying to
     * find a mapped value in the mappings first.
     */
    @Override
    public ObjectName getObjectName(Object managedBean, String beanKey) throws MalformedObjectNameException {
        String objectName = null;
        if (this.mergedMappings != null) {
            objectName = this.mergedMappings.getProperty(beanKey);
        }
        if (objectName == null) {
            objectName = beanKey;
        }
        return ObjectNameManager.getInstance(objectName);
    }

}

