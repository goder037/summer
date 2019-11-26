package com.rocket.summer.framework.data.repository.config;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.PropertiesFactoryBean;
import com.rocket.summer.framework.beans.factory.support.AbstractBeanDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionBuilder;
import com.rocket.summer.framework.data.repository.core.NamedQueries;
import com.rocket.summer.framework.data.repository.core.support.PropertiesBasedNamedQueries;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Builder to create a {@link BeanDefinition} for a {@link NamedQueries} instance.
 *
 * @author Oliver Gierke
 */
public class NamedQueriesBeanDefinitionBuilder {

    private final String defaultLocation;
    private String locations;

    /**
     * Creates a new {@link NamedQueriesBeanDefinitionBuilder} using the given default location.
     *
     * @param defaultLocation must not be {@literal null} or empty.
     */
    public NamedQueriesBeanDefinitionBuilder(String defaultLocation) {

        Assert.hasText(defaultLocation, "DefaultLocation must not be null nor empty!");
        this.defaultLocation = defaultLocation;
    }

    /**
     * Sets the (comma-separated) locations to load the properties files from to back the {@link NamedQueries} instance.
     *
     * @param locations must not be {@literal null} or empty.
     */
    public void setLocations(String locations) {

        Assert.hasText(locations, "Locations must not be null nor empty!");
        this.locations = locations;
    }

    /**
     * Builds a new {@link BeanDefinition} from the given source.
     *
     * @param source
     * @return
     */
    public BeanDefinition build(Object source) {

        BeanDefinitionBuilder properties = BeanDefinitionBuilder.rootBeanDefinition(PropertiesFactoryBean.class);

        String locationsToUse = StringUtils.hasText(locations) ? locations : defaultLocation;
        properties.addPropertyValue("locations", locationsToUse);

        if (!StringUtils.hasText(locations)) {
            properties.addPropertyValue("ignoreResourceNotFound", true);
        }

        AbstractBeanDefinition propertiesDefinition = properties.getBeanDefinition();
        propertiesDefinition.setSource(source);

        BeanDefinitionBuilder namedQueries = BeanDefinitionBuilder.rootBeanDefinition(PropertiesBasedNamedQueries.class);
        namedQueries.addConstructorArgValue(propertiesDefinition);

        AbstractBeanDefinition namedQueriesDefinition = namedQueries.getBeanDefinition();
        namedQueriesDefinition.setSource(source);

        return namedQueriesDefinition;
    }
}

