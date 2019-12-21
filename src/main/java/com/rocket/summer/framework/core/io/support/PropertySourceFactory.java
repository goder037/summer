package com.rocket.summer.framework.core.io.support;

import com.rocket.summer.framework.core.env.PropertySource;

import java.io.IOException;

/**
 * Strategy interface for creating resource-based {@link PropertySource} wrappers.
 *
 * @author Juergen Hoeller
 * @since 4.3
 * @see DefaultPropertySourceFactory
 */
public interface PropertySourceFactory {

    /**
     * Create a {@link PropertySource} that wraps the given resource.
     * @param name the name of the property source
     * @param resource the resource (potentially encoded) to wrap
     * @return the new {@link PropertySource} (never {@code null})
     * @throws IOException if resource resolution failed
     */
    PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException;

}
