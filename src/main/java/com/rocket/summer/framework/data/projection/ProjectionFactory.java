package com.rocket.summer.framework.data.projection;

import java.util.List;

/**
 * A factory to create projecting instances for other objects usually used to allow easy creation of representation
 * projections to define which properties of a domain objects shall be exported in which way.
 *
 * @author Oliver Gierke
 * @since 1.10
 */
public interface ProjectionFactory {

    /**
     * Creates a projection of the given type for the given source object. The individual mapping strategy is defined by
     * the implementations.
     *
     * @param projectionType the type to create, must not be {@literal null}.
     * @param source the object to create a projection for, can be {@literal null}
     * @return
     */
    <T> T createProjection(Class<T> projectionType, Object source);

    /**
     * Creates a projection instance for the given type.
     *
     * @param projectionType the type to create, must not be {@literal null}.
     * @return
     */
    <T> T createProjection(Class<T> projectionType);

    /**
     * Returns the properties that will be consumed by the given projection type.
     *
     * @param projectionType must not be {@literal null}.
     * @return
     * @deprecated use {@link #getProjectionInformation(Class)}
     */
    @Deprecated
    List<String> getInputProperties(Class<?> projectionType);

    /**
     * Returns the {@link ProjectionInformation} for the given projection type.
     *
     * @param projectionType must not be {@literal null}.
     * @return
     * @since 1.12
     */
    ProjectionInformation getProjectionInformation(Class<?> projectionType);
}

