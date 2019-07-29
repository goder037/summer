package com.rocket.summer.framework.boot;

import com.rocket.summer.framework.core.env.*;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.web.context.ConfigurableWebEnvironment;
import com.rocket.summer.framework.web.context.support.StandardServletEnvironment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for converting one type of {@link Environment} to another.
 *
 * @author Ethan Rubinson
 * @author Andy Wilkinson
 */
final class EnvironmentConverter {

    private static final String CONFIGURABLE_WEB_ENVIRONMENT_CLASS = "org.springframework.web.context.ConfigurableWebEnvironment";

    private static final Set<String> SERVLET_ENVIRONMENT_SOURCE_NAMES;

    static {
        final Set<String> names = new HashSet<String>();
        names.add(StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME);
        names.add(StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME);
        names.add(StandardServletEnvironment.JNDI_PROPERTY_SOURCE_NAME);
        SERVLET_ENVIRONMENT_SOURCE_NAMES = Collections.unmodifiableSet(names);
    }

    private final ClassLoader classLoader;

    /**
     * Creates a new {@link EnvironmentConverter} that will use the given
     * {@code classLoader} during conversion.
     * @param classLoader the class loader to use
     */
    EnvironmentConverter(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Converts the given {@code environment} to a {@link StandardEnvironment}. If the
     * environment is already a {@code StandardEnvironment} and is not a
     * {@link ConfigurableWebEnvironment} no conversion is performed and it is returned
     * unchanged.
     * @param environment the Environment to convert
     * @return the converted Environment
     */
    StandardEnvironment convertToStandardEnvironmentIfNecessary(
            ConfigurableEnvironment environment) {
        if (environment instanceof StandardEnvironment
                && !isWebEnvironment(environment, this.classLoader)) {
            return (StandardEnvironment) environment;
        }
        return convertToStandardEnvironment(environment);
    }

    private boolean isWebEnvironment(ConfigurableEnvironment environment,
                                     ClassLoader classLoader) {
        try {
            Class<?> webEnvironmentClass = ClassUtils
                    .forName(CONFIGURABLE_WEB_ENVIRONMENT_CLASS, classLoader);
            return (webEnvironmentClass.isInstance(environment));
        }
        catch (Throwable ex) {
            return false;
        }
    }

    private StandardEnvironment convertToStandardEnvironment(
            ConfigurableEnvironment environment) {
        StandardEnvironment result = new StandardEnvironment();
        result.setActiveProfiles(environment.getActiveProfiles());
        result.setConversionService(environment.getConversionService());
        copyNonServletPropertySources(environment, result);
        return result;
    }

    private void copyNonServletPropertySources(ConfigurableEnvironment source,
                                               StandardEnvironment target) {
        removeAllPropertySources(target.getPropertySources());
        for (PropertySource<?> propertySource : source.getPropertySources()) {
            if (!SERVLET_ENVIRONMENT_SOURCE_NAMES.contains(propertySource.getName())) {
                target.getPropertySources().addLast(propertySource);
            }
        }
    }

    private void removeAllPropertySources(MutablePropertySources propertySources) {
        Set<String> names = new HashSet<String>();
        for (PropertySource<?> propertySource : propertySources) {
            names.add(propertySource.getName());
        }
        for (String name : names) {
            propertySources.remove(name);
        }
    }

}

