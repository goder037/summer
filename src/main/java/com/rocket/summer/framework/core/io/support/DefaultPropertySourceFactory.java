package com.rocket.summer.framework.core.io.support;

import com.rocket.summer.framework.core.env.PropertySource;

import java.io.IOException;

/**
 * The default implementation for {@link PropertySourceFactory},
 * wrapping every resource in a {@link ResourcePropertySource}.
 *
 * @author Juergen Hoeller
 * @since 4.3
 * @see PropertySourceFactory
 * @see ResourcePropertySource
 */
public class DefaultPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        return (name != null ? new ResourcePropertySource(name, resource) : new ResourcePropertySource(resource));
    }

}