package com.rocket.summer.framework.data.repository.core.support;

import java.util.Properties;

import com.rocket.summer.framework.data.repository.core.NamedQueries;
import com.rocket.summer.framework.util.Assert;

/**
 * {@link NamedQueries} implementation backed by a {@link Properties} instance.
 *
 * @author Oliver Gierke
 */
public class PropertiesBasedNamedQueries implements NamedQueries {

    public static final NamedQueries EMPTY = new PropertiesBasedNamedQueries(new Properties());

    private final Properties properties;

    /**
     * Creates a new {@link PropertiesBasedNamedQueries} for the given {@link Properties} instance.
     *
     * @param properties
     */
    public PropertiesBasedNamedQueries(Properties properties) {
        Assert.notNull(properties, "Properties must not be null!");
        this.properties = properties;
    }

    /* (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.core.NamedQueries#hasNamedQuery(java.lang.String)
     */
    public boolean hasQuery(String queryName) {
        return properties.containsKey(queryName);
    }

    /* (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.core.NamedQueries#getNamedQuery(java.lang.String)
     */
    public String getQuery(String queryName) {
        return properties.getProperty(queryName);
    }
}

