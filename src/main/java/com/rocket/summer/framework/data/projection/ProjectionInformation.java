package com.rocket.summer.framework.data.projection;

import java.beans.PropertyDescriptor;
import java.util.List;

/**
 * Information about a projection type.
 *
 * @author Oliver Gierke
 * @since 1.12
 */
public interface ProjectionInformation {

    /**
     * Returns the projection type.
     *
     * @return will never be {@literal null}.
     */
    Class<?> getType();

    /**
     * Returns the properties that will be consumed by the projection type.
     *
     * @return will never be {@literal null}.
     */
    List<PropertyDescriptor> getInputProperties();

    /**
     * Returns whether supplying values for the properties returned via {@link #getInputProperties()} is sufficient to
     * create a working proxy instance. This will usually be used to determine whether the projection uses any dynamically
     * resolved properties.
     *
     * @return
     */
    boolean isClosed();
}

